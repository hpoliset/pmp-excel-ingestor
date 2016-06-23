ALTER TABLE welcome_email_log ADD subscribed TINYINT(1)  DEFAULT 0;
ALTER TABLE welcome_email_log ADD confirmed TINYINT(1)  DEFAULT 0;
ALTER TABLE welcome_email_log ADD email_status varchar(30);