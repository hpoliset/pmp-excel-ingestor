CREATE TABLE `user` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(200) DEFAULT NULL,
  `First_name` varchar(100) DEFAULT NULL,
  `Last_name` varchar(100) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `mobile` varchar(25) DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` VARCHAR(50) DEFAULT NULL,
  `country` VARCHAR(50) DEFAULT NULL,
  `user_type` varchar(10) DEFAULT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
