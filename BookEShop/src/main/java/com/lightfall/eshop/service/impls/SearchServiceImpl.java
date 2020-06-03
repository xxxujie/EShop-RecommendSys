package com.lightfall.eshop.service.impls;

import com.lightfall.eshop.dao.SearchMapper;
import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchMapper searchMapper;

    // 一页的查询结果
    public List<Book> getSearchResultInPage(String bookName,
                                     int startId, int pageSize) {
        return searchMapper.getSearchResultInPage(bookName, startId, pageSize);
    }

    // 得到查询结果的数量
    public int getSearchCount(String bookName) {
        return searchMapper.getSearchCount(bookName);
    }
}
