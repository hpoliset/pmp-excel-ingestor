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
	
	/**
	 * Method to get date counts
	 * 
	 * @param authToken This parameter is used to verify user in srcm 
	 * @param dashboardReq This parameter contains the values required(zone,country,state,etc.)
	 * @param accessLog This parameter is used by the method create log details of access
	 * @param user This parameter contains the properties of the user
	 * @return
	 */
	public ResponseEntity<?> getDashboardDataCounts(String authToken,DashboardRequest dashboardReq,PMPAPIAccessLog accessLog,User user);
	
	/**
	 * Method to get the list of zones for the provided country or coordinator email ids
	 * 
	 * @param authToken This parameter is used to verify user in srcm 
	 * @param dashboardReq This parameter contains the values required(zone,country,state,etc.)
	 * @param accessLog This parameter is used by the method create log details of access
	 * @param emailList This parameter contains the list of email ids of the coordinator
	 * @param userRole This parameter has the role of the user
	 * @return
	 */
	ResponseEntity<?> getListOfZones(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole);

	/**
	 * Method to get the list of centers for the provided country & zones or coordinator email ids
	 * 
	 * @param authToken This parameter is used to verify user in srcm 
	 * @param dashboardReq This parameter contains the values required(zone,country,state,etc.)
	 * @param accessLog This parameter is used by the method create log details of access
	 * @param emailList This parameter contains the list of email ids of the coordinator
	 * @param userRole This parameter has the role of the user
	 * @return
	 */
	ResponseEntity<?> getCenterList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole);
	
	/**
	 * Method to get the list of states for the provided country or coordinator email ids
	 * 
	 * @param authToken This parameter is used to verify user in srcm
	 * @param dashboardReq This parameter contains the values required(zone,country,state,etc.)
	 * @param accessLog This parameter is used by the method create log details of access
	 * @param emailList This parameter contains the list of email ids of the coordinator
	 * @param userRole
	 * @return
	 */
	ResponseEntity<?> getStateList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole);

	/**
	 * Method to get the list of cities for the provided country, state & district or coordinator email ids 
	 * 
	 * @param authToken This parameter is used to verify user in srcm
	 * @param dashboardReq This parameter contains the values required(zone,country,state,etc.)
	 * @param accessLog This parameter is used by the method create log details of access
	 * @param emailList This parameter contains the list of email ids of the coordinator
	 * @param userRole This parameter has the role of the user
	 * @return
	 */
	ResponseEntity<?> getCityList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String userRole);

	/**
	 * Method to get the list of districts for the provided country & state or coordinator email ids
	 * 
	 * @param authToken This parameter is used to verify user in srcm
	 * @param dashboardReq This parameter contains the values required(zone,country,state,etc.)
	 * @param accessLog This parameter is used by the method create log details of access
	 * @param emailList This parameter contains the list of email ids of the coordinator
	 * @param userRole This parameter has the role of the user
	 * @return
	 */
	ResponseEntity<?> getDistrictList(String authToken, DashboardRequest dashboardReq, PMPAPIAccessLog accessLog,List<String> emailList, String role);


}
