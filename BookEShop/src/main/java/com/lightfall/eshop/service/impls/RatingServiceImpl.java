package com.lightfall.eshop.service.impls;

import com.lightfall.eshop.dao.RatingMapper;
import com.lightfall.eshop.pojo.Rating;
import com.lightfall.eshop.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingMapper ratingMapper;

    // 插入一条 Rating
    public int addRating(Rating rating) {
        return ratingMapper.addRating(rating);
    }

    // 更新一条 Rating
    public int updateRating(Rating rating) {
        return ratingMapper.updateRating(rating);
    }

    // 查询用户是否已经评分过
    public boolean isRated(Rating rating) {
        Rating check = ratingMapper.checkRating(rating);
        if(check == null ) {
            return false;
        } else {
            return true;
        }
    }
}
