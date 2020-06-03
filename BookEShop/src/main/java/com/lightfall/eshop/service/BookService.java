package com.lightfall.eshop.service;


import com.lightfall.eshop.pojo.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookService {
    // 得到所有书籍
    List<Book> getAllBooks();

    // 根据 Id 取得某一本书的所有信息
    Book getBookById(int productId);

    // 得到一页的书
    List<Book> getBooksInPage(int startId, int pageSize);

    // 获得全部记录条数
    int getBooksCount();

    // 获得某一类的书
    List<Book> selectCategoryInPage(int category, int startId, int pageSize);

    // 根据 categoryId 获得 categoryName
    String getCategoryName(int category);

    // 获得指定类别的记录条数
    int getSelectCount(int category);

    // 根据书名模糊查询所有书
    List<Book> searchBook(String bookName);
}
