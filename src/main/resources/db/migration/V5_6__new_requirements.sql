ALTER TABLE program ADD batch_description VARCHAR(500) DEFAULT NULL;
ALTER TABLE program ADD program_address VARCHAR(255) DEFAULT NULL;
ALTER TABLE program ADD program_district VARCHAR(150) DEFAULT NULL;
ALTER TABLE program ADD organization_contact_designation VARCHAR(150) DEFAULT NULL;
ALTER TABLE program ADD program_channel_type VARCHAR(150) DEFAULT NULL;
ALTER TABLE session_details ADD preceptor_email VARCHAR(150) DEFAULT NULL AFTER preceptor_id_card_no;
ALTER TABLE session_details ADD preceptor_mobile VARCHAR(150) DEFAULT NULL AFTER preceptor_email;
ALTER TABLE participant ADD phone VARCHAR(50) DEFAULT NULL;
ALTER TABLE participant ADD district VARCHAR(150) DEFAULT NULL;
CREATE TABLE channel_type (
  id int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  channel_id int(4) UNSIGNED NOT NULL,
  name VARCHAR(100) NOT NULL,
  description LONGTEXT,
  active tinyint(1) DEFAULT '0' COMMENT '0 Means Active and 1 means Inactive',
  update_time TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_time TIMESTAMP NOT NULL,
  created_by varchar(45) DEFAULT 'ADMINISTRATOR',
  updated_by varchar(45) DEFAULT NULL,
  UNIQUE KEY Channel_Name_UNIQUE (name),
  CONSTRAINT channel_type_foreign_key FOREIGN KEY (channel_id) REFERENCES channel(id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
