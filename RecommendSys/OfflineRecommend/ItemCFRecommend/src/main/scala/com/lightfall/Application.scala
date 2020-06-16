package com.lightfall

import com.lightfall.config.TableConfig
import com.lightfall.utils.{DataUtils, GlobalUtils}
import org.apache.spark.sql.SaveMode

/**
 *  基于 ItemCF 的离线推荐
 */
object Application {

    def main(args: Array[String]):Unit = {
        // 得到 sparkSession
        val spark = GlobalUtils.InitAndGetSparkSession()

        import spark.implicits._

        // 读取 rating 表注册成临时表
        spark.table(TableConfig.RatingTable.TABLE_NAME).createOrReplaceTempView("tmpRating")

        // 提取出 (user_id, book_id, rating) 的三元组 RDD，并转化成 DF
        val ratingDF = spark.sql(
            "SELECT " +
                TableConfig.RatingTable.FIELD_USER_ID + " AS user_id," +
                TableConfig.RatingTable.FIELD_PRODUCT_ID +" AS product_id," +
                TableConfig.RatingTable.FIELD_SCORE +" AS score" +
                " FROM tmpRating"
        ).toDF().rdd.map (
            row =>
                (row(0).toString.toInt, row(1).toString.toInt, row(2).toString.toDouble)
        ).toDF("user_id", "book_id", "score")
            .cache()

        // 1. 统计每个商品的评分个数，按照 book_id 做 group by
        val ratingCountDF = ratingDF.groupBy("book_id").count()

        // 2. 在原有的评分表 rating 上添加 count 列
        val ratingWithCountDF = ratingDF.join(ratingCountDF, "book_id") // 按 book_id 匹配

        // 3. 将评分按用户 id 两两配对，统计两个商品被同一个用户评分过的次数
        val joinedDF = ratingWithCountDF.join(ratingWithCountDF, "user_id")
            .toDF("user_id", "book_id_1", "score_1", "count_1",
                "book_id_2", "score_2", "count_2")
            .select("user_id", "book_id_1", "count_1",
                "book_id_2", "count_2") // score 列没有用，去掉

        // 4. 创建一张临时表，用于写 sql 查询
        joinedDF.createOrReplaceTempView("joined")

        // 5. 按照 book_id_1, book_id_2 做 group by，统计 user_id 的数量，也就是对两个商品同时评分的人数
        val concurDF = spark.sql( // 可以用 """ 来隔行写 sql
            """
              |SELECT book_id_1
              |, book_id_2
              |, count(user_id) AS concur_count
              |, first(count_1) AS count_1
              |, first(count_2) AS count_2
              |FROM joined
              |GROUP BY book_id_1, book_id_2
              |""".stripMargin
        ).cache()

        var cnt = 0;
        // 6. 计算得到同现相似度表
        val simDF = concurDF.map {
            row =>
                val concurSim = concurSimCompute(row.getAs[Long]("concur_count"),
                    row.getAs[Long]("count_1"), row.getAs[Long]("count_2"))
                (row.getInt(0), row.getInt(1), concurSim)
        }
            .filter(row => row._1 != row._2)
            .filter(row => row._3.doubleValue() > 0.4)
            .rdd.zipWithIndex() // 加一列自增 id
            .map {
                row => (row._2, row._1._1, row._1._2, row._1._3)
            }.toDF(TableConfig.ConcurSimTable.FIELD_ID, TableConfig.ConcurSimTable.FIELD_PRODUCT_ID_1,
                TableConfig.ConcurSimTable.FIELD_PRODUCT_ID_2, TableConfig.ConcurSimTable.FIELD_SIM_SCORE)
        // 将 simDF 覆写进 hive
        DataUtils.storeDFInHive(simDF, TableConfig.ConcurSimTable.TABLE_NAME, SaveMode.Overwrite)
    }

    /**
     * 计算同现相似度
     * @param concurCount  对两个商品同时评分的用户数量
     * @param count_1      book 1 的评分个数
     * @param count_2      book 2 的评分个数
     * @return             同现相似度 -> BigDecimal
     */
    def concurSimCompute(concurCount: Long, count_1: Long, count_2: Long): BigDecimal = {
        concurCount / math.sqrt(count_1 * count_2)
    }

}
