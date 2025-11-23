/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50726 (5.7.26)
 Source Host           : localhost:3306
 Source Schema         : wether

 Target Server Type    : MySQL
 Target Server Version : 50726 (5.7.26)
 File Encoding         : 65001

 Date: 23/11/2025 18:18:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for machine
-- ----------------------------
DROP TABLE IF EXISTS `machine`;
CREATE TABLE `machine`  (
  `machine_id` varbinary(255) NOT NULL COMMENT '设备唯一id',
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备位置',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备状态',
  `water_add_switch` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT 'stm32的开水开关',
  `pause` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT 'stm的开水暂停',
  `enable_device` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备是否启用',
  `water_tank` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '水箱是否满',
  `fill_up` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '是否加满',
  `device_temperature` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备温度',
  `battery_level` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '电池电量',
  `total_water_addition` double NULL DEFAULT NULL COMMENT '总加水量',
  `latitude` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '经度',
  `longitude` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '纬度',
  `there_fee` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '是否有费率',
  PRIMARY KEY (`machine_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of machine
-- ----------------------------
INSERT INTO `machine` VALUES (0x6D6131, '实验室A区-更新', '离线', '1', '1', '1', '1', '1', '21', '38', 0, '31.230416', '121.473701', '0');
INSERT INTO `machine` VALUES (0x4D414348494E455F544553545F303031, '办公室B区', '离线', '1', '0', '1', '1', '1', '30', '65', 150.5, '39.9042', '116.4074', '0');
INSERT INTO `machine` VALUES (0x32, '8pcZdc3SKR', '离线', '0', '0', '0', '0', '0', '0', '0', 0, '0', '0', '0');

-- ----------------------------
-- Table structure for mqtt_user
-- ----------------------------
DROP TABLE IF EXISTS `mqtt_user`;
CREATE TABLE `mqtt_user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `salt` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_superuser` tinyint(1) NULL DEFAULT 0,
  `created` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `mqtt_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mqtt_user
-- ----------------------------
INSERT INTO `mqtt_user` VALUES (114, 'test', '269c74e5601e036cbb6d221e624cbd73858de8616ea3f17167363c0cfe8ba0ed', 'yy', 0, NULL);

-- ----------------------------
-- Table structure for rate_config
-- ----------------------------
DROP TABLE IF EXISTS `rate_config`;
CREATE TABLE `rate_config`  (
  `rate_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '费率配置id',
  `rate_day_rate` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '存储每台机器当前的费率',
  `machine_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备id',
  `service_fee` double NULL DEFAULT NULL COMMENT '服务费用',
  `price_per_liter` double NULL DEFAULT NULL COMMENT '每升水的价格',
  PRIMARY KEY (`rate_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rate_config
-- ----------------------------
INSERT INTO `rate_config` VALUES ('ra1', '0', 'ma1', 1.5, 0.8);
INSERT INTO `rate_config` VALUES ('ra2', 'O8TP7x7IqP', 'ma2', 0, 0.5);

-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record`  (
  `record_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '记录id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '用户名',
  `amount` double NULL DEFAULT NULL COMMENT '充值金额',
  `recharge_time` datetime NULL DEFAULT NULL COMMENT '充值时间',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '充值状态',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of record
-- ----------------------------
INSERT INTO `record` VALUES ('RECHARGE_1763654140812_07e88b4e', 'USER_1763626976193_f110c98c', '张三', 100, '2025-11-20 23:55:41', 'success', '微信支付充值');
INSERT INTO `record` VALUES ('T17636548063923ECEE9', 'USER_1763626976193_f110c98c', '用户25af4b34', 100, '2025-11-21 00:06:46', 'success', '余额充值 - 微信支付充值');
INSERT INTO `record` VALUES ('T17636548300197A548E', 'USER_1763626976193_f110c98c', '用户25af4b34', 100, '2025-11-21 00:07:10', 'success', '余额充值 - 微信支付充值');
INSERT INTO `record` VALUES ('T17636548508251E3E8D', 'USER_1763626976193_f110c98c', '用户25af4b34', 100, '2025-11-21 00:07:31', 'success', '余额充值 - 微信支付充值');
INSERT INTO `record` VALUES ('T17636548521167B8D2F', 'USER_1763626976193_f110c98c', '用户25af4b34', 100, '2025-11-21 00:07:32', 'success', '余额充值 - 微信支付充值');
INSERT INTO `record` VALUES ('T176365485315895487E', 'USER_1763626976193_f110c98c', '用户25af4b34', 100, '2025-11-21 00:07:33', 'success', '余额充值 - 微信支付充值');
INSERT INTO `record` VALUES ('T176365485412710A8E3', 'USER_1763626976193_f110c98c', '用户25af4b34', 100, '2025-11-21 00:07:34', 'success', '余额充值 - 微信支付充值');

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction`  (
  `transaction_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '交易记录id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '用户名',
  `machine_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备表的id',
  `order_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '订单状态',
  `total_liters` double NULL DEFAULT NULL COMMENT '总加水量',
  `final_amount` double NULL DEFAULT NULL COMMENT '最终金额',
  `start_time` datetime NULL DEFAULT NULL COMMENT '交易开始的时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '交易结束的时间',
  PRIMARY KEY (`transaction_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transaction
-- ----------------------------
INSERT INTO `transaction` VALUES ('1', 'uu1', NULL, 'ma1', '0', 719.33, 434.68, '2017-06-16 01:42:00', '2017-11-19 08:27:46');
INSERT INTO `transaction` VALUES ('2', 'uu1', NULL, 'ma1', '0', 262.81, 493.77, '2019-01-02 07:43:51', '2000-04-09 21:52:30');
INSERT INTO `transaction` VALUES ('TXN202310011200001', 'uu1', NULL, 'MACH001', 'COMPLETED', 5.5, 11, '2023-10-01 10:00:00', '2023-10-01 10:05:30');
INSERT INTO `transaction` VALUES ('TXN_1763561380071_73f8b606', 'USER_1763556182156_85c659e5', 'yansanxi', 'ma1', 'completed', 2000.4, 8.4, '2024-01-01 10:30:00', '2025-11-19 22:29:34');
INSERT INTO `transaction` VALUES ('TXN_1763562770592_f9fdb015', 'USER_1763556182156_85c659e5', 'yansanxi', 'ma1', '进行', 0, 0, '2024-01-01 10:30:00', NULL);
INSERT INTO `transaction` VALUES ('TXN_1763562868213_69b84b22', 'USER_1763556182156_85c659e5', 'yansanxi', 'ma1', '进行', 0, 0, '2024-01-01 10:30:00', NULL);
INSERT INTO `transaction` VALUES ('TXN_1763562928975_372cabc5', 'USER_1763556182156_85c659e5', 'yansanxi', 'ma1', '进行', 0, 0, '2024-01-01 10:30:00', NULL);
INSERT INTO `transaction` VALUES ('TXN_1763562948756_e452fcb8', 'USER_1763556182156_85c659e5', 'yansanxi', 'ma1', '进行', 0, 0, '2024-01-01 10:30:00', NULL);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '用户唯一标识',
  `open_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '微信用户唯一标识',
  `balance` double NULL DEFAULT NULL COMMENT '用户余额',
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '用户名称',
  `user_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '用户密码',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('uu1', 'UElqfSNZSw_updated', 790.25, 'Clara Black - 已更新', NULL);
INSERT INTO `users` VALUES ('uu2', 'YpnxPY9SD6', 388.01, 'Mori Yamato', NULL);
INSERT INTO `users` VALUES ('USER_1762422393507_14fd9273', NULL, 0, 'uuss', '123456');
INSERT INTO `users` VALUES ('USER_1763556182156_85c659e5', NULL, 100, 'yansanxi', '123456');
INSERT INTO `users` VALUES ('USER_1763626976193_f110c98c', 'wx_openid_123456', 600, '用户25af4b34', 'xsUZyiXH');

SET FOREIGN_KEY_CHECKS = 1;
