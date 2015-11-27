-- Coordinator Table

DROP TABLE IF EXISTS `coordinator`;

CREATE TABLE `coordinator` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(75) DEFAULT NULL,
  `email` varchar(75) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL, -- is it required?
  `other` varchar(45) DEFAULT NULL,
  `id_card_number` VARCHAR(100) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `uploaded_files`;

CREATE TABLE `uploaded_files` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `file_name` VARCHAR(128) NOT NULL,
  `file_content` LONGBLOB,
  `uploaded_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `organization`;

CREATE TABLE `organization` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(150) NOT NULL,
  `contact_name` VARCHAR(150) DEFAULT NULL,
  `email` VARCHAR(150) DEFAULT NULL,
  `web_site` varchar(150) DEFAULT NULL,
  `phone` varchar(25) DEFAULT NULL,
  `address_line1` varchar(100) DEFAULT NULL,
  `address_line2` varchar(100) DEFAULT NULL,
  `city` VARCHAR(50) DEFAULT NULL,
  `state` VARCHAR(50) DEFAULT NULL,
  `zip` VARCHAR(50) DEFAULT NULL,
  `country` VARCHAR(75) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL
);

DROP TABLE IF EXISTS `event`;

CREATE TABLE `event` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `channel_id` int(4) NOT NULL,
  `coordinator_id` int(4) NOT NULL,
  `organization_id` int(4) NOT NULL,
  `place` VARCHAR(75) DEFAULT NULL,
  `date` DATETIME NOT NULL,
  `address_line1` VARCHAR(75) DEFAULT NULL,
  `address_line2` VARCHAR(75) DEFAULT NULL,
  `city` VARCHAR(50) DEFAULT NULL,
  `state` VARCHAR(50) DEFAULT NULL,
  `zip` VARCHAR(50) DEFAULT NULL,
  `country` VARCHAR(75) DEFAULT NULL,
  `preceptor_name` varchar(45) DEFAULT NULL,
  `preceptor_id_card_number` varchar(45) DEFAULT NULL,
  `welcome_card_signed_by_name` varchar(45) DEFAULT NULL,
  `welcome_card_signer_id_card_number` varchar(45) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL

);



-- --------------------------------------------------
-- Staging table from here
DROP TABLE IF EXISTS `program`;

CREATE TABLE IF NOT EXISTS `program` (
  `program_id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `program_hash_code` varchar(50) NULL,
  `program_channel` varchar(150) DEFAULT NULL,
  `program_start_date` date DEFAULT NULL,
  `program_end_date` date DEFAULT NULL,
  `event_place` varchar(150) DEFAULT NULL,
  `event_city` varchar(150) DEFAULT NULL,
  `event_state` varchar(150) DEFAULT NULL,
  `event_country` varchar(50) DEFAULT NULL,
  `coordinator_name` varchar(150) DEFAULT NULL,
  `coordinator_email` varchar(150) DEFAULT NULL,
  `coordinator_mobile` varchar(150) DEFAULT NULL,
  `organization_name` VARCHAR(150) DEFAULT NULL,
  `organization_department` varchar(45) DEFAULT NULL,
  `organization_web_site` varchar(150) DEFAULT NULL,
  `organization_contact_name` varchar(100) DEFAULT NULL,
  `organization_contact_email` varchar(100) DEFAULT NULL,
  `organization_contact_mobile` varchar(25) DEFAULT NULL,
  `preceptor_name` varchar(45) DEFAULT NULL,
  `preceptor_id_card_number` varchar(45) DEFAULT NULL,
  `welcome_card_signed_by_name` varchar(45) DEFAULT NULL,
  `welcome_card_signer_id_card_number` varchar(45) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `batch_processed_time` DATETIME DEFAULT NULL,
  `coordinator_id` int(4) DEFAULT NULL,
  `event_id` int(4) DEFAULT NULL,
  `organization_id` int(4) DEFAULT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL,
  `created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `participant`;

CREATE TABLE IF NOT EXISTS `participant` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `excel_sheet_sequence_number` int(4) UNSIGNED NOT NULL, -- For now use the row number and later in v3.0 use the sequence no.
  `print_name` VARCHAR(150) NOT NULL,
  `first_name` varchar(150) DEFAULT NULL,
  `last_name` varchar(150) DEFAULT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `email` varchar(250) DEFAULT NULL,
  `mobile_phone` varchar(25) DEFAULT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `date_of_registration` date DEFAULT NULL,
  `abhyasi_id` varchar(100) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0' COMMENT '0 means Active\\n1 means Inactive',
  `address_line1` varchar(150) DEFAULT NULL,
  `address_line2` varchar(150) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `program_id` int(11) NOT NULL,
  `profession` varchar(50) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `id_card_number` varchar(45) DEFAULT NULL,
  `language` varchar(45) DEFAULT NULL,
  `sync_status` varchar(45) DEFAULT NULL,
  `introduced` tinyint(1) DEFAULT '0' COMMENT '0 means No   and 1 means Yes',
  `introduction_date` date DEFAULT NULL,
  `introduction_raw_date` varchar(50) DEFAULT NULL,
  `introduced_by` varchar(75) DEFAULT NULL,
  `welcome_card_number` varchar(45) DEFAULT NULL,
  `welcome_card_date` date DEFAULT NULL,
  `age_group` varchar(45) DEFAULT NULL,
  `upload_status` tinyint(1) DEFAULT '0' COMMENT '0 Means Uploaded, 1 means De-Dup done, 2- Normalized, 3 - Synced to AIMS',
  `first_sitting` int(11) DEFAULT NULL,
  `second_sitting` int(11) DEFAULT NULL,
  `third_sitting` int(11) DEFAULT NULL,
  `first_sitting_date` date DEFAULT NULL,
  `second_sitting_date` date DEFAULT NULL,
  `third_sitting_date` date DEFAULT NULL,
  `batch` varchar(50) DEFAULT NULL,
  `receive_updates` varchar(1) DEFAULT 'Y',
  `batch_processed_time` DATETIME DEFAULT NULL,
  `aims_sync_time` DATETIME DEFAULT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL,
  KEY `ProgramId_FK` (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
