package com.lightfall.eshop.controller;

import com.lightfall.eshop.pojo.Book;
import com.lightfall.eshop.pojo.Rating;
import com.lightfall.eshop.service.BookService;
import com.lightfall.eshop.service.RatingService;
import com.lightfall.eshop.service.RecommendService;
import com.lightfall.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    RatingService ratingService;
    @Autowired
    UserService userService;
    @Autowired
    BookService bookService;
    @Autowired
    RecommendService recommendService;

    @RequestMapping("/addRating")
    public ModelAndView addRating(@RequestParam("score") BigDecimal score,
                                  @RequestParam("bookId") int bookId,
                                  HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 登录信息
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        modelAndView.addObject("userInfo", userInfo);

        // 把 BigDecimal 转成 Double 好比较
        double v = score.doubleValue();
        if(v < 0 || v > 10) {
            modelAndView.addObject("ratingMsg", "请输入 0-10 之间的数字！");
        } else {
            int userId = userService.getUserIdByUserName(userInfo);
            Rating rating = new Rating(userId, bookId, score);

            if(ratingService.isRated(rating)) { // 如果评价过，就更新
                ratingService.updateRating(rating);
            } else { // 没有评价过，就加上
                ratingService.addRating(rating);
            }
            modelAndView.addObject("ratingMsg", "评价成功");
        }
        // 这里用转发，因为还需要原来的信息
        modelAndView.setViewName("forward:/book/detail/"+bookId);
        return modelAndView;
    }

}
