CREATE TABLE `coordinator_statistics` (
 `id` int(4) unsigned NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `old_coordinator_email` varchar(100) DEFAULT NULL,
  `new_coordinator_email` varchar(100) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `program_foriegn_key` (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 