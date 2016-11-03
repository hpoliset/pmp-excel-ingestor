package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.CurrentUser;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
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
	
	/**
	 * Method to validate the user with MySRCM.
	 * 
	 * @throws ParseException
	 */
	@Override
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
		
		/*authenticationResponse.setAccess_token(encryptDecryptAES.encrypt(authenticationResponse.getAccess_token(),
				env.getProperty("security.encrypt.token")));
		authenticationResponse.setRefresh_token(encryptDecryptAES.encrypt(authenticationResponse.getRefresh_token(),
				env.getProperty("security.encrypt.token")));*/ //removed token encryption
		LOGGER.debug("User:{} is validated and token is generated", authenticationRequest.getUsername());
		authHelper.doAutoLogin(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		session.setAttribute("Authentication", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		authenticationResponse.setIspmpAllowed(currentUser.getIsPmpAllowed());
		authenticationResponse.setIsSahajmargAllowed(currentUser.getIsSahajmargAllowed());
		return authenticationResponse;
	}
	
}
