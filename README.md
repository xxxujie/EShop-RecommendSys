# 图书商城+推荐系统

>  课上大作业

BookEShop 是 Web 端。使用了 SpringBoot，Mybatis 做持久层框架，Thymeleaf 做模板引擎。

RecommendSystem 是推荐系统。

- `recommed/LFM-Based`：基于 LFM 的隐语义模型离线推荐。可以给针对每个用户个性化推荐。
- `recommend/StatisticsRecommend`：统计推荐。包括历史热门、当前热门以及平均分排行。
- 实时推荐模块暂未完成。



Web 端与 RecommendSystem 之间通过 mysql 和日志交互。Web 端收集的信息存入 mysql 或者通过 log4j 写入日志。推荐系统读取 mysql 和日志收集信息，处理后写回 mysql。

