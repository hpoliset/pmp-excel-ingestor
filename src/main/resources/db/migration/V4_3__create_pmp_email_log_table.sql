CREATE TABLE pmp_email_log (
    id int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    program_id int(11) NOT NULL,
    coordinator_email varchar(150) NOT NULL,
    email_type varchar(50) NOT NULL,
    email_sent_status varchar(25) NOT NULL,
    error_message text DEFAULT NULL,
    email_sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;