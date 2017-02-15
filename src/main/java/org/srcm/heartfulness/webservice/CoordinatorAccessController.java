package org.srcm.heartfulness.webservice;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlErrorResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.CoordinatorAccessControlService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.CoordinatorAccessControlValidator;
import org.srcm.heartfulness.validator.EventDashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author Koustav Dutta
 *
 */

@RestController
@RequestMapping("/api/coordinatoraccess")
public class CoordinatorAccessController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessController.class);

	@Autowired
	CoordinatorAccessControlValidator coordntrAccssCntrlValidator ;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	CoordinatorAccessControlService coordntrAccssCntrlSrcv;

	
	/**
	 * This method is used to raise a request by the coordinators
	 * to access other events for which they don't have access.
	 * 
	 * @param token to be validated against MySRCM endpoint.
	 * @param event to get the auto generated event id of an event 
	 * for which the coordinator is requesting to get access.
	 * @param httpRequest to get the requested url details.
	 * @return CoordinatorAccessControlResponse 
	 * 
	 */
	@RequestMapping(value = "/addsecondarycoordinator", 
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addSecondaryCoordinatorRequest(@RequestHeader(value = "Authorization") String token,
			@RequestBody Event event,@Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,StackTraceUtils.convertPojoToJson(event));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
			if (null == userProfile) {

				LOGGER.info("UserProfile doesnot exists in MySrcm database");
				CoordinatorAccessControlErrorResponse eResponse = new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
				accessLog.setErrorMessage("UserProfile doesnot exists in MySrcm database");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<CoordinatorAccessControlErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}
			accessLog.setUsername(userProfile.getEmail());

			CoordinatorAccessControlErrorResponse eResponse = coordntrAccssCntrlValidator.checkMandatoryFields(event.getAutoGeneratedEventId());
			if(null != eResponse){
				accessLog.setErrorMessage(eResponse.getError_description());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<CoordinatorAccessControlErrorResponse>(eResponse, HttpStatus.PRECONDITION_REQUIRED);	
			}

			CoordinatorAccessControlResponse response = coordntrAccssCntrlSrcv.addSecondaryCoordinatorRequest(event.getAutoGeneratedEventId(),userProfile.getEmail(),accessLog);
			return new ResponseEntity<CoordinatorAccessControlResponse>(response,HttpStatus.OK);

		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {

			LOGGER.error("IllegalBlockSizeException | NumberFormatException | BadPaddingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid authorization token");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);

		} catch (HttpClientErrorException e) {

			LOGGER.error("HttpClientErrorException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);

		} catch (JsonParseException e) {

			LOGGER.error("JsonParseException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (JsonMappingException e) {

			LOGGER.error("JsonMappingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (IOException e) {

			LOGGER.error("IOException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {

			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid request");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		}
	}



	/**
	 * This method is used to approve the requests raised 
	 * against a particular event.
	 * @param token ,Token to be validated against MySRCM endpoint.
	 * @param pgrmCoordinators Details of the secondary coordinators
	 * who wants to raise a request to access other events
	 * @param httpRequest to get the requested url details.
	 * @return CoordinatorAccessControlResponse depending on the 
	 * success or failure response.
	 */
	@RequestMapping(value = "/approve", method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> approveSecondaryCoordinatorRequest(@RequestHeader(value = "Authorization") String token,@RequestBody ProgramCoordinators pgrmCoordinators
			,@Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,StackTraceUtils.convertPojoToJson(pgrmCoordinators));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		UserProfile userProfile = null;
		try {
			
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
			if (null == userProfile) {
				
				LOGGER.info("UserProfile doesnot exists in MySrcm database");
				CoordinatorAccessControlErrorResponse eResponse = new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
				accessLog.setErrorMessage("UserProfile doesnot exists in MySrcm database");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<CoordinatorAccessControlErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}
			accessLog.setUsername(userProfile.getEmail());
			
			CoordinatorAccessControlResponse response = coordntrAccssCntrlValidator.validateCoordinatorRequest(userProfile.getEmail(),pgrmCoordinators);
			if(response instanceof CoordinatorAccessControlErrorResponse){
				accessLog.setErrorMessage(((CoordinatorAccessControlErrorResponse) response).getError_description());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<CoordinatorAccessControlResponse>(response,HttpStatus.OK);
			}else {
				CoordinatorAccessControlResponse srcvResponse = coordntrAccssCntrlSrcv.approveSecondaryCoordinatorRequest(userProfile.getEmail(),pgrmCoordinators,accessLog);
				return new ResponseEntity<CoordinatorAccessControlResponse>(srcvResponse,HttpStatus.OK);
			}

		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {

			LOGGER.error("IllegalBlockSizeException | NumberFormatException | BadPaddingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid authorization token");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);

		} catch (HttpClientErrorException e) {

			LOGGER.error("HttpClientErrorException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);

		} catch (JsonParseException e) {

			LOGGER.error("JsonParseException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (JsonMappingException e) {

			LOGGER.error("JsonMappingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (IOException e) {

			LOGGER.error("IOException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {

			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid request");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		}
	}


	/**
	 * This method is used to get the list of pending
	 * request and approved request for a particular event.
	 * @param token,Token to be validated against MySRCM endpoint.
	 * @param event , eventId is used to get the list for an event.
	 * @param httpRequest httpRequest to get the requested url details.
	 * @return List<SecondaryCoordinatorRequest> or empty list depending
	 * if any request are available or not.
	 */
	@RequestMapping(value = "/getlistofsecondarycoordinator", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE , 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getRequestedCoordinatorList(@RequestHeader(value = "Authorization") String token,@RequestBody Event event,
			@Context HttpServletRequest httpRequest){

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,event.getAutoGeneratedEventId());
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
			if (null == userProfile) {
				LOGGER.info("UserProfile doesnot exists in MySrcm database");
				CoordinatorAccessControlErrorResponse eResponse = new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
				accessLog.setErrorMessage("UserProfile doesnot exists in MySrcm database");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<CoordinatorAccessControlErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}

			accessLog.setUsername(userProfile.getEmail());
			CoordinatorAccessControlResponse response = coordntrAccssCntrlValidator.validateRequesterDetails(userProfile.getEmail(),event.getAutoGeneratedEventId());

			if(response instanceof CoordinatorAccessControlErrorResponse){

				accessLog.setErrorMessage(((CoordinatorAccessControlErrorResponse) response).getError_description());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<CoordinatorAccessControlResponse>(response,HttpStatus.OK);

			}else {
				return coordntrAccssCntrlSrcv.getListOfSecondaryCoordinatorRequests(event.getAutoGeneratedEventId(),accessLog);
			}



		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {

			LOGGER.error("IllegalBlockSizeException | NumberFormatException | BadPaddingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid authorization token");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);

		} catch (HttpClientErrorException e) {

			LOGGER.error("HttpClientErrorException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);

		} catch (JsonParseException e) {

			LOGGER.error("JsonParseException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (JsonMappingException e) {

			LOGGER.error("JsonMappingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (IOException e) {

			LOGGER.error("IOException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from MySRCM");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {

			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid request");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		}
	}

}
