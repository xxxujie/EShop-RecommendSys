package com.lightfall.eshop.service.impls;

import com.lightfall.eshop.dao.RecommendMapper;
import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.pojo.Rating;
import com.lightfall.eshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    RecommendMapper recommendMapper;

    // 获得平均分排行
    public List<Book> getRankList() {
        return recommendMapper.getRankList();
    }

    // 根据书单列表获得每一本的平均评价
    public Map<Integer, BigDecimal> getAvgRating(List<Book> books) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Book book : books) {
            int bookId = book.getBookId();
            BigDecimal avgRating = recommendMapper.getAvgRating(bookId);
            result.put(bookId, avgRating);
        }
        return result;
    }

    // 获得历史热门图书
    public List<Book> getHotList() {
        return recommendMapper.getHotList();
    }

    // 获得当下热门图书
    public List<Book> getHotRecent() {
        return recommendMapper.getHotRecent();
    }

    // 获得用户个性化推荐列表
    public List<Book> getUserRecs(int userId) {
        return recommendMapper.getUserRecs(userId);
    }

    // 获得同现相似的推荐图书
    public List<Book> getConcurSimRecs(int bookId) {
        return recommendMapper.getConcurSimRecs(bookId);
    }
}
