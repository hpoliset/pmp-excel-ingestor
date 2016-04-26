CREATE TABLE IF NOT EXISTS `welcome_mail_subscribers` (
  `id` int(4) unsigned NOT NULL AUTO_INCREMENT,
  `print_name` varchar(60) CHARACTER SET utf8 DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `email_sent_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `unsubscribed` tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0 means subscribed/1 means unsubscribed',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

ALTER TABLE participant ADD welcome_mail_sent tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0 means not sent/1 means sent';