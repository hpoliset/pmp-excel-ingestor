package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.model.json.response.UpdateIntroductionResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class holds the web service end points for participant related dashboard
 * services.
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/participant")
public class ParticipantsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantsController.class);

	@Autowired
	private ProgramService programService;

	@Autowired
	private PmpParticipantService participantService;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	APIAccessLogService apiAccessLogService;

	/**
	 * Web service endpoint to fetch list of participants.
	 * 
	 * If list of participants are found successfully, the service returns an
	 * success response body with HTTP status 200.
	 * 
	 * If list of events are not found, the service returns an empty response
	 * body with HTTP status 200.
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param encryptedProgramId
	 *            to check whether the program already exists or not.
	 * @param token
	 *            Token to be validated against MySRCM endpoint.
	 * @return A ResponseEntity containing participant details if found, and a
	 *         HTTP status code as described in the method comment.
	 */
	@RequestMapping(value = "/getparticipantlist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getParticipantList(@RequestHeader(value = "Authorization") String token,
			@RequestBody(required = true) Event event, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(event), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		LOGGER.info("Fetching participant List. logger ID: {} " ,id);
		UserProfile userProfile = null;
		List<ParticipantRequest> participantList = new ArrayList<ParticipantRequest>();
		try {
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				LOGGER.info("UserProfile doesnot exists in MySrcm database");
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage("Invalid client credentials");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}
			accessLog.setUsername(userProfile.getEmail());
			if (null == event.getAutoGeneratedEventId() || event.getAutoGeneratedEventId().isEmpty()) {
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid Event ID");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage("Invalid Event ID");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			participantList = programService.getParticipantByEventId(event.getAutoGeneratedEventId());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setResponseBody(null);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<List<ParticipantRequest>>(participantList, HttpStatus.OK);

		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from mysrcm");
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from mysrcm");
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from mysrcm");
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid request");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Web service endpoint to fetch participant details.
	 * 
	 * @param request
	 *            contains seqId and eventId to get the participant details
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing participant details if found, and a
	 *         HTTP status code as described in the method comment.
	 */
	@RequestMapping(value = "/getparticipantdetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getPaticipantDetails(@RequestBody ParticipantRequest request,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(request), null);

		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		UserProfile userProfile = null;
		try {
			LOGGER.info("Fetching participant details. logger ID: {} ",id);
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			}
			accessLog.setUsername(userProfile.getEmail());
			if (null == request.getSeqId() || request.getSeqId().isEmpty() || null == request.getEventId()
					|| request.getEventId().isEmpty()) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, "event ID and Seq Id is required");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setErrorMessage("event ID and Seq Id is required");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.PRECONDITION_FAILED);
			} else {
				ParticipantRequest participant = participantService.getParticipantBySeqId(request);
				if (null != participant) {
					accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(participant));
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					return new ResponseEntity<ParticipantRequest>(participant, HttpStatus.OK);
				} else {
					ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid Seq Id");
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setErrorMessage("Invalid Seq Id");
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					return new ResponseEntity<ErrorResponse>(error, HttpStatus.PRECONDITION_FAILED);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Error while fecthing particpant details.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to create new participant.
	 * 
	 * @param request
	 *            contains eventId and participant information to create new
	 *            participant
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing participant details after creating
	 *         participant, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createParticipant(@RequestBody ParticipantRequest participant,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(participant), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		LOGGER.info("create participant  logger ID: {} " ,id);
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setErrorMessage(ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				accessLog.setUsername(userProfile.getEmail());
				Map<String, String> map = eventDashboardValidator.checkParticipantMandatoryFields(participant);
				if (!map.isEmpty()) {
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(map));
					accessLog.setErrorMessage(map.toString());
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					map.put("status", ErrorConstants.STATUS_FAILED);
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					ParticipantRequest newparticipant = participantService.createParticipant(participant);
					accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(newparticipant));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					return new ResponseEntity<ParticipantRequest>(newparticipant, HttpStatus.OK);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Error while creating particpant.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to update participant details.
	 * 
	 * @param request
	 *            contains seqId, eventId and other participant details which
	 *            need to be updated
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing participant details after updating
	 *         participant details, and a HTTP status code as described in the
	 *         method comment.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateParticipant(@RequestBody ParticipantRequest participant,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(participant), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		LOGGER.info("update particpant : logger ID: {} " ,id);
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setErrorMessage(ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			}
			accessLog.setUsername(userProfile.getEmail());

			Map<String, String> errors = eventDashboardValidator.checkUpdateParticipantMandatoryFields(participant);
			if (!errors.isEmpty()) {
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(errors));
				accessLog.setErrorMessage(errors.toString());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				errors.put("status", ErrorConstants.STATUS_FAILED);
				return new ResponseEntity<Map<String, String>>(errors, HttpStatus.PRECONDITION_FAILED);
			}
			ParticipantRequest newparticipant = participantService.createParticipant(participant);
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(newparticipant));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ParticipantRequest>(newparticipant, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Web service endpoint to delete participant from participant details and
	 * logs the deleted participant information in the deleted_participants
	 * table.
	 * 
	 * @param request
	 *            contains eventId and its list of seqIds to delete the
	 *            participant from participant table
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing status and its description based on
	 *         deletion, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteParticipant(@RequestBody ParticipantIntroductionRequest participantRequest,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(participantRequest), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		LOGGER.info("delete particpant : logger ID: {} " ,id);
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setErrorMessage(ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				accessLog.setUsername(userProfile.getEmail());
				Map<String, String> map = eventDashboardValidator.checkDeleteRequestMandatoryFields(participantRequest);
				if (!map.isEmpty()) {
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setErrorMessage(map.toString());
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(map));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					map.put("status", ErrorConstants.STATUS_FAILED);
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					List<UpdateIntroductionResponse> result = participantService.deleteParticipantsBySeqID(
							participantRequest, userProfile.getEmail());
					accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(result));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					return new ResponseEntity<List<UpdateIntroductionResponse>>(result, HttpStatus.OK);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"client-error : Invalid auth token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"json mapping-error : json data is not mapped properly");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to update participant introduction status for the
	 * list of participants.
	 * 
	 * @param request
	 *            contains eventId and its list of seqIds to update the
	 *            participant from participant table
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing status and its description based on
	 *         updation, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/updateintroductionstatus", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> UpdateParticipantIntroducedStatus(
			@RequestBody ParticipantIntroductionRequest participantRequest,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(participantRequest), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setErrorMessage(ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				LOGGER.info("SATRT :  logger ID: {} : Update introduction status method called : Partcicipants count - {} ",id,
						participantRequest.getParticipantIds().size());
				accessLog.setUsername(userProfile.getEmail());
				Map<String, String> map = eventDashboardValidator.checkIntroductionRequestMandatoryFields(
						participantRequest, id);
				if (!map.isEmpty()) {
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(map));
					accessLog.setErrorMessage(map.toString());
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					map.put("status", ErrorConstants.STATUS_FAILED);
					LOGGER.info("END : Update introduction status call : Partcicipants count - {} ",
							participantRequest.getParticipantIds().size());
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					List<UpdateIntroductionResponse> result = participantService.introduceParticipants(
							participantRequest, userProfile.getEmail(), id);
					accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(result));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					LOGGER.info("END : Update introduction status call : Partcicipants count - {} ",
							participantRequest.getParticipantIds().size());
					return new ResponseEntity<List<UpdateIntroductionResponse>>(result, HttpStatus.OK);
				}
			}

		} catch (HttpClientErrorException e) {
			LOGGER.error("HttpClientErrorException    :" + StackTraceUtils.convertStackTracetoString(e));
			Response eResponse = new Response(ErrorConstants.STATUS_FAILED,
					"client-error : Invalid auth token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception in Encryption/decryption :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("JsonParseException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("JsonMappingException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"json mapping-error : json data is not mapped properly");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("IOException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web Service end point to search the participants based on the given search
	 * field and search text and based on program start date
	 * 
	 * @param token
	 * @param searchRequest
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> searchEvents(@RequestHeader(value = "Authorization") String token,
			@RequestBody SearchRequest searchRequest, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(searchRequest), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		UserProfile userProfile = null;
		try {
			List<ParticipantRequest> participantList = new ArrayList<>();
			userProfile = eventDashboardValidator.validateToken(token, id);
			if (null == userProfile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setErrorMessage(ErrorConstants.INVALID_AUTH_TOKEN);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				accessLog.setUsername(userProfile.getEmail());
				participantList = participantService.searchParticipants(searchRequest);
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(participantList));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<List<ParticipantRequest>>(participantList, HttpStatus.OK);
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"client-error : Invalid auth token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"json mapping-error : json data is not mapped properly");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
