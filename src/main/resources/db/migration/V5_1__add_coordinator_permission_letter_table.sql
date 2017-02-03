CREATE TABLE `program_permission_letters` (
  `permission_letter_id` int(4) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `program_id` int(4) unsigned NOT NULL,
  `permission_letter_name` VARCHAR(150) NOT NULL,
  `permission_letter_path` VARCHAR(150) NOT NULL,
  `uploaded_by` VARCHAR(150) NOT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT `program_fk` FOREIGN KEY (`program_id`) REFERENCES `program`(`program_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;