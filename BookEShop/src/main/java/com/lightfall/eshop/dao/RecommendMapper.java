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
}
