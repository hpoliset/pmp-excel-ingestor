package org.srcm.heartfulness.validator.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.ReportsValidator;
import org.srcm.heartfulness.vo.ReportVO;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class ReportsValidatorImpl implements ReportsValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportsValidatorImpl.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Override
	public Response validateGenerateReportsRequest(PMPAPIAccessLog accessLog, String token, ReportVO reportVO) {
		Response eResponse = new Response(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
		} catch (HttpClientErrorException e) {
			eResponse.setDescription(ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("REPORTS : Exception while validating the user token. Exception : {} ", e);
			eResponse.setDescription(ErrorConstants.ERROR_WHILE_FETCHING_PROFILE_FROM_MYSRCM);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
			LOGGER.error("REPORTS : Exception while validating the user token. Exception : {} ", e);
			eResponse.setDescription(ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (Exception e) {
			LOGGER.error("REPORTS : Exception while validating the user token. Exception : {} ", e);
			eResponse.setDescription(ErrorConstants.ERROR_WHILE_FETCHING_PROFILE_FROM_MYSRCM);
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
			reportVO.setUsername(null == userProfile.getUser_email() ? userProfile.getEmail() 
					: userProfile.getUser_email().isEmpty() ? userProfile.getEmail() : userProfile.getUser_email());
		}
		User user = userProfileService.loadUserByEmail(null == userProfile.getUser_email() ? userProfile.getEmail() 
				: userProfile.getUser_email().isEmpty() ? userProfile.getEmail() : userProfile.getUser_email());
		if (null == user) {
			eResponse.setDescription(ErrorConstants.USER_DOESNOT_EXISTS);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		if ((null != user.getRole() && !user.getRole().isEmpty()
				&& !user.getRole().equalsIgnoreCase(PMPConstants.LOGIN_GCONNECT_ADMIN) && !user.getRole()
				.equalsIgnoreCase(PMPConstants.LOGIN_ROLE_ADMIN)) || !user.getIsPmpAllowed().equalsIgnoreCase("Y")) {
			eResponse.setDescription(ErrorConstants.ACCESS_DENIED_TO_VIEW_THIS_PAGE);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}

		Map<String, String> errors = new HashMap<String, String>();
		if (null == reportVO.getFromDate() || reportVO.getFromDate().isEmpty()) {
			errors.put("From Date", "From Date is required");
		}
		if (null == reportVO.getTillDate() || reportVO.getTillDate().isEmpty()) {
			errors.put("Till Date", "Till Date is required");
		}

		if (null != reportVO.getFromDate() && !reportVO.getFromDate().isEmpty() && null != reportVO.getTillDate()
				&& !reportVO.getTillDate().isEmpty()) {
			try {
				if (DateUtils.countNumDaysBetweenTwoDates(reportVO.getFromDate(), reportVO.getTillDate()) > 30) {
					errors.put("Date Range",
							"Please select valid Date range.Report can be generated for a maximum of 30 days.");
				}
				if (DateUtils.countNumDaysBetweenTwoDates(reportVO.getFromDate(), reportVO.getTillDate()) < 0) {
					errors.put("Date Range", "Please select valid Date range.");
				}
			} catch (ParseException e) {
				LOGGER.error(
						"Error while parsing the from date and to date for counting the date range. Exception : {} ", e);
				errors.put("Date Range", "Problem while counting Date range.");
			}
		}

		if (errors.size() > 0 || !errors.isEmpty()) {
			eResponse.setDescription(errors.toString());
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		return null;
	}

}
