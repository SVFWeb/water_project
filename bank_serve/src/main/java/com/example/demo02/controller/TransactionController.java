package com.example.demo02.controller;

import com.example.demo02.domain.ResponseResult;
import com.example.demo02.domain.Transaction;
import com.example.demo02.mapper.TransactionMapper;
import com.example.demo02.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo02.service.MqttMessageSender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TransactionController {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private MqttMessageSender mqttMessageSender;


    // 新增交易记录
    @PostMapping
    public ResponseEntity<ResponseResult> createTransaction(@RequestBody Transaction transaction) {
        try {

            // 生成交易ID（如果未提供）
            if (transaction.getTransactionId() == null || transaction.getTransactionId().trim().isEmpty()) {
                String transactionId = "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
                transaction.setTransactionId(transactionId);
            }

            // 检查交易记录是否已存在
            if (transactionMapper.existsByTransactionId(transaction.getTransactionId()) > 0) {
                return ResponseUtils.businessError("交易记录ID已存在");
            }

            // 设置默认值
            if (transaction.getOrderStatus() == null) {
                transaction.setOrderStatus("pending"); // 默认待处理状态
            }
            if (transaction.getTotalLiters() == null) {
                transaction.setTotalLiters(0.0); // 默认加水量为0
            }
            if (transaction.getFinalAmount() == null) {
                transaction.setFinalAmount(0.0); // 默认金额为0
            }
            if (transaction.getStartTime() == null) {
                transaction.setStartTime(LocalDateTime.now()); // 默认开始时间为当前时间
            }

            int result = transactionMapper.insert(transaction);
            if (result > 0) {
                //订单创建之后需要传递给mtqq打开开关的数据
                mqttMessageSender.sendMsg("lampline","{@water_add_switch:1}");
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("transactionId", transaction.getTransactionId());
                responseData.put("data", transaction);
                return ResponseUtils.ok(responseData, "交易记录创建成功");
            } else {
                return ResponseUtils.businessError("交易记录创建失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }


// 查询所有交易记录（手动分页）
    @GetMapping
    public ResponseEntity<ResponseResult> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 参数验证
            if (page < 1) page = 1;
            if (pageSize < 1 || pageSize > 100) pageSize = 10;

            // 获取所有数据
            List<Transaction> allTransactions = transactionMapper.findAll();

            // 手动分页逻辑
            int totalCount = allTransactions.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            // 计算分页范围
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalCount);

            if (fromIndex > totalCount) {
                fromIndex = totalCount;
            }

            List<Transaction> pageList = allTransactions.subList(fromIndex, toIndex);

            // 构建分页响应数据
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", pageList);
            pageData.put("currentPage", page);
            pageData.put("pageSize", pageSize);
            pageData.put("totalCount", totalCount);
            pageData.put("totalPages", totalPages);
            pageData.put("hasNext", page < totalPages);
            pageData.put("hasPrev", page > 1);

            return ResponseUtils.ok(pageData);
        } catch (Exception e) {
            return ResponseUtils.serverError("获取交易记录列表失败: " + e.getMessage());
        }
    }


    // 根据ID查询交易记录
    @GetMapping("/{transactionId}")
    public ResponseEntity<ResponseResult> getTransactionById(@PathVariable String transactionId) {
        try {
            Transaction transaction = transactionMapper.findByTransactionId(transactionId);
            if (transaction != null) {
                return ResponseUtils.ok(transaction);
            } else {
                return ResponseUtils.notFound();
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("查询交易记录失败: " + e.getMessage());
        }
    }

    // 根据用户ID查询交易记录
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseResult> getTransactionsByUserId(@PathVariable String userId) {
        try {
            List<Transaction> transactions = transactionMapper.findByUserId(userId);
            return ResponseUtils.ok(transactions);
        } catch (Exception e) {
            return ResponseUtils.serverError("查询用户交易记录失败: " + e.getMessage());
        }
    }

    // 根据设备ID查询交易记录
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<ResponseResult> getTransactionsByMachineId(@PathVariable String machineId) {
        try {
            List<Transaction> transactions = transactionMapper.findByMachineId(machineId);
            return ResponseUtils.ok(transactions);
        } catch (Exception e) {
            return ResponseUtils.serverError("查询设备交易记录失败: " + e.getMessage());
        }
    }

    // 根据订单状态查询交易记录
    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<ResponseResult> getTransactionsByStatus(@PathVariable String orderStatus) {
        try {
            List<Transaction> transactions = transactionMapper.findByOrderStatus(orderStatus);
            return ResponseUtils.ok(transactions);
        } catch (Exception e) {
            return ResponseUtils.serverError("查询状态交易记录失败: " + e.getMessage());
        }
    }

    // 更新交易记录
    @PutMapping("/{transactionId}")
    public ResponseEntity<ResponseResult> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!transactionId.equals(transaction.getTransactionId())) {
                return ResponseUtils.businessError("交易记录ID不匹配");
            }

            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = transactionMapper.update(transaction);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("transactionId", transactionId);
                return ResponseUtils.ok(responseData, "交易记录更新成功");
            } else {
                return ResponseUtils.businessError("交易记录更新失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }


    // 完成交易
    @PatchMapping("/{transactionId}/complete")
    public ResponseEntity<ResponseResult> completeTransaction(@PathVariable String transactionId, @RequestBody Map<String, Object> request) {
        try {
            Double totalLiters = (Double) request.get("totalLiters");
            Double finalAmount = (Double) request.get("finalAmount");

            if (totalLiters == null || totalLiters < 0) {
                return ResponseUtils.businessError("总加水量必须大于等于0");
            }
            if (finalAmount == null || finalAmount < 0) {
                return ResponseUtils.businessError("最终金额必须大于等于0");
            }

            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = transactionMapper.completeTransaction(transactionId, totalLiters, finalAmount, LocalDateTime.now(), "completed");
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("transactionId", transactionId);
                responseData.put("totalLiters", totalLiters);
                responseData.put("finalAmount", finalAmount);
                return ResponseUtils.ok(responseData, "交易完成成功");
            } else {
                return ResponseUtils.businessError("交易完成失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 删除交易记录
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ResponseResult> deleteTransaction(@PathVariable String transactionId) {
        try {
            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = transactionMapper.deleteById(transactionId);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("transactionId", transactionId);
                return ResponseUtils.ok(responseData, "交易记录删除成功");
            } else {
                return ResponseUtils.businessError("交易记录删除失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }


}