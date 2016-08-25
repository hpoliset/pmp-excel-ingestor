package org.srcm.heartfulness.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.APIAccessLogDetails;
import org.srcm.heartfulness.repository.APIAccesslogRepository;
import org.srcm.heartfulness.util.DateUtils;

@Service
public class APIAccessLogServiceImpl implements APIAccessLogService {

	@Autowired
	APIAccesslogRepository apiAccesslogRepository;

	@Override
	public void saveAccessLogData(APIAccessLogDetails logDetails) {
		apiAccesslogRepository.saveAccessLogData(logDetails);
	}

	@Override
	public void createLogDetails(String username, String ipAddress, String apiName, String requestTime) throws ParseException {
		APIAccessLogDetails logDetails = new APIAccessLogDetails();
		logDetails.setRequestTime(requestTime);
		logDetails.setApiName(apiName);
		logDetails.setIpAddress(ipAddress);
		logDetails.setUsername(username);
		logDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		saveAccessLogData(logDetails);
	}

}
