package com.example.demo02.domain;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("rate_config")
public class RateConfig {

    private String rateId;          // 费率配置id
    private String machineId;       // 设备id
    private String isActive;        // 是否激活这个费率  "1"表示激活，"0"表示未激活
    private Double pricePerLiter;   // 每升水的价格

    // Getter和Setter方法
    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public Double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(Double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
    }

}
