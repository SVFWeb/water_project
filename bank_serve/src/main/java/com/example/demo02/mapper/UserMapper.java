package com.example.demo02.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo02.domain.Users;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<Users>
{

    // 查询所有用户
    @Select("SELECT user_id, open_id, balance, user_name, user_password FROM users")
    List<Users> findAll();

    // 根据用户ID查询用户
    @Select("SELECT user_id, open_id, balance, user_name, user_password FROM users WHERE user_id = #{userId}")
    Users findByUserId(@Param("userId") String userId);

    // 根据微信openId查询用户
    @Select("SELECT user_id, open_id, balance, user_name, user_password FROM users WHERE open_id = #{openId}")
    Users findByOpenId(@Param("openId") String openId);

    // 根据用户名查询用户（用于登录）
    @Select("SELECT user_id, open_id, balance, user_name, user_password FROM users WHERE user_name = #{userName}")
    Users findByUserName(@Param("userName") String userName);

    // 根据用户名和密码查询用户（用于登录验证）
    @Select("SELECT user_id, open_id, balance, user_name, user_password FROM users WHERE user_name = #{userName} AND user_password = #{password}")
    Users findByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);

    // 插入用户
    @Insert("INSERT INTO users (user_id, open_id, balance, user_name, user_password) " +
            "VALUES (#{userId}, #{openId}, #{balance}, #{userName}, #{userPassword})")
    int insert(Users user);

    // 更新用户信息
    @Update("UPDATE users SET open_id = #{openId}, balance = #{balance}, " +
            "user_name = #{userName}, user_password = #{userPassword} " +
            "WHERE user_id = #{userId}")
    int update(Users user);

    // 更新用户余额
    @Update("UPDATE users SET balance = #{balance} WHERE user_id = #{userId}")
    int updateBalance(@Param("userId") String userId, @Param("balance") Double balance);

    // 更新用户密码
    @Update("UPDATE users SET user_password = #{newPassword} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") String userId, @Param("newPassword") String newPassword);

    // 删除用户
    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int deleteById(@Param("userId") String userId);

    // 检查用户是否存在
    @Select("SELECT COUNT(*) FROM users WHERE user_id = #{userId}")
    int existsByUserId(@Param("userId") String userId);

    // 检查用户名是否存在
    @Select("SELECT COUNT(*) FROM users WHERE user_name = #{userName}")
    int existsByUserName(@Param("userName") String userName);

    // 检查微信openId是否存在
    @Select("SELECT COUNT(*) FROM users WHERE open_id = #{openId}")
    int existsByOpenId(@Param("openId") String openId);

    // 用户余额充值
    @Update("UPDATE users SET balance = balance + #{amount} WHERE user_id = #{userId}")
    int rechargeBalance(@Param("userId") String userId, @Param("amount") Double amount);

    // 用户余额扣款
    @Update("UPDATE users SET balance = balance - #{amount} WHERE user_id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") String userId, @Param("amount") Double amount);

    // 统计用户数量
    @Select("SELECT COUNT(*) FROM users")
    int countAll();



}
