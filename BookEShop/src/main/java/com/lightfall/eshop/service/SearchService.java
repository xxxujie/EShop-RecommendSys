package com.lightfall.eshop.service;

import com.lightfall.eshop.pojo.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SearchService {
    // 一页的查询结果
    List<Book> getSearchResultInPage(String bookName,
                                     int startId, int pageSize);

    // 得到查询结果的数量
    int getSearchCount(String bookName);
}
