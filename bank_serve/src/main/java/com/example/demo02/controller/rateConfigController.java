package com.example.demo02.controller;

import com.example.demo02.domain.RateConfig;
import com.example.demo02.domain.ResponseResult;
import com.example.demo02.mapper.RateConfigMapper;
import com.example.demo02.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rate-config")
@CrossOrigin
public class rateConfigController {

    @Autowired
    private RateConfigMapper rateConfigMapper;

    // 新增费率配置
    @PostMapping
    public ResponseEntity<ResponseResult> createRateConfig(@RequestBody RateConfig rateConfig) {
        try {
            // 生成费率ID（如果未提供）
            if (rateConfig.getRateId() == null || rateConfig.getRateId().trim().isEmpty()) {
                String rateId = "RATE_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
                rateConfig.setRateId(rateId);
            }

            // 检查费率配置是否已存在
            if (rateConfigMapper.existsByRateId(rateConfig.getRateId()) > 0) {
                return ResponseUtils.businessError("费率配置ID已存在");
            }

            // 设置默认值
            if (rateConfig.getRateDayRate() == null) {
                rateConfig.setRateDayRate("默认费率");
            }
            if (rateConfig.getServiceFee() == null) {
                rateConfig.setServiceFee(0.0); // 默认服务费为0
            }
            if (rateConfig.getPricePerLiter() == null) {
                rateConfig.setPricePerLiter(0.0); // 默认每升价格为0
            }

            // 验证必需字段
            if (rateConfig.getMachineId() == null || rateConfig.getMachineId().trim().isEmpty()) {
                return ResponseUtils.businessError("设备ID不能为空");
            }

            int result = rateConfigMapper.insert(rateConfig);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("rateId", rateConfig.getRateId());
                responseData.put("data", rateConfig);
                return ResponseUtils.ok(responseData, "费率配置创建成功");
            } else {
                return ResponseUtils.businessError("费率配置创建失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 查询所有费率配置
    @GetMapping
    public ResponseEntity<ResponseResult> getAllRateConfigs() {
        try {
            List<RateConfig> rateConfigs = rateConfigMapper.findAll();
            return ResponseUtils.ok(rateConfigs);
        } catch (Exception e) {
            return ResponseUtils.serverError("获取费率配置列表失败: " + e.getMessage());
        }
    }

    // 根据ID查询费率配置
    @GetMapping("/{rateId}")
    public ResponseEntity<ResponseResult> getRateConfigById(@PathVariable String rateId) {
        try {
            RateConfig rateConfig = rateConfigMapper.findByRateId(rateId);
            if (rateConfig != null) {
                return ResponseUtils.ok(rateConfig);
            } else {
                return ResponseUtils.notFound();
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("查询费率配置失败: " + e.getMessage());
        }
    }

    // 根据设备ID查询费率配置
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<ResponseResult> getRateConfigByMachineId(@PathVariable String machineId) {
        try {
            List<RateConfig> rateConfigs = rateConfigMapper.findByMachineId(machineId);
            return ResponseUtils.ok(rateConfigs);
        } catch (Exception e) {
            return ResponseUtils.serverError("查询设备费率配置失败: " + e.getMessage());
        }
    }

    // 更新费率配置
    @PutMapping("/{rateId}")
    public ResponseEntity<ResponseResult> updateRateConfig(@PathVariable String rateId, @RequestBody RateConfig rateConfig) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!rateId.equals(rateConfig.getRateId())) {
                return ResponseUtils.businessError("费率配置ID不匹配");
            }

            // 检查费率配置是否存在
            if (rateConfigMapper.existsByRateId(rateId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = rateConfigMapper.update(rateConfig);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("rateId", rateId);
                return ResponseUtils.ok(responseData, "费率配置更新成功");
            } else {
                return ResponseUtils.businessError("费率配置更新失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 删除费率配置
    @DeleteMapping("/{rateId}")
    public ResponseEntity<ResponseResult> deleteRateConfig(@PathVariable String rateId) {
        try {
            // 检查费率配置是否存在
            if (rateConfigMapper.existsByRateId(rateId) == 0) {
                return ResponseUtils.notFound();
            }
            int result = rateConfigMapper.deleteById(rateId);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("rateId", rateId);
                return ResponseUtils.ok(responseData, "费率配置删除成功");
            } else {
                return ResponseUtils.businessError("费率配置删除失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }


}