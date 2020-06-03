package com.lightfall.eshop.config;

import lombok.extern.log4j.Log4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class LoginInterceptor implements HandlerInterceptor {

    // 登录拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws  Exception {
        HttpSession session = request.getSession();
        String userInfo = (String) session.getAttribute("userInfo");

        // 如果没有登录
        if(userInfo == null) {
            // 设置一下编码
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            request.setCharacterEncoding("utf-8");
            // 发送错误消息，转到登录页面
            request.setAttribute("errMsg", "请先登录！");
            request.getRequestDispatcher("/user/login").forward(request, response);
            return false;
        }

        return true;

    }
}
