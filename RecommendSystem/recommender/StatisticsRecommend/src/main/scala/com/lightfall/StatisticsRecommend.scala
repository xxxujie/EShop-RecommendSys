package com.lightfall

import java.sql.Timestamp
import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

case class Rating(userId: Int, BookId: Int, rating: BigDecimal, ratingTime: Timestamp)

object StatisticsRecommend {
    // 用 Map 记录一些配置，方便集中更改
    val config: Map[String, String] = Map(
        "spark.cores" -> "local[*]",
        "mysql.url" -> "jdbc:mysql://192.168.134.200:3306/aneuzon?useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC",
        "mysql.user" -> "root",
        "mysql.password" -> "M-992212.Schuco",
        "mysql.driver" -> "com.mysql.cj.jdbc.Driver"
    )

    def main(args: Array[String]):Unit = {

        // 加载 sparkSession 和 sqlSession
        val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("StatisticsRecommend")
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()
        spark.sparkContext.setLogLevel("WARN")
        val sqlContext = spark.sqlContext

        // jdbc 配置
        val prop = new Properties()
        prop.setProperty("user", config("mysql.user"))
        prop.setProperty("password", config("mysql.password"))
        prop.setProperty("driver", config("mysql.driver"))

        // 设置查询条件
        val table = "(select user_id, book_id, rating, rating_time from rating) as rating"

        // 获取指定的表
        val ratingDF = sqlContext.read.format("jdbc")
            .option("driver", prop.getProperty("driver"))
            .option("url", config("mysql.url"))
            .option("user", prop.getProperty("user"))
            .option("password", prop.getProperty("password"))
            .option("dbtable", table)
            .load().toDF()

        ratingDF.createOrReplaceTempView("ratings")
        ratingDF.show()

        // TODO: 用 spark sql 去做不同的统计
        // 1. 历史热门商品，按照评分个数统计
        val hotDF = spark.sql("select book_id as book_id, count(book_id) as rating_count from ratings group by book_id order by rating_count desc")
            .toDF()
        // 存入 hot 表，使用覆写模式
        storeDFInMysql(hotDF, "hot", prop, SaveMode.Overwrite)

        // 2. 近期热门商品，按照近期评分个数统计
        val recentDF = spark.sql("select book_id as book_id, count(book_id) as rating_count,"
            +"rating_time from ratings group by rating_time, book_id order by rating_time desc, rating_count desc")
        // 保存
        storeDFInMysql(recentDF, "hot_recent", prop, SaveMode.Overwrite)

        // 3. 优质商品统计，商品的平均分
        val avgDF = spark.sql("select book_id as book_id, avg(rating) as avg_rating " +
            "from ratings group by book_id order by avg_rating desc")
        storeDFInMysql(avgDF, "avg_rating", prop, SaveMode.Overwrite)

        spark.stop();
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
