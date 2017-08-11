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
	
	/**
	 * Method to validate country
	 * 
	 * @param country
	 * @return
	 */
	public ErrorResponse validateCountryField(String country);
	
	/**
	 * Method to validate zone
	 * 
	 * @param zone
	 * @return
	 */
	public ErrorResponse validateZoneField(String zone);
	
	/**
	 * Method to validate center
	 * 
	 * @param center
	 * @return
	 */
	public ErrorResponse validateCenterField(String center);

	/**
	 * Method to validate state 
	 * 
	 * @param state
	 * @return
	 */
	ErrorResponse validateStateField(String state);
	
	/**
	 * Method to validate city
	 * 
	 * @param city
	 * @return
	 */
	ErrorResponse validateCityField(String city);

	/**
	 * Method to validate district
	 * 
	 * @param district
	 * @return
	 */
	ErrorResponse validateDistrictField(String district);

}
