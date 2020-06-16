package com.lightfall.eshop.dao;

import com.lightfall.eshop.pojo.Rating;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RatingMapper {
    // 插入一条 Rating
    int addRating(Rating rating);
    // 更新一条 Rating
    int updateRating(Rating rating);
    // 查询用户是否已经评分过
    Rating checkRating(Rating rating);

}
