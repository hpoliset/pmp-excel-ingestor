/**
 * 
 */
package org.srcm.heartfulness.service;

import org.srcm.heartfulness.model.json.response.DashboardResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface DashboardService {
	
	public DashboardResponse getDashboardDataCounts(String authToken); 

}
