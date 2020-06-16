package com.lightfall.eshop.service;

import com.lightfall.eshop.pojo.Book;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RecommendService {
    // 获得平均分排行
    List<Book> getRankList();

    // 根据书单列表获得每一本的平均评价
    Map<Integer, BigDecimal> getAvgRating(List<Book> books);

    // 获得历史热门图书
    List<Book> getHotList();

    // 获得当下热门图书
    List<Book> getHotRecent();

    // 获得用户个性化推荐列表
    List<Book> getUserRecs(int userId);

    // 获得同现相似的推荐图书
    List<Book> getConcurSimRecs(int bookId);
}
