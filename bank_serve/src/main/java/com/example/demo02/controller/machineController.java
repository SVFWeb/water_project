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

import java.util.*;
import java.util.stream.Collectors;

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
            String controlTopic = "abc";

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
            String controlTopic = "abc";

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

    @PostMapping("/enable_device")
    public ResponseEntity<ResponseResult> controlEnableDevice(@RequestParam String water) {
        try {
            String waterCommand = water.toLowerCase().trim();
            String messagePayload;
            String commandDescription;

            // 根据water字段值确定发送的消息
            switch (waterCommand) {
                case "on":
                case "1":
                    messagePayload = "{@enable_device:1}";
                    commandDescription = "开启设备启用";
                    break;
                case "off":
                case "0":
                    messagePayload = "{@enable_device:0}";
                    commandDescription = "关闭设备启用";
                    break;
                default:
                    return ResponseUtils.businessError("water参数值必须是 'on' 或 'off'");
            }

            // 使用固定设备ID的主题
            String controlTopic = "abc";

            // 发送MQTT消息
            mqttMessageSender.sendMsg(controlTopic, messagePayload);

            System.out.println("💧 发送启用控制命令 - 设备: ma1, 主题: " + controlTopic +
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
            System.err.println("❌ 设备启用控制命令发送失败: " + e.getMessage());
            return ResponseUtils.serverError("设备启用控制命令发送失败: " + e.getMessage());
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

    // 修改设备信息（部分字段更新）
    @PatchMapping("/{machineId}")
    public ResponseEntity<ResponseResult> updateMachinePartial(
            @PathVariable String machineId,
            @RequestBody Map<String, Object> updateFields) {
        try {
            // 检查设备是否存在
            if (machineMapper.existsByMachineId(machineId) == 0) {
                return ResponseUtils.notFound();
            }

            // 获取现有设备信息
            Machine existingMachine = machineMapper.findByMachineId(machineId);
            if (existingMachine == null) {
                return ResponseUtils.notFound();
            }

            // 更新传递的字段，保持其他字段不变
            boolean hasUpdates = updateMachineFields(existingMachine, updateFields);

            if (!hasUpdates) {
                return ResponseUtils.businessError("没有提供有效的更新字段");
            }

            // 执行更新
            int result = machineMapper.update(existingMachine);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("machineId", machineId);
                responseData.put("updatedFields", getUpdatedFieldNames(updateFields));
                responseData.put("data", existingMachine);
                return ResponseUtils.ok(responseData, "设备信息更新成功");
            } else {
                return ResponseUtils.businessError("设备信息更新失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 更新设备字段的辅助方法
    private boolean updateMachineFields(Machine machine, Map<String, Object> updateFields) {
        boolean hasUpdates = false;

        // 设备名
        if (updateFields.containsKey("location") && updateFields.get("location") != null) {
            String location = updateFields.get("location").toString();
            if (!location.trim().isEmpty()) {
                machine.setLocation(location);
                hasUpdates = true;
            }
        }

        // 设备状态（默认离线）
        if (updateFields.containsKey("status") && updateFields.get("status") != null) {
            String status = updateFields.get("status").toString();
            if (!status.trim().isEmpty()) {
                machine.setStatus(status);
                hasUpdates = true;
            } else {
                machine.setStatus("offline"); // 默认离线
                hasUpdates = true;
            }
        } else {
            // 如果没有传递状态字段，保持原状态不变
        }

        // 开关（默认关）
        if (updateFields.containsKey("waterAddSwitch") && updateFields.get("waterAddSwitch") != null) {
            String waterAddSwitch = updateFields.get("waterAddSwitch").toString();
            if (!waterAddSwitch.trim().isEmpty()) {
                machine.setWaterAddSwitch(waterAddSwitch);
                hasUpdates = true;
            } else {
                machine.setWaterAddSwitch("0"); // 默认关
                hasUpdates = true;
            }
        }

        // 暂停状态
        if (updateFields.containsKey("pause") && updateFields.get("pause") != null) {
            String pause = updateFields.get("pause").toString();
            if (!pause.trim().isEmpty()) {
                machine.setPause(pause);
                hasUpdates = true;
            } else {
                machine.setPause("0"); // 默认未暂停
                hasUpdates = true;
            }
        }

        // 设备启用状态
        if (updateFields.containsKey("enableDevice") && updateFields.get("enableDevice") != null) {
            String enableDevice = updateFields.get("enableDevice").toString();
            if (!enableDevice.trim().isEmpty()) {
                machine.setEnableDevice(enableDevice);
                hasUpdates = true;
            } else {
                machine.setEnableDevice("1"); // 默认启用
                hasUpdates = true;
            }
        }

        // 水箱状态
        if (updateFields.containsKey("waterTank") && updateFields.get("waterTank") != null) {
            String waterTank = updateFields.get("waterTank").toString();
            if (!waterTank.trim().isEmpty()) {
                machine.setWaterTank(waterTank);
                hasUpdates = true;
            } else {
                machine.setWaterTank("0"); // 默认不满
                hasUpdates = true;
            }
        }

        // 是否加满（默认否）
        if (updateFields.containsKey("fillUp") && updateFields.get("fillUp") != null) {
            String fillUp = updateFields.get("fillUp").toString();
            if (!fillUp.trim().isEmpty()) {
                machine.setFillUp(fillUp);
                hasUpdates = true;
            } else {
                machine.setFillUp("0"); // 默认未加满
                hasUpdates = true;
            }
        }

        // 设备温度（默认为0）
        if (updateFields.containsKey("deviceTemperature") && updateFields.get("deviceTemperature") != null) {
            String deviceTemperature = updateFields.get("deviceTemperature").toString();
            if (!deviceTemperature.trim().isEmpty()) {
                machine.setDeviceTemperature(deviceTemperature);
                hasUpdates = true;
            } else {
                machine.setDeviceTemperature("0"); // 默认0
                hasUpdates = true;
            }
        }

        // 电池电量（默认为0）
        if (updateFields.containsKey("batteryLevel") && updateFields.get("batteryLevel") != null) {
            String batteryLevel = updateFields.get("batteryLevel").toString();
            if (!batteryLevel.trim().isEmpty()) {
                machine.setBatteryLevel(batteryLevel);
                hasUpdates = true;
            } else {
                machine.setBatteryLevel("0"); // 默认0
                hasUpdates = true;
            }
        }

        // 总加水量（默认0）
        if (updateFields.containsKey("totalWaterAddition") && updateFields.get("totalWaterAddition") != null) {
            try {
                Object totalWaterAdditionObj = updateFields.get("totalWaterAddition");
                if (totalWaterAdditionObj != null) {
                    Double totalWaterAddition;
                    if (totalWaterAdditionObj instanceof String) {
                        totalWaterAddition = Double.parseDouble(totalWaterAdditionObj.toString());
                    } else {
                        totalWaterAddition = (Double) totalWaterAdditionObj;
                    }
                    machine.setTotalWaterAddition(totalWaterAddition);
                    hasUpdates = true;
                }
            } catch (Exception e) {
                machine.setTotalWaterAddition(0.0); // 解析失败时设为默认值0
                hasUpdates = true;
            }
        }

        // 经度（默认0）
        if (updateFields.containsKey("longitude") && updateFields.get("longitude") != null) {
            String longitude = updateFields.get("longitude").toString();
            if (!longitude.trim().isEmpty()) {
                machine.setLongitude(longitude);
                hasUpdates = true;
            } else {
                machine.setLongitude("0.0"); // 默认0
                hasUpdates = true;
            }
        }

        // 纬度（默认0）
        if (updateFields.containsKey("latitude") && updateFields.get("latitude") != null) {
            String latitude = updateFields.get("latitude").toString();
            if (!latitude.trim().isEmpty()) {
                machine.setLatitude(latitude);
                hasUpdates = true;
            } else {
                machine.setLatitude("0.0"); // 默认0
                hasUpdates = true;
            }
        }

        // 是否有费率
        if (updateFields.containsKey("thereFee") && updateFields.get("thereFee") != null) {
            String thereFee = updateFields.get("thereFee").toString();
            if (!thereFee.trim().isEmpty()) {
                machine.setThereFee(thereFee);
                hasUpdates = true;
            } else {
                machine.setThereFee("0"); // 默认无费率
                hasUpdates = true;
            }
        }

        return hasUpdates;
    }

    // 获取更新的字段名称列表
    private List<String> getUpdatedFieldNames(Map<String, Object> updateFields) {
        return updateFields.keySet().stream()
                .filter(key -> updateFields.get(key) != null)
                .collect(Collectors.toList());
    }

    // 查询没有费率配置的设备
    @GetMapping("/without-rates")
    public ResponseEntity<ResponseResult> getMachinesWithoutRates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 参数验证
            if (page < 1) page = 1;
            if (pageSize < 1 || pageSize > 100) pageSize = 10;

            // 获取所有没有费率配置的设备
            List<Machine> allMachinesWithoutRates = machineMapper.findMachinesWithoutRateConfig();

            // 手动分页
            int totalCount = allMachinesWithoutRates.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            if (totalCount == 0) {
                Map<String, Object> emptyData = createEmptyPageData(page, pageSize);
                emptyData.put("message", "所有设备都已配置费率");
                return ResponseUtils.ok(emptyData);
            }

            if (page > totalPages) {
                page = totalPages;
            }

            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalCount);
            List<Machine> pageList = allMachinesWithoutRates.subList(fromIndex, toIndex);

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("list", pageList);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalCount", totalCount);
            responseData.put("totalPages", totalPages);
            responseData.put("hasNext", page < totalPages);
            responseData.put("hasPrev", page > 1);

            return ResponseUtils.ok(responseData, "查询到 " + totalCount + " 台设备未配置费率");
        } catch (Exception e) {
            return ResponseUtils.serverError("查询无费率设备失败: " + e.getMessage());
        }
    }
    // 创建空分页数据的辅助方法
    private Map<String, Object> createEmptyPageData(int page, int pageSize) {
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("list", Collections.emptyList());
        pageData.put("currentPage", page);
        pageData.put("pageSize", pageSize);
        pageData.put("totalCount", 0);
        pageData.put("totalPages", 0);
        pageData.put("hasNext", false);
        pageData.put("hasPrev", false);
        return pageData;
    }



}