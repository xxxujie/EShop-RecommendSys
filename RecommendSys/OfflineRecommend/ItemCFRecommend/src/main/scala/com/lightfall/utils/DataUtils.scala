package com.lightfall.utils

import org.apache.spark.sql.{DataFrame, SaveMode}

object DataUtils {

    /**
     * 把 DF 按指定模式存入指定的表
     * @param df         要存储的 DF
     * @param tableName  要存入的表名
     * @param saveMode   存储模式：Overwrite, Append 等
     */
    def storeDFInHive(df: DataFrame, tableName: String, saveMode: SaveMode) = {
        df.write.mode(saveMode)
            .option("field.delim", ",") // 字段分隔符（逗号）
            .option("line.delim", "\n") // 每行用换行符分隔
            .option("serialization.format", ",") // 序列化分隔符
            .saveAsTable(tableName)
    }

}
