package com.example.demo02.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
@TableName("transaction")
public class Transaction {
    @TableId
    private String transactionId;   // 交易记录id
    private String userId;          // 用户id
    private String userName;        // 用户名
    private String machineId;       // 设备表的id
    private String orderStatus;     // 订单状态
    private Double totalLiters;     // 总加水量
    private Double finalAmount;     // 最终金额
    private LocalDateTime startTime; // 交易开始的时间
    private LocalDateTime endTime;   // 交易结束的时间

    // 默认构造函数
    public Transaction() {
    }

    // 带参构造函数
    public Transaction(String transactionId, String userId, String machineId) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.machineId = machineId;
        this.orderStatus = "pending"; // 默认待处理状态
        this.totalLiters = 0.0;
        this.finalAmount = 0.0;
        this.startTime = LocalDateTime.now(); // 默认开始时间为当前时间
    }

    // Getter和Setter方法
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getTotalLiters() {
        return totalLiters;
    }

    public void setTotalLiters(Double totalLiters) {
        this.totalLiters = totalLiters;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", machineId='" + machineId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", totalLiters=" + totalLiters +
                ", finalAmount=" + finalAmount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}