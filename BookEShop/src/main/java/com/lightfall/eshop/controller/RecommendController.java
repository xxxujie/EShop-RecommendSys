package com.lightfall.eshop.controller;

import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @RequestMapping("/rank")
    public ModelAndView Rank(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 登录信息
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        modelAndView.addObject("userInfo", userInfo);

        List<Book> rankList = recommendService.getRankList();
        // 得到 bookId 和评价的 Map
        Map<Integer, BigDecimal> ratingMap = recommendService.getAvgRating(rankList);

        modelAndView.addObject("ratingMap", ratingMap);
        modelAndView.addObject("bookList", rankList);
        modelAndView.setViewName("rank");

        return modelAndView;
    }
}
