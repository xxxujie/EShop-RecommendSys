package com.lightfall

import com.lightfall.config.TableConfig
import com.lightfall.utils.{DataUtils, GlobalUtils}
import org.apache.spark.sql.SaveMode

/**
 * 离线统计服务
 */
object Application {

    def main(args: Array[String]):Unit = {
        // 得到 sparkSession
        val spark = GlobalUtils.InitAndGetSparkSession()

        // 读取 rating 表注册成临时表
        spark.table(TableConfig.RatingTable.TABLE_NAME).createOrReplaceTempView("tmpRating")

        // 1. 历史热门商品，按照评分个数统计
        val hotDF = spark.sql(
            "SELECT " +
                TableConfig.RatingTable.FIELD_PRODUCT_ID +" AS product_id," +
                " count(" + TableConfig.RatingTable.FIELD_PRODUCT_ID + ") AS rating_count" +
                " FROM tmpRating" +
                " GROUP BY product_id" +
                " ORDER BY rating_count desc"
        ).toDF(TableConfig.HotTable.FIELD_PRODUCT_ID, TableConfig.HotTable.FIELD_COUNT)
        // 覆盖原表写入
        DataUtils.storeDFInHive(hotDF, TableConfig.HotTable.TABLE_NAME, SaveMode.Overwrite)

        // 2. 近期热门商品，按照近期评分个数统计
        val recentDF = spark.sql(
            "SELECT " +
                TableConfig.RatingTable.FIELD_PRODUCT_ID + " AS product_id," +
                " count(" + TableConfig.RatingTable.FIELD_PRODUCT_ID +") AS rating_count," +
                TableConfig.RatingTable.FIELD_TIME + " AS rating_time" +
                " FROM tmpRating" +
                " GROUP BY rating_time, product_id " +
                " ORDER BY rating_time desc, rating_count desc"
        ).toDF(TableConfig.HotRecentTable.FIELD_PRODUCT_ID,
            TableConfig.HotRecentTable.FIELD_COUNT, TableConfig.HotRecentTable.FIELD_TIME)
        // 覆盖原表写入
        DataUtils.storeDFInHive(recentDF, TableConfig.HotRecentTable.TABLE_NAME, SaveMode.Overwrite)

        // 3. 优质商品统计，商品的平均分
        val avgDF = spark.sql(
            "SELECT " +
                TableConfig.RatingTable.FIELD_PRODUCT_ID +" AS product_id," +
                " avg(" + TableConfig.RatingTable.FIELD_SCORE + ") AS avg_score" +
                " FROM tmpRating" +
                " GROUP BY product_id" +
                " ORDER BY avg_score desc"
        ).toDF(TableConfig.AvgRatingTable.FIELD_PRODUCT_ID, TableConfig.AvgRatingTable.FIELD_AVG_SCORE)
        // 覆盖原表写入
        DataUtils.storeDFInHive(avgDF, TableConfig.AvgRatingTable.TABLE_NAME, SaveMode.Overwrite)

        spark.stop()
    }

}
