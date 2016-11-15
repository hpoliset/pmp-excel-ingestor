CREATE TABLE `program_coordinators` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `coordinator_name` varchar(255) DEFAULT NULL,
  `coordinator_email` varchar(255) NOT NULL,
  `is_primary_coordinator` tinyint(1) DEFAULT 0,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `program_id` (`program_id`),
  CONSTRAINT `program_coordinators_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `event_access_request` (
  `request_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `user_id` int(4) unsigned NOT NULL,
  `status` varchar(255) DEFAULT 'WAITING_FOR_APPROVAL' NOT NULL,
  `approved_by` varchar(255) DEFAULT NULL,
  `request_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `requested_by` varchar(255) NOT NULL,
  `approval_time` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `program_id` (`program_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `event_access_request_user_user_id_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `event_access_request_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;