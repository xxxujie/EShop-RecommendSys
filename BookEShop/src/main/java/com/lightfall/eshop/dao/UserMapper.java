package com.lightfall.eshop.dao;

import com.lightfall.eshop.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// 这个注解表示这是一个 mybatis 的 mapper 类
@Mapper
@Repository
public interface UserMapper {

    // 登录验证
    User loginCheck(@Param("username") String username);

    // 通过 username 获得昵称
    String getNickNameByUserName(@Param("username") String username);

    // 添加一个用户
    int addUser(User user);

    // 修改某个用户
    int updateUser(User user);

    int deleteUser(int id);

    // 根据用户名得到用户 id
    int getUserIdByUserName(@Param("username") String username);
}
