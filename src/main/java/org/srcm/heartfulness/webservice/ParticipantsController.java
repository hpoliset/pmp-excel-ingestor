package org.srcm.heartfulness.webservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.UpdateIntroductionResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.impl.PMPAuthTokenValidatorImpl;

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

	@Autowired
	PMPAuthTokenValidatorImpl authTokenVldtr;

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
	@RequestMapping(value = "/getparticipantlist", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getParticipantList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = true) Event event, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(event));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}
		LOGGER.info("Fetching participant List. logger ID: {} " ,accessLog.getId());
		List<ParticipantRequest> participantList = new ArrayList<ParticipantRequest>();

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		if (null == event.getAutoGeneratedEventId() || event.getAutoGeneratedEventId().isEmpty()) {
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.INVALID_OR_EMPTY_EVENTID);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.INVALID_OR_EMPTY_EVENTID, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}
		try{
			participantList = programService.getParticipantByEventId(event.getAutoGeneratedEventId(),emailList,user.getRole(),authToken,accessLog);
		} catch (Exception e) {
			LOGGER.error("Exception while fetching participant list  :{}", e);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS,null, StackTraceUtils.convertPojoToJson(participantList));
		return new ResponseEntity<List<ParticipantRequest>>(participantList, HttpStatus.OK);
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
	@RequestMapping(value = "/getparticipantdetails", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getPaticipantDetails(@RequestBody ParticipantRequest request,
			@RequestHeader(value = "Authorization") String authToken, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(request));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		if (null == request.getSeqId() || request.getSeqId().isEmpty()){
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.SEQ_ID_REQUIRED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.SEQ_ID_REQUIRED, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
		}else if(null == request.getEventId() || request.getEventId().isEmpty()){
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.INVALID_OR_EMPTY_EVENTID);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.INVALID_OR_EMPTY_EVENTID, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
		}else{
			ResponseEntity<?> response = participantService.getParticipantBySeqId(request,emailList,user.getRole(),accessLog,authToken);
			updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(response));
			return response;
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
	@RequestMapping(value = "/create", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> createParticipant(@RequestBody ParticipantRequest participant,
			@RequestHeader(value = "Authorization") String authToken, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(participant));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		//Map<String, String> map = eventDashboardValidator.checkParticipantMandatoryFields(participant);
		Map<String, String> map = eventDashboardValidator.checkParticipantMandatoryFields(emailList,user.getRole(),participant,authToken,accessLog);
		if (!map.isEmpty()) {
			map.put(DashboardConstants.STATUS, ErrorConstants.STATUS_FAILED);
			updatePMPAPIAccessLog(accessLog, ErrorConstants.STATUS_FAILED, map.toString(), StackTraceUtils.convertPojoToJson(map));
			return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
		} else {
			ParticipantRequest newparticipant;
			try {
				newparticipant = participantService.createParticipant(participant);
			} catch (Exception e) {
				LOGGER.error("Error while craeting participant record {}",e);
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
				updatePMPAPIAccessLog(accessLog, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(eResponse));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			updatePMPAPIAccessLog(accessLog, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(newparticipant));
			return new ResponseEntity<ParticipantRequest>(newparticipant, HttpStatus.OK);
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
	@RequestMapping(value = "/update", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> updateParticipant(@RequestBody ParticipantRequest participant,
			@RequestHeader(value = "Authorization") String authToken, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(participant));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		//Map<String, String> errors = eventDashboardValidator.checkUpdateParticipantMandatoryFields(participant);
		Map<String, String> errors = eventDashboardValidator.checkUpdateParticipantMandatoryFields(emailList,user.getRole(),participant,authToken,accessLog);
		if (!errors.isEmpty()) {
			errors.put(DashboardConstants.STATUS, ErrorConstants.STATUS_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, errors.toString(), StackTraceUtils.convertPojoToJson(errors));
			return new ResponseEntity<Map<String, String>>(errors, HttpStatus.PRECONDITION_FAILED);
		}
		ParticipantRequest newparticipant;
		try {
			newparticipant = participantService.createParticipant(participant);
		} catch (Exception e) {
			LOGGER.error("Exception while updating participant information   {}", e);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(newparticipant));
		return new ResponseEntity<ParticipantRequest>(newparticipant, HttpStatus.OK);
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
	@RequestMapping(value = "/delete", 
			method = RequestMethod.DELETE, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> deleteParticipant(@RequestBody ParticipantIntroductionRequest participantRequest,
			@RequestHeader(value = "Authorization") String authToken, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(participantRequest));
		LOGGER.info("delete particpant : logger ID: {} " ,accessLog.getId());

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		try{

			//Map<String, String> map = eventDashboardValidator.checkDeleteRequestMandatoryFields(participantRequest);
			Map<String, String> map = eventDashboardValidator.checkDeleteRequestMandatoryFields(emailList, user.getRole(), participantRequest, authToken, accessLog);
			if (!map.isEmpty()) {

				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, map.toString(), StackTraceUtils.convertPojoToJson(map));
				map.put("status", ErrorConstants.STATUS_FAILED);
				return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);

			} else {

				List<UpdateIntroductionResponse> result = participantService.deleteParticipantsBySeqID(participantRequest, accessLog.getUsername());
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(result));
				return new ResponseEntity<List<UpdateIntroductionResponse>>(result, HttpStatus.OK);
			}

		} catch(Exception ex){

			LOGGER.error("Exception while deleting participant information   {}", ex);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
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
	@RequestMapping(value = "/updateintroductionstatus", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> UpdateParticipantIntroducedStatus(
			@RequestBody ParticipantIntroductionRequest participantRequest,
			@RequestHeader(value = "Authorization") String authToken, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(participantRequest));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		try{

			LOGGER.info("SATRT :  logger ID: {} : Update introduction status method called : Partcicipants count - {} ",accessLog.getId(),
					participantRequest.getParticipantIds().size());

			Map<String, String> map = eventDashboardValidator.checkIntroductionRequestMandatoryFields(participantRequest, accessLog.getId());

			if (!map.isEmpty()) {

				map.put("status", ErrorConstants.STATUS_FAILED);
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, map.toString(), StackTraceUtils.convertPojoToJson(map));
				LOGGER.info("END : Update introduction status call : Partcicipants count - {} ",participantRequest.getParticipantIds().size());
				return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);

			} else {

				List<UpdateIntroductionResponse> result = participantService.introduceParticipants(
						participantRequest, accessLog.getUsername(), accessLog.getId());

				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(result));
				LOGGER.info("END : Update introduction status call : Partcicipants count - {} ",
						participantRequest.getParticipantIds().size());
				return new ResponseEntity<List<UpdateIntroductionResponse>>(result, HttpStatus.OK);

			}

		} catch(Exception ex){
			LOGGER.error("Exception while updating intoduction status  {}", ex);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
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
	@RequestMapping(value = "/search", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> searchParticipants(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody SearchRequest searchRequest, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(searchRequest));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		try{

			List<ParticipantRequest> participantList = new ArrayList<>();
			participantList = participantService.searchParticipants(searchRequest);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(participantList));
			return new ResponseEntity<List<ParticipantRequest>>(participantList, HttpStatus.OK);

		} catch(Exception ex){

			LOGGER.error("Exception while searching participant information   {}", ex);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/upload/exceldata", 
			method = RequestMethod.POST, 
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> createParticipant(@RequestParam("file") MultipartFile multipartFile,
			@RequestHeader(value = "Authorization") String authToken, @Context HttpServletRequest httpRequest,
			@RequestParam("eventId")String eventId) {

		String fileDetails =   "Filename         :" + multipartFile.getOriginalFilename()
		+ "File Size     :" + multipartFile.getSize()
		+ "Content-Type    :" + multipartFile.getContentType();

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson(fileDetails));

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {

			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		try{

			List<String> details = null;

			String errorMsg  = eventDashboardValidator.checkProgramAccess(emailList, user.getRole(), eventId, authToken , accessLog);
			if(!errorMsg.isEmpty()){
				ExcelUploadResponse excelUploadResponse = new ExcelUploadResponse();
				excelUploadResponse.setFileName(multipartFile.getOriginalFilename());
				excelUploadResponse.setStatus(ErrorConstants.STATUS_FAILED);
				excelUploadResponse.setErrorMsg(Arrays.asList(errorMsg));
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errorMsg, StackTraceUtils.convertPojoToJson(excelUploadResponse));
				return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse, HttpStatus.PRECONDITION_FAILED);
			}

			try{
				details = programService.fetchProgramAndParticipantDetails(eventId);
				if(Integer.parseInt(details.get(0)) <= 0){
					ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,ErrorConstants.INVALID_EVENT_ID);
					updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,ErrorConstants.INVALID_EVENT_ID, StackTraceUtils.convertPojoToJson(eResponse));
					return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
				}
			}catch(Exception ex){

				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,ErrorConstants.INVALID_EVENT_ID);
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertPojoToJson(eResponse), StackTraceUtils.convertStackTracetoString(ex));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			ResponseEntity<?> response = participantService.validateExcelAndPersistParticipantData(multipartFile.getOriginalFilename(), multipartFile.getBytes(),accessLog,details);
			details.clear();
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,null, StackTraceUtils.convertPojoToJson(response));
			return response;

		} catch(Exception ex){

			LOGGER.error("Exception while uploading participant information through dashboard   {}", ex);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	private PMPAPIAccessLog createPMPAPIAccessLog(String username,HttpServletRequest httpRequest,String requestBody){

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(username, httpRequest.getRemoteAddr(), 
				httpRequest.getRequestURI(),DateUtils.getCurrentTimeInMilliSec(), null, 
				ErrorConstants.STATUS_FAILED, null,requestBody);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		return accessLog;
	}


	private void updatePMPAPIAccessLog(PMPAPIAccessLog pmpApiAccessLog, String status, String errorMessage, String responseBody){

		pmpApiAccessLog.setStatus(status);
		pmpApiAccessLog.setErrorMessage(errorMessage);
		pmpApiAccessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		pmpApiAccessLog.setResponseBody(responseBody);
		apiAccessLogService.updatePmpAPIAccessLog(pmpApiAccessLog);
	}
}
