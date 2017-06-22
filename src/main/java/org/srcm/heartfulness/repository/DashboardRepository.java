/**
 * 
 */
package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.DashboardResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface DashboardRepository {

	public List<DashboardResponse> getCountForCenterCoordinator(DashboardRequest dashboardReq, List<String> centers);

	public List<DashboardResponse> getCountForZoneCoordinator(DashboardRequest dashboardReq,List<String> zones,List<String> centers);
	
	public List<DashboardResponse> getCountForCountryCoordinator(DashboardRequest dashboardReq,Boolean hierarchyType);
	
	public List<DashboardResponse> getCountForEventCoordinator(DashboardRequest dashboardReq,User user,List<String> emailList);

	List<String> getListOfZonesForCountryCoordinator(DashboardRequest dashboardReq);
	
	List<String> getListOfZonesForZoneOrCenterCoordinator(DashboardRequest dashboardReq, List<String> centers, List<String> zones);
	
	List<String> getListOfZonesForEventCoordinator(List<String> emailList, String userRole, DashboardRequest dashboardReq);

	List<String> getListOfCentersForCountryCoordinator(DashboardRequest dashboardReq);
	
	List<String> getListOfCentersForZoneOrCenterCoordinator(DashboardRequest dashboardReq, List<String> zones, List<String> centers);
	
	List<String> getListOfCentersForEventCoordinator(DashboardRequest dashboardReq, List<String> emailList,String userRole);

	List<String> getListOfStatesForCountryCoordinator(DashboardRequest dashboardReq);
	
	List<String> getListOfDistrictForCountryCoordinator(DashboardRequest dashboardReq);

	List<String> getListOfCitiesForCountryCoordinator(DashboardRequest dashboardReq);

	
	

}
