/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.SessionDetailsService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class SessionDetailsHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsHelper.class);

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	ProgramService programService;

	@Autowired
	SessionDetailsService sessionDtlsSrcv;

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
			accessLog.setUsername(userProfile.getEmail());
			SuccessResponse sResponse = new SuccessResponse(ErrorConstants.STATUS_SUCCESS,
					"Token validation successfull");
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
				if (null == sessionDetails.getPreceptorName()) {
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
	 * Method is used to generate an auto generated session id for every session
	 * details object.
	 * 
	 * @param digit
	 *            to set the limit of auto generated id.
	 * @return an auto generated session id.
	 */
	public String generateSessionId(int digit) {
		String generatedNumber = new String();
		SecureRandom secureRandomGenerator;
		try {
			secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
			byte[] randomBytes = new byte[128];
			secureRandomGenerator.nextBytes(randomBytes);
			int generatedInt = secureRandomGenerator.nextInt();
			generatedNumber = Integer.valueOf(Math.abs(generatedInt)).toString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Exception while generating Session Id {}", e.getMessage());
		}
		return generatedNumber.substring(0, digit);
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
				sessionId = sessionDtlsSrcv.getSessionId(sessionDetails.getAutoGeneratedSessionId());
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

		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, "Delete Session Details validation successfull");

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

		return new SuccessResponse(ErrorConstants.STATUS_SUCCESS, "Get Session Details validation successfull");
	}

}
