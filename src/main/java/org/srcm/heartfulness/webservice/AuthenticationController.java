package org.srcm.heartfulness.webservice;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
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


	/**
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
		try {
			LOGGER.debug("Trying to Authenticating :  {}", authenticationRequest.getUsername());
			SrcmAuthenticationResponse authenticationResponse = userProfileService.ValidateLogin(authenticationRequest);
			LOGGER.debug("User:{} is validate and token is generated", authenticationRequest.getUsername());
			return new ResponseEntity<SrcmAuthenticationResponse>(authenticationResponse, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
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
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "users", method = RequestMethod.POST)
	public ResponseEntity<?> createUsers(@RequestBody User user) {
		try {
			user.setUserType("se");
			user = userProfileService.createUser(user);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.",  e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
