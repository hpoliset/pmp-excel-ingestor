CREATE TABLE `deleted_participants` (
  `id` int(4) unsigned NOT NULL AUTO_INCREMENT,
  `program_id` int(4) unsigned NOT NULL,
  `seq_id` varchar(100) DEFAULT NULL,
  `print_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `introduced` tinyint(1) DEFAULT 0,
  `excel_sheet_sequence_number` int(4) DEFAULT NULL,
  `mobile_phone` varchar(25) DEFAULT NULL,
  `abhyasi_id` varchar(100) DEFAULT NULL,
  `deleted_by` varchar(100) DEFAULT NULL,
  `create_time` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 