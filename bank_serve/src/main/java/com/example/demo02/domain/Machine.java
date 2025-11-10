package com.example.demo02.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.persistence.*;
import java.util.Date;

@Table(name = "machine")
public class Machine {
    @TableId
    private String machineId;              // 设备唯一id
    private String location;               // 设备位置
    private String status;                 // 设备状态
    private String waterAddSwitch;         // stm32的开水开关
    private String fillUp;                 // 是否加满
    private String deviceTemperature;      // 设备温度
    private String batteryLevel;           // 电池电量
    private String latitudeAndLongitude;   // 经纬度

    // 默认构造函数
    public Machine() {
    }

    // 全参构造函数
    public Machine(String machineId, String location, String status, String waterAddSwitch,
                   String fillUp, String deviceTemperature, String batteryLevel, String latitudeAndLongitude) {
        this.machineId = machineId;
        this.location = location;
        this.status = status;
        this.waterAddSwitch = waterAddSwitch;
        this.fillUp = fillUp;
        this.deviceTemperature = deviceTemperature;
        this.batteryLevel = batteryLevel;
        this.latitudeAndLongitude = latitudeAndLongitude;
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

    public String getLatitudeAndLongitude() {
        return latitudeAndLongitude;
    }

    public void setLatitudeAndLongitude(String latitudeAndLongitude) {
        this.latitudeAndLongitude = latitudeAndLongitude;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId='" + machineId + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", waterAddSwitch='" + waterAddSwitch + '\'' +
                ", fillUp='" + fillUp + '\'' +
                ", deviceTemperature='" + deviceTemperature + '\'' +
                ", batteryLevel='" + batteryLevel + '\'' +
                ", latitudeAndLongitude='" + latitudeAndLongitude + '\'' +
                '}';
    }

}
