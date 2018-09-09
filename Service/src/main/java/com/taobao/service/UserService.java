package com.taobao.service;

import com.taobao.pojo.User;

public interface UserService {
    User login(User user);

    boolean register(User user);
}
