package com.lightfall.eshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        List<String> excludePath = new ArrayList<String>();
        excludePath.add("/user/login");
        excludePath.add("/index");
        excludePath.add("/");
        excludePath.add("/user/loginAction");
        excludePath.add("/user/register");
        excludePath.add("/user/registerAction");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }
}
