ALTER TABLE `user` MODIFY COLUMN role varchar(25) DEFAULT 'SEEKER';

UPDATE `user` SET `role`='SYSTEM_ADMIN' WHERE `role`='ROLE_ADMIN';

UPDATE `user` SET `role`='PRECEPTOR' WHERE `role`='ROLE_PRECEPTOR';

UPDATE `user` SET `role`='SEEKER' WHERE `role`='ROLE_SEEKER' OR `role`='';
