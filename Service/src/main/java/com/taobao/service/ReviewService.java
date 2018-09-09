package com.taobao.service;

import com.taobao.pojo.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getReviewByPid(Integer pid);
}
