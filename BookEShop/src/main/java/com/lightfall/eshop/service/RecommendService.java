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
}
