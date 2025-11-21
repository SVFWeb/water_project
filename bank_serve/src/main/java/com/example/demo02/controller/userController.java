package com.example.demo02.controller;

import com.example.demo02.domain.RechargeRecord;
import com.example.demo02.domain.ResponseResult;
import com.example.demo02.domain.Users;
import com.example.demo02.mapper.RechargeRecordMapper;
import com.example.demo02.mapper.UserMapper;
import com.example.demo02.service.MqttMessageSender;
import com.example.demo02.util.ResponseUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Validated
public class userController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MqttMessageSender mqttMessageSender;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<ResponseResult> register(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            String userPassword = request.get("userPassword");

            // 参数验证
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseUtils.businessError("用户名不能为空");
            }
            if (userPassword == null || userPassword.trim().isEmpty()) {
                return ResponseUtils.businessError("密码不能为空");
            }
            if (userPassword.length() < 6) {
                return ResponseUtils.businessError("密码长度不能少于6位");
            }

            // 检查用户名是否已存在
            if (userMapper.existsByUserName(userName) > 0) {
                return ResponseUtils.businessError("用户名已存在");
            }

            // 生成用户ID
            String userId = "USER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

            // 创建用户
            Users user = new Users();
            user.setUserId(userId);
            user.setUserName(userName);
            user.setUserPassword(userPassword);
            user.setBalance(0.0);

            int result = userMapper.insert(user);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", userId);
                responseData.put("userName", userName);
                return ResponseUtils.ok(responseData, "用户注册成功");
            } else {
                return ResponseUtils.businessError("用户注册失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<ResponseResult> login(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            String userPassword = request.get("userPassword");

            // 参数验证
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseUtils.businessError("用户名不能为空");
            }
            if (userPassword == null || userPassword.trim().isEmpty()) {
                return ResponseUtils.businessError("密码不能为空");
            }

            // 验证用户名和密码
            Users user = userMapper.findByUserNameAndPassword(userName, userPassword);
            if (user != null) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", user.getUserId());
                responseData.put("userName", user.getUserName());
                responseData.put("balance", user.getBalance());
                responseData.put("openId", user.getOpenId());
                return ResponseUtils.ok(responseData, "登录成功");
            } else {
                return ResponseUtils.businessError("用户名或密码错误");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }



    // 根据open_id查询或自动创建用户
    @PostMapping("/by-openid")
    public ResponseEntity<ResponseResult> getUserByOpenId(@RequestBody Map<String, String> request) {
        try {
            String openId = request.get("openId");

            // 参数验证
            if (openId == null || openId.trim().isEmpty()) {
                return ResponseUtils.businessError("openId不能为空");
            }

            // 查询用户是否存在
            Users existingUser = userMapper.findByOpenId(openId);

            if (existingUser != null) {
                // 用户已存在，返回用户信息（隐藏密码）
                existingUser.setUserPassword(null);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("user", existingUser);
                responseData.put("isNewUser", false);
                responseData.put("message", "用户已存在");

                return ResponseUtils.ok(responseData);
            } else {
                // 用户不存在，自动创建新用户
                return createNewUserByOpenId(openId);
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("查询用户失败: " + e.getMessage());
        }
    }

    // 自动创建新用户的辅助方法
    private ResponseEntity<ResponseResult> createNewUserByOpenId(String openId) {
        try {
            // 生成用户ID
            String userId = "USER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

            // 生成随机用户名和密码
            String randomUserName = generateRandomUserName();
            String randomPassword = generateRandomPassword();

            // 创建新用户
            Users newUser = new Users();
            newUser.setUserId(userId);
            newUser.setOpenId(openId);
            newUser.setUserName(randomUserName);
            newUser.setUserPassword(randomPassword);
            newUser.setBalance(0.0);

            int result = userMapper.insert(newUser);

            if (result > 0) {
                // 创建成功，返回用户信息（隐藏密码）
                newUser.setUserPassword(null);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("user", newUser);
                responseData.put("isNewUser", true);
                responseData.put("message", "新用户自动创建成功");

                // 修复这里：使用HashMap而不是Map.of()
                Map<String, String> credentials = new HashMap<>();
                credentials.put("generatedUserName", randomUserName);
                credentials.put("generatedPassword", randomPassword);
                credentials.put("note", "请妥善保存自动生成的用户名和密码");

                responseData.put("generatedCredentials", credentials);

                return ResponseUtils.ok(responseData);
            } else {
                return ResponseUtils.businessError("自动创建用户失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("自动创建用户失败: " + e.getMessage());
        }
    }

    // 生成随机用户名
    private String generateRandomUserName() {
        String prefix = "用户";
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return prefix + randomSuffix;
    }

    // 生成随机密码
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
    // 获取所有用户列表
    @GetMapping
    public ResponseEntity<ResponseResult> getAllUsers() {
        try {
            List<Users> users = userMapper.findAll();

            return ResponseUtils.ok(users);
        } catch (Exception e) {
            return ResponseUtils.serverError("获取用户列表失败: " + e.getMessage());
        }
    }

    // 根据用户ID获取用户详情
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseResult> getUserById(@PathVariable String userId) {
        try {
            Users user = userMapper.findByUserId(userId);
            if (user != null) {
                // 不返回密码信息
                user.setUserPassword(null);
                return ResponseUtils.ok(user);
            } else {
                return ResponseUtils.notFound();
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("查询用户失败: " + e.getMessage());
        }
    }

    // 更新用户信息
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseResult> updateUser(@PathVariable String userId, @RequestBody Users user) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!userId.equals(user.getUserId())) {
                return ResponseUtils.businessError("用户ID不匹配");
            }

            // 检查用户是否存在
            if (userMapper.existsByUserId(userId) == 0) {
                return ResponseUtils.notFound();
            }

            // 不更新密码字段
            Users existingUser = userMapper.findByUserId(userId);
            user.setUserPassword(existingUser.getUserPassword());

            int result = userMapper.update(user);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", userId);
                return ResponseUtils.ok(responseData, "用户信息更新成功");
            } else {
                return ResponseUtils.businessError("用户信息更新失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 修改密码
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ResponseResult> updatePassword(@PathVariable String userId, @RequestBody Map<String, String> request) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseUtils.businessError("旧密码和新密码都不能为空");
            }

            // 验证旧密码
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseUtils.notFound();
            }

            if (!oldPassword.equals(user.getUserPassword())) {
                return ResponseUtils.businessError("旧密码错误");
            }

            if (newPassword.length() < 6) {
                return ResponseUtils.businessError("新密码长度不能少于6位");
            }

            int result = userMapper.updatePassword(userId, newPassword);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", userId);
                return ResponseUtils.ok(responseData, "密码修改成功");
            } else {
                return ResponseUtils.businessError("密码修改失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 用户余额充值
// 用户余额充值（完整业务流程）
    @PatchMapping("/{userId}/recharge")
    public ResponseEntity<ResponseResult> rechargeBalance(@PathVariable String userId, @RequestBody Map<String, Object> request) {
        try {
            Double amount = (Double) request.get("amount");
            String remark = (String) request.get("remark");
            String paymentMethod = (String) request.get("paymentMethod"); // 支付方式

            // 参数验证
            if (amount == null || amount <= 0) {
                return ResponseUtils.businessError("充值金额必须大于0");
            }
            if (amount > 10000) {
                return ResponseUtils.businessError("单次充值金额不能超过10000元");
            }

            // 检查用户是否存在
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseUtils.notFound();
            }

            // 生成唯一交易号
            String transactionNo = "T" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            // 1. 更新用户余额
            int rechargeResult = userMapper.rechargeBalance(userId, amount);
            if (rechargeResult <= 0) {
                return ResponseUtils.businessError("余额充值失败");
            }

            // 2. 创建充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setRecordId(transactionNo);
            rechargeRecord.setUserId(userId);
            rechargeRecord.setUserName(user.getUserName());
            rechargeRecord.setAmount(amount);
            rechargeRecord.setRechargeTime(LocalDateTime.now());
            rechargeRecord.setStatus("success");

            // 设置备注信息
            StringBuilder recordRemark = new StringBuilder();
            if (paymentMethod != null) {
                recordRemark.append(paymentMethod).append("支付");
            } else {
                recordRemark.append("余额充值");
            }
            if (remark != null && !remark.trim().isEmpty()) {
                recordRemark.append(" - ").append(remark);
            }
            rechargeRecord.setRemark(recordRemark.toString());

            int recordResult = rechargeRecordMapper.insert(rechargeRecord);

            // 3. 获取更新后的用户信息
            Users updatedUser = userMapper.findByUserId(userId);

            // 4. 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("userName", updatedUser.getUserName());
            responseData.put("rechargeAmount", amount);
            responseData.put("newBalance", updatedUser.getBalance());
            responseData.put("transactionNo", transactionNo);
            responseData.put("rechargeTime", rechargeRecord.getRechargeTime());
            responseData.put("recordCreated", recordResult > 0);

            // 记录操作日志
            System.out.println("💰 用户充值成功 - 用户: " + userId +
                    ", 金额: " + amount +
                    ", 交易号: " + transactionNo);

            return ResponseUtils.ok(responseData, "余额充值成功");

        } catch (Exception e) {
            System.err.println("❌ 用户充值异常 - 用户: " + userId + ", 错误: " + e.getMessage());
            return ResponseUtils.serverError("充值失败: " + e.getMessage());
        }
    }
    // 用户余额扣款
    @PatchMapping("/{userId}/deduct")
    public ResponseEntity<ResponseResult> deductBalance(@PathVariable String userId, @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseUtils.businessError("扣款金额必须大于0");
            }

            // 检查用户是否存在
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseUtils.notFound();
            }

            if (user.getBalance() < amount) {
                return ResponseUtils.businessError("余额不足");
            }

            int result = userMapper.deductBalance(userId, amount);
            if (result > 0) {
                user = userMapper.findByUserId(userId);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", userId);
                responseData.put("deductAmount", amount);
                responseData.put("newBalance", user.getBalance());
                return ResponseUtils.ok(responseData, "扣款成功");
            } else {
                return ResponseUtils.businessError("扣款失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }

    // 获取用户余额
    @GetMapping("/{userId}/balance")
    public ResponseEntity<ResponseResult> getBalance(@PathVariable String userId) {
        try {
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseUtils.notFound();
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("balance", user.getBalance());
            return ResponseUtils.ok(responseData);
        } catch (Exception e) {
            return ResponseUtils.serverError("获取余额失败: " + e.getMessage());
        }
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseResult> deleteUser(@PathVariable String userId) {
        try {
            // 检查用户是否存在
            if (userMapper.existsByUserId(userId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = userMapper.deleteById(userId);
            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", userId);
                return ResponseUtils.ok(responseData, "用户删除成功");
            } else {
                return ResponseUtils.businessError("用户删除失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
        }
    }



    // 创建错误响应辅助方法
    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}
