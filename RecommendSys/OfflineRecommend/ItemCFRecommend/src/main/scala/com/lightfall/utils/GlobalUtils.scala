package com.lightfall.utils

import com.lightfall.config.GlobalConfig
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object GlobalUtils {

    /**
     * 初始化环境，并取得 sparkSession
     * @return  sparkSession
     */
    def InitAndGetSparkSession(): SparkSession = {
        // 设置访问 HDFS 的用户名，否则没有权限访问
        System.getProperties.setProperty("HADOOP_USER_NAME", "root")
        // 设置 hadoop 路径
        System.setProperty("hadoop.home.dir", GlobalConfig.HADOOP_HOME_DIR)

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ItemCFRecommend")
        val spark = SparkSession.builder().config(sparkConf)
            // 拷贝 hdfs-site.xml 不用设置，如果使用本地 hive，可通过该参数设置 metastore_db 的位置
            //.config("spark.sql.warehouse.dir", "/user/hive/warehouse")
            // 开启这个使 spark 和 hive 的 decimal 格式统一
            .config("spark.sql.parquet.writeLegacyFormat", true)
            // 让表能够覆盖原表
            .config("spark.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation", true)
            .enableHiveSupport() // 开启支持hive
            .getOrCreate()

        spark.sparkContext.setLogLevel("WARN")

        spark
    }

}
