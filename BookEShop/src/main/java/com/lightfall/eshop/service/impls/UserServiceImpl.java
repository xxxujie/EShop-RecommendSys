package com.lightfall.eshop.service.impls;

import com.lightfall.eshop.dao.UserMapper;
import com.lightfall.eshop.pojo.User;
import com.lightfall.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User loginCheck(String username) {
        User user = userMapper.loginCheck(username);
        return user;
    }

    @Override
    public String getNickNameByUserName(String username) {
        return userMapper.getNickNameByUserName(username);
    }

    // 添加一个用户
    public int addUser(User user) {
        return userMapper.addUser(user);
    }

    // 根据用户名得到用户 id
    public int getUserIdByUserName(String username) {
        return userMapper.getUserIdByUserName(username);
    }
}
