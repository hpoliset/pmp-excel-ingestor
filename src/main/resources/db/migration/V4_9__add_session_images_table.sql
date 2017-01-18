CREATE TABLE `session_images` (
  `image_id` int(4) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `session_id` int(4) unsigned NOT NULL,
  `image_name` VARCHAR(150) NOT NULL,
  `image_path` VARCHAR(150) NOT NULL,
  `uploaded_by` VARCHAR(150) NOT NULL,
  `update_time` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT `session_images_fk` FOREIGN KEY (`session_id`) REFERENCES `session_details`(`session_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE program ADD is_ewelcome_id_generation_disabled varchar(1) NOT NULL DEFAULT 'E';