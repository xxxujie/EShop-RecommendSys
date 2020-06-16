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

    // 以下都是用于存放结果和数据展示的表
    /**
     * 历史热门表
     */
    object HotTable {
        val TABLE_NAME = "aneuzon.hot"

        val FIELD_PRODUCT_ID = "book_id" // 商品 id -> int
        val FIELD_COUNT = "rating_count" // 被评分次数 -> bigint
    }

    /**
     * 最近热门表
     */
    object HotRecentTable {
        val TABLE_NAME = "aneuzon.hot_recent"

        val FIELD_PRODUCT_ID = "book_id" // 商品 id -> int
        val FIELD_COUNT = "rating_count" // 被评分次数 -> bigint
        val FIELD_TIME = "rating_time" // 评分时间 -> timestamp
    }

    /**
     * 商品平均分表
     */
    object AvgRatingTable {
        val TABLE_NAME = "aneuzon.avg_rating"

        val FIELD_PRODUCT_ID = "book_id" // 商品 id -> int
        val FIELD_AVG_SCORE = "avg_rating" // 商品平均分 -> decimal
    }

}
