-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: maintain
-- ------------------------------------------------------
-- Server version	5.1.73

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `deleteLog`
--

DROP TABLE IF EXISTS `deleteLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deleteLog` (
  `id` varchar(20) DEFAULT NULL,
  `schemax` varchar(100) DEFAULT NULL,
  `tablex` varchar(100) DEFAULT NULL,
  `datex` varchar(20) DEFAULT NULL,
  `sqlx` varchar(1000) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `total` varchar(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `importLog`
--

DROP TABLE IF EXISTS `importLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `importLog` (
  `id` varchar(18) NOT NULL,
  `tablex` varchar(100) DEFAULT NULL,
  `schemax` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `importdate` varchar(255) DEFAULT NULL,
  `input` varchar(500) DEFAULT NULL,
  `output` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `importconfig`
--

DROP TABLE IF EXISTS `importconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu` (
  `id` varchar(11) NOT NULL DEFAULT '',
  `label` varchar(200) DEFAULT NULL,
  `url` varchar(4000) DEFAULT NULL,
  `pid` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `qrytask`
--

DROP TABLE IF EXISTS `qrytask`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subtask_status`
--

DROP TABLE IF EXISTS `subtask_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subtask_status` (
  `id` varchar(36) NOT NULL,
  `phone_num` varchar(20) DEFAULT NULL,
  `finish_flag` varchar(11) DEFAULT NULL,
  `pid` varchar(36) NOT NULL,
  `dst_path` varchar(200) DEFAULT NULL,
  `sub_total_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`,`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_status`
--

DROP TABLE IF EXISTS `task_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  `passwd` varchar(100) DEFAULT NULL,
  `is_enable` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-15  9:50:41
