--ALTER TABLE program ADD sqs_push_status TINYINT(1) DEFAULT 0;
ALTER TABLE user MODIFY address VARCHAR(255) DEFAULT NULL;
ALTER TABLE uploaded_files ADD status varchar(50) NULL;
ALTER TABLE program ADD user_id int(4) UNSIGNED NULL;
ALTER TABLE program ADD uploaded_file_id int(4) UNSIGNED NULL;