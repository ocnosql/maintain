-- ----------------------------
-- Table structure for create_hiveTable
-- ----------------------------
DROP TABLE IF EXISTS `create_hiveTable`;
CREATE TABLE `create_hiveTable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hiveTableName` varchar(100) DEFAULT NULL COMMENT 'hive表名',
  `hbaseTableName` varchar(100) DEFAULT NULL COMMENT 'hbase表名',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(2) DEFAULT NULL COMMENT '状态(0：失败 1：成功)',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for deleteLog
-- ----------------------------
DROP TABLE IF EXISTS `deleteLog`;
CREATE TABLE `deleteLog` (
  `id` varchar(20) DEFAULT NULL,
  `schemax` varchar(100) DEFAULT NULL,
  `tablex` varchar(100) DEFAULT NULL,
  `sqlx` varchar(1000) DEFAULT NULL,
  `dstatus` varchar(20) DEFAULT NULL,
  `dtotal` varchar(20) DEFAULT NULL,
  `qstatus` varchar(20) DEFAULT NULL,
  `qtotal` varchar(20) DEFAULT NULL,
  `ddate` varchar(20) DEFAULT NULL,
  `qdate` varchar(20) DEFAULT NULL,
  `bup` varchar(500) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for importconfig
-- ----------------------------
DROP TABLE IF EXISTS `importconfig`;
CREATE TABLE `importconfig` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cname` varchar(1000) DEFAULT NULL,
  `separatorx` varchar(100) DEFAULT NULL,
  `loadType` varchar(100) DEFAULT NULL,
  `inputPath` varchar(1000) DEFAULT NULL,
  `rowkey` varchar(100) DEFAULT NULL,
  `generator` varchar(100) DEFAULT NULL,
  `outputPath` varchar(1000) DEFAULT NULL,
  `algocolumn` varchar(100) DEFAULT NULL,
  `schemax` varchar(100) DEFAULT NULL,
  `tablex` varchar(100) DEFAULT NULL,
  `callback` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for importLog
-- ----------------------------
DROP TABLE IF EXISTS `importLog`;
CREATE TABLE `importLog` (
  `id` varchar(18) NOT NULL,
  `tablex` varchar(100) DEFAULT NULL,
  `schemax` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `importdate` varchar(255) DEFAULT NULL,
  `input` varchar(500) DEFAULT NULL,
  `output` varchar(500) DEFAULT NULL,
  `total` varchar(20) DEFAULT NULL,
  `success_total` varchar(20) DEFAULT NULL,
  `fail_total` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


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
INSERT INTO `menu` VALUES ('1', '主键查询', 'check/rowkeyQuery.jsp', '');
INSERT INTO `menu` VALUES ('1.1', '主键查询', 'check/rowkeyQuery.jsp', '1');
INSERT INTO `menu` VALUES ('1.2', '查询前缀生成', 'check/rowkeyBuild.jsp', '1');
INSERT INTO `menu` VALUES ('10', '数据导出', 'check/rowkeyOutput.jsp', null);
INSERT INTO `menu` VALUES ('10.1', '主键导出', 'check/rowkeyOutput.jsp', '10');
INSERT INTO `menu` VALUES ('10.2', '主键导出(批量)', 'check/rowkeyBatchOutput.jsp', '10');
INSERT INTO `menu` VALUES ('3', '数据删除', 'check/deleteRows.jsp', null);
INSERT INTO `menu` VALUES ('3.1', '数据删除', 'check/deleteRows.jsp', '3');
INSERT INTO `menu` VALUES ('3.2', '删除历史查询', 'check/deleteHistory.jsp', '3');
INSERT INTO `menu` VALUES ('4', '批量导入', null, null);
INSERT INTO `menu` VALUES ('4.1', '新建表', 'check/createHiveTable2.jsp', '4');
INSERT INTO `menu` VALUES ('4.2', '导入配置', 'check/importConfig.jsp', '4');
INSERT INTO `menu` VALUES ('4.3', '数据导入', 'check/importData.jsp', '4');
INSERT INTO `menu` VALUES ('6', '非主键任务列表', 'check/norowkeyTask.jsp', null);
INSERT INTO `menu` VALUES ('6.1', '非主键统计语句提交', 'check/norowkeySql.jsp', '6');
INSERT INTO `menu` VALUES ('6.2', '非主键任务列表', 'check/norowkeyTask.jsp', '6');
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
  `filePath` varchar(500) DEFAULT NULL COMMENT '数据文件路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

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