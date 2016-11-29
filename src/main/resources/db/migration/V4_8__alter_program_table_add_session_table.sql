ALTER TABLE `program` ADD `coordinator_abhyasi_id` VARCHAR(50) NULL DEFAULT NULL AFTER `coordinator_mobile`,
ADD `coordinator_permission_letter_path` VARCHAR(255) NULL DEFAULT NULL AFTER `coordinator_abhyasi_id`,
ADD `program_zone` VARCHAR(255) NULL DEFAULT NULL AFTER `event_country`,
ADD `program_center` VARCHAR(255) NULL DEFAULT NULL AFTER `program_zone`,
ADD `organization_batch_no` VARCHAR(50) NULL DEFAULT NULL AFTER `organization_web_site`,
ADD `organization_city` VARCHAR(255) NULL DEFAULT NULL AFTER `organization_batch_no`,
ADD `organization_location` VARCHAR(255) NULL DEFAULT NULL AFTER `organization_batch_no`,
ADD `organization_full_address` VARCHAR(255) NULL DEFAULT NULL AFTER `organization_location`,
ADD `organization_decision_maker_name` VARCHAR(50) NULL DEFAULT NULL AFTER `organization_contact_mobile`,
ADD `organization_decision_maker_email` VARCHAR(50) NULL DEFAULT NULL AFTER `organization_decision_maker_name`,
ADD `organization_decision_maker_phone_no` INT(15) NULL DEFAULT NULL AFTER `organization_decision_maker_email`;

CREATE TABLE IF NOT EXISTS `session_details` (
  `session_id` int(4) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `program_id` int(4) unsigned NOT NULL,
  `session_no` varchar(25) NOT NULL,
  `event_date` date NOT NULL,
  `no_of_participants` int(50) NOT NULL,
  `no_of_new_participants` int(50) NOT NULL,
  `topic_covered` varchar(255) NOT NULL,
  `preceptor_name` varchar(45) NOT NULL,
  `preceptor_id` int(45) NOT NULL,
  `comments` int(255) DEFAULT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   CONSTRAINT `session_details_fk` FOREIGN KEY (`program_id`) REFERENCES `program`(`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)