/**
 * 
 */
package org.srcm.heartfulness.validator;

import java.sql.Date;

import org.srcm.heartfulness.model.json.response.ErrorResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface DashboardValidator {
	
	public  Date convertToSqlDate(String date);
	
	public ErrorResponse validateCountryField(String country);
	
	public ErrorResponse validateZoneField(String zone);
	
	public ErrorResponse validateCenterField(String center);
	
	
	
	

}
