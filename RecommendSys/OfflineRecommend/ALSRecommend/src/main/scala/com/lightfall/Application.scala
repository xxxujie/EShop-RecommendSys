package com.lightfall

import com.lightfall.config.TableConfig
import com.lightfall.utils.{DataUtils, GlobalUtils}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.jblas.DoubleMatrix

/**
 * 基于 ALS 的离线推荐
 */
object Application {

    def main(args: Array[String]):Unit = {
        // 得到 sparkSession
        val spark = GlobalUtils.InitAndGetSparkSession()

        // 读取 rating 表注册成临时表
        spark.table(TableConfig.RatingTable.TABLE_NAME).createOrReplaceTempView("tmpRating")

        // 提取出 (user_id, book_id, rating) 的三元组 RDD
        val ratingRDD = spark.sql(
            "SELECT " +
                TableConfig.RatingTable.FIELD_USER_ID + " AS user_id," +
                TableConfig.RatingTable.FIELD_PRODUCT_ID +" AS product_id," +
                TableConfig.RatingTable.FIELD_SCORE +" AS score" +
                " FROM tmpRating"
        ).toDF().rdd.map (
            row =>
                (row(0).toString.toInt, row(1).toString.toInt, row(2).toString.toDouble)
        ).cache()

        // 提取出参与过评价的用户集和商品集
        val userRDD = ratingRDD.map(_._1).distinct()
        val bookRDD = ratingRDD.map(_._2).distinct()

        // 1. 训练隐语义模型
        val model = getModel(ratingRDD, 50, 10, 0.01)

        // 2. 获得预测评分矩阵
        val preRatingRDD = getPredictMatrix(model, userRDD, bookRDD)

        // 3. 从预测评分矩阵中提取用户的推荐列表
        val preSchemas = Seq(TableConfig.UserRecsTable.FIELD_USER_ID,
            TableConfig.UserRecsTable.FIELD_PRODUCT_ID, TableConfig.UserRecsTable.FIELD_REC_SCORE)
        val userRecs = getUserRecs(spark, preRatingRDD, preSchemas)
        // 把推荐列表覆盖写入 hive
        DataUtils.storeDFInHive(userRecs, TableConfig.UserRecsTable.TABLE_NAME, SaveMode.Overwrite)

        // 4. 利用商品的特征向量，计算商品的余弦相似度表
        val simSchemas = Seq(TableConfig.CosSimTable.FIELD_PRODUCT_ID_1,
            TableConfig.CosSimTable.FIELD_PRODUCT_ID_2, TableConfig.CosSimTable.FIELD_SIM_SCORE)
        val cosSim = getCosSim(spark, model, simSchemas)
        // 把余弦相似度表覆盖写入 hive
        DataUtils.storeDFInHive(cosSim, TableConfig.CosSimTable.TABLE_NAME, SaveMode.Overwrite)

        cosSim.rdd.map {
            x => (x.getInt(0), (x.getInt(1), x.getDecimal(2)))
        }

        spark.stop()
    }

    /**
     * 得到训练的模型
     * @param trainData  训练集 RDD，包含 (Int, Int, Double) 的元组
     * @param rank       隐特征个数
     * @param iterations ALS 训练轮次
     * @param lambda     正则化系数
     * @return           训练好的模型 -> MatrixFactorizationModel
     */
    def getModel(trainData: RDD[(Int, Int, Double)], rank: Int, iterations: Int, lambda: Double)
    : MatrixFactorizationModel = {
        // 要把 ratingRDD 变成 mlib 要求的格式（即 RDD[Rating]）
        val ratings = trainData.map(x => Rating(x._1, x._2, x._3))
        ALS.train(ratings, rank, iterations, lambda)
    }

    /**
     * 得到预测评分矩阵（即 RDD[Rating])
     * @param model       训练好的模型
     * @param userRDD     用户 id RDD
     * @param productRDD  商品 id RDD
     * @return            预测评分矩阵 -> RDD[Rating]
     */
    def getPredictMatrix(model: MatrixFactorizationModel,userRDD: RDD[Int], productRDD: RDD[Int])
    : RDD[Rating] = {
        val userProductRDD = userRDD.cartesian(productRDD)
        model.predict(userProductRDD)
    }

    /**
     * 得到用户推荐列表
     * @param sparkSession  sparkSession
     * @param preRatingRDD  预测评分矩阵
     * @param schemas       返回的表的字段格式
     * @return              用户推荐列表 -> DataFrame
     */
    def getUserRecs(sparkSession: SparkSession, preRatingRDD: RDD[Rating], schemas: Seq[String])
    : DataFrame = {
        import sparkSession.implicits._
        preRatingRDD.filter(_.rating > 0)
            .map (
                row => (row.user, row.product, BigDecimal(row.rating))
            ).toDF(schemas: _*)
    }

    /**
     * 得到余弦相似度表
     * @param sparkSession  sparkSession
     * @param model         训练好的模型
     * @param schemas       返回的表的字段格式
     * @return              余弦相似度表 -> DataFrame
     */
    def getCosSim(sparkSession: SparkSession, model: MatrixFactorizationModel, schemas: Seq[String])
    : DataFrame = {
        import sparkSession.implicits._
        val productFeatures = model.productFeatures.map {
            case (productId, features) => (productId, new DoubleMatrix(features))
        } // 把 features 由 Array 转成 jblas 矩阵，用于计算

        // 两两配对商品，计算余弦相似度
        productFeatures.cartesian(productFeatures)
            .filter {
                case (a, b) => a._1 != b._1
            } // 由于跟自己做笛卡尔积，相似度肯定很高，要过滤掉自己
            .map {
                case (a, b) =>
                    val simScore = cosSimCompute(a._2, b._2)
                    (a._1, b._1, BigDecimal(simScore)) // 返回 a 的 id，b 的 id，相似度
            }
            .filter(_._3 >= 0.4) // 只留下相似度大于 0.4 的
            .toDF(schemas: _*)
    }

    /**
     * 计算两个矩阵的余弦相似度
     * @param lMatrix  矩阵1
     * @param rMatrix  矩阵2
     * @return         计算结果 -> Double
     */
    def cosSimCompute(lMatrix: DoubleMatrix, rMatrix: DoubleMatrix): Double = {
        lMatrix.dot(rMatrix) / (lMatrix.norm2() * rMatrix.norm2())
    }

}
