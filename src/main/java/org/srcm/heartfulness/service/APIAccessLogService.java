package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;

/**
 * Service provider class for the logger details of PMP API's.
 * 
 * @author himasreev
 *
 */
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

	List<PMPAPIAccessLog> loadPmpApiAccessLogData();

	List<PMPAPIAccessLog> loadPmpApiAccessErrorLogData(String accessLogId);

	List<PMPAPIAccessLogDetails> loadPmpApiLogDetailsData(String accessLogId);

	List<PMPAPIAccessLogDetails> loadPmpApiErrorLogDetailsData(String logDetailsId);

}
