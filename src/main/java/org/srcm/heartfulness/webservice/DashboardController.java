/**
 * 
 */
package org.srcm.heartfulness.webservice;

import java.util.ArrayList;
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
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.DashboardService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.DashboardValidator;
import org.srcm.heartfulness.validator.impl.PMPAuthTokenValidatorImpl;

/**
 * @author Koustav Dutta
 *
 */
@RestController
@RequestMapping(value = "/api/dashboard/")
public class DashboardController {

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	PMPAuthTokenValidatorImpl authTokenVldtr;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	DashboardService dashboardService;

	@Autowired
	DashboardValidator dashboardValidator;

	
	/**
	 * Web service endpoint to get the counts of event,
	 * participant,session,event location and future event counts.
	 * 
	 * If counts are fetched successfully then success response 
	 *  with counts is returned and HTTP status is 200.
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param authToken,Token to be validated against MySRCM endpoint.
	 * @param dashboardReq, Request object to get input params.
	 * @param httpRequest, to fetch ipAddess of the request.
	 * @return DashboardResponse if successfully fetched count information.
	 */
	@RequestMapping(value = "/getcount",
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )

	public ResponseEntity<?> getEventDetails(
			@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DashboardRequest dashboardReq,
			@Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		if(null != dashboardReq.getFromDate() && !dashboardReq.getFromDate().isEmpty()){

			if(null == dashboardValidator.convertToSqlDate(dashboardReq.getFromDate())){
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.INVALID_SS_FROM_DATE);
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.INVALID_SS_FROM_DATE, StackTraceUtils.convertPojoToJson(eResponse));
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}else{
				dashboardReq.setSqlFromDate(dashboardValidator.convertToSqlDate(dashboardReq.getFromDate()));
			}
		}

		if(null != dashboardReq.getToDate() && !dashboardReq.getToDate().isEmpty()){
			if(null == dashboardValidator.convertToSqlDate(dashboardReq.getToDate())){
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.INVALID_SS_TO_DATE);
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.INVALID_SS_TO_DATE , StackTraceUtils.convertPojoToJson(eResponse));
				return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
			}else{
				dashboardReq.setSqlTodate(dashboardValidator.convertToSqlDate(dashboardReq.getToDate()));
			}
		}


		LOGGER.info("Trying to fetch dashboard data for log in user {}",accessLog.getUsername());
		ResponseEntity<?> dashboardRsp = dashboardService.getDashboardDataCounts(authToken,dashboardReq,accessLog,user);
		updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(dashboardRsp));
		return dashboardRsp;

	}

	@RequestMapping(value = "/getzones",
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<?> getZoneDetails(@RequestHeader(value = "Authorization") String authToken, 
			@RequestBody DashboardRequest dashboardReq, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
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

		LOGGER.info("Trying to fetch list of zones for log in user {}",accessLog.getUsername());
		ResponseEntity<?> dashboardRsp = dashboardService.getListOfZones(authToken,dashboardReq,accessLog,emailList,user.getRole());
		updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(dashboardRsp));
		return dashboardRsp;
	}

	@RequestMapping(value = "/getcenters", 
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<?> getCenters(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DashboardRequest dashboardReq, @Context HttpServletRequest httpRequest) {

		List<String> emailList = new ArrayList<String>();
		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		ErrorResponse errResopnse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(null != errResopnse){
			LOGGER.info("{} for log in user {}",errResopnse.getError_description(),accessLog.getUsername());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errResopnse.getError_description(), StackTraceUtils.convertPojoToJson(errResopnse));
			return new ResponseEntity<ErrorResponse>(errResopnse, HttpStatus.PRECONDITION_REQUIRED);
		}

		errResopnse = dashboardValidator.validateZoneField(dashboardReq.getZone());
		if(null != errResopnse){
			LOGGER.info("{} for log in user {}",errResopnse.getError_description(),accessLog.getUsername());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errResopnse.getError_description(), StackTraceUtils.convertPojoToJson(errResopnse));
			return new ResponseEntity<ErrorResponse>(errResopnse, HttpStatus.PRECONDITION_REQUIRED);
		}

		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		LOGGER.info("Trying to fetch list of centers for log in user {}",accessLog.getUsername());
		ResponseEntity<?> dashboardRsp = dashboardService.getCenterList(authToken, dashboardReq, accessLog, emailList, user.getRole());
		updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(dashboardRsp));
		return dashboardRsp;

	}

	@RequestMapping(value = "/getstates",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<?> getStates(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DashboardRequest dashboardReq, @Context HttpServletRequest httpRequest) {

		List<String> emailList = new ArrayList<String>();
		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		ErrorResponse errResopnse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(null != errResopnse){
			LOGGER.info("{} for log in user {}",errResopnse.getError_description(),accessLog.getUsername());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errResopnse.getError_description(), StackTraceUtils.convertPojoToJson(errResopnse));
			return new ResponseEntity<ErrorResponse>(errResopnse, HttpStatus.PRECONDITION_REQUIRED);
		}

		/*if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}*/

		LOGGER.info("Trying to fetch list of states for log in user {}",accessLog.getUsername());
		ResponseEntity<?> dashboardRsp = dashboardService.getStateList(authToken, dashboardReq, accessLog, emailList, user.getRole());
		updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(dashboardRsp.getBody()));
		return dashboardRsp;

	}

	@RequestMapping(value = "/getdistricts",
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<?> getListOfDistrict(@RequestHeader(value = "Authorization") String authToken, 
			@RequestBody DashboardRequest dashboardReq, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		//validate user
		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

		//list of mails for the user
		List<String> emailList = new ArrayList<String>();
		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		LOGGER.info("Trying to fetch list of district for the state {} for log in user {}",dashboardReq.getState(),accessLog.getUsername());
		ResponseEntity<?> dashboardRsp = dashboardService.getDistrictList(authToken, dashboardReq, accessLog, emailList, user.getRole());
		updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(dashboardRsp.getBody()));
		return dashboardRsp;
	}

	@RequestMapping(value = "/getcities", 
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<?> getCities(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DashboardRequest dashboardReq, @Context HttpServletRequest httpRequest) {

		List<String> emailList = new ArrayList<String>();
		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}
		ErrorResponse errResopnse = dashboardValidator.validateCountryField(dashboardReq.getCountry());
		if(errResopnse != null){
			LOGGER.info("{} for log in user {}",errResopnse.getError_description(),accessLog.getUsername());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errResopnse.getError_description(), StackTraceUtils.convertPojoToJson(errResopnse));
			return new ResponseEntity<ErrorResponse>(errResopnse, HttpStatus.PRECONDITION_REQUIRED);
		}
		errResopnse = dashboardValidator.validateStateField(dashboardReq.getState());
		if(errResopnse != null){
			LOGGER.info("{} for log in user {}",errResopnse.getError_description(),accessLog.getUsername());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errResopnse.getError_description(), StackTraceUtils.convertPojoToJson(errResopnse));
			return new ResponseEntity<ErrorResponse>(errResopnse, HttpStatus.PRECONDITION_REQUIRED);
		}
		errResopnse = dashboardValidator.validateDistrictField(dashboardReq.getDistrict());
		if(errResopnse != null){
			LOGGER.info("{} for log in user {}",errResopnse.getError_description(),accessLog.getUsername());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,errResopnse.getError_description(), StackTraceUtils.convertPojoToJson(errResopnse));
			return new ResponseEntity<ErrorResponse>(errResopnse, HttpStatus.PRECONDITION_REQUIRED);
		}

		if(null != user.getAbyasiId()){
			emailList = userProfileService.getEmailsWithAbhyasiId(user.getAbyasiId());
		}
		if(emailList.size() == 0){
			emailList.add(accessLog.getUsername());
		}

		LOGGER.info("Trying to fetch list of cities for log in user {}",accessLog.getUsername());
		ResponseEntity<?> dashboardRsp = dashboardService.getCityList(authToken, dashboardReq, accessLog, emailList, user.getRole());
		updatePMPAPIAccessLog(accessLog, accessLog.getStatus(), accessLog.getErrorMessage(), StackTraceUtils.convertPojoToJson(dashboardRsp.getBody()));
		return dashboardRsp;

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
