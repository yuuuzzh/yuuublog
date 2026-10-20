/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028 (8.0.28)
 Source Host           : localhost:3306
 Source Schema         : yuuu_blog

 Target Server Type    : MySQL
 Target Server Version : 80028 (8.0.28)
 File Encoding         : 65001

 Date: 01/05/2024 21:27:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_records
-- ----------------------------
DROP TABLE IF EXISTS `chat_records`;
CREATE TABLE `chat_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
  `conversationId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会话id',
  `query` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提问',
  `answer` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回答',
  `timeStamp` bigint NULL DEFAULT NULL COMMENT '聊天时间戳',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_records
-- ----------------------------
INSERT INTO `chat_records` VALUES (1, 14787164048686, '8b3e1f9d-549c-4e73-9e88-dd67b47112a1', 'hello', 'Hello! How can I assist you today?', 1706607466416);

-- ----------------------------
-- Table structure for login_config
-- ----------------------------
DROP TABLE IF EXISTS `login_config`;
CREATE TABLE `login_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录ip',
  `dev_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录设备',
  `login_time` timestamp NULL DEFAULT NULL COMMENT '登录时间',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户登录地址',
  `user_id` bigint NULL DEFAULT NULL COMMENT '登录用户id',
  `type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型(1：登录信息 ， 2：访客信息)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_config
-- ----------------------------
INSERT INTO `login_config` VALUES (3, 'yuuu', 'yuuu', '127.0.0.1', 'Computer', '2024-05-01 20:24:11', '23412332@qq.com', 'Windows 10', 'Chrome 12', '暂无位置信息', 1, '1');
INSERT INTO `login_config` VALUES (4, 'ceshi', 'ceshi', '127.0.0.1', '其他', '2024-01-26 11:03:24', '123@qq.com', 'Windows 10', 'Chrome 12', '暂无位置信息', 2, '1');
INSERT INTO `login_config` VALUES (5, NULL, '游客719fb9c7', '127.0.0.1', 'Computer', '2024-05-01 20:24:12', NULL, 'Windows 10', 'Chrome 12', NULL, NULL, '2');
INSERT INTO `login_config` VALUES (6, 'ccc', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (7, 'ccc1', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (8, 'ccc2', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (9, 'ccc3', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (10, 'ccc4', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (11, 'ccc5', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (12, 'ccc6', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (13, 'ccc7', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (14, 'ccc8', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (15, 'ccc9', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (16, 'ccc10', 'ccc', '127.0.0.1', 'Computer', '2024-01-31 13:55:27', '132@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048687, '1');
INSERT INTO `login_config` VALUES (19, 'root', 'observer', '127.0.0.1', 'Computer', '2024-05-01 20:40:57', '1234@qq.com', 'Windows 10', 'Chrome 12', NULL, 14787164048686, '1');

-- ----------------------------
-- Table structure for message_board
-- ----------------------------
DROP TABLE IF EXISTS `message_board`;
CREATE TABLE `message_board`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `message` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '留言内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_board
-- ----------------------------
INSERT INTO `message_board` VALUES (1, '游客aa5dd852', '//img.xjh.me/img/63176492_p0.jpg', '666');
INSERT INTO `message_board` VALUES (2, 'root', '//img.xjh.me/img/57793127_p0.jpg', '好腻害');
INSERT INTO `message_board` VALUES (3, '游客612ea758', '//img.xjh.me/img/59770850_p0_master1200.jpg', '666666');
INSERT INTO `message_board` VALUES (4, '游客673363bd', '//img.xjh.me/img/62835061_p0.jpg', 'hello world');
INSERT INTO `message_board` VALUES (5, '游客491c7d09', '//img.xjh.me/img/59030275_p0.jpg', 'ok');
INSERT INTO `message_board` VALUES (6, '游客809be8f9', '//img.xjh.me/img/47689519_p0.jpg', '1');
INSERT INTO `message_board` VALUES (7, '游客ed2b3dbd', '//img.xjh.me/img/63228515_p0.jpg', '1111');
INSERT INTO `message_board` VALUES (8, '游客155ef1ec', '//img.xjh.me/img/45061337_p0.jpg', '123456');
INSERT INTO `message_board` VALUES (9, '游客abbcf270', '//img.xjh.me/img/62522848_p0.jpg', '11111');
INSERT INTO `message_board` VALUES (10, '游客fbcb16e9', '//img.xjh.me/img/63244308_p1_master1200.jpg', '1234');
INSERT INTO `message_board` VALUES (11, '游客17aaaf66', '//img.xjh.me/img/63807928_p0.jpg', '12345');
INSERT INTO `message_board` VALUES (12, '游客12e7c81a', '//img.xjh.me/img/49504907_p0.jpg', '6666');
INSERT INTO `message_board` VALUES (13, 'root', '//img.xjh.me/img/50240230_p0.jpg', '666666');
INSERT INTO `message_board` VALUES (14, 'root', '//img.xjh.me/img/60473935_p0.jpg', '1');
INSERT INTO `message_board` VALUES (15, 'yuuu', 'http://qiniu.yuuu.online/2023/08/11/8dbfd3b51126440ca7a2b239cc628b5c.jpg', 'hi');
INSERT INTO `message_board` VALUES (16, 'yuuu', 'http://qiniu.yuuu.online/2023/08/11/8dbfd3b51126440ca7a2b239cc628b5c.jpg', '11112222');
INSERT INTO `message_board` VALUES (17, 'ccc', '//img.xjh.me/img/63875284_p0.jpg', '123456');
INSERT INTO `message_board` VALUES (18, 'ccc', '//img.xjh.me/img/63875284_p0.jpg', '111');
INSERT INTO `message_board` VALUES (19, 'yuuu', 'http://qiniu.yuuu.online/2023/08/11/8dbfd3b51126440ca7a2b239cc628b5c.jpg', '混蛋');
INSERT INTO `message_board` VALUES (20, 'yuuu', 'http://qiniu.yuuu.online/2023/08/11/8dbfd3b51126440ca7a2b239cc628b5c.jpg', '**');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `menu_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2070 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, 1, 'M', '0', '0', '', 'system', 0, '2021-11-12 10:46:19', 0, NULL, '系统管理目录', '0');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 1, 'C', '0', '0', 'system:user:list', 'user', 0, '2021-11-12 10:46:19', 1, '2022-07-31 15:47:58', '用户管理菜单', '0');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 1, 'C', '0', '0', 'system:role:list', 'peoples', 0, '2021-11-12 10:46:19', 0, NULL, '角色管理菜单', '0');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', 1, 'C', '0', '0', 'system:menu:list', 'tree-table', 0, '2021-11-12 10:46:19', 0, NULL, '菜单管理菜单', '0');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', 1, 'F', '0', '0', 'system:user:query', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', 1, 'F', '0', '0', 'system:user:add', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', 1, 'F', '0', '0', 'system:user:edit', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', 1, 'F', '0', '0', 'system:user:remove', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', 1, 'F', '0', '0', 'system:user:export', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', 1, 'F', '0', '0', 'system:user:import', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', 1, 'F', '0', '0', 'system:user:resetPwd', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', 1, 'F', '0', '0', 'system:role:query', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', 1, 'F', '0', '0', 'system:role:add', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', 1, 'F', '0', '0', 'system:role:edit', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', 1, 'F', '0', '0', 'system:role:remove', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', 1, 'F', '0', '0', 'system:role:export', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', 1, 'F', '0', '0', 'system:menu:query', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', 1, 'F', '0', '0', 'system:menu:add', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', 1, 'F', '0', '0', 'system:menu:edit', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', 1, 'F', '0', '0', 'system:menu:remove', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (2017, '内容管理', 0, 4, 'content', NULL, 1, 'M', '0', '0', NULL, 'table', 1, '2022-01-08 02:44:38', 1, '2022-07-31 12:34:23', '', '0');
INSERT INTO `sys_menu` VALUES (2018, '分类管理', 2017, 1, 'category', 'content/category/index', 1, 'C', '0', '0', 'content:category:list', 'example', 1, '2022-01-08 02:51:45', NULL, '2022-01-08 02:51:45', '', '0');
INSERT INTO `sys_menu` VALUES (2019, '文章管理', 2017, 2, 'article', 'content/article/index', 1, 'C', '0', '0', 'content:article:list', 'build', 1, '2022-01-08 02:53:10', 1, '2024-01-19 14:10:54', '', '0');
INSERT INTO `sys_menu` VALUES (2021, '标签管理', 2017, 6, 'tag', 'content/tag/index', 1, 'C', '0', '0', 'content:tag:index', 'button', 1, '2022-01-08 02:55:37', NULL, '2022-01-08 02:55:50', '', '0');
INSERT INTO `sys_menu` VALUES (2022, '友链管理', 2017, 4, 'link', 'content/link/index', 1, 'C', '0', '0', 'content:link:list', '404', 1, '2022-01-08 02:56:50', NULL, '2022-01-08 02:56:50', '', '0');
INSERT INTO `sys_menu` VALUES (2023, '写博文', 0, 0, 'write', 'content/article/write/index', 1, 'C', '0', '0', 'content:article:writer', 'build', 1, '2022-01-08 03:39:58', 1, '2022-07-31 22:07:05', '', '0');
INSERT INTO `sys_menu` VALUES (2024, '友链新增', 2022, 0, '', NULL, 1, 'F', '0', '0', 'content:link:add', '#', 1, '2022-01-16 07:59:17', 1, '2024-01-19 14:35:44', '', '0');
INSERT INTO `sys_menu` VALUES (2025, '友链修改', 2022, 1, '', NULL, 1, 'F', '0', '0', 'content:link:edit', '#', 1, '2022-01-16 07:59:44', 1, '2024-01-19 14:35:52', '', '0');
INSERT INTO `sys_menu` VALUES (2026, '友链删除', 2022, 1, '', NULL, 1, 'F', '0', '0', 'content:link:remove', '#', 1, '2022-01-16 08:00:05', 1, '2024-01-19 14:36:00', '', '0');
INSERT INTO `sys_menu` VALUES (2027, '友链查询', 2022, 3, '', NULL, 1, 'F', '0', '0', 'content:link:query', '#', 1, '2022-01-16 08:04:09', 1, '2024-01-19 14:36:13', '', '0');
INSERT INTO `sys_menu` VALUES (2028, '导出分类', 2018, 3, '', NULL, 1, 'F', '0', '0', 'content:category:export', '#', 1, '2022-01-21 07:06:59', 1, '2024-01-19 14:34:45', '', '0');
INSERT INTO `sys_menu` VALUES (2049, '文章删除', 2019, 3, '', NULL, 1, 'F', '0', '0', 'content:article:remove', '#', 1, '2024-01-19 14:14:10', 1, '2024-01-19 14:14:10', '', '0');
INSERT INTO `sys_menu` VALUES (2050, '文章发布', 2059, 3, '', NULL, 1, 'F', '0', '0', 'content:article:publish', '#', 1, '2024-01-19 14:16:05', 1, '2024-01-19 15:09:06', '', '0');
INSERT INTO `sys_menu` VALUES (2051, '新增分类', 2018, 3, '', NULL, 1, 'F', '0', '0', 'content:category:add', '#', 1, '2024-01-19 14:21:50', 1, '2024-01-19 14:34:54', '', '0');
INSERT INTO `sys_menu` VALUES (2052, '删除分类', 2018, 3, '', NULL, 1, 'F', '0', '0', 'content:category:remove', '#', 1, '2024-01-19 14:22:10', 1, '2024-01-19 14:35:03', '', '0');
INSERT INTO `sys_menu` VALUES (2053, '修改分类', 2018, 3, '', NULL, 1, 'F', '0', '0', 'content:category:edit', '#', 1, '2024-01-19 14:22:40', 1, '2024-01-19 14:35:11', '', '0');
INSERT INTO `sys_menu` VALUES (2054, '查询分类', 2018, 3, '', NULL, 1, 'F', '0', '0', 'content:category:query', '#', 1, '2024-01-19 14:22:59', 1, '2024-01-19 14:35:20', '', '0');
INSERT INTO `sys_menu` VALUES (2055, '新增标签', 2021, 3, '', NULL, 1, 'F', '0', '0', 'content:tag:add', '#', 1, '2024-01-19 14:24:00', 1, '2024-01-19 14:24:00', '', '0');
INSERT INTO `sys_menu` VALUES (2056, '删除标签', 2021, 3, '', NULL, 1, 'F', '0', '0', 'content:tag:remove', '#', 1, '2024-01-19 14:24:15', 1, '2024-01-19 14:24:15', '', '0');
INSERT INTO `sys_menu` VALUES (2057, '修改标签', 2021, 3, '', NULL, 1, 'F', '0', '0', 'content:tag:edit', '#', 1, '2024-01-19 14:24:32', 1, '2024-01-19 14:24:32', '', '0');
INSERT INTO `sys_menu` VALUES (2058, '查询标签', 2021, 3, '', NULL, 1, 'F', '0', '0', 'content:tag:query', '#', 1, '2024-01-19 14:24:51', 1, '2024-01-19 14:24:51', '', '0');
INSERT INTO `sys_menu` VALUES (2059, '文章发布管理', 2023, 2, 'write', 'content/article/write/index', 1, 'C', '0', '0', 'content:article:writer', 'eye', 1, '2024-01-19 15:08:57', 1, '2024-01-19 15:08:57', '', '1');
INSERT INTO `sys_menu` VALUES (2060, '日志管理', 0, 1, 'log', NULL, 1, 'M', '0', '0', NULL, 'form', 1, '2024-02-04 22:33:04', 1, '2024-02-04 22:33:04', '', '0');
INSERT INTO `sys_menu` VALUES (2061, '登录日志', 2060, 2, 'log', 'log/login/index', 1, 'C', '0', '0', 'log:login:list', 'skill', 1, '2024-02-04 22:38:16', 1, '2024-05-01 20:23:57', '', '0');
INSERT INTO `sys_menu` VALUES (2062, '操作日志', 2060, 2, 'operation', 'log/operation/index', 1, 'C', '1', '0', 'log:operation:list', 'button', 1, '2024-02-04 22:40:00', 1, '2024-04-30 21:04:16', '', '0');
INSERT INTO `sys_menu` VALUES (2063, '访客日志', 2060, 2, 'visitor', 'log/visitor/index', 1, 'C', '1', '0', 'log:visitor:list', 'date-range', 1, '2024-02-04 22:40:54', 1, '2024-04-30 21:04:21', '', '0');
INSERT INTO `sys_menu` VALUES (2064, '敏感词管理', 1, 4, 'sensitive', 'system/sensitive/index', 1, 'C', '0', '0', 'system:sensitive:list', 'eye', 1, '2024-04-30 21:00:50', 1, '2024-05-01 09:41:15', '', '0');
INSERT INTO `sys_menu` VALUES (2065, '敏感词查询', 2064, 1, '', NULL, 1, 'F', '0', '0', 'system:sensitive:query', '#', 1, '2024-05-01 09:33:03', 1, '2024-05-01 09:56:17', '', '0');
INSERT INTO `sys_menu` VALUES (2066, '新增敏感词', 2064, 2, '', NULL, 1, 'F', '0', '0', 'system:sensitive:add', '#', 1, '2024-05-01 09:49:36', 1, '2024-05-01 09:49:36', '', '0');
INSERT INTO `sys_menu` VALUES (2067, '修改敏感词', 2064, 3, '', NULL, 1, 'F', '0', '0', 'system:sensitive:edit', '#', 1, '2024-05-01 09:49:56', 1, '2024-05-01 09:49:56', '', '0');
INSERT INTO `sys_menu` VALUES (2068, '删除敏感词', 2064, 4, '', NULL, 1, 'F', '0', '0', 'system:sensitive:remove', '#', 1, '2024-05-01 09:50:12', 1, '2024-05-01 09:50:12', '', '0');
INSERT INTO `sys_menu` VALUES (2069, '查看文章', 2019, 1, '', NULL, 1, 'F', '0', '0', 'content:article:query', '#', 1, '2024-05-01 15:39:32', 1, '2024-05-01 15:39:32', '', '0');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '0', '0', 0, '2021-11-12 10:46:19', 0, NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (22, '菜单管理员', 'menuManager', 1, '0', '0', 1, '2024-01-18 17:19:39', 1, '2024-01-19 13:48:33', '执行菜单的CRUD');
INSERT INTO `sys_role` VALUES (23, '观察者', 'watcher', 1, '0', '0', 1, '2024-01-19 15:02:40', 1, '2024-05-01 15:39:45', '测试人员专用');
INSERT INTO `sys_role` VALUES (24, '测试角色2', 'test', 2, '0', '0', 1, '2024-01-22 11:03:03', 1, '2024-01-26 14:10:22', NULL);
INSERT INTO `sys_role` VALUES (25, '测试角色2', '2', 2, '0', '1', 1, '2024-01-22 11:03:14', 1, '2024-01-22 11:03:14', NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (22, 1);
INSERT INTO `sys_role_menu` VALUES (22, 101);
INSERT INTO `sys_role_menu` VALUES (22, 1008);
INSERT INTO `sys_role_menu` VALUES (22, 1012);
INSERT INTO `sys_role_menu` VALUES (23, 1);
INSERT INTO `sys_role_menu` VALUES (23, 100);
INSERT INTO `sys_role_menu` VALUES (23, 101);
INSERT INTO `sys_role_menu` VALUES (23, 102);
INSERT INTO `sys_role_menu` VALUES (23, 1001);
INSERT INTO `sys_role_menu` VALUES (23, 1003);
INSERT INTO `sys_role_menu` VALUES (23, 1005);
INSERT INTO `sys_role_menu` VALUES (23, 1006);
INSERT INTO `sys_role_menu` VALUES (23, 1008);
INSERT INTO `sys_role_menu` VALUES (23, 1010);
INSERT INTO `sys_role_menu` VALUES (23, 1012);
INSERT INTO `sys_role_menu` VALUES (23, 1013);
INSERT INTO `sys_role_menu` VALUES (23, 1015);
INSERT INTO `sys_role_menu` VALUES (23, 2017);
INSERT INTO `sys_role_menu` VALUES (23, 2018);
INSERT INTO `sys_role_menu` VALUES (23, 2019);
INSERT INTO `sys_role_menu` VALUES (23, 2021);
INSERT INTO `sys_role_menu` VALUES (23, 2022);
INSERT INTO `sys_role_menu` VALUES (23, 2023);
INSERT INTO `sys_role_menu` VALUES (23, 2025);
INSERT INTO `sys_role_menu` VALUES (23, 2027);
INSERT INTO `sys_role_menu` VALUES (23, 2028);
INSERT INTO `sys_role_menu` VALUES (23, 2053);
INSERT INTO `sys_role_menu` VALUES (23, 2054);
INSERT INTO `sys_role_menu` VALUES (23, 2057);
INSERT INTO `sys_role_menu` VALUES (23, 2058);
INSERT INTO `sys_role_menu` VALUES (23, 2060);
INSERT INTO `sys_role_menu` VALUES (23, 2061);
INSERT INTO `sys_role_menu` VALUES (23, 2064);
INSERT INTO `sys_role_menu` VALUES (23, 2065);
INSERT INTO `sys_role_menu` VALUES (23, 2067);
INSERT INTO `sys_role_menu` VALUES (23, 2069);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NULL' COMMENT '用户名',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NULL' COMMENT '昵称',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NULL' COMMENT '密码',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户类型：0代表普通用户，1代表管理员',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `github_id` int NULL DEFAULT NULL COMMENT 'GitHub用户唯一标识符',
  `phone_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户性别（0男，1女，2未知）',
  `avatar` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人的用户id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`user_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14787164048688 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'yuuu', 'yuuu', '$2a$10$Jnq31rRkNV3RNzXe0REsEOSKaYK8UgVZZqlNlNXqn.JeVcj2NdeZy', '1', '0', '23412332@qq.com', NULL, '18888888888', '0', 'http://qiniu.yuuu.online/2023/08/11/8dbfd3b51126440ca7a2b239cc628b5c.jpg', 0, '2022-01-05 09:01:56', 0, '2022-01-30 15:37:03', 0);
INSERT INTO `sys_user` VALUES (2, 'ceshi', 'ceshi', '$2a$10$Bkk4AVwFP7zrRjDRJFpsi.zRCNi0HA1OKVB4ZtpqgbASBDFsPnux.', '0', '0', '12345678@qq.com', NULL, '', '0', NULL, 1, '2024-01-18 17:23:17', 1, '2024-05-01 15:29:35', 0);
INSERT INTO `sys_user` VALUES (14787164048686, 'root', 'observer', '$2a$10$p9Rw6Q.CuMCtVgf3labZB.7aRoLhK6M5pWncSADUF6vrsMafrBg0i', '0', '0', '1234@qq.com', NULL, '13212345678', '2', 'http://qiniu.yuuu.online/2023/08/11/8dbfd3b51126440ca7a2b239cc628b5c.jpg', 1, '2024-01-22 21:31:23', 1, '2024-01-26 14:10:36', 0);
INSERT INTO `sys_user` VALUES (14787164048687, 'ccc', 'ccc', '$2a$10$EiPkrqkVBf6E9XF6gLTBd.GNbTx6keCGqRLykitV90gjuMPaeHiiO', '0', '0', '13245678@qq.com', NULL, '13211112222', '0', '//img.xjh.me/img/63875284_p0.jpg', 1, '2024-01-31 13:50:34', 1, '2024-05-01 15:29:40', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 23);
INSERT INTO `sys_user_role` VALUES (14787164048684, 23);
INSERT INTO `sys_user_role` VALUES (14787164048686, 23);

-- ----------------------------
-- Table structure for yuuu_article
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_article`;
CREATE TABLE `yuuu_article`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文章内容',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `category_id` bigint NULL DEFAULT NULL COMMENT '所属分类id',
  `thumbnail` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否置顶（0否，1是）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '状态（0已发布，1草稿）',
  `view_count` bigint NULL DEFAULT 0 COMMENT '访问量',
  `is_comment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否允许评论 1是，0否',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  `likes` bigint NULL DEFAULT NULL COMMENT '点赞量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_article
-- ----------------------------
INSERT INTO `yuuu_article` VALUES (1, 'SpringSecurity从入门到精通', '## 课程介绍\n![image20211219121555979.png](https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/31/e7131718e9e64faeaf3fe16404186eb4.png)\n\n## 0. 简介1\n\n​	**Spring Security** 是 Spring 家族中的一个安全管理框架。相比与另外一个安全框架**Shiro**，它提供了更丰富的功能，社区资源也比Shiro丰富。\n\n​	一般来说中大型的项目都是使用**SpringSecurity** 来做安全框架。小项目有Shiro的比较多，因为相比与SpringSecurity，Shiro的上手更加的简单。\n\n​	 一般Web应用的需要进行**认证**和**授权**。\n\n​		**认证：验证当前访问系统的是不是本系统的用户，并且要确认具体是哪个用户**\n\n​		**授权：经过认证后判断当前用户是否有权限进行某个操作**\n\n​	而认证和授权也是SpringSecurity作为安全框架的核心功能。\n\n\n\n## 1. 快速入门\n\n### 1.1 准备工作\n\n​	我们先要搭建一个简单的SpringBoot工程\n\n① 设置父工程 添加依赖\n\n~~~~\n    <parent>\n        <groupId>org.springframework.boot</groupId>\n        <artifactId>spring-boot-starter-parent</artifactId>\n        <version>2.5.0</version>\n    </parent>\n    <dependencies>\n        <dependency>\n            <groupId>org.springframework.boot</groupId>\n            <artifactId>spring-boot-starter-web</artifactId>\n        </dependency>\n        <dependency>\n            <groupId>org.projectlombok</groupId>\n            <artifactId>lombok</artifactId>\n            <optional>true</optional>\n        </dependency>\n    </dependencies>\n~~~~\n\n② 创建启动类\n\n~~~~\n@SpringBootApplication\npublic class SecurityApplication {\n\n    public static void main(String[] args) {\n        SpringApplication.run(SecurityApplication.class,args);\n    }\n}\n\n~~~~\n\n③ 创建Controller\n\n~~~~java\n\nimport org.springframework.web.bind.annotation.RequestMapping;\nimport org.springframework.web.bind.annotation.RestController;\n\n@RestController\npublic class HelloController {\n\n    @RequestMapping(\"/hello\")\n    public String hello(){\n        return \"hello\";\n    }\n}\n\n~~~~\n\n\n\n### 1.2 引入SpringSecurity\n\n​	在SpringBoot项目中使用SpringSecurity我们只需要引入依赖即可实现入门案例。\n\n~~~~xml\n        <dependency>\n            <groupId>org.springframework.boot</groupId>\n            <artifactId>spring-boot-starter-security</artifactId>\n        </dependency>\n~~~~\n\n​	引入依赖后我们在尝试去访问之前的接口就会自动跳转到一个SpringSecurity的默认登陆页面，默认用户名是user,密码会输出在控制台。\n\n​	必须登陆之后才能对接口进行访问。\n\n\n\n## 2. 认证\n\n### 2.1 登陆校验流程\n![image20211215094003288.png](https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/31/414a87eeed344828b5b00ffa80178958.png)', 'SpringSecurity框架教程-Spring Security+JWT实现项目级前端分离认证授权', 2, 'https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/31/948597e164614902ab1662ba8452e106.png', '1', '0', 171, '0', NULL, '2022-01-23 23:20:11', NULL, '2023-12-19 16:17:33', 0, 2);
INSERT INTO `yuuu_article` VALUES (2, 'weq', 'adadaeqe', 'adad', 2, 'https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/15/fd2e9460c58a4af3bbeae5d9ed581688.png', '1', '0', 24, '0', NULL, '2022-01-21 14:58:30', NULL, NULL, 0, NULL);
INSERT INTO `yuuu_article` VALUES (3, 'dad', 'asdasda', 'sadad', 1, 'https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/15/737a0ed0b8ea430d8700a12e76aa1cd1.png', '1', '0', 34, '0', NULL, '2022-01-18 14:58:34', NULL, NULL, 0, NULL);
INSERT INTO `yuuu_article` VALUES (5, 'sdad', '![Snipaste_20220115_165812.png](https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/15/1d9d283f5d874b468078b183e4b98b71.png)\r\n\r\n## sda \r\n\r\n222\r\n### sdasd newnewnew', NULL, 2, '', '1', '0', 46, '0', NULL, '2022-01-17 14:58:37', NULL, NULL, 0, NULL);
INSERT INTO `yuuu_article` VALUES (8, '111', '11111![微信图片_20211208162758.jpg](http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/fec2e9b4cf294cd7bd9d501e924ad9ff.jpg)', '111', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/3e7ad08fc40d4da39a46ac412566b0af.png', '1', '0', 0, '0', 1, '2023-03-16 13:37:35', 1, '2023-03-16 13:37:35', 0, NULL);
INSERT INTO `yuuu_article` VALUES (9, '1234', '111', '1234', 1, '', '1', '0', 0, '0', 1, '2023-03-17 05:10:00', 1, '2023-03-17 05:10:00', 0, NULL);
INSERT INTO `yuuu_article` VALUES (10, '12345', '111', '12345', 1, '', '0', '0', 0, '1', 1, '2023-03-17 05:11:14', 1, '2023-03-17 05:11:14', 0, NULL);
INSERT INTO `yuuu_article` VALUES (11, '11111', '11111![微信图片_20211208162758.jpg](http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/fec2e9b4cf294cd7bd9d501e924ad9ff.jpg)', '11111', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/3e7ad08fc40d4da39a46ac412566b0af.png', '1', '0', 0, '0', 1, '2023-03-19 10:39:36', 1, '2023-03-19 10:39:36', 0, NULL);
INSERT INTO `yuuu_article` VALUES (13, '1112', '11111![微信图片_20211208162758.jpg](http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/fec2e9b4cf294cd7bd9d501e924ad9ff.jpg)', '1112', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/3e7ad08fc40d4da39a46ac412566b0af.png', '1', '0', 1, '0', 1, '2023-03-19 11:13:50', 1, '2023-03-19 11:13:50', 1, NULL);
INSERT INTO `yuuu_article` VALUES (14, '11', '11111![微信图片_20211208162758.jpg](http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/fec2e9b4cf294cd7bd9d501e924ad9ff.jpg)', '1', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/3e7ad08fc40d4da39a46ac412566b0af.png', '1', '0', 0, '0', 1, '2023-03-19 11:16:01', 1, '2023-03-19 11:16:01', 1, NULL);
INSERT INTO `yuuu_article` VALUES (15, '14', '11111![微信图片_20211208162758.jpg](http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/fec2e9b4cf294cd7bd9d501e924ad9ff.jpg)', '14', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/3e7ad08fc40d4da39a46ac412566b0af.png', '1', '0', 0, '0', 1, '2023-03-19 11:18:57', 1, '2023-03-19 11:18:57', 0, NULL);
INSERT INTO `yuuu_article` VALUES (16, '16', '11111![微信图片_20211208162758.jpg](http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/fec2e9b4cf294cd7bd9d501e924ad9ff.jpg)', '16', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/03/16/3e7ad08fc40d4da39a46ac412566b0af.png', '1', '0', 0, '0', 1, '2023-03-19 11:21:42', 1, '2023-03-19 11:26:02', 1, NULL);
INSERT INTO `yuuu_article` VALUES (17, '最后测评', '测试111', '测试成功', 1, '', '0', '0', 0, '0', 1, '2023-03-19 11:49:55', 1, '2023-03-19 11:51:16', 0, NULL);
INSERT INTO `yuuu_article` VALUES (18, '新建文章测试', '# 1 ', NULL, 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/05/31/7b4fd82aeba64a75904e1ede0a41aa22.jpg', '1', '1', 0, '0', 1, '2023-05-31 17:22:39', 1, '2023-05-31 21:02:37', 0, NULL);
INSERT INTO `yuuu_article` VALUES (19, '2', '', NULL, 2, '', '1', '1', 0, '0', 1, '2023-05-31 17:23:34', 1, '2023-05-31 17:23:34', 0, NULL);
INSERT INTO `yuuu_article` VALUES (20, '测试保存草稿箱', '', NULL, 2, '', '1', '1', 0, '0', 1, '2023-05-31 17:24:57', 1, '2023-05-31 17:24:57', 0, NULL);
INSERT INTO `yuuu_article` VALUES (21, '21点01分', '# 2023', '2023', 1, '', '1', '0', 3, '0', 1, '2023-05-31 21:02:12', 1, '2023-05-31 21:02:12', 0, NULL);
INSERT INTO `yuuu_article` VALUES (22, '111', '### 字号一\n#### 字号二', '111', 1, 'http://rr2wi3rfl.bkt.clouddn.com/2023/06/07/0311b83b8d94430cad6bf14d769195d3.jpg', '0', '0', 5, '0', 1, '2023-06-07 17:54:01', 1, '2023-06-07 17:54:01', 0, NULL);

-- ----------------------------
-- Table structure for yuuu_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_article_tag`;
CREATE TABLE `yuuu_article_tag`  (
  `article_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `tag_id` bigint NOT NULL DEFAULT 0 COMMENT '标签id',
  PRIMARY KEY (`article_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_article_tag
-- ----------------------------
INSERT INTO `yuuu_article_tag` VALUES (1, 4);
INSERT INTO `yuuu_article_tag` VALUES (2, 1);
INSERT INTO `yuuu_article_tag` VALUES (2, 4);
INSERT INTO `yuuu_article_tag` VALUES (3, 4);
INSERT INTO `yuuu_article_tag` VALUES (3, 5);
INSERT INTO `yuuu_article_tag` VALUES (4, 4);
INSERT INTO `yuuu_article_tag` VALUES (5, 14);
INSERT INTO `yuuu_article_tag` VALUES (6, 4);
INSERT INTO `yuuu_article_tag` VALUES (7, 6);
INSERT INTO `yuuu_article_tag` VALUES (8, 4);
INSERT INTO `yuuu_article_tag` VALUES (9, 1);
INSERT INTO `yuuu_article_tag` VALUES (10, 5);
INSERT INTO `yuuu_article_tag` VALUES (11, 6);
INSERT INTO `yuuu_article_tag` VALUES (12, 4);
INSERT INTO `yuuu_article_tag` VALUES (13, 1);
INSERT INTO `yuuu_article_tag` VALUES (14, 4);
INSERT INTO `yuuu_article_tag` VALUES (18, 4);
INSERT INTO `yuuu_article_tag` VALUES (19, 4);
INSERT INTO `yuuu_article_tag` VALUES (20, 4);

-- ----------------------------
-- Table structure for yuuu_category
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_category`;
CREATE TABLE `yuuu_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名',
  `pid` bigint NULL DEFAULT -1 COMMENT '父分类id，如果没有父分类为-1',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态0:正常,1禁用',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_category
-- ----------------------------
INSERT INTO `yuuu_category` VALUES (1, 'java', -1, 'wsd', '0', NULL, NULL, NULL, NULL, 0);
INSERT INTO `yuuu_category` VALUES (2, 'PHP', -1, 'wsd', '0', NULL, NULL, 1, '2024-05-01 09:46:20', 0);
INSERT INTO `yuuu_category` VALUES (15, 'Spring', -1, 'Spring全家桶', '0', NULL, NULL, 1, '2024-05-01 09:30:09', 0);

-- ----------------------------
-- Table structure for yuuu_comment
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_comment`;
CREATE TABLE `yuuu_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '评论类型（0代表文章评论，1代表友链评论）',
  `article_id` bigint NULL DEFAULT NULL COMMENT '文章id',
  `root_id` bigint NULL DEFAULT -1 COMMENT '根评论id',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论内容',
  `to_comment_user_id` bigint NULL DEFAULT -1 COMMENT '所回复的目标评论的userid',
  `to_comment_id` bigint NULL DEFAULT -1 COMMENT '回复目标评论id',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  `likes` bigint NULL DEFAULT NULL COMMENT '点赞量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_comment
-- ----------------------------
INSERT INTO `yuuu_comment` VALUES (2, '0', 1, -1, '[哈哈]SDAS', -1, -1, 1, '2022-01-29 08:01:24', 1, '2024-01-26 16:03:28', 0, 1);
INSERT INTO `yuuu_comment` VALUES (3, '0', 1, -1, '是大多数', -1, -1, 1, '2022-01-29 16:07:24', 1, '2023-08-21 17:10:37', 0, 0);
INSERT INTO `yuuu_comment` VALUES (4, '0', 1, -1, '撒大声地', -1, -1, 1, '2022-01-29 16:12:09', 1, '2023-08-21 16:32:34', 0, 0);
INSERT INTO `yuuu_comment` VALUES (5, '0', 1, -1, '你再说什么', -1, -1, 1, '2022-01-29 18:19:56', 1, '2023-08-21 10:27:13', 0, 0);
INSERT INTO `yuuu_comment` VALUES (6, '0', 1, -1, 'hffd', -1, -1, 1, '2022-01-29 22:13:52', 1, '2023-08-21 16:50:30', 0, 0);
INSERT INTO `yuuu_comment` VALUES (9, '0', 1, 2, '你说什么', 1, 2, 1, '2022-01-29 22:18:40', 1, '2023-08-21 16:32:51', 0, 0);
INSERT INTO `yuuu_comment` VALUES (10, '0', 1, 2, '哈哈哈哈[哈哈]', 1, 9, 1, '2022-01-29 22:29:15', 1, '2023-08-21 11:35:21', 0, 0);
INSERT INTO `yuuu_comment` VALUES (11, '0', 1, 2, 'we全文', 1, 10, 3, '2022-01-29 22:29:55', 1, '2023-08-21 11:48:06', 0, 0);
INSERT INTO `yuuu_comment` VALUES (12, '0', 1, -1, '王企鹅', -1, -1, 1, '2022-01-29 22:30:20', 1, '2022-01-29 22:30:20', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (13, '0', 1, -1, '什么阿是', -1, -1, 1, '2022-01-29 22:30:56', 1, '2022-01-29 22:30:56', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (14, '0', 1, -1, '新平顶山', -1, -1, 1, '2022-01-29 22:32:51', 1, '2022-01-29 22:32:51', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (15, '0', 1, -1, '2222', -1, -1, 1, '2022-01-29 22:34:38', 1, '2022-01-29 22:34:38', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (16, '0', 1, 2, '3333', 1, 11, 1, '2022-01-29 22:34:47', 1, '2023-08-21 17:06:26', 0, 0);
INSERT INTO `yuuu_comment` VALUES (17, '0', 1, 2, '回复weqedadsd', 3, 11, 1, '2022-01-29 22:38:00', 1, '2023-08-21 11:48:26', 0, 0);
INSERT INTO `yuuu_comment` VALUES (18, '0', 1, -1, 'sdasd', -1, -1, 1, '2022-01-29 23:18:19', 1, '2022-01-29 23:18:19', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (19, '0', 1, -1, '111', -1, -1, 1, '2022-01-29 23:22:23', 1, '2022-01-29 23:22:23', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (20, '0', 1, 1, '你说啥？', 1, 1, 1, '2022-01-30 10:06:21', 1, '2023-08-18 11:09:52', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (21, '0', 1, -1, '友链添加个呗', -1, -1, 1, '2022-01-30 10:06:50', 1, '2022-01-30 10:06:50', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (22, '1', 1, -1, '友链评论2', -1, -1, 1, '2022-01-30 10:08:28', 1, '2022-01-30 10:08:28', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (23, '1', 1, 22, '回复友链评论3', 1, 22, 1, '2022-01-30 10:08:50', 1, '2022-01-30 10:08:50', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (24, '1', 1, -1, '友链评论4444', -1, -1, 1, '2022-01-30 10:09:03', 1, '2022-01-30 10:09:03', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (25, '1', 1, 22, '收到的', 1, 22, 1, '2022-01-30 10:13:28', 1, '2022-01-30 10:13:28', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (26, '0', 1, -1, 'sda', -1, -1, 1, '2022-01-30 10:39:05', 1, '2022-01-30 10:39:05', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (27, '0', 1, 1, '说你咋地', 1, 20, 14787164048662, '2022-01-30 17:19:30', 14787164048662, '2023-08-18 11:09:43', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (28, '0', 1, 1, 'sdad', 1, 1, 14787164048662, '2022-01-31 11:11:20', 14787164048662, '2023-08-18 11:36:56', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (29, '0', 1, -1, '你说是的ad', -1, -1, 14787164048662, '2022-01-31 14:10:11', 14787164048662, '2022-01-31 14:10:11', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (30, '0', 1, 1, '撒大声地', 1, 1, 14787164048662, '2022-01-31 20:19:18', 14787164048662, '2023-08-18 11:36:52', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (33, '0', 1, -1, '09点44分', -1, -1, 1, '2023-03-06 09:44:50', 1, '2023-03-06 09:44:50', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (34, '1', 1, -1, '10点09分', -1, -1, 1, '2023-03-06 10:09:49', 1, '2023-03-06 10:09:49', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (35, '1', 1, 34, '111', 1, 34, 1, '2023-03-06 10:10:05', 1, '2023-03-06 10:10:05', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (36, '0', 1, -1, '15点22分', -1, -1, 14787164048663, '2023-03-07 15:22:28', 14787164048663, '2023-03-07 15:22:28', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (37, '1', 1, -1, '[右哼哼]', -1, -1, 14787164048680, '2023-07-08 09:27:11', 14787164048680, '2023-07-08 09:27:11', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (38, '0', 22, -1, '11', -1, -1, 1, '2023-08-11 10:08:23', 1, '2023-08-11 10:08:23', 0, NULL);
INSERT INTO `yuuu_comment` VALUES (39, '0', 13, -1, '你是一个**', -1, -1, 1, '2024-04-30 21:58:37', 1, '2024-04-30 21:58:37', 0, 0);
INSERT INTO `yuuu_comment` VALUES (40, '0', 13, -1, '你是一个蛋', -1, -1, 1, '2024-04-30 21:58:48', 1, '2024-04-30 21:58:48', 0, 0);

-- ----------------------------
-- Table structure for yuuu_liked
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_liked`;
CREATE TABLE `yuuu_liked`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NULL DEFAULT NULL COMMENT '文章id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `is_like_a` tinyint NULL DEFAULT NULL COMMENT '文章是否点赞，1点赞，0未点赞',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '评论id',
  `is_like_c` tinyint NULL DEFAULT NULL COMMENT '评论是否点赞，1点赞，0未点赞',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_liked
-- ----------------------------
INSERT INTO `yuuu_liked` VALUES (25, NULL, 14787164048680, 0, 2, 0);
INSERT INTO `yuuu_liked` VALUES (26, NULL, 14787164048680, 0, 16, 0);
INSERT INTO `yuuu_liked` VALUES (27, NULL, 14787164048680, 0, 3, 0);
INSERT INTO `yuuu_liked` VALUES (28, 1, 14787164048681, 1, NULL, 0);
INSERT INTO `yuuu_liked` VALUES (29, 1, 14787164048677, 1, NULL, 0);
INSERT INTO `yuuu_liked` VALUES (30, NULL, 14787164048686, 0, 2, 1);

-- ----------------------------
-- Table structure for yuuu_link
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_link`;
CREATE TABLE `yuuu_link`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `logo` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '网站地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '2' COMMENT '审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '友链' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_link
-- ----------------------------
INSERT INTO `yuuu_link` VALUES (1, 'sda', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fn1.itc.cn%2Fimg8%2Fwb%2Frecom%2F2016%2F05%2F10%2F146286696706220328.PNG&refer=http%3A%2F%2Fn1.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1646205529&t=f942665181eb9b0685db7a6f59d59975', 'sda', 'https://www.baidu.com', '0', NULL, '2022-01-13 08:25:47', NULL, '2022-01-13 08:36:14', 0);
INSERT INTO `yuuu_link` VALUES (2, 'sda', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fn1.itc.cn%2Fimg8%2Fwb%2Frecom%2F2016%2F05%2F10%2F146286696706220328.PNG&refer=http%3A%2F%2Fn1.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1646205529&t=f942665181eb9b0685db7a6f59d59975', 'dada', 'https://www.qq.com', '0', NULL, '2022-01-13 09:06:10', NULL, '2022-01-13 09:07:09', 0);
INSERT INTO `yuuu_link` VALUES (3, 'sa', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fn1.itc.cn%2Fimg8%2Fwb%2Frecom%2F2016%2F05%2F10%2F146286696706220328.PNG&refer=http%3A%2F%2Fn1.itc.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1646205529&t=f942665181eb9b0685db7a6f59d59975', 'da', 'https://www.taobao.com', '0', NULL, '2022-01-13 09:23:01', 1, '2023-03-16 13:07:18', 0);
INSERT INTO `yuuu_link` VALUES (4, '333', '11', '222', '11', '2', 1, '2023-03-16 12:42:50', 1, '2023-03-16 13:03:12', 1);
INSERT INTO `yuuu_link` VALUES (5, '222', '111', '111', '111', '1', 1, '2023-03-16 13:07:28', 1, '2023-03-16 13:07:33', 1);

-- ----------------------------
-- Table structure for yuuu_sensitive
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_sensitive`;
CREATE TABLE `yuuu_sensitive`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sensitives` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '敏感词',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '敏感词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_sensitive
-- ----------------------------
INSERT INTO `yuuu_sensitive` VALUES (1, '混蛋', '测试1', '2024-04-30 00:00:00', 1, '2024-05-01 09:39:03', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (2, '测试', '测试', '2024-05-01 00:00:00', 1, '2024-04-30 21:03:32', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (3, '1', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:45:59', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (4, '2', '2', '2024-05-02 00:00:00', 1, '2024-05-01 10:46:03', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (5, '2', '2', '2024-05-02 00:00:00', 1, '2024-05-01 10:47:24', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (6, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (7, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (8, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (9, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (10, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (11, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (12, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (13, 'cs', '1', '2024-05-01 00:00:00', 1, '2024-05-01 10:47:51', 1, 0);
INSERT INTO `yuuu_sensitive` VALUES (14, '1', '2', '2024-05-01 00:00:00', 1, '2024-05-01 10:50:55', 1, 0);

-- ----------------------------
-- Table structure for yuuu_tag
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_tag`;
CREATE TABLE `yuuu_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签名',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of yuuu_tag
-- ----------------------------
INSERT INTO `yuuu_tag` VALUES (1, 'Mybatis', NULL, NULL, NULL, '2022-01-11 09:20:50', 0, 'weqwe');
INSERT INTO `yuuu_tag` VALUES (2, 'asdas', NULL, '2022-01-11 09:20:55', NULL, '2022-01-11 09:20:55', 0, 'weqw');
INSERT INTO `yuuu_tag` VALUES (3, 'weqw', NULL, '2022-01-11 09:21:07', NULL, '2022-01-11 09:21:07', 0, 'qweqwe');
INSERT INTO `yuuu_tag` VALUES (4, 'Java', NULL, '2022-01-13 15:22:43', NULL, '2022-01-13 15:22:43', 0, 'sdad');
INSERT INTO `yuuu_tag` VALUES (5, 'WAD', NULL, '2022-01-13 15:22:47', NULL, '2022-01-13 15:22:47', 0, 'ASDAD');
INSERT INTO `yuuu_tag` VALUES (6, 'CCC', 6, '2023-03-15 14:16:13', 1, '2023-03-16 05:49:07', 0, 'CCC');
INSERT INTO `yuuu_tag` VALUES (9, NULL, 1, '2023-03-16 01:17:11', NULL, '2023-03-16 01:17:11', 0, NULL);
INSERT INTO `yuuu_tag` VALUES (10, NULL, 1, '2023-03-16 01:21:58', NULL, '2023-03-16 01:21:58', 0, NULL);
INSERT INTO `yuuu_tag` VALUES (11, 'c', 6, '2023-03-16 03:01:05', 1, '2023-03-16 12:44:15', 1, 'c');
INSERT INTO `yuuu_tag` VALUES (12, NULL, 1, '2023-03-16 03:03:32', NULL, '2023-03-16 03:03:32', 1, NULL);
INSERT INTO `yuuu_tag` VALUES (13, NULL, 1, '2023-03-16 03:07:30', NULL, '2023-03-16 03:07:30', 1, NULL);
INSERT INTO `yuuu_tag` VALUES (14, '1111', 1, '2023-03-16 03:08:18', NULL, '2023-03-16 03:08:18', 1, '1111');
INSERT INTO `yuuu_tag` VALUES (15, 'C#', 1, '2023-03-16 04:50:44', NULL, '2023-03-16 04:50:44', 0, 'C#');
INSERT INTO `yuuu_tag` VALUES (16, 'C##', 1, '2023-03-16 04:51:41', NULL, '2023-03-16 04:51:41', 0, 'C##');
INSERT INTO `yuuu_tag` VALUES (17, 'cs1', 1, '2023-03-16 05:50:09', 1, '2023-03-16 05:50:18', 0, '测试');
INSERT INTO `yuuu_tag` VALUES (18, '12', 1, '2023-03-16 05:50:50', 1, '2023-03-17 04:16:15', 0, '1');
INSERT INTO `yuuu_tag` VALUES (19, '2', 1, '2023-03-16 05:50:54', NULL, '2023-03-16 05:50:54', 0, '2');
INSERT INTO `yuuu_tag` VALUES (20, '3', 1, '2023-03-16 05:50:59', NULL, '2023-03-16 05:50:59', 0, '3');
INSERT INTO `yuuu_tag` VALUES (21, '1234', 1, '2023-03-17 00:56:03', NULL, '2023-03-17 00:56:03', 0, '1234');

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 关于我（单条配置，站点只维护 id=1 一条记录）
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_about`;
CREATE TABLE `yuuu_about`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '称呼/昵称',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '一句话头衔，如：后端工程师 / 摄影爱好者',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `cover` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '关于我页面顶部封面图',
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '签名/座右铭',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '个人介绍正文（Markdown）',
  `skills` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '技能标签，逗号分隔，支持「名称:熟练度」写法',
  `location` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '所在城市',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '邮箱',
  `github` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'GitHub 主页',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '个人网站',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关于我' ROW_FORMAT = Dynamic;

INSERT INTO `yuuu_about` (`id`, `name`, `title`, `avatar`, `cover`, `summary`, `content`, `skills`, `location`, `email`, `github`, `website`, `status`, `create_time`, `update_time`, `del_flag`)
VALUES (1, 'yuuu', '一直在路上', '', '', '生命的意义在于不顺从', '', '', '', '', '', '', '0', NOW(), NOW(), 0);

-- ----------------------------
-- 邮件订阅（新文章发布后推送给订阅者）
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_subscribe`;
CREATE TABLE `yuuu_subscribe`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订阅邮箱',
  `token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退订令牌',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0订阅中 1已退订）',
  `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'web' COMMENT '来源',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subscribe_email`(`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮件订阅' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 文章收藏（稍后读）
-- ----------------------------
DROP TABLE IF EXISTS `yuuu_favorite`;
CREATE TABLE `yuuu_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户id',
  `article_id` bigint NOT NULL DEFAULT 0 COMMENT '文章id',
  `create_time` datetime NULL DEFAULT NULL,
  `del_flag` int NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_favorite_user_article`(`user_id`, `article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章收藏' ROW_FORMAT = Dynamic;

-- 后台菜单：关于（顶层，所有角色可见）
INSERT INTO `sys_menu` VALUES (2083, '关于', 0, 9, 'about', 'about/index', 1, 'C', '0', '0', '', 'documentation', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_role_menu` (role_id, menu_id) SELECT r.id, 2083 FROM `sys_role` r
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` rm WHERE rm.role_id = r.id AND rm.menu_id = 2083);

-- 评论审核状态字段：0待审核 1通过 2屏蔽（已有评论默认通过）
ALTER TABLE `yuuu_comment` ADD COLUMN `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '状态（0待审核 1通过 2屏蔽）' AFTER `type`;
UPDATE `yuuu_comment` SET `status` = '1' WHERE `status` IS NULL;

-- 后台菜单：评论管理（父级为「内容管理」2017）
INSERT INTO `sys_menu` VALUES (2076, '评论管理', 2017, 7, 'comment', 'content/comment/index', 1, 'C', '0', '0', 'content:comment:list', 'comment', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2077, '评论查询', 2076, 1, '', NULL, 1, 'F', '0', '0', 'content:comment:query', '#', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2078, '评论审核', 2076, 2, '', NULL, 1, 'F', '0', '0', 'content:comment:audit', '#', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2079, '评论删除', 2076, 3, '', NULL, 1, 'F', '0', '0', 'content:comment:remove', '#', 1, NOW(), 1, NOW(), '', '0');

-- 后台菜单：邮件订阅（父级为「内容管理」2017）
INSERT INTO `sys_menu` VALUES (2073, '邮件订阅', 2017, 6, 'subscribe', 'content/subscribe/index', 1, 'C', '0', '0', 'content:subscribe:list', 'message', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2074, '邮件订阅查询', 2073, 1, '', NULL, 1, 'F', '0', '0', 'content:subscribe:query', '#', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2075, '邮件订阅删除', 2073, 2, '', NULL, 1, 'F', '0', '0', 'content:subscribe:remove', '#', 1, NOW(), 1, NOW(), '', '0');

-- 后台菜单：关于我（父级为「内容管理」2017）
INSERT INTO `sys_menu` VALUES (2070, '关于我', 2017, 5, 'about', 'content/about/index', 1, 'C', '0', '0', 'content:about:list', 'user', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2071, '关于我查询', 2070, 1, '', NULL, 1, 'F', '0', '0', 'content:about:query', '#', 1, NOW(), 1, NOW(), '', '0');
INSERT INTO `sys_menu` VALUES (2072, '关于我修改', 2070, 2, '', NULL, 1, 'F', '0', '0', 'content:about:edit', '#', 1, NOW(), 1, NOW(), '', '0');
