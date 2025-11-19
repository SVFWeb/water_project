package com.example.demo02.controller;

import com.example.demo02.domain.Machine;
import com.example.demo02.domain.ResponseResult;
import com.example.demo02.mapper.MachineMapper;
import com.example.demo02.service.MqttHealthService;
import com.example.demo02.service.MqttMessageSender;
import com.example.demo02.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/machine")
@CrossOrigin
public class machineController {

    @Autowired
    private MachineMapper machineMapper;

    @Autowired
    MqttMessageSender mqttMessageSender;

    @Autowired
    private MqttHealthService mqttHealthService;

    //连接mtqq
    /**
     * 简单的MQTT状态检查
     */

    @GetMapping("/health")
    public Map<String, Object> checkMqttHealth() {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean isConnected = mqttHealthService.checkMqttConnection();
            String connectionInfo = mqttHealthService.getMqttConnectionInfo();

            result.put("status", isConnected ? "connected" : "disconnected");
            result.put("connected", isConnected);
            result.put("connectionInfo", connectionInfo);
            result.put("timestamp", System.currentTimeMillis());
            result.put("message", isConnected ? "MQTT服务连接正常" : "MQTT服务连接异常");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("connected", false);
            result.put("message", "MQTT健康检查异常: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
        }

        return result;
    }

    //开关
    // 简单的水开关控制接口 - 使用固定设备ID
    @PostMapping("/water")
    public ResponseEntity<ResponseResult> controlWater(@RequestParam String water) {
        try {
            String waterCommand = water.toLowerCase().trim();
            String messagePayload;
            String commandDescription;

            // 根据water字段值确定发送的消息
            switch (waterCommand) {
                case "on":
                case "1":
                    messagePayload = "{@water_add_switch:1}";
                    commandDescription = "打开水开关";
                    break;
                case "off":
                case "0":
                    messagePayload = "{@water_add_switch:0}";
                    commandDescription = "关闭水开关";
                    break;
                default:
                    return ResponseUtils.businessError("water参数值必须是 'on' 或 'off'");
            }

            // 使用固定设备ID的主题
            String controlTopic = "lampline";

            // 发送MQTT消息
            mqttMessageSender.sendMsg(controlTopic, messagePayload);

            System.out.println("💧 发送水控制命令 - 设备: ma1, 主题: " + controlTopic +
                    ", 命令: " + messagePayload);

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("machineId", "ma1");
            responseData.put("topic", controlTopic);
            responseData.put("payload", messagePayload);
            responseData.put("command", waterCommand);
            responseData.put("description", commandDescription);

            return ResponseUtils.ok(responseData, commandDescription + "命令发送成功");

        } catch (Exception e) {
            System.err.println("❌ 水控制命令发送失败: " + e.getMessage());
            return ResponseUtils.serverError("水控制命令发送失败: " + e.getMessage());
        }
    }
    //暂停
    @PostMapping("/pause")
    public ResponseEntity<ResponseResult> controlPause(@RequestParam String water) {
        try {
            String waterCommand = water.toLowerCase().trim();
            String messagePayload;
            String commandDescription;

            // 根据water字段值确定发送的消息
            switch (waterCommand) {
                case "on":
                case "1":
                    messagePayload = "{@pause:1}";
                    commandDescription = "开启暂停";
                    break;
                case "off":
                case "0":
                    messagePayload = "{@pause:0}";
                    commandDescription = "关闭暂停";
                    break;
                default:
                    return ResponseUtils.businessError("water参数值必须是 'on' 或 'off'");
            }

            // 使用固定设备ID的主题
            String controlTopic = "lampline";

            // 发送MQTT消息
            mqttMessageSender.sendMsg(controlTopic, messagePayload);

            System.out.println("💧 发送暂停控制命令 - 设备: ma1, 主题: " + controlTopic +
                    ", 命令: " + messagePayload);

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("machineId", "ma1");
            responseData.put("topic", controlTopic);
            responseData.put("payload", messagePayload);
            responseData.put("command", waterCommand);
            responseData.put("description", commandDescription);

            return ResponseUtils.ok(responseData, commandDescription + "命令发送成功");

        } catch (Exception e) {
            System.err.println("❌ 暂停控制命令发送失败: " + e.getMessage());
            return ResponseUtils.serverError("暂停控制命令发送失败: " + e.getMessage());
        }
    }
    //是否启用
    // 检查设备是否已启动
    @GetMapping("/enable_device/status")
    public ResponseEntity<ResponseResult> checkDeviceStatus() {
        try {
            // 查询固定设备ID的启用状态
            Machine machine = machineMapper.findByMachineId("ma1");

            if (machine == null) {
                return ResponseUtils.businessError("设备不存在");
            }

            String enableStatus = machine.getEnableDevice();
            boolean isEnabled = "1".equals(enableStatus);

            String statusDescription = isEnabled ? "1" : "0";

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("machineId", "ma1");
            responseData.put("enable_device", enableStatus);
            responseData.put("isEnabled", isEnabled);
            responseData.put("status", statusDescription);

            return ResponseUtils.ok(responseData, statusDescription);

        } catch (Exception e) {
            System.err.println("❌ 查询设备状态失败: " + e.getMessage());
            return ResponseUtils.serverError("查询设备状态失败: " + e.getMessage());
        }
    }
    // 新增设备
    @PostMapping
    public ResponseEntity<ResponseResult> createMachine(@RequestBody Machine machine) {
        try {
            // 生成设备ID（如果未提供）
            if (machine.getMachineId() == null || machine.getMachineId().trim().isEmpty()) {
                String machineId = "MACHINE_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
                machine.setMachineId(machineId);
            }

            // 检查设备是否已存在
            if (machineMapper.existsByMachineId(machine.getMachineId()) > 0) {
                return ResponseUtils.businessError("设备ID已存在");
            }

            // 设置默认值
            if (machine.getEnableDevice() == null) {
                machine.setEnableDevice("1"); // 默认启用
            }
            if (machine.getTotalWaterAddition() == null) {
                machine.setTotalWaterAddition(0.0); // 默认总加水量为0
            }

            int result = machineMapper.insert(machine);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("machineId", machine.getMachineId());
                responseData.put("data", machine);
                return ResponseUtils.ok(responseData, "设备创建成功");
            } else {
                return ResponseUtils.businessError("设备创建失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 根据ID查询设备
    @GetMapping("/{machineId}")
    public ResponseEntity<ResponseResult> getMachineById(@PathVariable String machineId) {
        try {
            Machine machine = machineMapper.findByMachineId(machineId);
            if (machine != null) {
                return ResponseUtils.ok(machine);
            } else {
                return ResponseUtils.notFound();
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("查询设备失败: " + e.getMessage());
        }
    }

    // 查询所有设备
    @GetMapping
    public ResponseEntity<ResponseResult> getAllMachines() {
        try {
            List<Machine> machines = machineMapper.findAll();
            return ResponseUtils.ok(machines);
        } catch (Exception e) {
            return ResponseUtils.serverError("获取设备列表失败: " + e.getMessage());
        }
    }

    // 删除设备
    @DeleteMapping("/{machineId}")
    public ResponseEntity<ResponseResult> deleteMachine(@PathVariable String machineId) {
        try {
            // 检查设备是否存在
            if (machineMapper.existsByMachineId(machineId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = machineMapper.deleteById(machineId);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("machineId", machineId);
                return ResponseUtils.ok(responseData, "设备删除成功");
            } else {
                return ResponseUtils.businessError("设备删除失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 根据状态查询设备
    @GetMapping("/status/{status}")
    public ResponseEntity<ResponseResult> getMachinesByStatus(@PathVariable String status) {
        try {
            List<Machine> machines = machineMapper.findByStatus(status);
            return ResponseUtils.ok(machines);
        } catch (Exception e) {
            return ResponseUtils.serverError("查询设备失败: " + e.getMessage());
        }
    }

    // 创建错误响应辅助方法
    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}