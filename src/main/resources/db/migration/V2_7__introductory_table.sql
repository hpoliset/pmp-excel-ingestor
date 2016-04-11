CREATE TABLE `introductory_details` (
  `introduction_id` int(4) unsigned NOT NULL AUTO_INCREMENT,
  `id` int(4) unsigned NOT NULL,
  `required_introduction` varchar(1) DEFAULT 'N',
  `request_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message` varchar(100) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`introduction_id`),
  KEY `user_foriegn_key` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
