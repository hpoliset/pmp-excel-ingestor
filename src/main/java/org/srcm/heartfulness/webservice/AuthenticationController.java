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
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.AuthenticationService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
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
	private AESEncryptDecrypt encryptDecryptAES;

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
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(authenticationRequest), null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			LOGGER.debug("Trying to Authenticate :  {}", authenticationRequest.getUsername());
			SrcmAuthenticationResponse authenticationResponse = authenticationService.validateLogin(
					authenticationRequest, session, id);
			model.addAttribute("Auth", session.getAttribute("Authentication"));
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(authenticationResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<SrcmAuthenticationResponse>(authenticationResponse, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			ErrorResponse error = new ErrorResponse("Invalid Credentials.", "");
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, e.getStatusCode());
		} catch (IOException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOGGER.error("Error occured while authenticating :{}", e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "Server Connection time out");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
