package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;

/**
 * @author himasreev
 *
 */
public interface APIAccesslogRepository {

	int createOrUpdatePmpAPIAccessLog(PMPAPIAccessLog accessLog);

	int createOrUpdatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails);

	List<PMPAPIAccessLog> fetchPmpApiAccessLogData();

	List<PMPAPIAccessLog> fetchPmpApiAccessErrorLogData(String accessLogId);

	List<PMPAPIAccessLogDetails> fetchPmpApiLogDetailsData(String accessLogId);

	List<PMPAPIAccessLogDetails> fetchPmpApiErrorLogDetailsData(String logDetailsId);

}
