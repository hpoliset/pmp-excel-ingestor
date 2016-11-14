CREATE TABLE `program_coordinators` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `coordinator_name` varchar(255) DEFAULT NULL,
  `coordinator_email` varchar(255) NOT NULL,
  `is_primary_coordinator` tinyint(1) DEFAULT 0,
  `create_time` TIMESTAMP NOT NULL,
   `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `program_id` (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `program_coordinators`
  ADD CONSTRAINT `program_coordinators_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

CREATE TABLE IF NOT EXISTS `event_access_request` (
  `request_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `user_id` int(4) unsigned NOT NULL,
  `status` varchar(255) NOT NULL,
  `approved_by` varchar(255) DEFAULT NULL,
  `request_time` timestamp NOT NULL,
  `requested_by` varchar(255) NOT NULL,
  `approval_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `program_id` (`program_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

ALTER TABLE `event_access_request`
  ADD CONSTRAINT `event_access_request_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `event_access_request_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;