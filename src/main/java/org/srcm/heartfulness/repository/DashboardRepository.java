/**
 * 
 */
package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.DashboardResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface DashboardRepository {

	public DashboardResponse getCountForCountryCoordinator(DashboardRequest dashboardReq);

	public DashboardResponse getCountForCenterCoordinator(DashboardRequest dashboardReq, List<String> centers);

	

}
