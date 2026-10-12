/*
 Navicat Premium Data Transfer

 Source Server         : AA555
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : harmony_expense

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 10/07/2026 14:46:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint(1) NOT NULL COMMENT '0=支出 1=收入',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '餐饮', 0);
INSERT INTO `category` VALUES (2, '交通', 0);
INSERT INTO `category` VALUES (3, '购物', 0);
INSERT INTO `category` VALUES (4, '娱乐', 0);
INSERT INTO `category` VALUES (5, '住房', 0);
INSERT INTO `category` VALUES (6, '通讯', 0);
INSERT INTO `category` VALUES (7, '医疗', 0);
INSERT INTO `category` VALUES (8, '教育', 0);
INSERT INTO `category` VALUES (9, '人情', 0);
INSERT INTO `category` VALUES (10, '其他支出', 0);
INSERT INTO `category` VALUES (11, '工资', 1);
INSERT INTO `category` VALUES (12, '兼职', 1);
INSERT INTO `category` VALUES (13, '红包', 1);
INSERT INTO `category` VALUES (14, '投资收益', 1);
INSERT INTO `category` VALUES (15, '其他收入', 1);

-- ----------------------------
-- Table structure for expense_record
-- ----------------------------
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0=支出 1=收入',
  `amount` decimal(10, 2) NOT NULL,
  `category_id` bigint NOT NULL,
  `expense_date` date NULL DEFAULT 'curdate()',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_expense_date`(`expense_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '记账记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expense_record
-- ----------------------------
INSERT INTO `expense_record` VALUES (1, 1, 0, 35.00, 1, '2026-05-01', '早餐', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (2, 1, 0, 68.00, 1, '2026-05-02', '午餐', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (3, 1, 0, 120.00, 2, '2026-05-03', '打车', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (4, 1, 0, 299.00, 3, '2026-05-05', '日用品', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (5, 1, 0, 45.00, 1, '2026-05-06', '奶茶', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (6, 1, 0, 15.00, 2, '2026-05-07', '公交', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (7, 1, 0, 188.00, 4, '2026-05-08', '电影票', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (8, 1, 0, 56.00, 1, '2026-05-10', '外卖', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (9, 1, 0, 800.00, 5, '2026-05-12', '水电燃气', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (10, 1, 0, 30.00, 4, '2026-05-15', '话费充值', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (11, 1, 1, 16000.00, 11, '2026-05-15', '5月工资', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (12, 1, 0, 88.00, 1, '2026-05-18', '晚餐', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (13, 1, 0, 350.00, 3, '2026-04-03', '春装', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (14, 1, 0, 200.00, 4, '2026-04-05', 'KTV', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (15, 1, 0, 50.00, 1, '2026-04-08', '早餐', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (16, 1, 0, 75.00, 2, '2026-04-10', '打车', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (17, 1, 1, 15000.00, 11, '2026-04-15', '4月工资', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (18, 1, 1, 500.00, 12, '2026-04-20', '周末兼职', 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `expense_record` VALUES (19, 3, 0, 150.00, 4, '2026-05-25', '洗脚', 0, '2026-05-25 15:31:45', '2026-05-25 15:31:45');
INSERT INTO `expense_record` VALUES (20, 3, 1, 666.00, 13, '2026-05-24', '', 0, '2026-05-25 15:32:30', '2026-05-25 15:32:30');
INSERT INTO `expense_record` VALUES (21, 3, 1, 3000.00, 11, '2026-04-23', '', 0, '2026-05-25 16:13:40', '2026-05-25 16:13:40');
INSERT INTO `expense_record` VALUES (22, 3, 0, 888.00, 10, '2026-04-19', '', 0, '2026-05-25 16:14:08', '2026-05-25 16:14:08');
INSERT INTO `expense_record` VALUES (23, 3, 0, 563.00, 3, '2026-03-25', '', 0, '2026-05-25 16:23:06', '2026-05-25 16:23:06');
INSERT INTO `expense_record` VALUES (24, 3, 1, 8582.00, 11, '2026-03-25', '', 0, '2026-05-25 16:23:22', '2026-05-25 16:23:22');
INSERT INTO `expense_record` VALUES (25, 3, 0, 892.00, 3, '2026-05-23', '', 0, '2026-05-25 16:53:23', '2026-05-25 16:53:23');
INSERT INTO `expense_record` VALUES (26, 3, 1, 2599.00, 12, '2026-05-25', '', 0, '2026-05-25 16:53:35', '2026-05-25 16:53:35');
INSERT INTO `expense_record` VALUES (27, 1, 0, 55.00, 6, '2026-02-22', '测试', 0, '2026-06-02 08:49:10', '2026-06-02 08:49:10');
INSERT INTO `expense_record` VALUES (28, 1, 0, 552.00, 7, '2026-03-02', '', 0, '2026-06-02 08:57:48', '2026-06-02 08:57:48');
INSERT INTO `expense_record` VALUES (29, 1, 0, 2.00, 2, '2026-06-09', '', 0, '2026-06-09 09:35:49', '2026-06-09 09:35:49');
INSERT INTO `expense_record` VALUES (30, 1, 0, 400.00, 3, '2026-06-06', '', 0, '2026-06-09 09:37:17', '2026-06-09 09:37:17');
INSERT INTO `expense_record` VALUES (31, 1, 1, 400.00, 14, '2026-06-10', '哈哈哈', 0, '2026-06-10 15:20:28', '2026-06-10 15:20:28');
INSERT INTO `expense_record` VALUES (32, 5, 0, 15.00, 1, '2026-06-17', '', 0, '2026-06-17 14:56:43', '2026-06-17 14:56:43');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'test', 'ca089c6df8f709501019f267474b7a1f', '测试用户', 'test@example.com', NULL, 0, '2026-05-22 15:17:46', '2026-05-22 15:18:14');
INSERT INTO `sys_user` VALUES (2, 'demo', '3e979f4a9d0df06039df465915a4298e', '演示用户', 'demo@example.com', NULL, 0, '2026-05-22 15:17:46', '2026-05-22 15:17:46');
INSERT INTO `sys_user` VALUES (3, '李勇测试', 'ca089c6df8f709501019f267474b7a1f', '李勇测试', NULL, NULL, 0, '2026-05-25 15:30:03', '2026-05-25 15:30:03');
INSERT INTO `sys_user` VALUES (4, 'zhang', 'ca089c6df8f709501019f267474b7a1f', 'zhang', NULL, NULL, 0, '2026-06-10 14:42:13', '2026-06-10 14:42:13');
INSERT INTO `sys_user` VALUES (5, '123456', 'ca089c6df8f709501019f267474b7a1f', 'shujukuceshi', NULL, NULL, 0, '2026-06-17 14:55:06', '2026-06-17 14:55:06');

SET FOREIGN_KEY_CHECKS = 1;
