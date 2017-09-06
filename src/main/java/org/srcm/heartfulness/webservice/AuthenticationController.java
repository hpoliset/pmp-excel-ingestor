package org.srcm.heartfulness.webservice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.AuthenticationService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller - Login authentication services.
 * 
 * @author HimaSree
 *
 */
@RestController
@RequestMapping("/api/")
public class AuthenticationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	Environment env;

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	APIAccessLogService apiAccessLogService;

	ObjectMapper mapper = new ObjectMapper();

	/**
	 * Method to authenticate the user with user email and password by calling
	 * MySRCM API.
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session,
			ModelMap model, @Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(authenticationRequest.getUsername(),
				httpRequest.getRemoteAddr(), httpRequest.getRequestURI(), DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(authenticationRequest.getUsername()), null);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		try {

			LOGGER.info("Trying to Authenticate :  {}", authenticationRequest.getUsername());
			SrcmAuthenticationResponse authenticationResponse = authenticationService.validateLogin(authenticationRequest, session, accessLog.getId());
			//model.addAttribute("Auth", session.getAttribute("Authentication"));
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(authenticationResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<SrcmAuthenticationResponse>(authenticationResponse, HttpStatus.OK);

		} catch (HttpClientErrorException e) {

			ErrorResponse error = new ErrorResponse("Invalid Credentials.", "");
			LOGGER.error("Error occured while authenticating :{}, error_message :{}", authenticationRequest.getUsername(), e.getMessage());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, e.getStatusCode());

		} catch (JsonParseException |JsonMappingException e) {

			LOGGER.error("JsonParseException    :" + StackTraceUtils.convertStackTracetoString(e));
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,"Error while processing request");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);

		} catch (IOException e) {

			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "");
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);

		} catch (Exception e) {

			LOGGER.error("Error occured while authenticating :{}", e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "Server Connection time out");
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


	@RequestMapping(value= "/authenticateduserprofile",method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	
	public ResponseEntity<?> authenticateAndgetUserProfileFromMysrcm (
			@RequestBody AuthenticationRequest authenticationRequest, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(authenticationRequest.getUsername(),httpRequest,StackTraceUtils.convertPojoToJson(authenticationRequest));

		LOGGER.info("Trying to Authenticate and fetch profile for :  {}", authenticationRequest.getUsername());
		
		try {
			
			ResponseEntity<?> response  =  authenticationService.validateUserAndFetchProfile(authenticationRequest, accessLog.getId());
			updatePMPAPIAccessLog(accessLog, ErrorConstants.STATUS_SUCCESS, null, String.valueOf(response.getBody()));
			return response;
			
		} catch(Exception ex){
			
			LOGGER.error("Exception while authenticating and fetching user profile for : {}", authenticationRequest.getUsername());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
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
