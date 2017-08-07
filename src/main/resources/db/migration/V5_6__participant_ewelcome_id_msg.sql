ALTER TABLE participant ADD ewelcome_id_generation_msg VARCHAR(255) DEFAULT NULL;
ALTER TABLE participant ADD department VARCHAR(255) DEFAULT NULL AFTER profession;