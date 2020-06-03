package com.lightfall.eshop.service;


import com.lightfall.eshop.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserService {
    // 登录验证
    User loginCheck(String username);

    String getNickNameByUserName(String username);

    // 添加一个用户
    int addUser(User user);

    // 根据用户名得到用户 id
    int getUserIdByUserName(String username);
}
