package org.srcm.heartfulness.validator.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.enumeration.SessionSearchFields;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.request.SearchSession;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.SessionDetailsService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.SessionDetailsValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class SessionDetailsValidatorImpl implements SessionDetailsValidator{

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsValidatorImpl.class);

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	ProgramService programService;

	@Autowired
	SessionDetailsService sessionDetailsService;

	/**
	 * This method is used to validate the authentication token against
	 * MYSRCM.If the token is successfully authenticated then a success response
	 * is returned else an error response is returned.
	 * 
	 * @param authToken
	 *            authToken Token to authenticate with mysrcm.
	 * @param accessLog
	 *            to persist the api log details.
	 * @return PMPResponse success or failure response
	 */
	/*@Override
	public PMPResponse validateAuthToken(String authToken, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(authToken, accessLog.getId());
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			eResponse.setError_description("Invalid authorization token");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (HttpClientErrorException httpcee) {
			eResponse.setError_description("Invalid client credentials");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(httpcee));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (JsonParseException jpe) {
			eResponse.setError_description("Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(jpe));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (JsonMappingException jme) {
			eResponse.setError_description("Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(jme));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (IOException ioe) {
			eResponse.setError_description("Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} catch (Exception e) {
			eResponse.setError_description("Invalid request");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setError_description(ErrorConstants.INVALID_CREDENTIALS);
			accessLog.setErrorMessage("UserProfile doesnot exists in MySrcm database");
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			accessLog.setUsername(
					null == userProfile.getUser_email() ? userProfile.getEmail() 
							: userProfile.getUser_email().isEmpty() ? userProfile.getEmail() : userProfile.getUser_email());
			SuccessResponse sResponse = new SuccessResponse(ErrorConstants.STATUS_SUCCESS,
					"Token validation successfull");
			return sResponse;
		}
	}*/

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
			LOGGER.error("Failed to create or update access log ");
		}

	}

	/**
	 * Method is used to validate the mandatory session details parameters. If
	 * all the parameters are correct a success response is returned else an
	 * error response is returned.
	 * 
	 * @param sessionDetails
	 *            To validate mandatory session details parameters.
	 * @param accessLog
	 *            to create the pmp api access log details in db.
	 * @return success response is returned else an error response is returned
	 */
	@Override
	public PMPResponse validateSessionDetailsParams(SessionDetails sessionDetails, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");
		PMPAPIAccessLogDetails accessLogDetails = null;
		AbhyasiUserProfile userProfile = null;

		if (null == sessionDetails.getEventId() || sessionDetails.getEventId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_EVENT_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_EVENT_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			int programId = 0;
			try {
				programId = programService.getProgramIdByEventId(sessionDetails.getEventId());
			} catch (Exception ex) {
				LOGGER.error("Program Id is not available for AutoGenerated Id :" + sessionDetails.getEventId());
			}

			if (programId <= 0) {
				eResponse.setError_description(ErrorConstants.INVALID_EVENT_ID);
				accessLog.setErrorMessage(ErrorConstants.INVALID_EVENT_ID);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} else {
				sessionDetails.setProgramId(programId);
			}
		}

		if (null == sessionDetails.getSessionNumber() || sessionDetails.getSessionNumber().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_SESSION_NUMBER);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_SESSION_NUMBER);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else if (null == sessionDetails.getSessionStringDate() || sessionDetails.getSessionStringDate().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_SESSION_DATE);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_SESSION_DATE);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			try {
				Date sessionDate = DateUtils.parseDate(sessionDetails.getSessionStringDate());
				sessionDetails.setSessionDate(sessionDate);
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sessionDetails.setSessionStringDate(sdf.format(sessionDetails.getSessionDate()));
			} catch (Exception e) {
				eResponse.setError_description(ErrorConstants.INVALID_DATE_FORMAT);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if (sessionDetails.getNumberOfParticipants() <= 0) {
			eResponse.setError_description(ErrorConstants.INVALID_PCTPT_COUNT);
			accessLog.setErrorMessage(ErrorConstants.INVALID_PCTPT_COUNT);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if (sessionDetails.getNumberOfNewParticipants() < 0) {
			eResponse.setError_description(ErrorConstants.INVALID_NEW_PCTPT_COUNT);
			accessLog.setErrorMessage(ErrorConstants.INVALID_NEW_PCTPT_COUNT);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if (null == sessionDetails.getPreceptorEmail() || sessionDetails.getPreceptorEmail().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_PRECEPTOR_EMAIL);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_PRECEPTOR_EMAIL);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}else if(!sessionDetails.getPreceptorEmail().matches(ExpressionConstants.EMAIL_REGEX)){
			eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_EMAIL);
			accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_EMAIL);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if(null != sessionDetails.getPreceptorMobile()){
			if(!sessionDetails.getPreceptorMobile().isEmpty() && !sessionDetails.getPreceptorMobile().matches(ExpressionConstants.MOBILE_REGEX)){
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_MOBILE);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_MOBILE);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		String preceptorCardNo = sessionDetails.getPreceptorIdCardNo();
		if (null == preceptorCardNo || preceptorCardNo.isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_PRECEPTOR_ID_CARD_NO);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_PRECEPTOR_ID_CARD_NO);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {

			accessLogDetails = new PMPAPIAccessLogDetails(accessLog.getId(), EndpointConstants.GET_USER_PROFILE,
					DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null, preceptorCardNo,
					null);
			setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, null);

			try {

				userProfile = srcmRestTemplate.getAbyasiProfile(preceptorCardNo).getUserProfile()[0];

				if (null == userProfile) {
					eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
					accessLogDetails.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
					setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
					accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
					setPMPAccessLogAndPersist(accessLog, eResponse);
					return eResponse;
				}
				if (null == sessionDetails.getPreceptorName() || sessionDetails.getPreceptorName().isEmpty()) {
					sessionDetails.setPreceptorName(userProfile.getName());
				}
			} catch (HttpClientErrorException httpcee) {
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(httpcee));
				setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} catch (JsonParseException | JsonMappingException e) {
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
				setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} catch (Exception ex) {
				eResponse.setError_description(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, eResponse);
				accessLog.setErrorMessage(ErrorConstants.INVALID_PRECEPTOR_ID_CARD_NO);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}
		accessLogDetails.setErrorMessage("");
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		setPMPAPIAccessLogDetailsAndPersist(accessLogDetails, userProfile);
		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, "Session Details validation successfull");
	}

	/**
	 * Method is used to persist the pmp api access log details data in db.
	 * 
	 * @param accessLogDetails
	 *            to set the pmp api access log details data.
	 * @param responseBody
	 *            to set the response body which we get after calling MYSRCM api
	 *            to validate preceptor details.
	 */
	private void setPMPAPIAccessLogDetailsAndPersist(PMPAPIAccessLogDetails accessLogDetails, Object responseBody) {
		try {
			accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(responseBody));
			accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		} catch (Exception ex) {
			LOGGER.error("Failed to create or update access log details");
		}
	}

	/**
	 * Method is used to validate the delete session details api call
	 * parameters.For a particular event and a particular session this method
	 * will validate the parameters provided are correct or wrong.
	 * 
	 * @param sessionDetails
	 *            to get the auto generated event and session id.
	 * @param accessLog
	 *            to persist the access log details in db.
	 * @return success or error response depending on the parameters validated.
	 */
	@Override
	public PMPResponse validateDeleteSessionDetailParams(SessionDetails sessionDetails, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");

		if (null == sessionDetails.getEventId() || sessionDetails.getEventId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_EVENT_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_EVENT_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			int programId = 0;
			try {
				programId = programService.getProgramIdByEventId(sessionDetails.getEventId());
			} catch (Exception ex) {
				LOGGER.error("Program Id is not available for AutoGenerated Id :" + sessionDetails.getEventId());
			}

			if (programId <= 0) {
				eResponse.setError_description(ErrorConstants.INVALID_EVENT_ID);
				accessLog.setErrorMessage(ErrorConstants.INVALID_EVENT_ID);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} else {
				sessionDetails.setProgramId(programId);
			}
		}

		if (null == sessionDetails.getAutoGeneratedSessionId() || sessionDetails.getAutoGeneratedSessionId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_SESSION_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_SESSION_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			int sessionId = 0;
			try {
				sessionId = sessionDetailsService.getSessionId(sessionDetails.getAutoGeneratedSessionId());
			} catch (Exception ex) {
				LOGGER.error("Session Id is not available for AutoGenerated Id :"
						+ sessionDetails.getAutoGeneratedSessionId());
			}

			if (sessionId <= 0) {
				eResponse.setError_description(ErrorConstants.INVALID_SESSION_ID);
				accessLog.setErrorMessage(ErrorConstants.INVALID_SESSION_ID);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, DashboardConstants.VALIDATION_RESPONSE);

	}

	/**
	 * Method is used to validate the get session details list parametrs.
	 * 
	 * @param sessionDetails
	 *            object is used to get the auto generated event id.
	 * @param accessLog
	 *            is used to create log details in pmp.
	 * @return success or error response depending on the validation.
	 */
	@Override
	public PMPResponse validateGetSessionDetailsParams(SessionDetails sessionDetails, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");

		if (null == sessionDetails.getEventId() || sessionDetails.getEventId().isEmpty()) {
			eResponse.setError_description(ErrorConstants.EMPTY_EVENT_ID);
			accessLog.setErrorMessage(ErrorConstants.EMPTY_EVENT_ID);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		} else {
			int programId = 0;
			try {
				programId = programService.getProgramIdByEventId(sessionDetails.getEventId());
			} catch (Exception ex) {
				LOGGER.error("Program Id is not available for AutoGenerated Id :" + sessionDetails.getEventId());
			}

			if (programId <= 0) {
				eResponse.setError_description(ErrorConstants.INVALID_EVENT_ID);
				accessLog.setErrorMessage(ErrorConstants.INVALID_EVENT_ID);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			} else {
				sessionDetails.setProgramId(programId);
			}
		}

		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, DashboardConstants.VALIDATION_RESPONSE);
	}

	@Override
	public PMPResponse validateSearchSessionParams(SearchSession searchSession, PMPAPIAccessLog accessLog) {

		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");

		if(null == searchSession.getSearchField()){
			eResponse.setError_description(DashboardConstants.INVALID_SEARCH_FIELD);
			accessLog.setErrorMessage(DashboardConstants.INVALID_SEARCH_FIELD);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}else if(null == searchSession.getSearchText()){
			eResponse.setError_description(DashboardConstants.INVALID_SEARCH_TEXT);
			accessLog.setErrorMessage(DashboardConstants.INVALID_SEARCH_TEXT);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		for(SessionSearchFields fields : SessionSearchFields.values()){

			if(fields.name().equals(searchSession.getSearchField())){
				searchSession.setDbSearchField(fields.getFieldValue());
				break;
			}
		}
		if(null == searchSession.getDbSearchField() && !searchSession.getSearchField().isEmpty()){
			eResponse.setError_description(DashboardConstants.INVALID_SEARCH_FIELD);
			accessLog.setErrorMessage(DashboardConstants.INVALID_SEARCH_FIELD);
			setPMPAccessLogAndPersist(accessLog, eResponse);
			return eResponse;
		}

		if(null != searchSession.getDateFrom() && !searchSession.getDateFrom().isEmpty()){
			try {
				DateUtils.parseToSqlDate(searchSession.getDateFrom());
			} catch (ParseException e) {
				eResponse.setError_description(DashboardConstants.INVALID_SS_FROM_DATE);
				accessLog.setErrorMessage(DashboardConstants.INVALID_SS_FROM_DATE);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		if(null != searchSession.getDateTo() && !searchSession.getDateTo().isEmpty()){
			try {
				DateUtils.parseToSqlDate(searchSession.getDateTo());
			} catch (ParseException e) {
				eResponse.setError_description(DashboardConstants.INVALID_SS_TO_DATE);
				accessLog.setErrorMessage(DashboardConstants.INVALID_SS_TO_DATE);
				setPMPAccessLogAndPersist(accessLog, eResponse);
				return eResponse;
			}
		}

		SessionDetails sessionDetails = new SessionDetails();
		sessionDetails.setEventId(searchSession.getEventId());
		//sessionDetails.setAutoGeneratedSessionId(searchSession.getAutoGeneratedSessionId());

		PMPResponse pmpResponse = validateGetSessionDetailsParams(sessionDetails,accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return pmpResponse;
		}else{
			return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, DashboardConstants.VALIDATION_RESPONSE);
		}
	}

}
