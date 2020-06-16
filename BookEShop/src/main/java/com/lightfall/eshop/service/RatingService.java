package com.lightfall.eshop.service;

import com.lightfall.eshop.pojo.Rating;

public interface RatingService {
    // 插入一条 Rating
    int addRating(Rating rating);
    // 更新一条 Rating
    int updateRating(Rating rating);
    // 查询用户是否已经评分过
    boolean isRated(Rating rating);
}
