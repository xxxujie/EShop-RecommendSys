# 图书商城+推荐系统

BookEShop 是 Web 端。基于 SpringBoot，使用 Mybatis 做持久层框架，Thymeleaf 做模板引擎。

RecommendSys 是推荐系统。

- `OfflineRecommend/ALSRecommend`：ALS 模型离线推荐。可以给针对每个用户个性化推荐。
- `OfflineRecommend/StatisticsRecommend`：统计推荐。包括历史热门、当前热门以及平均分排行。
- `OfflineRecommend/ItemCFRecommend`：基于 ItemCF 的离线推荐。可计算两个商品的同现相似度。
