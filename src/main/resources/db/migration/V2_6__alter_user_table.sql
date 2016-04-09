ALTER TABLE `user` ADD `is_pmp_allowed` varchar(4) DEFAULT 'N';
ALTER TABLE `user` ADD `is_sahajmarg_allowed` varchar(4) DEFAULT 'N';
ALTER TABLE `user` ADD `role` varchar(25) DEFAULT 'ROLE_SEEKER';
ALTER TABLE `user` ADD `abyasi_id` int(25) DEFAULT 0;