package com.example.demo02.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 费率配置实体类
 */
@TableName("rate_config")
public class RateConfig {
    @TableId
    private String rateId;          // 费率配置id
    private String rateDayRate;     // 存储每台机器当前的费率
    private String machineId;       // 设备id
    private Double serviceFee;      // 服务费
    private Double pricePerLiter;   // 每升价格

    // 默认构造函数
    public RateConfig() {
    }

    // 带参构造函数
    public RateConfig(String rateId, String machineId, Double serviceFee, Double pricePerLiter) {
        this.rateId = rateId;
        this.machineId = machineId;
        this.serviceFee = serviceFee;
        this.pricePerLiter = pricePerLiter;
        this.rateDayRate = "默认费率"; // 默认值
    }

    // Getter和Setter方法
    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getRateDayRate() {
        return rateDayRate;
    }

    public void setRateDayRate(String rateDayRate) {
        this.rateDayRate = rateDayRate;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(Double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

    @Override
    public String toString() {
        return "RateConfig{" +
                "rateId='" + rateId + '\'' +
                ", rateDayRate='" + rateDayRate + '\'' +
                ", machineId='" + machineId + '\'' +
                ", serviceFee=" + serviceFee +
                ", pricePerLiter=" + pricePerLiter +
                '}';
    }
}