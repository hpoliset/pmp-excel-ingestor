package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.CreateUserRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;

/**
 * 
 * @author HimaSree
 *
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	Environment env;

	@Autowired
	APIAccessLogService apiAccessLogService;

	/**
	 * Method to get the user profile from the MySRCM and persists user details
	 * in PMP DB, if the user details is not available in PMP.
	 * 
	 * @param accessToken
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization") String token,
			@Context HttpServletRequest httpRequest) throws ParseException {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		UserProfile srcmProfile = null;
		try {
			Result result = userProfileService.getUserProfile(
					encryptDecryptAES.decrypt(token, env.getProperty("security.encrypt.token")), id);
			srcmProfile = result.getUserProfile()[0];
			User user = userProfileService.loadUserByEmail(srcmProfile.getEmail());
			if (null == user) {
				user = new User();
				user.setName(srcmProfile.getName());
				user.setFirst_name(srcmProfile.getFirst_name());
				user.setLast_name(srcmProfile.getLast_name());
				user.setEmail(srcmProfile.getEmail());
				user.setAbyasiId(srcmProfile.getRef());
				userProfileService.save(user);
			}
			accessLog.setUsername(srcmProfile.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(e.getResponseBodyAsString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", "IOException occured.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("IOException occured.");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("Internal Server Error.");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method to update the user details in PMP.
	 * 
	 * @param id
	 *            userId of the user whose details need to be updated.
	 * @param user
	 *            , holds user details.
	 * @param token
	 *            , Token to be validated against MYSRCM endpoint.
	 * @return
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest)
			throws ParseException {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null);
		int accesslogId = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		UserProfile srcmProfile = null;
		try {
			Result result = userProfileService.getUserProfile(
					encryptDecryptAES.decrypt(token, env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)), accesslogId);
			srcmProfile = result.getUserProfile()[0];
			User pmpUser = userProfileService.loadUserByEmail(srcmProfile.getEmail());
			if (pmpUser != null && id == pmpUser.getId()) {
				if (id == pmpUser.getId()) {
					userProfileService.save(user);
				}
			}
			accessLog.setUsername(srcmProfile.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(e.getResponseBodyAsString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", "IOException occured.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("IOException occured.");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Internal Server Error.", e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("Internal Server Error.");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Method to create new profile to the user by calling srcm api
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "users", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user, @Context HttpServletRequest httpRequest)
			throws ParseException {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(user.getEmail(), httpRequest.getRemoteAddr(),
				httpRequest.getRequestURI(), DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED,
				null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			user.setUserType("se");
			User newUser = userProfileService.createUser(user, id, httpRequest.getRequestURI());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(newUser, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(e.getResponseBodyAsString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("IOException");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("Internal server error");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method to create new profile to the user by calling srcm api
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "mobile/users", method = RequestMethod.POST)
	public ResponseEntity<?> mobileAppUserCreate(@RequestBody CreateUserRequest user,
			@Context HttpServletRequest httpRequest) throws ParseException {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(user.getEmail(), httpRequest.getRemoteAddr(),
				httpRequest.getRequestURI(), DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED,
				null);
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			user.setUserType("se");
			User newUser = userProfileService.createUser(user, id, httpRequest.getRequestURI());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(newUser, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(e.getResponseBodyAsString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("IOException");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage("Internal server error");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
