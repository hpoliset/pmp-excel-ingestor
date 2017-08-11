ALTER TABLE session_details ADD first_sitting_by int(25)  DEFAULT 0 AFTER preceptor_id_card_no;
ALTER TABLE participant ADD session_id int(4) UNSIGNED DEFAULT 0;
