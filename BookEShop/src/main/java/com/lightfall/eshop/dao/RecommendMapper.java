package com.lightfall.eshop.dao;

import com.lightfall.eshop.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Mapper
@Repository
public interface RecommendMapper {
    // 获得平均分排行
    List<Book> getRankList();

    // 根据书的 id 获得平均评价
    BigDecimal getAvgRating(@Param("book_id") int book_id);

    // 获得历史热门图书
    List<Book> getHotList();

    // 获得当下热门图书
    List<Book> getHotRecent();

    // 获得用户个性化推荐列表
    List<Book> getUserRecs(@Param("userId") int userId);

    // 获得同现相似的推荐图书
    List<Book> getConcurSimRecs(@Param("bookId") int bookId);
}
