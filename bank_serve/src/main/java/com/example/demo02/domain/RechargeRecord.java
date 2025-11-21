package com.example.demo02.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 用户充值记录实体类
 */
@TableName("record")
public class RechargeRecord {
    @TableId
    private String recordId;        // 记录id
    private String userId;          // 用户id
    private String userName;        // 用户名
    private Double amount;          // 充值金额
    private LocalDateTime rechargeTime; // 充值时间
    private String status;          // 充值状态
    private String remark;          // 备注信息

    // 默认构造函数
    public RechargeRecord() {
    }

    // 带参构造函数
    public RechargeRecord(String recordId, String userId, Double amount) {
        this.recordId = recordId;
        this.userId = userId;
        this.amount = amount;
        this.rechargeTime = LocalDateTime.now();
        this.status = "pending"; // 默认待处理状态
    }

    // Getter和Setter方法
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(LocalDateTime rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "RechargeRecord{" +
                "recordId='" + recordId + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", amount=" + amount +
                ", rechargeTime=" + rechargeTime +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}