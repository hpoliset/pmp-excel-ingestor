ALTER TABLE `user` ADD `is_pmp_allowed` varchar(4) DEFAULT 'N';
ALTER TABLE `user` ADD `is_sahajmarg_allowed` varchar(4) DEFAULT 'N';
ALTER TABLE `user` ADD `role` varchar(25) DEFAULT 'ROLE_SEEKER';
ALTER TABLE `user` ADD `abyasi_id` int(25) DEFAULT 0;

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
