CREATE TABLE pmp_api_access_log (
  id int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username varchar(100) DEFAULT NULL,
  ip_address varchar(50) DEFAULT NULL,
  api_name varchar(255) DEFAULT NULL,  
  total_requested_time varchar(50) DEFAULT NULL,
  total_response_time varchar(50) DEFAULT NULL,
  status varchar(25) DEFAULT NULL,
  error_message varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE pmp_api_access_log_details (
    id int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pmp_access_log_id  int(4) UNSIGNED NOT NULL,
    endpoint varchar(255) DEFAULT NULL,
    requested_time varchar(50) DEFAULT NULL,
    response_time varchar(50) DEFAULT NULL,
    status varchar(25) DEFAULT NULL,
    error_message varchar(500) DEFAULT NULL,
    CONSTRAINT `pmp_api_access_log_fk` FOREIGN KEY (`pmp_access_log_id`) REFERENCES `pmp_api_access_log`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;