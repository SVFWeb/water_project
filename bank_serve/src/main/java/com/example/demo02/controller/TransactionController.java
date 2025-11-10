package com.example.demo02.controller;

import com.example.demo02.domain.Transaction;
import com.example.demo02.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    @Autowired
    private TransactionMapper transactionMapper;

    // 获取所有交易记录（带分页）
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            int offset = (page - 1) * size;

            List<Transaction> transactions = transactionMapper.findWithPagination(offset, size);
            int totalCount = transactionMapper.getTotalCount();

            Map<String, Object> response = new HashMap<>();
            response.put("total", totalCount);
            response.put("page", page);
            response.put("size", size);
            response.put("data", transactions);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据交易ID获取交易详情
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        try {
            Transaction transaction = transactionMapper.findByTransactionId(transactionId);
            if (transaction != null) {
                return ResponseEntity.ok(transaction);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据用户ID获取交易记录
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable String userId) {
        try {
            List<Transaction> transactions = transactionMapper.findByUserId(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据设备ID获取交易记录
    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<Transaction>> getTransactionsByMachineId(@PathVariable String machineId) {
        try {
            List<Transaction> transactions = transactionMapper.findByMachineId(machineId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据订单状态获取交易记录
    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String orderStatus) {
        try {
            List<Transaction> transactions = transactionMapper.findByOrderStatus(orderStatus);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 创建交易记录（开始交易）
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            // 检查交易ID是否已存在
            if (transactionMapper.existsByTransactionId(transaction.getTransactionId()) > 0) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易ID已存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 设置默认开始时间
            if (transaction.getStartTime() == null) {
                transaction.setStartTime(LocalDateTime.now());
            }

            int result = transactionMapper.insert(transaction);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "交易记录创建成功");
                response.put("transactionId", transaction.getTransactionId());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易记录创建失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 更新交易记录
    @PutMapping("/{transactionId}")
    public ResponseEntity<?> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!transactionId.equals(transaction.getTransactionId())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易ID不匹配");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = transactionMapper.update(transaction);
            if (result > 0) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "交易记录更新成功");
                response.put("transactionId", transactionId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易记录更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 更新订单状态
    @PatchMapping("/{transactionId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String transactionId, @RequestBody Map<String, String> request) {
        try {
            String orderStatus = request.get("orderStatus");
            if (orderStatus == null || orderStatus.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "订单状态不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = transactionMapper.updateOrderStatus(transactionId, orderStatus);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "订单状态更新成功");
                response.put("transactionId", transactionId);
                response.put("newStatus", orderStatus);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "订单状态更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 完成交易（更新结束信息）
    @PatchMapping("/{transactionId}/complete")
    public ResponseEntity<?> completeTransaction(@PathVariable String transactionId, @RequestBody Map<String, Object> request) {
        try {
            Double totalLiters = (Double) request.get("totalLiters");
            Double finalAmount = (Double) request.get("finalAmount");
            String orderStatus = (String) request.get("orderStatus");

            if (totalLiters == null || finalAmount == null || orderStatus == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "所有结束参数都不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseEntity.notFound().build();
            }

            String endTime = LocalDateTime.now().toString();
            int result = transactionMapper.completeTransaction(transactionId, totalLiters, finalAmount, endTime, orderStatus);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "交易完成信息更新成功");
                response.put("transactionId", transactionId);
                response.put("totalLiters", totalLiters);
                response.put("finalAmount", finalAmount);
                response.put("endTime", endTime);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易完成信息更新失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 开始交易（设置开始时间）
    @PatchMapping("/{transactionId}/start")
    public ResponseEntity<?> startTransaction(@PathVariable String transactionId) {
        try {
            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseEntity.notFound().build();
            }

            String startTime = LocalDateTime.now().toString();
            int result = transactionMapper.startTransaction(transactionId, startTime);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "交易开始时间设置成功");
                response.put("transactionId", transactionId);
                response.put("startTime", startTime);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易开始时间设置失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 删除交易记录
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String transactionId) {
        try {
            // 检查交易记录是否存在
            if (transactionMapper.existsByTransactionId(transactionId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = transactionMapper.deleteById(transactionId);
            if (result > 0) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "交易记录删除成功");
                response.put("transactionId", transactionId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "交易记录删除失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 获取用户交易统计
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserTransactionStats(@PathVariable String userId) {
        try {
            Double totalAmount = transactionMapper.getTotalAmountByUserId(userId);
            List<Transaction> transactions = transactionMapper.findByUserId(userId);
            int transactionCount = transactions.size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", userId);
            stats.put("totalTransactions", transactionCount);
            stats.put("totalAmount", totalAmount != null ? totalAmount : 0.0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "获取用户统计失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }






}
