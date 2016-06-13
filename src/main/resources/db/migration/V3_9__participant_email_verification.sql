ALTER TABLE participant ADD is_valid_email TINYINT(1) DEFAULT 0 NOT NULL;
ALTER TABLE participant ADD is_email_verified TINYINT(1) DEFAULT 0 NOT NULL;