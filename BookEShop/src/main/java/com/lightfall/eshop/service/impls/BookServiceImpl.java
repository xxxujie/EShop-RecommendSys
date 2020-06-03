package com.lightfall.eshop.service.impls;

import com.lightfall.eshop.dao.BookMapper;
import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    // 得到所有书籍
    public List<Book> getAllBooks() {
        return bookMapper.getAllBooks();
    }

    // 根据 Id 取得某一本书的所有信息
    public Book getBookById(int bookId) {
        return bookMapper.getBookById(bookId);
    }

    // 得到一页的书
    public List<Book> getBooksInPage(int startId, int pageSize) {
        Map<String, Integer> pageInfo = new HashMap<String, Integer>();
        pageInfo.put("startId", startId);
        pageInfo.put("pageSize", pageSize);
        List<Book> productsInPage = bookMapper.getBooksInPage(pageInfo);
        return productsInPage;
    }

    // 获得全部记录条数
    public int getBooksCount() {
        return bookMapper.getBooksCount();
    }

    // 获得某一类的书
    public List<Book> selectCategoryInPage(int category, int startId, int pageSize) {
        return bookMapper.selectCategoryInPage(category, startId, pageSize);
    }

    // 根据 categoryId 获得 categoryName
    public String getCategoryName(int category) {
        return bookMapper.getCategoryName(category);
    }

    // 获得指定类别的记录条数
    public int getSelectCount(int category) {
        return bookMapper.getSelectCount(category);
    }

    // 根据书名模糊查询所有书
    public List<Book> searchBook(String bookName) {
        return bookMapper.searchBook(bookName);
    }


}
