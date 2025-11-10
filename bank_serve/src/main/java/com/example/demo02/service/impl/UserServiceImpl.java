package com.example.demo02.service.impl;

import com.example.demo02.domain.Users;
import com.example.demo02.mapper.UserMapper;
import com.example.demo02.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 专家档案Service业务层处理
 * 
 * @author line
 * @date 2024-10-29
 */
@Service
public class UserServiceImpl implements IUserService
{
    @Autowired
    private UserMapper userMapper;

    @Override
    public Users findUserByPhoneAndPassword(String uPhone, String uAccountPassword) {
        return null;
    }
}
