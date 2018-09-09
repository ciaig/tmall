package com.taobao.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.taobao.dao.UserMapper;
import com.taobao.pojo.User;
import com.taobao.pojo.UserExample;
import com.taobao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User login(User user) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNameEqualTo(user.getName());
        criteria.andPasswordEqualTo(user.getPassword());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size()==0){
            return null;
        }else{
            return users.get(0);
        }
    }

    @Override
    public boolean register(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(user.getName());
        if(userMapper.selectByExample(userExample).size()!=0){
            return false;
        }else{
            userMapper.insert(user);
            return true;
        }
    }
}
