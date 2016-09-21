ALTER TABLE user MODIFY COLUMN `abyasi_id` varchar(25);

ALTER TABLE user ADD language_preference varchar(25);
ALTER TABLE user ADD age_Group varchar(25);
ALTER TABLE user ADD zipcode varchar(25);