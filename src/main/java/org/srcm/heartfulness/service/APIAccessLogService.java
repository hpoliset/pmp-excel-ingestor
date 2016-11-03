package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;

public interface APIAccessLogService {

	/**
	 * Service to persist the API request and response
	 * information<PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	int createPmpAPIAccessLog(PMPAPIAccessLog accessLog);

	/**
	 * Service to persist the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	int createPmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails);

	/**
	 * Service to update the PMP API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogDetailsId
	 */
	void updatePmpAPIAccessLog(PMPAPIAccessLog accessLog);

	/**
	 * Service to update the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogDetailsId
	 */
	void updatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails);

	/**
	 * Service to load the PMP API api access details from the PMP DB.
	 * 
	 * @param accessLog
	 * @return list of <PMPAPIAccessLog>
	 */
	List<PMPAPIAccessLog> loadPmpApiAccessLogData();

	/**
	 * Service to load the PMP API access error, request and response details from the PMP DB.
	 * 
	 * @param accessLogId
	 * @return list of <PMPAPIAccessLog>
	 */
	List<PMPAPIAccessLog> loadPmpApiAccessErrorLogData(String accessLogId);

	/**
	 * Service to load the PMP API details from the PMP DB.
	 * 
	 * @param accessLogId
	 * @return list of <PMPAPIAccessLog>
	 */
	List<PMPAPIAccessLogDetails> loadPmpApiLogDetailsData(String accessLogId);

	/**
	 * Service to load the PMP API access error, request and response details from the PMP DB.
	 * 
	 * @param logDetailsId
	 * @return list of <PMPAPIAccessLog>
	 */
	List<PMPAPIAccessLogDetails> loadPmpApiErrorLogDetailsData(String logDetailsId);

}
