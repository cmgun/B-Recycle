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

 Date: 19/10/2022 09:43:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ent_info
-- ----------------------------
DROP TABLE IF EXISTS `ent_info`;
CREATE TABLE `ent_info`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int NOT NULL COMMENT '用户id',
  `access_contract_addr` varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '准入合约地址',
  `access_status` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '准入类型：0-无需准入，1-准入审批中，2-准入审批拒绝，3-审批通过',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '企业信息扩展表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
