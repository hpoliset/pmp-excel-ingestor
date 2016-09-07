ALTER TABLE pmp_api_access_log ADD request_body text DEFAULT NULL;
ALTER TABLE pmp_api_access_log ADD response_body text DEFAULT NULL;
ALTER TABLE pmp_api_access_log_details ADD request_body text DEFAULT NULL;
ALTER TABLE pmp_api_access_log_details ADD response_body text DEFAULT NULL;
ALTER TABLE pmp_api_access_log MODIFY COLUMN error_message text DEFAULT NULL;
ALTER TABLE pmp_api_access_log_details MODIFY COLUMN error_message text DEFAULT NULL;