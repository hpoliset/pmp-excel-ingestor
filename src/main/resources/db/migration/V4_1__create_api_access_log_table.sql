CREATE TABLE `api_access_log` (
  `id` int(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username` varchar(100) DEFAULT NULL,
  `ip_address` varchar(100) DEFAULT NULL,
  `api_name` varchar(100) NOT NULL,
  `request_time` varchar(100) NOT NULL,
  `response_time` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
