CREATE TABLE `channel` (
  `Channel_ID` int(11) NOT NULL,
  `Channel_Name` varchar(100) NOT NULL,
  `Channel_Descr` longtext,
  `Channel_SPOC` varchar(150) DEFAULT NULL,
  `Channel_Lead` varchar(150) DEFAULT NULL,
  `Active` tinyint(1) DEFAULT '0' COMMENT '0 Means Active and 1 means Inactive',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Channel_ID`),
  UNIQUE KEY `Channel_Name_UNIQUE` (`Channel_Name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `coordinator` (
  `Coordinator_ID` int(11) NOT NULL,
  `Coord_Name` varchar(75) DEFAULT NULL,
  `Coord_Email` varchar(75) DEFAULT NULL,
  `Coord_Mobile` varchar(45) DEFAULT NULL,
  `Coord_Phone` varchar(45) DEFAULT NULL,
  `Coord_Other` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Coordinator_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `error_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `error_message` varchar(500) DEFAULT NULL,
  `entity` varchar(45) DEFAULT NULL COMMENT 'Pass table name or function SP name',
  `error_code` varchar(45) DEFAULT NULL COMMENT 'Pass only My SQL Error Codes (numbers)',
  `error_type` varchar(45) DEFAULT 'Debug',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2198 DEFAULT CHARSET=latin1 COMMENT='Logs errors and debug messages';


CREATE TABLE `files_upload` (
  `upload_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(128) DEFAULT NULL,
  `file_data` longblob,
  `upload_date` date DEFAULT NULL,
  PRIMARY KEY (`upload_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `maturity` (
  `Maturity_ID` int(11) NOT NULL,
  `Maturity_Order` int(11) NOT NULL,
  `Maturity_Code` varchar(45) DEFAULT NULL,
  `Maturity_Desc` varchar(250) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Maturity_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `organization` (
  `Organization_ID` int(11) NOT NULL,
  `Organization_Name` varchar(150) NOT NULL,
  `Organization_SPOC` varchar(150) DEFAULT NULL,
  `Organization_Email` varchar(75) DEFAULT NULL,
  `Organization_Website` varchar(100) DEFAULT NULL,
  `Organization_Phone` varchar(25) DEFAULT NULL,
  `Org_Address_Line_1` varchar(100) DEFAULT NULL,
  `Org_Address_line_2` varchar(100) DEFAULT NULL,
  `Org_City` varchar(50) DEFAULT NULL,
  `Org_State` varchar(50) DEFAULT NULL,
  `Org_Zip` varchar(45) DEFAULT NULL,
  `Org_Country` varchar(75) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `Organization_Dept` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Organization_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `event` (
  `Event_ID` int(11) NOT NULL,
  `Event_Place` varchar(75) DEFAULT NULL,
  `Event_Date` date DEFAULT NULL,
  `Event_Address_1` varchar(75) DEFAULT NULL,
  `Event_Address_2` varchar(75) DEFAULT NULL,
  `Event_City` varchar(45) DEFAULT NULL,
  `Event_State` varchar(45) DEFAULT NULL,
  `Event_Country` varchar(45) DEFAULT NULL,
  `Event_Zip` varchar(25) DEFAULT NULL,
  `Event_Remarks` varchar(500) DEFAULT NULL,
  `Prefect_ID` varchar(25) DEFAULT NULL,
  `Prefect_Name` varchar(25) DEFAULT NULL,
  `Channel_ID` int(11) DEFAULT NULL,
  `Coordinator_ID` int(11) DEFAULT NULL,
  `Organization_ID` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `upload_id` int(11) DEFAULT NULL COMMENT 'The file upload reference to the table files_upload',
  PRIMARY KEY (`Event_ID`),
  KEY `Channel_Fkey2` (`Channel_ID`),
  KEY `Coord_id_FKey2` (`Coordinator_ID`),
  KEY `Organization_id_FKey2` (`Organization_ID`),
  CONSTRAINT `Channel_Fkey2` FOREIGN KEY (`Channel_ID`) REFERENCES `channel` (`Channel_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Coord_id_FKey2` FOREIGN KEY (`Coordinator_ID`) REFERENCES `coordinator` (`Coordinator_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Organization_id_FKey2` FOREIGN KEY (`Organization_ID`) REFERENCES `organization` (`Organization_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `participation` (
  `Participation_ID` int(11) NOT NULL,
  `Seeker_ID` int(11) DEFAULT NULL,
  `Event_ID` int(11) DEFAULT NULL,
  `Attribute1` varchar(150) DEFAULT NULL COMMENT 'Future Use',
  `Attribute2` varchar(150) DEFAULT NULL COMMENT 'Future Use',
  `Future` varchar(150) DEFAULT NULL COMMENT 'Future Use',
  `Introduced` tinyint(1) DEFAULT '1' COMMENT '0 means Introduced, 1 means Not Introduced',
  `Introduced_date` date DEFAULT NULL,
  `Introduced_By` varchar(75) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Participation_ID`),
  KEY `Seeker_FKey1` (`Seeker_ID`),
  KEY `event_id_FKey` (`Event_ID`),
  CONSTRAINT `Seeker_FKey1` FOREIGN KEY (`Seeker_ID`) REFERENCES `seeker` (`Seeker_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `event_id_FKey` FOREIGN KEY (`Event_ID`) REFERENCES `event` (`Event_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `seeker` (
  `Seeker_ID` int(11) NOT NULL,
  `First_Name` varchar(150) DEFAULT NULL,
  `Middle_Name` varchar(75) DEFAULT NULL,
  `Last_Name` varchar(75) DEFAULT NULL,
  `Email` varchar(150) DEFAULT NULL,
  `Phone_Mobile` varchar(25) DEFAULT NULL,
  `Phone` varchar(25) DEFAULT NULL,
  `Gender` int(11) DEFAULT NULL,
  `Age` tinyint(4) DEFAULT NULL,
  `Age_Group` varchar(10) DEFAULT NULL,
  `Welcome_Card_Num` varchar(20) DEFAULT NULL,
  `Welcome_Card_Date` date DEFAULT NULL,
  `Welcome_msg_sent` tinyint(1) DEFAULT '0' COMMENT '0 Means Welcome Msg Not Sent, 1 means Welcome Msg Sent',
  `Language` varchar(45) DEFAULT NULL,
  `Email_Subscribe` tinyint(1) DEFAULT '0' COMMENT '0 Means Subscribed, 1 means UnSubsribed',
  `Text_Subscribe` tinyint(1) DEFAULT '0' COMMENT '0 Means Subscribed, 1 means UnSubsribed',
  `Abhyasi_ID` varchar(100) DEFAULT NULL COMMENT 'Synced from AIMS',
  `Occupation` varchar(45) DEFAULT NULL,
  `Status` tinyint(1) DEFAULT '0' COMMENT '0 Means Active, 1 means Inactive',
  `Upload_Status` tinyint(1) DEFAULT '0' COMMENT '0 Means Uploaded, 1 means De-Dup done, 2- Normalized, 3 - Synced to AIMS',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `first_sitting` int(11) DEFAULT NULL,
  `second_sitting` int(11) DEFAULT NULL,
  `third_sitting` int(11) DEFAULT NULL,
  `first_sitting_date` date DEFAULT NULL,
  `second_sitting_date` date DEFAULT NULL,
  `third_sitting_date` date DEFAULT NULL,
  `occupation_stream` varchar(50) DEFAULT NULL,
  `batch` varchar(50) DEFAULT NULL,
  `receive_updates` varchar(1) DEFAULT 'Y',
  `CITY` varchar(75) DEFAULT NULL,
  `state` varchar(75) DEFAULT NULL,
  `country` varchar(75) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`Seeker_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `seeker_maturity` (
  `Seeker_Maturity_ID` int(11) NOT NULL,
  `Seeker_ID` int(11) DEFAULT NULL,
  `Maturity_ID` int(11) NOT NULL,
  `Maturity_Start_Date` date DEFAULT NULL,
  `Maturity_End_Date` date DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Seeker_Maturity_ID`),
  KEY `Seeker_FKey2` (`Seeker_ID`),
  KEY `Maturity_FKey2` (`Maturity_ID`),
  CONSTRAINT `Maturity_FKey2` FOREIGN KEY (`Maturity_ID`) REFERENCES `maturity` (`Maturity_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Seeker_FKey2` FOREIGN KEY (`Seeker_ID`) REFERENCES `seeker` (`Seeker_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `sequence_data` (
  `sequence_name` varchar(100) NOT NULL,
  `sequence_increment` int(11) unsigned NOT NULL DEFAULT '1',
  `sequence_min_value` int(11) unsigned NOT NULL DEFAULT '1',
  `sequence_max_value` bigint(20) unsigned NOT NULL DEFAULT '18446744073709551615',
  `sequence_cur_value` bigint(20) unsigned DEFAULT '1',
  `sequence_cycle` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sequence_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


CREATE TABLE `welcome_log` (
  `First_Name` varchar(150) DEFAULT NULL,
  `Email` varchar(250) DEFAULT NULL,
  `Welcome_msg_sent` tinyint(1) DEFAULT '0' COMMENT '0 means Not Sent, and 1 means Sent',
  `Subscribe` tinyint(1) DEFAULT '0' COMMENT '0 means Subscribed, and 1 means Not Subscribed',
  UNIQUE KEY `Email_UNIQUE` (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------
-- Staging table from here

CREATE TABLE `program` (
  `Program_id` int(11) NOT NULL,
  `program_channel` varchar(150) DEFAULT NULL,
  `Program_date` date DEFAULT NULL,
  `Coord_Name` varchar(150) DEFAULT NULL,
  `Coord_Email` varchar(150) DEFAULT NULL,
  `Coord_Center_Name` varchar(150) DEFAULT NULL,
  `Coord_Country` varchar(45) DEFAULT NULL,
  `Inst_Name` varchar(150) DEFAULT NULL,
  `Inst_Website` varchar(150) DEFAULT NULL,
  `Program_raw_Date` varchar(15) DEFAULT NULL,
  `Program_Start_Date` date DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `Created_by` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `Coord_State` varchar(45) DEFAULT NULL,
  `Ins_dept` varchar(45) DEFAULT NULL,
  `Prefect_Name` varchar(45) DEFAULT NULL,
  `Prefect_ID` varchar(45) DEFAULT NULL,
  `Welcome_Card_Sign` varchar(45) DEFAULT NULL,
  `Welcome_Sign_ID` varchar(45) DEFAULT NULL,
  `Remarks` varchar(500) DEFAULT NULL,
  `Coord_Mobile` varchar(15) DEFAULT NULL,
  `inst_spoc_name` varchar(100) DEFAULT NULL,
  `inst_spoc_mobile` varchar(25) DEFAULT NULL,
  `inst_email` varchar(100) DEFAULT NULL,
  `event_city` varchar(100) DEFAULT NULL,
  `event_other` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `seeker_aims` (
  `First_Name` varchar(150) DEFAULT NULL,
  `Last_Name` varchar(150) DEFAULT NULL,
  `Middle_Name` varchar(50) DEFAULT NULL,
  `Email` varchar(250) DEFAULT NULL,
  `Phone_Mobile` varchar(25) DEFAULT NULL,
  `Gender` int(11) DEFAULT NULL,
  `Date_of_Birth` date DEFAULT NULL,
  `Date_of_Registration` date DEFAULT NULL,
  `Abhyasi_ID` varchar(100) DEFAULT NULL,
  `Status` tinyint(1) DEFAULT '0' COMMENT '0 means Active\\n1 means Inactive',
  `Address_Line_1` varchar(150) DEFAULT NULL,
  `Address_Line_2` varchar(150) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `State` varchar(50) DEFAULT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `Seeker_ID` int(11) NOT NULL,
  `Program_id` int(11) NOT NULL,
  `Occupation` varchar(50) DEFAULT NULL,
  `Remarks` varchar(500) DEFAULT NULL,
  `ID_Card_Num` varchar(45) DEFAULT NULL,
  `Language` varchar(45) DEFAULT NULL,
  `Sync_Status` varchar(45) DEFAULT NULL,
  `Introduced` tinyint(1) DEFAULT '0' COMMENT '0 means No   and 1 means Yes',
  `Introduced_Date` date DEFAULT NULL,
  `Introduced_raw_date` varchar(50) DEFAULT NULL,
  `Introduced_By` varchar(75) DEFAULT NULL,
  `Welcome_Card_Num` varchar(45) DEFAULT NULL,
  `Welcome_Card_Date` date DEFAULT NULL,
  `Age_Group` varchar(45) DEFAULT NULL,
  `Upload_Status` tinyint(1) DEFAULT '0' COMMENT '0 Means Uploaded, 1 means De-Dup done, 2- Normalized, 3 - Synced to AIMS',
  `first_sitting` int(11) DEFAULT NULL,
  `second_sitting` int(11) DEFAULT NULL,
  `third_sitting` int(11) DEFAULT NULL,
  `first_sitting_date` date DEFAULT NULL,
  `second_sitting_date` date DEFAULT NULL,
  `third_sitting_date` date DEFAULT NULL,
  `occupation_stream` varchar(50) DEFAULT NULL,
  `batch` varchar(50) DEFAULT NULL,
  `receive_updates` varchar(1) DEFAULT 'Y',
  PRIMARY KEY (`Seeker_ID`),
  KEY `ProgramId_FK` (`Program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
