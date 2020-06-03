package com.lightfall

import java.sql.Timestamp
import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
//import org.apache.spark.mllib._
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import scala.math.BigDecimal

// 定义标准评分对象
case class BookRating(user_id: Int, book_id: Int, rating: BigDecimal)
// 标准推荐对象
case class Recommendation(bookId: Int, rating: BigDecimal)
// 定义用户的推荐列表
case class UserRecs(user_id: Int, book_id: Int, recommendation: BigDecimal)
// 定义商品相似度列表
case class ProductRecs(bookId: Int, recs: Seq[Recommendation])

object LFMBasedRecommend {
    val USER_RECS = "UserRecs"
    val BOOK_RECS = "BookRecs"
    val USER_MAX_RECOMMENDATION = 20

    // 用 Map 记录一些配置，方便集中更改
    val config: Map[String, String] = Map(
        "spark.cores" -> "local[*]",
        "mysql.url" -> "jdbc:mysql://192.168.134.200:3306/aneuzon?useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC",
        "mysql.user" -> "root",
        "mysql.password" -> "M-992212.Schuco",
        "mysql.driver" -> "com.mysql.cj.jdbc.Driver"
    )


    def main(array: Array[String]): Unit = {
        // 加载 sparkSession 和 sqlSession
        val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("LFMBasedRecommend")
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()
        spark.sparkContext.setLogLevel("WARN")
        val sqlContext = spark.sqlContext

        // jdbc 配置
        val prop = new Properties()
        prop.setProperty("user", config("mysql.user"))
        prop.setProperty("password", config("mysql.password"))
        prop.setProperty("driver", config("mysql.driver"))

        import spark.implicits._
        // 设置查询条件
        val table = "(select user_id, book_id, rating, rating_time from rating) as rating"

        // 把 rating 表转化成 RDD，并进行 map 变成
        // (user_id, book_id, rating) 三元组
        val ratingRDD = sqlContext.read.format("jdbc")
            .option("driver", prop.getProperty("driver"))
            .option("url", config("mysql.url"))
            .option("user", prop.getProperty("user"))
            .option("password", prop.getProperty("password"))
            .option("dbtable", table)
            .load().as[BookRating].rdd.map(
            row => (row.user_id, row.book_id, row.rating.bigDecimal.doubleValue())
        ).cache()

        // 提取出用户和商品的数据集
        val userRDD = ratingRDD.map(_._1).distinct()
        val bookRDD = ratingRDD.map(_._2).distinct()

        // TODO: 核心计算过程
        // 1. 训练隐语义模型
        // 要把 ratingRDD 变成 mlib 要求的格式
        val trainData = ratingRDD.map(x => Rating(x._1, x._2, x._3))
        // rank：隐特征个数
        // iterations：迭代次数
        // lambda：正则化系数
        val (rank, iterations, lambda) = (5, 10, 0.01)
        val model = ALS.train(trainData, rank, iterations, lambda)

        // 2. 获得预测评分矩阵，得到用户的推荐列表
        // 用 userRDD 和 bookRDD 做笛卡尔积，得到空的 userBookRDD
        val userBook =  userRDD.cartesian(bookRDD)
        val preRating = model.predict(userBook)

        val schemas = Seq("user_id", "book_id", "rec_degree")
        // 从预测评分矩阵中提取得到用户推荐列表
        val userRecs = preRating.filter(_.rating>0)
                .map(
                    rating => (rating.user, rating.product, BigDecimal(rating.rating))
                ).toDF(schemas: _*)
        storeDFInMysql(userRecs, "user_recs", prop, SaveMode.Overwrite)

        // 3. 利用商品的特征向量，计算商品的相似度

        spark.stop()

    }

    // 把 DF 存进表
    def storeDFInMysql(df: DataFrame, tableName: String, prop: Properties, saveMode: SaveMode): Unit = {
        df.write
            .option("url", config("mysql.url"))
            .option("user", config("mysql.user"))
            .option("password", config("mysql.password"))
            .option("driver", config("mysql.driver"))
            .option("dbtable", tableName)
            .mode(saveMode)
            .format("jdbc")
            .save()
    }
}
