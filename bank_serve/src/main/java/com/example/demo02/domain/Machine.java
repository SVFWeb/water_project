package com.example.demo02.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("machine")
public class Machine {
    @TableId
    private String machineId;           // 设备唯一id
    private String location;            // 设备位置
    private String status;              // 设备状态
    private String waterAddSwitch;      // stm32的开水开关
    private String pause;               // stm的开水暂停
    private String enableDevice;        // 设备是否启用
    private String waterTank;           // 水箱是否满
    private String fillUp;              // 是否加满
    private String deviceTemperature;   // 设备温度
    private String batteryLevel;        // 电池电量
    private Double totalWaterAddition;  // 总加水量
    private String latitude;            // 经度
    private String longitude;           // 纬度
    private String thereFee;            // 是否有费率

    // 默认构造函数
    public Machine() {
    }

    // 带设备ID的构造函数，其他字段设为默认值
    public Machine(String machineId) {
        this.machineId = machineId;
        this.location = "0";           // 默认位置
        this.status = "0";           // 默认离线状态
        this.waterAddSwitch = "0";         // 默认关闭
        this.pause = "0";                  // 默认未暂停
        this.enableDevice = "1";           // 默认启用
        this.waterTank = "0";              // 默认水箱不满
        this.fillUp = "0";                 // 默认未加满
        this.deviceTemperature = "0";      // 默认温度0
        this.batteryLevel = "0";           // 默认电量0
        this.totalWaterAddition = 0.0;     // 默认总加水量0
        this.latitude = "0.0";             // 默认纬度
        this.longitude = "0.0";            // 默认经度
        this.thereFee = "0";               // 默认无费率
    }

    // Getter和Setter方法
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWaterAddSwitch() {
        return waterAddSwitch;
    }

    public void setWaterAddSwitch(String waterAddSwitch) {
        this.waterAddSwitch = waterAddSwitch;
    }

    public String getPause() {
        return pause;
    }

    public void setPause(String pause) {
        this.pause = pause;
    }

    public String getEnableDevice() {
        return enableDevice;
    }

    public void setEnableDevice(String enableDevice) {
        this.enableDevice = enableDevice;
    }

    public String getWaterTank() {
        return waterTank;
    }

    public void setWaterTank(String waterTank) {
        this.waterTank = waterTank;
    }

    public String getFillUp() {
        return fillUp;
    }

    public void setFillUp(String fillUp) {
        this.fillUp = fillUp;
    }

    public String getDeviceTemperature() {
        return deviceTemperature;
    }

    public void setDeviceTemperature(String deviceTemperature) {
        this.deviceTemperature = deviceTemperature;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Double getTotalWaterAddition() {
        return totalWaterAddition;
    }

    public void setTotalWaterAddition(Double totalWaterAddition) {
        this.totalWaterAddition = totalWaterAddition;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getThereFee() {
        return thereFee;
    }

    public void setThereFee(String thereFee) {
        this.thereFee = thereFee;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId='" + machineId + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", waterAddSwitch='" + waterAddSwitch + '\'' +
                ", pause='" + pause + '\'' +
                ", enableDevice='" + enableDevice + '\'' +
                ", waterTank='" + waterTank + '\'' +
                ", fillUp='" + fillUp + '\'' +
                ", deviceTemperature='" + deviceTemperature + '\'' +
                ", batteryLevel='" + batteryLevel + '\'' +
                ", totalWaterAddition=" + totalWaterAddition +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", thereFee='" + thereFee + '\'' +
                '}';
    }
}