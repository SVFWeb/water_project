package com.example.demo02.controller;

import com.example.demo02.domain.ResponseResult;
import com.example.demo02.domain.Users;
import com.example.demo02.mapper.UserMapper;
import com.example.demo02.service.MqttMessageSender;
import com.example.demo02.util.ResponseUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Validated
public class userController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MqttMessageSender mqttMessageSender;

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


    // 获取所有用户列表
    @GetMapping
    public ResponseEntity<ResponseResult> getAllUsers() {
        try {
            List<Users> users = userMapper.findAll();
            // 不返回密码信息
            users.forEach(user -> user.setUserPassword(null));
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
    @PatchMapping("/{userId}/recharge")
    public ResponseEntity<ResponseResult> rechargeBalance(@PathVariable String userId, @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseUtils.businessError("充值金额必须大于0");
            }

            // 检查用户是否存在
            if (userMapper.existsByUserId(userId) == 0) {
                return ResponseUtils.notFound();
            }

            int result = userMapper.rechargeBalance(userId, amount);
            if (result > 0) {
                Users user = userMapper.findByUserId(userId);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("userId", userId);
                responseData.put("rechargeAmount", amount);
                responseData.put("newBalance", user.getBalance());
                return ResponseUtils.ok(responseData, "余额充值成功");
            } else {
                return ResponseUtils.businessError("余额充值失败");
            }
        } catch (Exception e) {
            return ResponseUtils.serverError("服务器错误: " + e.getMessage());
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
