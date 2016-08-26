package org.srcm.heartfulness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.repository.APIAccesslogRepository;

@Service
public class APIAccessLogServiceImpl implements APIAccessLogService {

	@Autowired
	APIAccesslogRepository apiAccesslogRepository;

	/**
	 * Service to persist the API request and response
	 * information<PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	@Override
	public int createPmpAPIAccessLog(PMPAPIAccessLog accessLog) {
		return apiAccesslogRepository.createOrUpdatePmpAPIAccessLog(accessLog);
	}

	/**
	 * Service to persist the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	@Override
	public int createPmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails) {
		return apiAccesslogRepository.createOrUpdatePmpAPIAccesslogDetails(accessLogDetails);
	}

	/**
	 * Service to update the PMP API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogDetailsId
	 */
	@Override
	public void updatePmpAPIAccessLog(PMPAPIAccessLog accessLog) {
		apiAccesslogRepository.createOrUpdatePmpAPIAccessLog(accessLog);

	}

	/**
	 * Service to update the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogDetailsId
	 */
	@Override
	public void updatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails) {
		apiAccesslogRepository.createOrUpdatePmpAPIAccesslogDetails(accessLogDetails);
	}

}
