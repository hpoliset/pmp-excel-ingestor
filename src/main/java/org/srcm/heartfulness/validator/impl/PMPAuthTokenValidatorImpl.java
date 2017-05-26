/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.TokenValidationConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.PMPAuthTokenValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class PMPAuthTokenValidatorImpl implements PMPAuthTokenValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsValidatorImpl.class);

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;
	

	@Override
	public PMPResponse validateAuthToken(String authToken, PMPAPIAccessLog accessLog) {
		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(authToken, accessLog.getId());
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			eResponse.setError_description(TokenValidationConstants.INVALID_AUTH_TOKEN);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (HttpClientErrorException httpcee) {
			eResponse.setError_description(TokenValidationConstants.INVALID_CLIENT_CREDENTIALS);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(httpcee));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (JsonParseException jpe) {
			eResponse.setError_description(TokenValidationConstants.ERROR_PROFILE);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (JsonMappingException jme) {
			eResponse.setError_description(TokenValidationConstants.ERROR_PROFILE);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (IOException ioe) {
			eResponse.setError_description(TokenValidationConstants.ERROR_PROFILE);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (Exception e) {
			eResponse.setError_description(TokenValidationConstants.INVALID_REQUEST);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setError_description(TokenValidationConstants.INVALID_CREDENTIALS);
			accessLog.setErrorMessage(TokenValidationConstants.USER_DOESNOT_EXIST_IN_MYSRCM);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			accessLog.setUsername(
					null == userProfile.getUser_email() ? userProfile.getEmail() 
							: userProfile.getUser_email().isEmpty() ? userProfile.getEmail() : userProfile.getUser_email());
			SuccessResponse sResponse = new SuccessResponse(ErrorConstants.STATUS_SUCCESS,TokenValidationConstants.TOKEN_VALIDATION_SUCCESSFUL);
			return sResponse;
		}
	}
	
	/**
	 * Method is used to set the PMP API access log details.
	 * 
	 * @param accessLog
	 *            accessLog to persist the api log details.
	 * @param eResponse
	 *            to set the error response.
	 */
	private void setPMPAccessLogAndPersist(PMPAPIAccessLog accessLog, ErrorResponse eResponse) {

		try {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		} catch (Exception ex) {
			LOGGER.error("Failed to create or update PMP API access log ");
		}

	}

}
