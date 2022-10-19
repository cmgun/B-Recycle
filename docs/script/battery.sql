/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : brecycle

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 19/10/2022 09:43:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for battery
-- ----------------------------
DROP TABLE IF EXISTS `battery`;
CREATE TABLE `battery`  (
  `id` int NOT NULL COMMENT '电池id',
  `batch_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '批次号',
  `info` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '其他电池信息',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '合约地址',
  `create_time` datetime NULL DEFAULT NULL COMMENT '出厂日期',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '规格代码',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '电池类型代码',
  `vdc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标称电压',
  `mah` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标称容量',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '电池状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '电池信息;记录额外的电池信息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
