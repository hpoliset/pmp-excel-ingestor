--ALTER TABLE program ADD sqs_push_status TINYINT(1) DEFAULT 0;
ALTER TABLE user MODIFY address VARCHAR(255) DEFAULT NULL;
ALTER TABLE uploaded_files ADD status varchar(50) NULL;
ALTER TABLE program ADD user_id int(4) UNSIGNED DEFAULT 0;
ALTER TABLE program ADD uploaded_file_id int(4) UNSIGNED DEFAULT 0;
--Changes for eWelcome Id generation
ALTER TABLE participant ADD ewelcome_id_generation_msg VARCHAR(255) DEFAULT NULL;
ALTER TABLE participant ADD department VARCHAR(255) DEFAULT NULL AFTER profession;