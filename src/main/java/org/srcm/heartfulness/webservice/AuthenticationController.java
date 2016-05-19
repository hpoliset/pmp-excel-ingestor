package org.srcm.heartfulness.webservice;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.CurrentUser;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.service.AuthenticationService;
import org.srcm.heartfulness.service.UserProfileService;

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

	/**
	 * method to authenticate the user with user email and password by calling
	 * srcm api
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session,
			HttpServletRequest request, ModelMap model) {
		try {
			LOGGER.debug("Trying to Authenticate :  {}", authenticationRequest.getUsername());
			SrcmAuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
			authHelper.doAutoLogin(authenticationRequest.getUsername(), authenticationRequest.getPassword());
			session.setAttribute("Authentication", SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal());
			model.addAttribute("Auth", session.getAttribute("Authentication"));
			CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			authenticationResponse.setIspmpAllowed(currentUser.getIsPmpAllowed());
			authenticationResponse.setIsSahajmargAllowed(currentUser.getIsSahajmargAllowed());
			return new ResponseEntity<SrcmAuthenticationResponse>(authenticationResponse, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Invalid Credentials.", "");
			return new ResponseEntity<ErrorResponse>(error, e.getStatusCode());
		} catch (IOException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "Server Connection time out");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to create new profile to the user by calling MySRCM
	 * API.
	 * 
	 * @param user
	 *            holds user details.
	 * 
	 * @return User with 200 OK status.
	 * 
	 *         If some exception is raised while processing the request, error
	 *         response is returned with respective HttpStatus code.
	 */
	@RequestMapping(value = "users", method = RequestMethod.POST)
	public ResponseEntity<?> createUsers(@RequestBody User user) {
		try {
			user.setUser_type("se");
			user = userProfileService.createUser(user);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.PLEASE_TRY_AFTER_SOMETIME, e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.PLEASE_TRY_AFTER_SOMETIME, e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
