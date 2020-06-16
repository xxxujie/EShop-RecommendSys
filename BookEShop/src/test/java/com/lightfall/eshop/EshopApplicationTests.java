package com.lightfall.eshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@SpringBootTest
class EshopApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() throws SQLException {

        // opsForValue 操作字符串
        // opsForList 操作 List
        // opsForSet
        // opsForHash
        // opsForZSet
        // opsForGeo
        // opsForHyperLog

        // 除了基本的操作，我们常用的方法都可以直接通过 RedisTemplate 操作

        // 获取连接
        // RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        // connection.flushDb();
        // connection.flushAll();

        // 简单的使用
        redisTemplate.opsForValue().set("myKey", "cxj");
        String myKey = (String) redisTemplate.opsForValue().get("myKey");
        System.out.println(myKey);
    }

}
