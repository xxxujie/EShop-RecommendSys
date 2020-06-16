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

    /**
     * 用户个性化推荐表
     */
    object UserRecsTable {
        val TABLE_NAME = "aneuzon.user_recs"

        val FIELD_ID = "recs_id" // 主键 -> int
        val FIELD_USER_ID = "user_id" // 用户 id -> int
        val FIELD_PRODUCT_ID = "book_id" // 商品 id -> int
        val FIELD_REC_SCORE = "rec_score" // 推荐度 -> decimal
    }

    /**
     * 商品余弦相似度表
     */
    object CosSimTable {
        val TABLE_NAME = "aneuzon.cos_sim"

        val FIELD_PRODUCT_ID_1 = "book_id_1" // 商品1 id -> int
        val FIELD_PRODUCT_ID_2 = "book_id_2" // 商品2 id -> int
        val FIELD_SIM_SCORE = "sim_score" // 余弦相似度 -> decimal
    }

}
