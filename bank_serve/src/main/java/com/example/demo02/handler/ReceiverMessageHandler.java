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
     * 增量更新设备 - 只更新接收到的字段
     */
    private void incrementallyUpdateMachine(JsonNode jsonNode) {
        try {
            System.out.println("📋 开始增量更新设备字段...");

            boolean hasUpdates = false;

            // 分别更新每个接收到的字段
            if (jsonNode.has("water_add_switch") && !jsonNode.get("water_add_switch").isNull()) {
                String value = jsonNode.get("water_add_switch").asText();
                int result = machineMapper.updateWaterAddSwitch(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新水位开关: " + value);
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

            if (jsonNode.has("latitude_and_longitude") && !jsonNode.get("latitude_and_longitude").isNull()) {
                String value = jsonNode.get("latitude_and_longitude").asText();
                int result = machineMapper.updateLatitudeLongitude(fixedMachineId, value);
                if (result > 0) {
                    System.out.println("✅ 更新经纬度: " + value);
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
                System.out.println("✅ 设备数据增量更新完成! ID: " + fixedMachineId);
            } else {
                System.out.println("ℹ️ 没有需要更新的字段");
            }

        } catch (Exception e) {
            System.err.println("❌ 增量更新设备数据异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 只设置接收到的字段
     */
    private void setReceivedFields(Machine machine, JsonNode jsonNode) {
        if (jsonNode.has("water_add_switch") && !jsonNode.get("water_add_switch").isNull()) {
            machine.setWaterAddSwitch(jsonNode.get("water_add_switch").asText());
        }
        if (jsonNode.has("fill_up") && !jsonNode.get("fill_up").isNull()) {
            machine.setFillUp(jsonNode.get("fill_up").asText());
        }
        if (jsonNode.has("device_temperature") && !jsonNode.get("device_temperature").isNull()) {
            machine.setDeviceTemperature(jsonNode.get("device_temperature").asText());
        }
        if (jsonNode.has("battery_level") && !jsonNode.get("battery_level").isNull()) {
            machine.setBatteryLevel(jsonNode.get("battery_level").asText());
        }
        if (jsonNode.has("latitude_and_longitude") && !jsonNode.get("latitude_and_longitude").isNull()) {
            machine.setLatitudeAndLongitude(jsonNode.get("latitude_and_longitude").asText());
        }
        if (jsonNode.has("status") && !jsonNode.get("status").isNull()) {
            machine.setStatus(jsonNode.get("status").asText());
        }
        if (jsonNode.has("location") && !jsonNode.get("location").isNull()) {
            machine.setLocation(jsonNode.get("location").asText());
        }
    }
    /**
     * 从JSON设置设备字段
     */
    private void setMachineFieldsFromJson(Machine machine, JsonNode jsonNode) {
        // 实时数据字段
        machine.setWaterAddSwitch(getStringFromJson(jsonNode, "water_add_switch"));
        machine.setFillUp(getStringFromJson(jsonNode, "fill_up"));
        machine.setDeviceTemperature(getStringFromJson(jsonNode, "device_temperature"));
        machine.setBatteryLevel(getStringFromJson(jsonNode, "battery_level"));
        machine.setLatitudeAndLongitude(getStringFromJson(jsonNode, "latitude_and_longitude"));

        // 基本信息字段
        machine.setStatus(getStringFromJson(jsonNode, "status"));
        machine.setLocation(getStringFromJson(jsonNode, "location"));
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
