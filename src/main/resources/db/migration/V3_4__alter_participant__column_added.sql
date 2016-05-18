ALTER TABLE participant ADD is_bounced TINYINT(1)  DEFAULT 0;
ALTER TABLE participant ADD confirmation_mail_sent TINYINT(1)  DEFAULT 0;