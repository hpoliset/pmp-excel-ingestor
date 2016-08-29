/**
 * 
 */
package org.srcm.heartfulness.repository;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;

/**
 * @author himasreev
 *
 */

public interface APIAccesslogRepository {

	int createOrUpdatePmpAPIAccessLog(PMPAPIAccessLog accessLog);
	
	int createOrUpdatePmpAPIAccesslogDetails(PMPAPIAccessLogDetails accessLogDetails);

}
