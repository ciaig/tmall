package com.taobao.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.taobao.dao.ReviewMapper;
import com.taobao.dao.UserMapper;
import com.taobao.pojo.Review;
import com.taobao.pojo.ReviewExample;
import com.taobao.pojo.User;
import com.taobao.pojo.UserExample;
import com.taobao.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public List<Review> getReviewByPid(Integer pid) {
        ReviewExample reviewExample = new ReviewExample();
        reviewExample.createCriteria().andPidEqualTo(pid);
        List<Review> reviews = reviewMapper.selectByExample(reviewExample);
        for(Review review:reviews){
            User user = userMapper.selectByPrimaryKey(review.getUid());
            review.setUser(user);
        }
        return reviews;
    }
}
