package com.lightfall.eshop.dao;

import com.lightfall.eshop.pojo.Rating;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RatingMapper {
    // 插入一条 Rating
    int addRating(Rating rating);
}
