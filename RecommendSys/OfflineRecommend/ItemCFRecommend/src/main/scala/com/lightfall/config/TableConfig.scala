package com.lightfall.config

object TableConfig {

    /**
     * 评分表
     * 整个离线推荐都基于此表
     */
    object RatingTable {
        // 表名
        val TABLE_NAME = "aneuzon.rating"
        // 各字段名
        val FIELD_ID = "rating_id" // 主键 -> int
        val FIELD_USER_ID = "user_id" // 用户 id -> int
        val FIELD_PRODUCT_ID = "book_id" // 商品 id -> int
        val FIELD_SCORE = "rating" // 用户对商品的评分 -> decimal
        val FIELD_TIME = "rating_time" // 评分时间 -> timestamp
    }

    ///**
    // * 评分表
    // * 整个离线推荐都基于此表
    // */
    //object RatingTable {
    //    // 表名
    //    val TABLE_NAME = "eshop.rating"
    //    // 各字段名
    //    val FIELD_ID = "timestamp" // 主键 -> int
    //    val FIELD_USER_ID = "user_mapping" // 用户 id -> int
    //    val FIELD_PRODUCT_ID = "pro_mapping" // 商品 id -> int
    //    val FIELD_SCORE = "score" // 用户对商品的评分 -> decimal
    //    val FIELD_TIME = "create_time" // 评分时间 -> timestamp
    //}

    /**
     * 商品同现相似度表
     */
    object ConcurSimTable {
        val TABLE_NAME = "aneuzon.concur_sim"

        val FIELD_ID = "concur_id" // 主键 -> int
        val FIELD_PRODUCT_ID_1 = "book_id_1" // 商品1 id -> int
        val FIELD_PRODUCT_ID_2 = "book_id_2" // 商品2 id -> int
        val FIELD_SIM_SCORE = "sim_score" // 同现相似度 -> decimal
    }

}
