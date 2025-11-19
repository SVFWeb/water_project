package com.example.demo02.handler;

// 更新导入路径

import com.example.demo02.domain.Machine;
import com.example.demo02.domain.Transaction;
import com.example.demo02.mapper.MachineMapper;
import com.example.demo02.mapper.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReceiverMessageHandler implements MessageHandler {


    @Autowired
    private MachineMapper machineMapper;
    @Autowired
    private ObjectMapper objectMapper; // JSON解析器

    // 从配置文件中读取固定设备ID，如果没有配置则使用默认值
    @Value("${fixed.machine.id:ma1}")
    private String fixedMachineId;

    // 添加初始化检查
    @PostConstruct
    public void init() {
        System.out.println("=== ReceiverMessageHandler 初始化检查 ===");
        System.out.println("transactionRepository: " + (machineMapper != null ? "✅ 已注入" : "❌ 未注入"));
        System.out.println("===================================");
    }


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
//        String payload = message.getPayload().toString();
//        MessageHeaders headers = message.getHeaders();
//        String topicName = headers.get("mqtt_receivedTopic").toString();

        String payload = message.getPayload().toString();
        MessageHeaders headers = message.getHeaders();
        String topicName = headers.get("mqtt_receivedTopic").toString();

        System.out.println("收到MQTT消息 - 主题: " + topicName);
        System.out.println("消息内容: " + payload);


        // 解析JSON并更新设备表
        // 使用固定设备ID增量更新设备表
        incrementallyUpdateFixedMachine(payload, topicName);

    }
    private void incrementallyUpdateFixedMachine(String payload, String topic) {
        try {
            System.out.println("🔄 开始增量更新设备数据...");
            System.out.println("🎯 固定设备ID: " + fixedMachineId);

            // 解析JSON
            JsonNode jsonNode = objectMapper.readTree(payload);
            System.out.println("✅ JSON解析成功");

            // 检查设备是否存在，不存在则自动创建
            if (machineMapper.existsByMachineId(fixedMachineId) == 0) {
                System.out.println("🆕 固定设备不存在，自动创建: " + fixedMachineId);

            } else {
                // 增量更新固定设备
                incrementallyUpdateMachine(jsonNode);
            }

        } catch (Exception e) {
            System.err.println("❌ MQTT设备数据更新失败: " + e.getMessage());
            e.printStackTrace();
            System.err.println("💥 失败的消息内容: " + payload);
        }
    }


    /**
     * 增量更新设备字段 - 根据新设备表结构
     */
    /**
     * 增量更新设备字段 - 根据新设备表结构
     */
    private void incrementallyUpdateMachine(JsonNode jsonNode) {
        try {
            System.out.println("📋 开始增量更新设备字段...");

            boolean hasUpdates = false;

            // 根据新设备表结构更新字段
            if (jsonNode.has("water_add_switch") && !jsonNode.get("water_add_switch").isNull()) {
                String value = jsonNode.get("water_add_switch").asText();
                int result = machineMapper.updateWaterAddSwitch(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新开水开关: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("pause") && !jsonNode.get("pause").isNull()) {
                String value = jsonNode.get("pause").asText();
                int result = machineMapper.updatePause(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新暂停状态: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("enable_device") && !jsonNode.get("enable_device").isNull()) {
                String value = jsonNode.get("enable_device").asText();
                int result = machineMapper.updateEnableDevice(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新设备启用状态: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("water_tank") && !jsonNode.get("water_tank").isNull()) {
                String value = jsonNode.get("water_tank").asText();
                int result = machineMapper.updateWaterTank(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新水箱状态: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("fill_up") && !jsonNode.get("fill_up").isNull()) {
                String value = jsonNode.get("fill_up").asText();
                int result = machineMapper.updateFillUp(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新是否加满: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("device_temperature") && !jsonNode.get("device_temperature").isNull()) {
                String value = jsonNode.get("device_temperature").asText();
                int result = machineMapper.updateDeviceTemperature(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新设备温度: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("battery_level") && !jsonNode.get("battery_level").isNull()) {
                String value = jsonNode.get("battery_level").asText();
                int result = machineMapper.updateBatteryLevel(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新电池电量: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("total_water_addition") && !jsonNode.get("total_water_addition").isNull()) {
                Double value = jsonNode.get("total_water_addition").asDouble();
                int result = machineMapper.updateTotalWaterAddition(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新总加水量: " + value);
                    hasUpdates = true;
                }
            }

            // 修正：分别处理经纬度字段
            boolean latitudeUpdated = false;
            boolean longitudeUpdated = false;

            // 处理经度 (longitude)
            if (jsonNode.has("longitude") && !jsonNode.get("longitude").isNull()) {
                String longitude = jsonNode.get("longitude").asText();
                int result = machineMapper.updateLongitude(fixedMachineId, longitude);
                if (result > 0) {
                    System.out.println("✅ 更新经度: " + longitude);
                    longitudeUpdated = true;
                    hasUpdates = true;
                }
            }

            // 处理纬度 (latitude)
            if (jsonNode.has("latitude") && !jsonNode.get("latitude").isNull()) {
                String latitude = jsonNode.get("latitude").asText();
                int result = machineMapper.updateLatitude(fixedMachineId, latitude);
                if (result > 0) {
                    System.out.println("✅ 更新纬度: " + latitude);
                    latitudeUpdated = true;
                    hasUpdates = true;
                }
            }

            // 兼容旧版经纬度字段（latitude_and_longitude）
            if (jsonNode.has("latitude_and_longitude") && !jsonNode.get("latitude_and_longitude").isNull()) {
                String latLng = jsonNode.get("latitude_and_longitude").asText();
                // 简单解析经纬度字符串（格式如："31.2304,121.4737"）
                String[] parts = latLng.split(",");
                if (parts.length == 2) {
                    // 分别更新纬度和经度
                    String latitude = parts[0].trim();
                    String longitude = parts[1].trim();

                    int latResult = machineMapper.updateLatitude(fixedMachineId, latitude);
                    int lngResult = machineMapper.updateLongitude(fixedMachineId, longitude);

                    if (latResult > 0) {
                        System.out.println("✅ 更新纬度(兼容格式): " + latitude);
                        latitudeUpdated = true;
                        hasUpdates = true;
                    }
                    if (lngResult > 0) {
                        System.out.println("✅ 更新经度(兼容格式): " + longitude);
                        longitudeUpdated = true;
                        hasUpdates = true;
                    }
                }
            }

            if (jsonNode.has("there_fee") && !jsonNode.get("there_fee").isNull()) {
                String value = jsonNode.get("there_fee").asText();
                int result = machineMapper.updateThereFee(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新是否有费率: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("status") && !jsonNode.get("status").isNull()) {
                String value = jsonNode.get("status").asText();
                int result = machineMapper.updateStatus(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新设备状态: " + value);
                    hasUpdates = true;
                }
            }

            if (jsonNode.has("location") && !jsonNode.get("location").isNull()) {
                String value = jsonNode.get("location").asText();
                int result = machineMapper.updateLocation(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新设备位置: " + value);
                    hasUpdates = true;
                }
            }

            if (hasUpdates) {
                System.out.println("🎉 设备数据增量更新完成! ID: " + fixedMachineId);
            } else {
                System.out.println("ℹ️ 没有需要更新的字段");
            }

        } catch (Exception e) {
            System.err.println("❌ 增量更新设备数据异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 从JSON中安全获取字符串字段
     */
    private String getStringFromJson(JsonNode jsonNode, String fieldName) {
        try {
            if (jsonNode.has(fieldName) && !jsonNode.get(fieldName).isNull()) {
                String value = jsonNode.get(fieldName).asText();
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 从JSON中提取设备ID
     */
    private String extractMachineId(JsonNode jsonNode) {
        // 支持多种设备ID字段名
        String[] possibleIdFields = {"machine_id", "deviceId", "device_id", "machineId", "id"};

        for (String field : possibleIdFields) {
            if (jsonNode.has(field) && !jsonNode.get(field).isNull()) {
                String machineId = jsonNode.get(field).asText();
                if (machineId != null && !machineId.trim().isEmpty()) {
                    return machineId.trim();
                }
            }
        }
        return null;
    }



}
