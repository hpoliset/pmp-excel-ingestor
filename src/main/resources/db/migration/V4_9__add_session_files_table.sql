CREATE TABLE `session_files` (
  `file_id` int(4) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `session_id` int(4) unsigned NOT NULL,
  `file_name` VARCHAR(150) NOT NULL,
  `file_path` VARCHAR(150) NOT NULL,
  `file_type` VARCHAR(150) NOT NULL,
  `uploaded_by` int(5) NOT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT `session_details_fk` FOREIGN KEY (`session_id`) REFERENCES `session_details`(`session_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;