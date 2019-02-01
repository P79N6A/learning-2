CREATE USER 'web'@'%' IDENTIFIED BY 'web';
GRANT ALL PRIVILEGES ON web.* to web@'%' Identified by 'web';
GRANT ALL PRIVILEGES ON web.* to web@'localhost' Identified by 'web';
FLUSH PRIVILEGES;

DROP TABLE IF EXISTS `web`.`users`;
CREATE TABLE `web`.`users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
