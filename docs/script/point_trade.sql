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

 Date: 19/10/2022 09:43:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for point_trade
-- ----------------------------
DROP TABLE IF EXISTS `point_trade`;
CREATE TABLE `point_trade`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `seller_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '卖方账户',
  `buyer_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '买方账户',
  `amount` decimal(24, 6) NULL DEFAULT NULL COMMENT '交易积分',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '交易状态',
  `version` int NULL DEFAULT NULL COMMENT '乐观锁',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '积分交易表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
