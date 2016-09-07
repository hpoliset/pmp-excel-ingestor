package org.srcm.heartfulness.webservice;

import java.io.IOException;

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
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.APIAccessLogService;
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

	ObjectMapper mapper = new ObjectMapper();

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
			@Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(token), null);
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
				user.setAbyasiId(srcmProfile.getAbhyasi_id());
				userProfileService.save(user);
			}
			user.setMembershipId(String.valueOf(user.getAbyasiId()));
			accessLog.setUsername(srcmProfile.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(user));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(e.getResponseBodyAsString()));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", "IOException occured.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
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
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest) {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(user), null);
		int accesslogId = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		UserProfile srcmProfile = null;
		try {
			Result result = userProfileService.getUserProfile(
					encryptDecryptAES.decrypt(token, env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)), accesslogId);
			srcmProfile = result.getUserProfile()[0];
			User pmpUser = userProfileService.loadUserByEmail(srcmProfile.getEmail());
			if (pmpUser != null && id == pmpUser.getId()) {
				if (id == pmpUser.getId()) {
					user.setMembershipId(user.getMembershipId() == null ? "0" : user.getMembershipId());
					user.setAbyasiId(user.getMembershipId() == null ? 0 : Integer.valueOf(user.getMembershipId()));
					userProfileService.save(user);
				}
			}
			accessLog.setUsername(srcmProfile.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(user));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(e.getResponseBodyAsString()));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", "IOException occured.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Internal Server Error.", e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
