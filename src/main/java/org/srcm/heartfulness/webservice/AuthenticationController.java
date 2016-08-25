package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.text.ParseException;

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
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.AuthenticationService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;

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

	/**
	 * Method to authenticate the user with user email and password by calling
	 * MySRCM API.
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session,
			ModelMap model, @Context HttpServletRequest httpRequest) throws ParseException {
		String requestTime = DateUtils.getCurrentTimeInMilliSec();
		try {

			LOGGER.debug("Trying to Authenticate :  {}", authenticationRequest.getUsername());
			SrcmAuthenticationResponse authenticationResponse = authenticationService.validateLogin(
					authenticationRequest, session);
			model.addAttribute("Auth", session.getAttribute("Authentication"));
			apiAccessLogService.createLogDetails((null == authenticationRequest.getUsername() ? null
					: authenticationRequest.getUsername()), httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
					requestTime);
			return new ResponseEntity<SrcmAuthenticationResponse>(authenticationResponse, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Invalid Credentials.", "");
			apiAccessLogService.createLogDetails((null == authenticationRequest.getUsername() ? null
					: authenticationRequest.getUsername()), httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
					requestTime);
			return new ResponseEntity<ErrorResponse>(error, e.getStatusCode());
		} catch (IOException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "");
			apiAccessLogService.createLogDetails((null == authenticationRequest.getUsername() ? null
					: authenticationRequest.getUsername()), httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
					requestTime);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "Server Connection time out");
			apiAccessLogService.createLogDetails((null == authenticationRequest.getUsername() ? null
					: authenticationRequest.getUsername()), httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
					requestTime);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
