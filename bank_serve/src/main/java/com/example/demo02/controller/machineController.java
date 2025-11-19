package com.example.demo02.controller;

import com.example.demo02.domain.Machine;
import com.example.demo02.domain.ResponseResult;
import com.example.demo02.mapper.MachineMapper;
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