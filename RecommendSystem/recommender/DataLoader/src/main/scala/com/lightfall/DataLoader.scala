package com.lightfall

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Properties

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SQLContext, SparkSession}
// book
// "3","Android系统源代码情景分析"," 罗升阳 ","  电子工业出版社 ","2012-10-1","109","1"
case class Book(bookId: Int, bookName: String, price: BigDecimal, category: Int)

// rating
// "1","19","3","8.5","2020-6-1 23:41:22"
case class Rating(userId: Int, BookId: Int, rating: BigDecimal, ratingTime: Timestamp)

object DataLoader {

    def main(args: Array[String]): Unit = {

        // 用 Map 记录一些配置，方便集中更改
        val config: Map[String, String] = Map(
            "spark.cores" -> "local[*]",
            "mysql.url" -> "jdbc:mysql://192.168.134.200:3306/aneuzon?useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC",
            "mysql.user" -> "root",
            "mysql.password" -> "M-992212.Schuco",
            "mysql.driver" -> "com.mysql.cj.jdbc.Driver"
        )

        // 加载 sparkSession 和 sqlSession
        val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("DataLoader")
        val spark = SparkSession.builder().config(sparkConf).getOrCreate()
        spark.sparkContext.setLogLevel("WARN")
        val sqlContext = spark.sqlContext

        // jdbc 配置
        val prop = new Properties()
        prop.setProperty("user", config("mysql.user"))
        prop.setProperty("password", config("mysql.password"))
        prop.setProperty("driver", config("mysql.driver"))
        // 获取指定的表
        val bookDF = sqlContext.read.jdbc(config("mysql.url"), "book", prop);
        val ratingDF = sqlContext.read.jdbc(config("mysql.url"), "rating", prop);



//        import spark.implicits._
//
//        // 加载数据
//        val bookRDD = spark.sparkContext.textFile(BOOK_DATA_PATH)
//        val bookDF = bookRDD.map(item => {
//            val attr = item.split(",")
//            Book(attr(0).toInt, attr(1).trim, attr(5).toInt, attr(6).toInt)
//        }).toDF()
//
//        val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
//        println(ratingRDD.take(5))
//        val ratingDF = ratingRDD.map(item => {
//            val attr = item.split(",")
//            Rating(attr(1).toInt, attr(2).toInt, attr(3).toDouble)
//        })


    }
}
