package com.example.demo02.controller;

import com.example.demo02.domain.Users;
import com.example.demo02.mapper.UserMapper;
import com.example.demo02.service.MqttMessageSender;
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
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            String userPassword = request.get("userPassword");

            // 参数验证
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名不能为空"));
            }
            if (userPassword == null || userPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("密码不能为空"));
            }
            if (userPassword.length() < 6) {
                return ResponseEntity.badRequest().body(createErrorResponse("密码长度不能少于6位"));
            }

            // 检查用户名是否已存在
            if (userMapper.existsByUserName(userName) > 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名已存在"));
            }

            // 生成用户ID
            String userId = "USER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

            // 创建用户 - 使用Users类
            Users user = new Users();
            user.setUserId(userId);
            user.setUserName(userName);
            user.setUserPassword(userPassword);
            user.setBalance(0.0);

            int result = userMapper.insert(user);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "用户注册成功");
                response.put("userId", userId);
                response.put("userName", userName);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("用户注册失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            String userPassword = request.get("userPassword");

            // 参数验证
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名不能为空"));
            }
            if (userPassword == null || userPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("密码不能为空"));
            }

            // 验证用户名和密码
            Users user = userMapper.findByUserNameAndPassword(userName, userPassword);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "登录成功");
                response.put("userId", user.getUserId());
                response.put("userName", user.getUserName());
                response.put("balance", user.getBalance());
                response.put("openId", user.getOpenId());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名或密码错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 微信用户登录/注册
    @PostMapping("/wechat-login")
    public ResponseEntity<?> wechatLogin(@RequestBody Map<String, String> request) {
        try {
            String openId = request.get("openId");
            String userName = request.get("userName");

            if (openId == null || openId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("微信openId不能为空"));
            }

            // 检查微信用户是否已存在
            Users existingUser = userMapper.findByOpenId(openId);
            if (existingUser != null) {
                // 用户已存在，直接登录
                Map<String, Object> response = new HashMap<>();
                response.put("message", "微信登录成功");
                response.put("userId", existingUser.getUserId());
                response.put("userName", existingUser.getUserName());
                response.put("balance", existingUser.getBalance());
                response.put("openId", existingUser.getOpenId());
                return ResponseEntity.ok(response);
            } else {
                // 新微信用户，自动注册
                String userId = "WX_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

                Users newUser = new Users();
                newUser.setUserId(userId);
                newUser.setOpenId(openId);
                newUser.setUserName(userName != null ? userName : "微信用户_" + userId.substring(0, 6));
                newUser.setBalance(0.0);

                int result = userMapper.insert(newUser);
                if (result > 0) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "微信用户自动注册并登录成功");
                    response.put("userId", userId);
                    response.put("userName", newUser.getUserName());
                    response.put("balance", newUser.getBalance());
                    response.put("openId", openId);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(createErrorResponse("微信用户注册失败"));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 获取所有用户列表
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        try {
            List<Users> users = userMapper.findAll();
            // 不返回密码信息
            users.forEach(user -> user.setUserPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 根据用户ID获取用户详情
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            Users user = userMapper.findByUserId(userId);
            if (user != null) {
                // 不返回密码信息
                user.setUserPassword(null);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 更新用户信息
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody Users user) {
        try {
            // 确保URL中的ID与请求体中的ID一致
            if (!userId.equals(user.getUserId())) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户ID不匹配"));
            }

            // 检查用户是否存在
            if (userMapper.existsByUserId(userId) == 0) {
                return ResponseEntity.notFound().build();
            }

            // 不更新密码字段
            Users existingUser = userMapper.findByUserId(userId);
            user.setUserPassword(existingUser.getUserPassword());

            int result = userMapper.update(user);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "用户信息更新成功");
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("用户信息更新失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 修改密码
    @PatchMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable String userId, @RequestBody Map<String, String> request) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("旧密码和新密码都不能为空"));
            }

            // 验证旧密码
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            if (!oldPassword.equals(user.getUserPassword())) {
                return ResponseEntity.badRequest().body(createErrorResponse("旧密码错误"));
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest().body(createErrorResponse("新密码长度不能少于6位"));
            }

            int result = userMapper.updatePassword(userId, newPassword);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "密码修改成功");
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("密码修改失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 用户余额充值
    @PatchMapping("/{userId}/recharge")
    public ResponseEntity<?> rechargeBalance(@PathVariable String userId, @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("充值金额必须大于0"));
            }

            // 检查用户是否存在
            if (userMapper.existsByUserId(userId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = userMapper.rechargeBalance(userId, amount);
            if (result > 0) {
                Users user = userMapper.findByUserId(userId);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "余额充值成功");
                response.put("userId", userId);
                response.put("rechargeAmount", amount);
                response.put("newBalance", user.getBalance());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("余额充值失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 用户余额扣款
    @PatchMapping("/{userId}/deduct")
    public ResponseEntity<?> deductBalance(@PathVariable String userId, @RequestBody Map<String, Double> request) {
        try {
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("扣款金额必须大于0"));
            }

            // 检查用户是否存在
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            if (user.getBalance() < amount) {
                return ResponseEntity.badRequest().body(createErrorResponse("余额不足"));
            }

            int result = userMapper.deductBalance(userId, amount);
            if (result > 0) {
                user = userMapper.findByUserId(userId);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "扣款成功");
                response.put("userId", userId);
                response.put("deductAmount", amount);
                response.put("newBalance", user.getBalance());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("扣款失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 获取用户余额
    @GetMapping("/{userId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String userId) {
        try {
            Users user = userMapper.findByUserId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("balance", user.getBalance());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("获取余额失败: " + e.getMessage()));
        }
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            // 检查用户是否存在
            if (userMapper.existsByUserId(userId) == 0) {
                return ResponseEntity.notFound().build();
            }

            int result = userMapper.deleteById(userId);
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "用户删除成功");
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("用户删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("服务器错误: " + e.getMessage()));
        }
    }

    // 创建错误响应辅助方法
    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}
