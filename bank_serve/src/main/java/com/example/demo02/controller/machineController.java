package com.example.demo02.controller;



import com.example.demo02.domain.Machine;
import com.example.demo02.mapper.MachineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo02.service.MqttMessageSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/machines")

public class machineController {


    @Autowired
    private MachineMapper machineMapper;
    @Autowired
    private MqttMessageSender mqttMessageSender;

    // 开关
    @GetMapping("/water")
    public ResponseEntity<String> getAllMachineWater() {
        try {
            mqttMessageSender.sendMsg("lampline",0,"{@water_add_switch:1}");
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 获取所有设备列表
    @GetMapping
    public ResponseEntity<List<Machine>> getAllMachines() {
        try {
            List<Machine> machines = machineMapper.findAll();
            return ResponseEntity.ok(machines);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据设备ID获取设备详情
    @GetMapping("/{machineId}")
    public ResponseEntity<Machine> getMachineById(@PathVariable String machineId) {
        try {
            Machine machine = machineMapper.findById(machineId);
            if (machine != null) {
                return ResponseEntity.ok(machine);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据状态查询设备
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Machine>> getMachinesByStatus(@PathVariable String status) {
        try {
            List<Machine> machines = machineMapper.findByStatus(status);
            return ResponseEntity.ok(machines);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据位置查询设备
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Machine>> getMachinesByLocation(@PathVariable String location) {
        try {
            List<Machine> machines = machineMapper.findByLocation(location);
            return ResponseEntity.ok(machines);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    // 更新设备信息
    @PutMapping("/{machineId}")
    public ResponseEntity<?> updateMachine(@PathVariable String machineId, @RequestBody Machine machine) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!machineId.equals(machine.getMachineId())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "设备ID不匹配");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查设备是否存在
            if (machineMapper.existsByMachineId(machineId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = machineMapper.update(machine);
            if (result > 0) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "设备信息更新成功");
                response.put("machineId", machineId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "设备信息更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 更新设备状态
    @PatchMapping("/{machineId}/status")
    public ResponseEntity<?> updateMachineStatus(@PathVariable String machineId, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "状态不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查设备是否存在
            if (machineMapper.existsByMachineId(machineId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = machineMapper.updateStatus(machineId, status);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "设备状态更新成功");
                response.put("machineId", machineId);
                response.put("newStatus", status);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "设备状态更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 更新设备实时数据（从MQTT接收的数据）
    @PatchMapping("/{machineId}/device-data")
    public ResponseEntity<?> updateDeviceData(@PathVariable String machineId, @RequestBody Map<String, String> request) {
        try {
            String waterAddSwitch = request.get("water_add_switch");
            String fillUp = request.get("fill_up");
            String deviceTemperature = request.get("device_temperature");
            String batteryLevel = request.get("battery_level");
            String latitudeAndLongitude = request.get("latitude_and_longitude");

            // 检查设备是否存在
            if (machineMapper.existsByMachineId(machineId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = machineMapper.updateDeviceData(machineId, waterAddSwitch, fillUp,
                    deviceTemperature, batteryLevel, latitudeAndLongitude);

            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "设备数据更新成功");
                response.put("machineId", machineId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "设备数据更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 删除设备
    @DeleteMapping("/{machineId}")
    public ResponseEntity<?> deleteMachine(@PathVariable String machineId) {
        try {
            // 检查设备是否存在
            if (machineMapper.existsByMachineId(machineId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = machineMapper.deleteById(machineId);
            if (result > 0) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "设备删除成功");
                response.put("machineId", machineId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "设备删除失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 获取设备统计信息
    @GetMapping("/stats")
    public ResponseEntity<?> getMachineStats() {
        try {
            int totalCount = machineMapper.countAll();
            int onlineCount = machineMapper.countByStatus("在线");
            int offlineCount = machineMapper.countByStatus("离线");
            int maintenanceCount = machineMapper.countByStatus("维护中");

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", totalCount);
            stats.put("online", onlineCount);
            stats.put("offline", offlineCount);
            stats.put("maintenance", maintenanceCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取统计信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }




}
