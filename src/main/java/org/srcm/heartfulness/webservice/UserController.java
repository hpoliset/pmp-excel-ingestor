package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.CreateUserRequest;
import org.srcm.heartfulness.model.json.response.CreateUserErrorResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.UserProfileManagementValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author HimaSree
 *
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	Environment env;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	UserProfileManagementValidator uservalidator;

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
				StackTraceUtils.convertPojoToJson(token));
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			User user = userProfileService.getUserProfileAndCreateUser(token, id);
			/*
			 * Result result = userProfileService.getUserProfile(token, id);
			 * srcmProfile = result.getUserProfile()[0]; User user =
			 * userProfileService.loadUserByEmail(srcmProfile.getEmail()); if
			 * (null == user) { user = new User();
			 * user.setName(srcmProfile.getName());
			 * user.setFirst_name(srcmProfile.getFirst_name());
			 * user.setLast_name(srcmProfile.getLast_name());
			 * user.setEmail(srcmProfile.getEmail());
			 * user.setAbyasiId(srcmProfile.getRef());
			 * user.setGender(srcmProfile.getGender());
			 * user.setMobile(srcmProfile.getMobile());
			 * user.setAgeGroup(String.valueOf(srcmProfile.getAge()));
			 * user.setZipcode(srcmProfile.getPostal_code());
			 * user.setAddress(srcmProfile.getStreet());
			 * userProfileService.save(user); }
			 */
			accessLog.setUsername(user.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(user));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error occured while fecthing user :{}", e);
			Response error = new Response(ErrorConstants.STATUS_FAILED, "Invalid auth token.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(e.getResponseBodyAsString()));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(error, e.getStatusCode());
		} catch (IOException e) {
			LOGGER.error("Error occured while fecthing user :{}", e);
			Response error = new Response(ErrorConstants.STATUS_FAILED, "IOException occured.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			LOGGER.error("Error occured while fecthing user :{}", e);
			Response error = new Response(ErrorConstants.STATUS_FAILED, "Internal Server Error.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(error, HttpStatus.INTERNAL_SERVER_ERROR);
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
		try {
			User newUser = userProfileService.updateUserDetails(token, accesslogId, user, id);
			/*
			 * Result result = userProfileService.getUserProfile(token,
			 * accesslogId); srcmProfile = result.getUserProfile()[0]; User
			 * pmpUser =
			 * userProfileService.loadUserByEmail(srcmProfile.getEmail()); if
			 * (pmpUser != null && id == pmpUser.getId()) { if (id ==
			 * pmpUser.getId()) { userProfileService.save(user); } }
			 */
			accessLog.setUsername(newUser.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(newUser));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<User>(newUser, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error occured while update user :{}", e);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(e.getResponseBodyAsString()));
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			LOGGER.error("Error occured while update user :{}", e);
			ErrorResponse error = new ErrorResponse("IOException occured.", ErrorConstants.STATUS_FAILED);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			LOGGER.error("Error occured while update user :{}", e);
			ErrorResponse error = new ErrorResponse("Internal Server Error.", ErrorConstants.STATUS_FAILED);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
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
	@RequestMapping(value = "/mobile/users", method = RequestMethod.POST)
	public ResponseEntity<?> userCreate(@RequestBody CreateUserRequest user, @Context HttpServletRequest httpRequest)
			throws ParseException {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(user));
		int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			String description = uservalidator.checkCreateUserManadatoryFields(user);
			if (null != description) {
				Response errors = new Response(ErrorConstants.STATUS_FAILED, description);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(errors));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<Response>(errors, HttpStatus.PRECONDITION_FAILED);
			}
			user.setUserType("se");
			User newUser = userProfileService.createUser(user, id, httpRequest.getRequestURI());
			StringBuilder userResponse = new StringBuilder();
			userResponse.append("name : " + newUser.getName());
			userResponse.append(", " + "email : " + newUser.getEmail());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			LOGGER.debug("User profile created - {} ", newUser.getEmail());
			Response response = new Response(ErrorConstants.STATUS_SUCCESS, userResponse.toString());
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				Response error = null;
				CreateUserErrorResponse createUserErrorResponse = mapper.readValue(e.getResponseBodyAsString(),
						CreateUserErrorResponse.class);
				if (null != createUserErrorResponse.getNon_field_errors()
						&& !createUserErrorResponse.getNon_field_errors().isEmpty()) {
					error = new Response(ErrorConstants.STATUS_FAILED, createUserErrorResponse.getNon_field_errors()
							.get(0).replace("\"", ""));
				} else if (null != createUserErrorResponse.getEmail() && !createUserErrorResponse.getEmail().isEmpty()) {
					error = new Response(ErrorConstants.STATUS_FAILED, createUserErrorResponse.getEmail().get(0));
				} else if (null != createUserErrorResponse.getDetail()
						&& !createUserErrorResponse.getDetail().isEmpty()) {
					error = new Response(ErrorConstants.STATUS_FAILED, createUserErrorResponse.getDetail());
				} else {
					error = new Response(ErrorConstants.STATUS_FAILED, e.getResponseBodyAsString());
				}
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				System.out.println(StackTraceUtils.convertStackTracetoString(e));
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				LOGGER.debug("Error while creating user profile - {} ", e.getMessage());
				return new ResponseEntity<Response>(error, e.getStatusCode());
			} catch (JsonParseException | JsonMappingException e1) {
				LOGGER.debug("Error while creating user profile - {} ", e1.getMessage());
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e1));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				LOGGER.debug("Error while creating user profile - {} ", e1.getMessage());
				Response error = new Response(ErrorConstants.STATUS_FAILED, "JsonException occured.");
				return new ResponseEntity<Response>(error, HttpStatus.REQUEST_TIMEOUT);
			} catch (IOException e1) {
				LOGGER.debug("Error while creating user profile - {} ", e1.getMessage());
				Response error = new Response(ErrorConstants.STATUS_FAILED, "IOException occured.");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e1));
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<Response>(error, HttpStatus.REQUEST_TIMEOUT);
			} catch (Exception e1) {
				LOGGER.debug("Error while creating user profile - {} ", e1.getMessage());
				Response error = new Response(ErrorConstants.STATUS_FAILED, "Internal Server Error.");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e1));
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<Response>(error, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (IOException e) {
			LOGGER.debug("Error while creating user profile - {} ", e.getMessage());
			Response error = new Response(ErrorConstants.STATUS_FAILED, "IOException occured.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug("Error while creating user profile - {} ", e.getMessage());
			Response error = new Response(ErrorConstants.STATUS_FAILED, "Internal Server Error.");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(error));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


}
