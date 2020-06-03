package com.lightfall.eshop.dao;

import com.lightfall.eshop.pojo.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface BookMapper {
    // 得到所有书籍
    List<Book> getAllBooks();

    // 得到一页的书
    List<Book> getBooksInPage(Map<String, Integer> pageInfo);

    // 根据 Id 取得某一本书的所有信息
    Book getBookById(@Param("bookId")int bookId);

    // 获得全部记录条数
    int getBooksCount();

    // 获得指定类别的记录条数
    int getSelectCount(@Param("category") int category);

    // 获得一页某一类的书
    List<Book> selectCategoryInPage(@Param("category") int category,
                              @Param("startId") int startId,
                              @Param("pageSize") int pageSize);

    // 根据 categoryId 获得 categoryName
    String getCategoryName(@Param("category") int category);

    // 根据书名模糊查询所有书
    List<Book> searchBook(@Param("bookName") String bookName);
}
