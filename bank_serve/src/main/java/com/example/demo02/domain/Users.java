package com.example.demo02.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("users")
public class Users {
    @TableId
    private String userId;          // 用户唯一标识
    private String openId;          // 微信用户唯一标识
    private Double balance;         // 用户余额
    private String userName;        // 用户名称
    private String userPassword;    // 用户密码

    // 默认构造函数
    public Users() {
    }

    // 注册用构造函数
    public Users(String userId, String userName, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.balance = 0.0; // 默认余额为0
    }



    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId='" + userId + '\'' +
                ", openId='" + openId + '\'' +
                ", balance=" + balance +
                ", userName='" + userName + '\'' +
                ", userPassword='" + (userPassword != null ? "***" : "null") + '\'' +
                '}';
    }



}
