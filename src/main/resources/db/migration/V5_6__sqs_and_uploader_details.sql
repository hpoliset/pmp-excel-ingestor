ALTER TABLE program ADD sqs_push_status TINYINT(1) DEFAULT 0;
ALTER TABLE user MODIFY address VARCHAR(255) DEFAULT NULL;
ALTER TABLE uploaded_files ADD status varchar(50) DEFAULT 'FAILED';