package com.example.demo02.service;

import com.example.demo02.domain.Users;

/**
 * 专家档案Service接口
 *
 * @author line
 * @date 2024-10-29
 */
//public interface IUserService
//{
//    /**
//     * 查询专家档案
//     *
//     * @param id 专家档案ID
//     * @return 专家档案
//     */
//    public User selectUserById(Long id);
//
//}
public interface IUserService {

    Users findUserByPhoneAndPassword(String uPhone, String uAccountPassword);
}

