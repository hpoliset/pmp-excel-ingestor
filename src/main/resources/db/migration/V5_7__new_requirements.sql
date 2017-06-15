ALTER TABLE program ADD batch_description VARCHAR(500) DEFAULT NULL;
ALTER TABLE program ADD program_address VARCHAR(255) DEFAULT NULL;
ALTER TABLE program ADD program_district VARCHAR(150) DEFAULT NULL;
ALTER TABLE program ADD organization_contact_designation VARCHAR(150) DEFAULT NULL;
ALTER TABLE program ADD program_channel_type int(4) UNSIGNED NOT NULL;
ALTER TABLE session_details ADD preceptor_email VARCHAR(150) DEFAULT NULL AFTER preceptor_id_card_no;
ALTER TABLE session_details ADD preceptor_mobile VARCHAR(150) DEFAULT NULL AFTER preceptor_email;
ALTER TABLE participant ADD phone VARCHAR(50) DEFAULT NULL;
ALTER TABLE participant ADD district VARCHAR(150) DEFAULT NULL;
CREATE TABLE channel_type (
  id int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  channel_id int(4) UNSIGNED NOT NULL,
  name VARCHAR(100) NOT NULL,
  description LONGTEXT,
  active tinyint(1) DEFAULT '1' COMMENT '1 Means Active and 0 means Inactive',
  update_time TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_time TIMESTAMP NOT NULL,
  created_by varchar(45) DEFAULT 'ADMINISTRATOR',
  updated_by varchar(45) DEFAULT NULL,
  UNIQUE KEY Channel_Name_UNIQUE (name),
  CONSTRAINT channel_type_foreign_key FOREIGN KEY (channel_id) REFERENCES channel(id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE coordinator_history (
  id int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  program_id int(4) UNSIGNED NOT NULL,
  coordinator_name VARCHAR(150) NOT NULL,
  coordinator_email VARCHAR(150) NOT NULL,
  abhyasi_id VARCHAR(25) DEFAULT NULL,
  assigned_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  removal_time TIMESTAMP NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT program_id_foreign_key FOREIGN KEY (program_id) REFERENCES program(program_id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS `program_testimonials` (
  `testimonial_id` int(4) unsigned NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `testimonial_name` varchar(150) NOT NULL,
  `testimonial_path` varchar(150) NOT NULL,
  `testimonial_type` varchar(150) NOT NULL,
  `uploaded_by` varchar(150) NOT NULL,
  `update_time` timestamp ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`testimonial_id`),
  CONSTRAINT `testimonial_program_fk` FOREIGN KEY (`program_id`) REFERENCES `program`(`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

ALTER TABLE session_images ADD COLUMN file_type VARCHAR(50) AFTER image_path;
ALTER TABLE program ADD program_status varchar(50) DEFAULT NULL;

