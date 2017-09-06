package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.TokenValidationConstants;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.CurrentUser;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Autowired
	SrcmRestTemplate srcmRest;

	@Autowired
	Environment env;

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	private UserProfileService userProfileService;

	/**
	 * Method to validate the user with MySRCM.
	 * 
	 * @throws ParseException
	 */
	/*@Override
	public SrcmAuthenticationResponse validateLogin(AuthenticationRequest authenticationRequest, HttpSession session,
			int id) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException,
			ParseException {
		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id,
				EndpointConstants.AUTHENTICATION_TOKEN_URL, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(authenticationRequest.getUsername()), null);
		int accessdetailsID = apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		accessLogDetails.setId(accessdetailsID);
		SrcmAuthenticationResponse authenticationResponse = srcmRest.authenticate(authenticationRequest);
		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(authenticationResponse));
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
		authenticationResponse.setAccess_token(encryptDecryptAES.encrypt(authenticationResponse.getAccess_token(),
				env.getProperty("security.encrypt.token")));
		authenticationResponse.setRefresh_token(encryptDecryptAES.encrypt(authenticationResponse.getRefresh_token(),
				env.getProperty("security.encrypt.token")));
		LOGGER.debug("User:{} is validated and token is generated", authenticationRequest.getUsername());
		authHelper.doAutoLogin(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		session.setAttribute("Authentication", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		authenticationResponse.setIspmpAllowed(currentUser.getIsPmpAllowed());
		authenticationResponse.setIsSahajmargAllowed(currentUser.getIsSahajmargAllowed());
		return authenticationResponse;
	}*/


	@Override
	public SrcmAuthenticationResponse validateLogin(AuthenticationRequest authenticationRequest, HttpSession session,
			int id) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException,ParseException {

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id,
				EndpointConstants.AUTHENTICATION_TOKEN_URL, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(authenticationRequest.getUsername()), null);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);

		SrcmAuthenticationResponse authenticationResponse = srcmRest.authenticate(authenticationRequest);
		LOGGER.info("User:{} is validated and token is generated", authenticationRequest.getUsername());

		User user = userProfileService.getUserProfileAndCreateUser(authenticationResponse.getAccess_token(), id);
		user.setPassword(authenticationRequest.getPassword());
		authHelper.doAutoLogin(user);
		session.setAttribute("Authentication", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		authenticationResponse.setIspmpAllowed(user.getIsPmpAllowed());
		authenticationResponse.setIsSahajmargAllowed(user.getIsSahajmargAllowed());

		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(authenticationResponse));
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
		return authenticationResponse;
	}

	@Override
	public ResponseEntity<?> validateUserAndFetchProfile(AuthenticationRequest authenticationRequest,int id) {

		//save authenticate log details in PMP
		PMPAPIAccessLogDetails authenticateAccessLogDetails = createPMPAPIAccessLogDetails(id, EndpointConstants.AUTHENTICATION_TOKEN_URL, StackTraceUtils.convertPojoToJson(authenticationRequest));

		//pass authentication object to MYSRCM to validate credentials
		SrcmAuthenticationResponse authenticationResponse = null;
		ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "");

		try {
			authenticationResponse = srcmRest.authenticate(authenticationRequest);
			updatePMPAPIAccessLogDetails(authenticateAccessLogDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(authenticationResponse));
		} catch (HttpClientErrorException | IOException ex) {

			eResponse.setError_description(TokenValidationConstants.INVALID_USER_CREDENTIALS);
			updatePMPAPIAccessLogDetails(authenticateAccessLogDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);

		} catch(Exception ex){

			eResponse.setError_description(TokenValidationConstants.INVALID_AUTHENTICATE_RESPONSE_FROM_MYSRCM);
			updatePMPAPIAccessLogDetails(authenticateAccessLogDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);

		}
		LOGGER.info("User:{} is validated and token is generated ", authenticationRequest.getUsername());

		//save fetch user details in PMP
		PMPAPIAccessLogDetails fetchUserAccessLogDetails = createPMPAPIAccessLogDetails(id,EndpointConstants.GET_USER_PROFILE, StackTraceUtils.convertPojoToJson(authenticationRequest));

		Result result;
		UserProfile srcmProfile = null;
		try {

			result = srcmRest.getUserProfile(authenticationResponse.getAccess_token());
			updatePMPAPIAccessLogDetails(fetchUserAccessLogDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(result));
			srcmProfile = result.getUserProfile()[0];

			if(null != srcmProfile){

				LOGGER.info("Profile received from MYSRCM {}",srcmProfile);
				User pmpUser = new User();
				pmpUser.setId(srcmProfile.getId());
				pmpUser.setName(srcmProfile.getName());
				pmpUser.setFirst_name(srcmProfile.getFirst_name());
				pmpUser.setLast_name(srcmProfile.getLast_name());
				pmpUser.setAbyasiId(srcmProfile.getRef());
				pmpUser.setEmail(srcmProfile.getEmail());
				pmpUser.setAgeGroup(String.valueOf(srcmProfile.getAge()));
				pmpUser.setGender(srcmProfile.getGender());
				pmpUser.setState(srcmProfile.getState());
				pmpUser.setCountry(String.valueOf(srcmProfile.getCountry()));
				pmpUser.setMobile(srcmProfile.getMobile());
				pmpUser.setZipcode(srcmProfile.getPostal_code());
				pmpUser.setAddress(srcmProfile.getStreet());
				updatePMPAPIAccessLogDetails(fetchUserAccessLogDetails, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(pmpUser));
				return new ResponseEntity<User>(pmpUser,HttpStatus.OK);
			}else{
				LOGGER.info("Profile is not available in MYSRCM ");
				eResponse.setError_description(TokenValidationConstants.USER_DOESNOT_EXIST_IN_MYSRCM);
				updatePMPAPIAccessLogDetails(fetchUserAccessLogDetails, ErrorConstants.STATUS_SUCCESS, TokenValidationConstants.USER_DOESNOT_EXIST_IN_MYSRCM, StackTraceUtils.convertPojoToJson(eResponse));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.OK);
			}

		} catch (HttpClientErrorException | IOException | ParseException ex) {

			eResponse.setError_description(TokenValidationConstants.ERROR_PROFILE);
			updatePMPAPIAccessLogDetails(fetchUserAccessLogDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);

		} catch(Exception ex){

			eResponse.setError_description(TokenValidationConstants.INVALID_GET_USER_RESPONSE_FROM_MYSRCM);
			updatePMPAPIAccessLogDetails(fetchUserAccessLogDetails, ErrorConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.OK);

		}
	}

	private PMPAPIAccessLogDetails createPMPAPIAccessLogDetails(int pmpAccessLogId,String endpoint, String requestBody){

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(pmpAccessLogId,endpoint, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, requestBody, null);

		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		return accessLogDetails;
	}


	private void updatePMPAPIAccessLogDetails(PMPAPIAccessLogDetails pmpApiAccessLogDetails, String status, String errorMessage, String responseBody){

		pmpApiAccessLogDetails.setStatus(status);
		pmpApiAccessLogDetails.setErrorMessage(errorMessage);
		pmpApiAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		pmpApiAccessLogDetails.setResponseBody(responseBody);
		apiAccessLogService.updatePmpAPIAccesslogDetails(pmpApiAccessLogDetails);
	}

}
