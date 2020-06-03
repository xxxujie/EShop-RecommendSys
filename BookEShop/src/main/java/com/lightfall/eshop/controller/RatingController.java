package com.lightfall.eshop.controller;

import com.lightfall.eshop.pojo.Rating;
import com.lightfall.eshop.service.RatingService;
import com.lightfall.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    RatingService ratingService;
    @Autowired
    UserService userService;

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
            modelAndView.setViewName("book");
        } else {
            int userId = userService.getUserIdByUserName(userInfo);
            Rating rating = new Rating(userId, bookId, score);
            try {
                ratingService.addRating(rating);
            } catch (Exception e) {
                e.getStackTrace();
                modelAndView.addObject("ratingMsg", "你已经评价过这本书了！");
                modelAndView.setViewName("forward:/book/detail/"+bookId);
                return modelAndView;
            }
            modelAndView.addObject("ratingMsg", "评价成功");
        }
        modelAndView.setViewName("forward:/book/detail/"+bookId);
        return modelAndView;
    }

}
