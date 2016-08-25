package org.srcm.heartfulness.service;

import java.text.ParseException;

import org.srcm.heartfulness.model.APIAccessLogDetails;

public interface APIAccessLogService {

	void saveAccessLogData(APIAccessLogDetails logDetails);

	void createLogDetails(String username, String ipAddress, String apiName, String requestTime) throws ParseException;

}
