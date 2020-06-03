package com.lightfall.eshop.dao;

import com.lightfall.eshop.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SearchMapper {
    // 根据书名模糊查询所有书
    List<Book> searchBook(@Param("bookName") String bookName);

    // 一页的模糊查询结果
    List<Book> getSearchResultInPage(@Param("bookName") String bookName,
                                     @Param("startId") int startId,
                                     @Param("pageSize") int pageSize);

    // 得到查询结果的数量
    int getSearchCount(@Param("bookName") String bookName);
}
