UPDATE participant set welcome_mail_sent=1 WHERE create_time < CURDATE()-1;