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

 Date: 11/11/2025 09:06:11
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
  `fill_up` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '是否加满',
  `device_temperature` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备温度',
  `battery_level` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '电池电量',
  `latitude_and_longitude` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '经纬度',
  PRIMARY KEY (`machine_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of machine
-- ----------------------------
INSERT INTO `machine` VALUES (0x6D6131, '测试位置', '在线', 'ON', 'YES', '25.5', '80%', '39.9042,116.4074');

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
  `rate_config` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '存储每台机器当前的费率',
  `rate_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '费率配置id',
  `machine_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '设备id',
  `is_active` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '是否激活这个费率 1表示激活，0表示没激活',
  `price_per_liter` double NULL DEFAULT NULL COMMENT '每升水的价格',
  PRIMARY KEY (`rate_id`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_croatian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rate_config
-- ----------------------------
INSERT INTO `rate_config` VALUES ('ycoobCaLWj', 'ra1', 'ma1', '0', 0.5);
INSERT INTO `rate_config` VALUES ('O8TP7x7IqP', 'ra2', 'ma2', '0', 0.5);

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction`  (
  `transaction_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NOT NULL COMMENT '交易记录id',
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_croatian_ci NULL DEFAULT NULL COMMENT '用户id',
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
INSERT INTO `transaction` VALUES ('1', 'uu1', 'ma1', '0', 719.33, 434.68, '2017-06-16 01:42:00', '2017-11-19 08:27:46');
INSERT INTO `transaction` VALUES ('2', 'uu1', 'ma1', '0', 262.81, 493.77, '2019-01-02 07:43:51', '2000-04-09 21:52:30');
INSERT INTO `transaction` VALUES ('TXN202310011200001', 'uu1', 'MACH001', 'COMPLETED', 5.5, 11, '2023-10-01 10:00:00', '2023-10-01 10:05:30');

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

SET FOREIGN_KEY_CHECKS = 1;
