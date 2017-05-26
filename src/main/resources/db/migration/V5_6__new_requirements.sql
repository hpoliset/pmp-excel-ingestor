ALTER TABLE program ADD batch_description VARCHAR(500) DEFAULT NULL;
ALTER TABLE program ADD program_address VARCHAR(255) DEFAULT NULL;
ALTER TABLE program ADD program_district VARCHAR(150) DEFAULT NULL;
ALTER TABLE program ADD organization_contact_designation VARCHAR(150) DEFAULT NULL;
ALTER TABLE program ADD program_channel_type VARCHAR(150) DEFAULT NULL;
ALTER TABLE session_details ADD preceptor_email VARCHAR(150) DEFAULT NULL AFTER preceptor_id_card_no;
ALTER TABLE session_details ADD preceptor_mobile VARCHAR(150) DEFAULT NULL AFTER preceptor_email;