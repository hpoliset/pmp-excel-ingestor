ALTER TABLE `program` ADD `coordinator_abhyasi_id` VARCHAR(50) NULL DEFAULT NULL AFTER `coordinator_mobile`,
ADD `coordinator_permission_letter_path` VARCHAR(255) NULL DEFAULT NULL AFTER `coordinator_abhyasi_id`,
ADD `program_zone` VARCHAR(255) NULL DEFAULT NULL AFTER `event_country`,
ADD `program_center` VARCHAR(255) NULL DEFAULT NULL AFTER `program_zone`,
ADD `organization_batch_no` VARCHAR(50) NULL DEFAULT NULL AFTER `organization_web_site`,
ADD `organization_city` VARCHAR(150) NULL DEFAULT NULL AFTER `organization_batch_no`,
ADD `organization_location` VARCHAR(150) NULL DEFAULT NULL AFTER `organization_batch_no`,
ADD `organization_full_address` VARCHAR(255) NULL DEFAULT NULL AFTER `organization_location`,
ADD `organization_decision_maker_name` VARCHAR(150) NULL DEFAULT NULL AFTER `organization_contact_mobile`,
ADD `organization_decision_maker_email` VARCHAR(150) NULL DEFAULT NULL AFTER `organization_decision_maker_name`,
ADD `organization_decision_maker_phone_no` VARCHAR(50) NULL DEFAULT NULL AFTER `organization_decision_maker_email`;

CREATE TABLE IF NOT EXISTS `session_details` (
  `session_id` int(4) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `auto_generated_session_id` VARCHAR(10) NOT NULL,
  `program_id` int(4) unsigned NOT NULL,
  `session_number` VARCHAR(150) NOT NULL,
  `session_date` date NOT NULL,
  `number_of_participants` int(5) NOT NULL,
  `number_of_new_participants` int(5) NOT NULL,
  `topic_covered` VARCHAR(500) DEFAULT NULL,
  `preceptor_name` VARCHAR(150) NOT NULL,
  `preceptor_id_card_no` VARCHAR(45) NOT NULL,
  `comments` VARCHAR(500) DEFAULT NULL,
  `is_deleted` TINYINT(1) DEFAULT 0 NOT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
   CONSTRAINT `session_details_fk` FOREIGN KEY (`program_id`) REFERENCES `program`(`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;