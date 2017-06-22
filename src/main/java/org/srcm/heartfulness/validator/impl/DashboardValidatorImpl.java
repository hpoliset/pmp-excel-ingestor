/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.DashboardValidator;

/**
 * @author koustavd
 *
 */
@Component
public class DashboardValidatorImpl implements DashboardValidator{
	
	Logger LOGGER = LoggerFactory.getLogger(DashboardValidatorImpl.class);
	
	
	@Override
	public  Date convertToSqlDate(String date) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(ExpressionConstants.DATE_FORMAT);
		Date sqlDate = null;
		try {
			 sqlDate = DateUtils.parseToSqlDate( formatter.format(formatter.parse(date)));
		} catch (Exception e) {
			LOGGER.error("Unable to parse date {}",e.getMessage());
		}
		return sqlDate;
	}


	@Override
	public ErrorResponse validateCountryField(String country) {
		if(null == country || country.isEmpty()){
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.COUNTRY_REQUIRED);
		}
		return null;
	}


	@Override
	public ErrorResponse validateZoneField(String zone) {
		if(null == zone || zone.isEmpty()){
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.ZONE_REQUIRED);
		}
		return null;
	}


	@Override
	public ErrorResponse validateCenterField(String center) {
		if(null == center || center.isEmpty()){
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.CENTER_REQUIRED);
		}
		return null;
	}

	@Override
	public ErrorResponse validateStateField(String state) {
		if(null == state || state.isEmpty()){
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.STATE_REQUIRED);
		}
		return null;
	}
	
	@Override
	public ErrorResponse validateDistrictField(String district) {
		if(null == district || district.isEmpty()){
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.DISTRICT_REQUIRED);
		}
		return null;
	}
	
	@Override
	public ErrorResponse validateCityField(String city) {
		if(null == city || city.isEmpty()){
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.CITY_REQUIRED);
		}
		return null;
	}
}
