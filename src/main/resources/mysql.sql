/*
Navicat MySQL Data Transfer

Source Server         : 10.15.46.16
Source Server Version : 50173
Source Host           : 10.15.46.16:3306
Source Database       : maintain

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2016-08-11 10:27:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` varchar(11) NOT NULL DEFAULT '',
  `label` varchar(200) DEFAULT NULL,
  `url` varchar(4000) DEFAULT NULL,
  `pid` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES ('0', '任务列表', 'check/taskList.jsp', null);
INSERT INTO `menu` VALUES ('1', '主键查询', 'check/rowkeyQuery.jsp', '');
INSERT INTO `menu` VALUES ('1.1', '主键查询', 'check/rowkeyQuery.jsp', '1');
INSERT INTO `menu` VALUES ('1.2', '查询前缀生成', 'check/rowkeyBuild.jsp', '1');
INSERT INTO `menu` VALUES ('10', '数据导出', 'check/rowkeyOutput.jsp', null);
INSERT INTO `menu` VALUES ('10.1', '主键导出', 'check/rowkeyOutput.jsp', '10');
INSERT INTO `menu` VALUES ('10.2', '主键导出(批量)', 'check/rowkeyBatchOutput.jsp', '10');
INSERT INTO `menu` VALUES ('2', '统计分析', 'check/sql.jsp', '');
INSERT INTO `menu` VALUES ('3', '详单条数稽核', 'check/tableCount.jsp', null);
INSERT INTO `menu` VALUES ('4', '一致性稽核', 'check/consistencyCheck.jsp', null);
INSERT INTO `menu` VALUES ('5', '数据修改', 'check/updateRows.jsp', null);
INSERT INTO `menu` VALUES ('6', '非主键任务列表', 'check/norowkeyTask.jsp', '');
INSERT INTO `menu` VALUES ('8', '非主键统计语句提交', 'check/norowkeySql.jsp', null);
INSERT INTO `menu` VALUES ('9', '可查询表空间和表', 'tableschem', null);

-- ----------------------------
-- Table structure for qrytask
-- ----------------------------
DROP TABLE IF EXISTS `qrytask`;
CREATE TABLE `qrytask` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) DEFAULT NULL COMMENT '任务状态(0:进行中 1:已完成)',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT '更新时间',
  `timeDiff` varchar(100) DEFAULT NULL COMMENT '相差时长(秒)',
  `totalCount` varchar(20) DEFAULT NULL COMMENT '总行数',
  `tempTable` varchar(50) DEFAULT NULL COMMENT '临时表名',
  `querySql` varchar(2000) DEFAULT NULL COMMENT '查询语句',
  `cloumnsSql` varchar(2000) DEFAULT NULL COMMENT '列名',
  `filePath`  varchar(500) DEFAULT NULL COMMENT '数据文件路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of qrytask
-- ----------------------------

-- ----------------------------
-- Table structure for subtask_status
-- ----------------------------
DROP TABLE IF EXISTS `subtask_status`;
CREATE TABLE `subtask_status` (
  `id` varchar(36) NOT NULL,
  `phone_num` varchar(20) DEFAULT NULL,
  `finish_flag` varchar(11) DEFAULT NULL,
  `pid` varchar(36) NOT NULL,
  `dst_path` varchar(200) DEFAULT NULL,
  `sub_total_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of subtask_status
-- ----------------------------

-- ----------------------------
-- Table structure for task_status
-- ----------------------------
DROP TABLE IF EXISTS `task_status`;
CREATE TABLE `task_status` (
  `id` varchar(36) NOT NULL,
  `task_type` varchar(11) DEFAULT NULL COMMENT '0--rowkeybatch query',
  `sql_str` varchar(1000) DEFAULT NULL,
  `finish_flag` varchar(11) DEFAULT NULL COMMENT '0--not finish 1--finished',
  `dst_path` varchar(400) DEFAULT NULL,
  `columns_str` varchar(4000) NOT NULL,
  `total_count` int(11) DEFAULT '0',
  `create_time` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of task_status
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  `passwd` varchar(100) DEFAULT NULL,
  `is_enable` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'admin', '1');
