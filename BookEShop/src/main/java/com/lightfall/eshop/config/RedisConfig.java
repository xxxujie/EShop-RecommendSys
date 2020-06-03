package com.lightfall.eshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import java.net.UnknownHostException;

@Configuration
public class RedisConfig {

    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
        throws UnknownHostException {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
