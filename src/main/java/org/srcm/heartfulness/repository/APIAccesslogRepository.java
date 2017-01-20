package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;

/**
 *  Repository interface for managing <code>Channel</code> domain objects.
 * 
 * @author himasreev
 *
 */
public interface APIAccesslogRepository {

	/**
	 * Method to persist the API request and response
	 * information<PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	int createOrUpdatePmpAPIAccessLog(PMPAPIAccessLog accessLog);

	/**
	 * Method to persist the MySRCM API request and response information
	 * <PMPAPIAccessLog> in the DB.
	 * 
	 * @param accessLog
	 * @return accessLogId
	 */
	int createOrUpdatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails);

	List<PMPAPIAccessLog> fetchPmpApiAccessLogData();

	List<PMPAPIAccessLog> fetchPmpApiAccessErrorLogData(String accessLogId);

	List<PMPAPIAccessLogDetails> fetchPmpApiLogDetailsData(String accessLogId);

	List<PMPAPIAccessLogDetails> fetchPmpApiErrorLogDetailsData(String logDetailsId);

}
