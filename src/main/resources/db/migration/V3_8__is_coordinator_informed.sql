ALTER TABLE participant ADD is_co_ordinator_informed TINYINT(1) DEFAULT 0;
UPDATE participant SET is_co_ordinator_informed = 1 WHERE welcome_mail_sent = 1;