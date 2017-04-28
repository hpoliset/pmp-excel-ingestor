ALTER TABLE participant ADD total_days tinyint(2) DEFAULT NULL;
ALTER TABLE program MODIFY COLUMN created_source VARCHAR(50);