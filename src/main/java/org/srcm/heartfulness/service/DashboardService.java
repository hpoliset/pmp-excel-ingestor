/**
 * 
 */
package org.srcm.heartfulness.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;

/**
 * @author Koustav Dutta
 *
 */
public interface DashboardService {
	
	public ResponseEntity<?> getDashboardDataCounts(String authToken,DashboardRequest dashboardReq,PMPAPIAccessLog accessLog,User user);

	ResponseEntity<?> getZones(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole);

	ResponseEntity<?> getCenterList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole); 
			

}
