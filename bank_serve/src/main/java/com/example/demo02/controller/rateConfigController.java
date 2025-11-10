package com.example.demo02.controller;

import com.example.demo02.domain.RateConfig;
import com.example.demo02.mapper.RateConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rate-config")

public class rateConfigController {


    @Autowired
    private RateConfigMapper rateConfigMapper;

    // 获取所有费率配置
    @GetMapping
    public ResponseEntity<List<RateConfig>> getAllRateConfigs() {
        try {
            List<RateConfig> rateConfigs = rateConfigMapper.findAll();
            return ResponseEntity.ok(rateConfigs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据费率ID获取配置
    @GetMapping("/{rateId}")
    public ResponseEntity<RateConfig> getRateConfigById(@PathVariable String rateId) {
        try {
            RateConfig rateConfig = rateConfigMapper.findByRateId(rateId);
            if (rateConfig != null) {
                return ResponseEntity.ok(rateConfig);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据设备ID获取费率配置
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<RateConfig>> getRateConfigsByMachineId(@PathVariable String machineId) {
        try {
            List<RateConfig> rateConfigs = rateConfigMapper.findByMachineId(machineId);
            return ResponseEntity.ok(rateConfigs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{rateId}/exists")
    public ResponseEntity<?> checkRateConfigExists(@PathVariable String rateId) {
        try {
            boolean exists = rateConfigMapper.existsByRateId(rateId) > 0;
            Map<String, Boolean> result = new HashMap<>();
            result.put("exists", exists);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }




}
