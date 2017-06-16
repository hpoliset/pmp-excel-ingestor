/**
 * 
 */
package org.srcm.heartfulness.service;

import org.springframework.http.ResponseEntity;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.request.DashboardRequest;

/**
 * @author Koustav Dutta
 *
 */
public interface DashboardService {
	
	public ResponseEntity<?> getDashboardDataCounts(String authToken,DashboardRequest dashboardReq,PMPAPIAccessLog accessLog); 

}
