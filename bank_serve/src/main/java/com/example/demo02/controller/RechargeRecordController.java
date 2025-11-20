package com.example.demo02.controller;

import com.example.demo02.domain.RechargeRecord;
import com.example.demo02.domain.ResponseResult;
import com.example.demo02.mapper.RechargeRecordMapper;
import com.example.demo02.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recharge-record")
@CrossOrigin
public class RechargeRecordController {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    // 新增充值记录
    @PostMapping
    public ResponseEntity<ResponseResult> createRechargeRecord(@RequestBody RechargeRecord rechargeRecord) {
        try {
            // 生成记录ID（如果未提供）
            if (rechargeRecord.getRecordId() == null || rechargeRecord.getRecordId().trim().isEmpty()) {
                String recordId = "RECHARGE_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
                rechargeRecord.setRecordId(recordId);
            }

            // 检查记录是否已存在
            if (rechargeRecordMapper.existsByRecordId(rechargeRecord.getRecordId()) > 0) {
                return ResponseUtils.businessError("充值记录ID已存在");
            }

            // 设置默认值
            if (rechargeRecord.getRechargeTime() == null) {
                rechargeRecord.setRechargeTime(LocalDateTime.now());
            }
            if (rechargeRecord.getStatus() == null) {
                rechargeRecord.setStatus("pending");
            }
            if (rechargeRecord.getAmount() == null) {
                rechargeRecord.setAmount(0.0);
            }

            int result = rechargeRecordMapper.insert(rechargeRecord);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("recordId", rechargeRecord.getRecordId());
                responseData.put("data", rechargeRecord);
                return ResponseUtils.ok(responseData, "充值记录创建成功");
            } else {
                return ResponseUtils.businessError("充值记录创建失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 查询所有充值记录
    @GetMapping
    public ResponseEntity<ResponseResult> getAllRechargeRecords() {
        try {
            List<RechargeRecord> records = rechargeRecordMapper.findAll();
            return ResponseUtils.ok(records);
        } catch (Exception e) {
            return ResponseUtils.serverError("获取充值记录列表失败: " + e.getMessage());
        }
    }



    // 根据用户ID查询充值记录
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseResult> getRechargeRecordsByUserId(@PathVariable String userId) {
        try {
            List<RechargeRecord> records = rechargeRecordMapper.findByUserId(userId);
            return ResponseUtils.ok(records);
        } catch (Exception e) {
            return ResponseUtils.serverError("查询用户充值记录失败: " + e.getMessage());
        }
    }

    // 更新充值记录
    @PutMapping("/{recordId}")
    public ResponseEntity<ResponseResult> updateRechargeRecord(@PathVariable String recordId, @RequestBody RechargeRecord rechargeRecord) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!recordId.equals(rechargeRecord.getRecordId())) {
                return ResponseUtils.businessError("记录ID不匹配");
            }

            // 检查记录是否存在
            if (rechargeRecordMapper.existsByRecordId(recordId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = rechargeRecordMapper.update(rechargeRecord);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("recordId", recordId);
                return ResponseUtils.ok(responseData, "充值记录更新成功");
            } else {
                return ResponseUtils.businessError("充值记录更新失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 删除充值记录
    @DeleteMapping("/{recordId}")
    public ResponseEntity<ResponseResult> deleteRechargeRecord(@PathVariable String recordId) {
        try {
            // 检查记录是否存在
            if (rechargeRecordMapper.existsByRecordId(recordId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = rechargeRecordMapper.deleteById(recordId);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("recordId", recordId);
                return ResponseUtils.ok(responseData, "充值记录删除成功");
            } else {
                return ResponseUtils.businessError("充值记录删除失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }




}