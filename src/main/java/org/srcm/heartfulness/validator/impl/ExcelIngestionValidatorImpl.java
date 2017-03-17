package org.srcm.heartfulness.validator.impl;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.enumeration.ExcelAndReportAdmins;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.ExcelIngestionValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author himasreev
 *
 */
@Component
public class ExcelIngestionValidatorImpl implements ExcelIngestionValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelIngestionValidatorImpl.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Override
	public Response validateExcelUploadRequest(PMPAPIAccessLog accessLog, String token) {
		Response eResponse = new Response(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
		} catch (HttpClientErrorException e) {
			LOGGER.error("EXCEL UPLOAD :Exception while validating the user token. Exception : {} ",e);
			eResponse.setDescription(ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("EXCEL UPLOAD :Exception while validating the user token. Exception : {} ",e);
			eResponse.setDescription(ErrorConstants.ERROR_WHILE_FETCHING_PROFILE_FROM_MYSRCM);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
			LOGGER.error("EXCEL UPLOAD :Exception while validating the user token. Exception : {} ",e);
			eResponse.setDescription(ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (Exception e) {
			LOGGER.error("EXCEL UPLOAD :Exception while validating the user token. Exception : {} ",e);
			eResponse.setDescription(ErrorConstants.ERROR_WHILE_FETCHING_PROFILE_FROM_MYSRCM);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setDescription(ErrorConstants.INVALID_CREDENTIALS);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} else {
			accessLog.setUsername(null == userProfile.getUser_email() ? userProfile.getEmail() 
					: userProfile.getUser_email().isEmpty() ? userProfile.getEmail() : userProfile.getUser_email());
		}
		User user = userProfileService.loadUserByEmail(null == userProfile.getUser_email() ? userProfile.getEmail() 
				: userProfile.getUser_email().isEmpty() ? userProfile.getEmail() : userProfile.getUser_email());
		boolean valid=false;
		if (null == user) {
			eResponse.setDescription(ErrorConstants.USER_DOESNOT_EXISTS);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}else{
			if (null != user.getRole()) {
				for (ExcelAndReportAdmins excelAndReportAdmins : ExcelAndReportAdmins.values()) {
					if (user.getRole().equals(excelAndReportAdmins.getValue())) {
						valid=true;
						break;
					}
				}
			}
		}
		if (!valid || !user.getIsPmpAllowed().equalsIgnoreCase("Y")) {
			eResponse.setDescription(ErrorConstants.ACCESS_DENIED_TO_VIEW_THIS_PAGE);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		return null;
	}

}
