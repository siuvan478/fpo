/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50710
Source Host           : localhost:3306
Source Database       : fpo

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2018-02-27 17:07:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fpo_attachment
-- ----------------------------
DROP TABLE IF EXISTS `fpo_attachment`;
CREATE TABLE `fpo_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biz_id` bigint(20) NOT NULL COMMENT '业务ID',
  `biz_type` tinyint(4) NOT NULL COMMENT '业务类型 1=采购 2=报价',
  `name` varchar(255) DEFAULT NULL COMMENT '附件名',
  `suffix` varchar(20) DEFAULT NULL COMMENT '后缀',
  `path` varchar(255) DEFAULT NULL COMMENT '存放路径',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 1=正常 0=删除',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fpo_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for fpo_order_details
-- ----------------------------
DROP TABLE IF EXISTS `fpo_order_details`;
CREATE TABLE `fpo_order_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `header_id` bigint(20) NOT NULL COMMENT '主表ID',
  `number` varchar(20) DEFAULT NULL COMMENT '编号',
  `name` varchar(50) NOT NULL COMMENT '产品名称',
  `spec` varchar(50) DEFAULT NULL COMMENT '规格',
  `brands` varchar(50) DEFAULT NULL COMMENT '品牌',
  `description` varchar(500) DEFAULT NULL COMMENT '其他描述',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `unit` varchar(20) NOT NULL COMMENT '单位',
  `picture` varchar(255) DEFAULT NULL COMMENT '示意图',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0=删除 1=正常',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fpo_order_details
-- ----------------------------
INSERT INTO `fpo_order_details` VALUES ('1', '4', null, '钢化膜', null, null, null, '9999', '张', null, '0', '2018-02-26 15:47:55', '2018-02-26 15:47:55');
INSERT INTO `fpo_order_details` VALUES ('2', '4', null, '钢化膜1', null, null, null, '9999', '张', null, '0', '2018-02-26 16:03:59', '2018-02-26 16:03:59');
INSERT INTO `fpo_order_details` VALUES ('3', '4', null, '钢化膜1', null, null, null, '9999', '张', null, '1', '2018-02-26 16:05:45', '2018-02-26 16:05:45');

-- ----------------------------
-- Table structure for fpo_order_header
-- ----------------------------
DROP TABLE IF EXISTS `fpo_order_header`;
CREATE TABLE `fpo_order_header` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(50) NOT NULL COMMENT '采购单名称',
  `invoice_mode` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发票方式 0=不用发票 1=增值税普票 2=增值税专票',
  `quote_mode` varchar(20) NOT NULL COMMENT '报价要求 1=报价含税 2=报价含运费',
  `payment_mode` tinyint(4) NOT NULL COMMENT '付款方式 0=其他 1=收货后付款 2=预付款',
  `payment_remark` varchar(20) DEFAULT NULL COMMENT '付款方式其他说明',
  `receipt_date` datetime NOT NULL COMMENT '收货日期',
  `receipt_address` varchar(100) NOT NULL COMMENT '收货地址',
  `remark` varchar(3000) DEFAULT NULL COMMENT '备注',
  `company_name` varchar(100) DEFAULT NULL COMMENT '单位名称',
  `contact` varchar(20) NOT NULL COMMENT '联系人名称',
  `contact_info` varchar(50) NOT NULL COMMENT '联系方式',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '采购单状态 0=删除 1=正常',
  `user_id` bigint(20) NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='采购单-主表';

-- ----------------------------
-- Records of fpo_order_header
-- ----------------------------
INSERT INTO `fpo_order_header` VALUES ('4', 'siuvan的采购单222', '1', '1', '2', null, '2018-02-26 16:05:45', '新港东路618号', null, null, 'Siuvan', '17620021827', '1', '6', '2018-02-26 15:47:55', '2018-02-26 15:47:55');

-- ----------------------------
-- Table structure for fpo_quote_details
-- ----------------------------
DROP TABLE IF EXISTS `fpo_quote_details`;
CREATE TABLE `fpo_quote_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `header_id` bigint(20) NOT NULL COMMENT '报价单主表ID',
  `order_detail_id` bigint(20) NOT NULL COMMENT '采购单从表ID',
  `unit_price` decimal(20,2) NOT NULL COMMENT '单价',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 1=正常 0=删除',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fpo_quote_details
-- ----------------------------
INSERT INTO `fpo_quote_details` VALUES ('1', '6', '3', '0.50', '8000', '1', '2018-02-27 15:44:23', '2018-02-27 15:44:23');

-- ----------------------------
-- Table structure for fpo_quote_header
-- ----------------------------
DROP TABLE IF EXISTS `fpo_quote_header`;
CREATE TABLE `fpo_quote_header` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_header_id` bigint(20) NOT NULL COMMENT '报价单主表ID',
  `remark` varchar(200) DEFAULT NULL COMMENT '报价说明',
  `company_name` varchar(100) NOT NULL COMMENT '报价单位',
  `contact` varchar(20) NOT NULL COMMENT '联系人',
  `contact_info` varchar(50) NOT NULL COMMENT '联系方式',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 1=正常 0=删除',
  `user_id` bigint(20) NOT NULL COMMENT '报价人userId',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fpo_quote_header
-- ----------------------------
INSERT INTO `fpo_quote_header` VALUES ('1', '4', 'kahjfajklsfhajkldfas', 'first company', 'Siuvan', '17620021827', '1', '6', '2018-02-27 15:41:50', '2018-02-27 15:41:50');
INSERT INTO `fpo_quote_header` VALUES ('2', '4', 'kahjfajklsfhajkldfas', 'first company', 'Siuvan', '17620021827', '1', '6', '2018-02-27 15:41:50', '2018-02-27 15:41:50');
INSERT INTO `fpo_quote_header` VALUES ('3', '4', 'kahjfajklsfhajkldfas', 'first company', 'Siuvan', '17620021827', '1', '6', '2018-02-27 15:42:30', '2018-02-27 15:42:30');
INSERT INTO `fpo_quote_header` VALUES ('4', '4', 'kahjfajklsfhajkldfas', 'first company', 'Siuvan', '17620021827', '1', '6', '2018-02-27 15:42:30', '2018-02-27 15:42:30');
INSERT INTO `fpo_quote_header` VALUES ('6', '4', 'kahjfajklsfhajkldfas', 'first company', 'Siuvan', '17620021827', '1', '6', '2018-02-27 15:44:23', '2018-02-27 15:44:23');

-- ----------------------------
-- Table structure for fpo_template
-- ----------------------------
DROP TABLE IF EXISTS `fpo_template`;
CREATE TABLE `fpo_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL COMMENT '列名',
  `filed` varchar(50) NOT NULL COMMENT '字段名',
  `java_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据类型 1=String 2=Integer 3=Date',
  `index` int(11) NOT NULL DEFAULT '0' COMMENT '排列顺序',
  `status` smallint(4) NOT NULL DEFAULT '1' COMMENT '状态 1=正常 0=删除',
  `create_date` datetime NOT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fpo_template
-- ----------------------------
INSERT INTO `fpo_template` VALUES ('1', '编号', 'number', '1', '1', '1', '2018-02-27 09:55:23', null);
INSERT INTO `fpo_template` VALUES ('2', '产品名称', 'name', '1', '2', '1', '2018-02-27 09:55:28', null);
INSERT INTO `fpo_template` VALUES ('3', '规格', 'spec', '1', '3', '1', '2018-02-27 09:55:32', null);
INSERT INTO `fpo_template` VALUES ('4', '品牌', 'brands', '1', '4', '1', '2018-02-27 09:55:35', null);
INSERT INTO `fpo_template` VALUES ('5', '其他描述', 'description', '1', '5', '1', '2018-02-27 09:55:38', null);
INSERT INTO `fpo_template` VALUES ('6', '数量', 'quantity', '2', '6', '1', '2018-02-27 09:55:42', null);
INSERT INTO `fpo_template` VALUES ('7', '单位', 'unit', '1', '7', '1', '2018-02-27 09:55:46', null);

-- ----------------------------
-- Table structure for fpo_user
-- ----------------------------
DROP TABLE IF EXISTS `fpo_user`;
CREATE TABLE `fpo_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `username` varchar(11) NOT NULL COMMENT '用户名(即为手机号)',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `salt` varchar(50) NOT NULL COMMENT '盐值',
  `reg_time` datetime NOT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fpo_user
-- ----------------------------
INSERT INTO `fpo_user` VALUES ('6', null, '17620021827', '322fc680c4cb9508ac131196d0a507d8154a538e', '1d50ef15f0a8ab65', '2018-02-02 15:22:38');
INSERT INTO `fpo_user` VALUES ('7', null, '17620021822', '901531e9d7c034fa1c062b1acf41b8b0129c103b', '63bf3cfb5e630080', '2018-02-02 17:59:18');
