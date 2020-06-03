package com.lightfall.eshop.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userId;
    private String username;
    private String password;
    private String nickName;
    private Timestamp createTime;

    // 注册的时候不用 id，在数据库自动自增
    public User(String username, String password, String nickName) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
    }
}
