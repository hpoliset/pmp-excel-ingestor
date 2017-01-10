package org.srcm.heartfulness.webservice;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.helper.SessionDetailsHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.SessionDetailsService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * @author Koustav Dutta
 *
 */
@RestController
@RequestMapping("/api/event")
public class SessionDetailsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsController.class);

	@Autowired
	SessionDetailsHelper sessionDtlsHlpr;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SessionDetailsService sessionDtlsSrcv;

	/**
	 * Web service endpoint to create and update a session details for a
	 * particular event. The HTTP request body is expected to contain a
	 * SessionDetails object in JSON format. The SessionDetails is persisted in
	 * the data repository.
	 * 
	 * If created or updated successfully, the service returns an success
	 * response body with HTTP status 200.
	 * 
	 * If not created successfully, the service returns error messages.
	 * 
	 * @param authToken
	 *            Token to be validated against mysrcm endpoint.
	 * @param sessionDetails
	 *            SessionDetails object to be created in PMP backend.
	 * @param httpRequest
	 *            to track the ip address from where request is coming.
	 * @return SessionDetails json object if SessionDetails is created
	 *         successfully.
	 * 
	 */
	@RequestMapping(value = "/session", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createOrUpdateSessionDetails(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody SessionDetails sessionDetails, @Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(sessionDetails));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		PMPResponse tokenResponse = sessionDtlsHlpr.validateAuthToken(authToken, accessLog);
		if (tokenResponse instanceof ErrorResponse) {
			return new ResponseEntity<PMPResponse>(tokenResponse, HttpStatus.OK);
		}

		PMPResponse validationResponse = sessionDtlsHlpr.validateSessionDetailsParams(sessionDetails, accessLog);
		if (validationResponse instanceof ErrorResponse) {
			return new ResponseEntity<PMPResponse>(validationResponse, HttpStatus.OK);
		}

		PMPResponse serviceResponse = sessionDtlsSrcv.saveOrUpdateSessionDetails(sessionDetails);

		if (serviceResponse instanceof SuccessResponse) {

			accessLog.setErrorMessage("");
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(sessionDetails));
			if (((SuccessResponse) serviceResponse).getSuccess_description().equals(
					ErrorConstants.SESSION_SUCCESSFULLY_CREATED)) {
				try {
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				} catch (Exception ex) {
					LOGGER.error("Exception while updating logger", ex);
				}
				return new ResponseEntity<SessionDetails>(sessionDetails, HttpStatus.OK);
			} else if (((SuccessResponse) serviceResponse).getSuccess_description().equals(
					ErrorConstants.SESSION_SUCCESSFULLY_UPDATED)) {
				try {
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				} catch (Exception ex) {
					LOGGER.error("Exception while updating logger", ex);
				}
				return new ResponseEntity<PMPResponse>(serviceResponse, HttpStatus.OK);
			}

		}

		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson(serviceResponse));
		accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(serviceResponse));
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);

		return new ResponseEntity<PMPResponse>(serviceResponse, HttpStatus.OK);
	}

	/**
	 * Web service endpoint to delete a session details for a particular event.
	 * The HTTP request body is expected to contain a SessionDetails object in
	 * JSON format. The SessionDetails is deleted from the data repository.
	 * 
	 * If session details is deleted successfully, the service returns an
	 * success response body with HTTP status 200.
	 * 
	 * If not created successfully, the service returns error messages.
	 * 
	 * 
	 * @param authToken
	 *            authToken Token to be validated against mysrcm endpoint.
	 * @param sessionDetails
	 *            SessionDetails object to get the auto generated session id for
	 *            deleting session details.
	 * @param httpRequest
	 *            to track the ip address from where request is coming.
	 * @return Success response if Session details is successfully deleted.
	 * 
	 */
	@RequestMapping(value = "/session/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteSessionDetails(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody SessionDetails sessionDetails, @Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(sessionDetails));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		PMPResponse tokenResponse = sessionDtlsHlpr.validateAuthToken(authToken, accessLog);
		if (tokenResponse instanceof ErrorResponse) {
			return new ResponseEntity<PMPResponse>(tokenResponse, HttpStatus.OK);
		}

		PMPResponse validationResponse = sessionDtlsHlpr.validateDeleteSessionDetailParams(sessionDetails, accessLog);
		if (validationResponse instanceof ErrorResponse) {
			return new ResponseEntity<PMPResponse>(validationResponse, HttpStatus.OK);
		}

		PMPResponse serviceResponse = sessionDtlsSrcv.deleteSessionDetail(sessionDetails);

		if (serviceResponse instanceof SuccessResponse) {
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setErrorMessage("");
		} else {
			accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson(serviceResponse));
		}

		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(serviceResponse));
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);

		return new ResponseEntity<PMPResponse>(serviceResponse, HttpStatus.OK);
	}

	/**
	 * Web service endpoint to get a list of session details for a particular
	 * event. The HTTP request body is expected to contain a SessionDetails
	 * object in JSON format.
	 * 
	 * If session details are avaibale for a particular event, it is returned in
	 * a list format with HTTP status 200.
	 * 
	 * If not an empty list is returned.
	 * 
	 * @param authToken
	 *            Token to be validated against mysrcm endpoint.
	 * @param sessionDetails
	 *            SessionDetails object to get the auto generated event id and
	 *            get a list of SessionDetails for that particular event.
	 * @param httpRequest
	 *            to track the ip address from where request is coming.
	 * @return list of SessionDetails for a particular event.
	 * 
	 */
	@RequestMapping(value = "/session/sessionlist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSessionDetails(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody SessionDetails sessionDetails, @Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(sessionDetails));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		PMPResponse tokenResponse = sessionDtlsHlpr.validateAuthToken(authToken, accessLog);
		if (tokenResponse instanceof ErrorResponse) {
			return new ResponseEntity<PMPResponse>(tokenResponse, HttpStatus.OK);
		}

		PMPResponse validationResponse = sessionDtlsHlpr.validateGetSessionDetailsParams(sessionDetails, accessLog);
		if (validationResponse instanceof ErrorResponse) {
			return new ResponseEntity<PMPResponse>(validationResponse, HttpStatus.OK);
		}

		List<SessionDetails> sessionDtlsList = sessionDtlsSrcv.getSessionDetails(sessionDetails.getProgramId(),
				sessionDetails.getEventId());

		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(sessionDtlsList));
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);

		return new ResponseEntity<List<SessionDetails>>(sessionDtlsList, HttpStatus.OK);
	}

}
