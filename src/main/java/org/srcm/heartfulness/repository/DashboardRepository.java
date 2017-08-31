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
	/**
	 * Method to get count for country coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param hierarchyType,To validate coordinator type
	 * @return List<DashboardResponse> based on DashboardRequest object.
	 */
	public List<DashboardResponse> getCountForCountryCoordinatorOrPresident(DashboardRequest dashboardReq,Boolean hierarchyType,String currentPositionType);
	/**
	 * Method to get count for zone coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param zones
	 * @param centers
	 * @return
	 */
	public List<DashboardResponse> getCountForZoneCoordinator(DashboardRequest dashboardReq,List<String> zones,List<String> centers);
	/**
	 * Method to get count for center coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param centers
	 * @return
	 */
	public List<DashboardResponse> getCountForCenterCoordinator(DashboardRequest dashboardReq, List<String> centers);
	/**
	 * Method to get count for Event coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param user
	 * @param emailList
	 * @return
	 */
	public List<DashboardResponse> getCountForEventCoordinator(DashboardRequest dashboardReq,User user,List<String> emailList);
	/**
	 * Method to get list of zones for country coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @return
	 */
	public List<String> getListOfZonesForCountryCoordinatorOrPresident(DashboardRequest dashboardReq);
	/**
	 * Method to get list of zones for zone/center coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param centers
	 * @param zones
	 * @return
	 */
	public List<String> getListOfZonesForZoneOrCenterCoordinator(DashboardRequest dashboardReq, List<String> centers, List<String> zones);
	/**
	 * Method to get list of zones for event coordinator
	 * @param emailList
	 * @param userRole
	 * @param dashboardReq,Request object to get input params.
	 * @return
	 */
	public List<String> getListOfZonesForEventCoordinator(List<String> emailList, String userRole, DashboardRequest dashboardReq);
	/**
	 * Method to get list of centers for country coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @return
	 */
	public List<String> getListOfCentersForCountryCoordinatorOrPresident(DashboardRequest dashboardReq);
	/**
	 * Method to get list of centers for zone/center coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param zones
	 * @param centers
	 * @return
	 */
	public List<String> getListOfCentersForZoneOrCenterCoordinator(DashboardRequest dashboardReq, List<String> zones, List<String> centers);
	/**
	 * Method to get list of centers for event coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @param emailList
	 * @param userRole
	 * @return
	 */
	public List<String> getListOfCentersForEventCoordinator(DashboardRequest dashboardReq, List<String> emailList,String userRole);
	/**
	 * Method to get list of states for country coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @return
	 */
	public List<String> getListOfStatesForCountryCoordinatorOrPresident(DashboardRequest dashboardReq);
	/**
	 * Method to get list of districts for country coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @return
	 */
	public List<String> getListOfDistrictForCountryCoordinatorOrPresident(DashboardRequest dashboardReq);
	/**
	 * Method to get list of cities for country coordinator
	 * @param dashboardReq,Request object to get input params.
	 * @return
	 */
	public List<String> getListOfCitiesForCountryCoordinatorOrPresident(DashboardRequest dashboardReq);

}
