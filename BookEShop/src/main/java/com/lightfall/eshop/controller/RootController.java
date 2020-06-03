package com.lightfall.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class RootController {

    // 首页
    @RequestMapping({"/", "/index"})
    public String root(HttpServletRequest request, Model model) {
        // 通过 Session 获得登录信息
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");
        model.addAttribute("userInfo", userInfo);

        return "index";
    }

    // 404 页面
    @RequestMapping("/**")
    public String error(Model model) {
        return "404";
    }
}
