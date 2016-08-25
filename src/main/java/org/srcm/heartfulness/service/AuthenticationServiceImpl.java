package org.srcm.heartfulness.service;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.CurrentUser;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Autowired
	SrcmRestTemplate srcmRest;

	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	Environment env;
	
	@Autowired
	AuthorizationHelper authHelper;

	/**
	 * Method to validate the user with MySRCM.
	 */
	@Override
	public SrcmAuthenticationResponse validateLogin(AuthenticationRequest authenticationRequest, HttpSession session)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		SrcmAuthenticationResponse authenticationResponse = srcmRest.authenticate(authenticationRequest);
		authenticationResponse.setAccess_token(encryptDecryptAES.encrypt(authenticationResponse.getAccess_token(),
				env.getProperty("security.encrypt.token")));
		authenticationResponse.setRefresh_token(encryptDecryptAES.encrypt(authenticationResponse.getRefresh_token(),
				env.getProperty("security.encrypt.token")));
		LOGGER.debug("User:{} is validated and token is generated", authenticationRequest.getUsername());
		authHelper.doAutoLogin(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		session.setAttribute("Authentication", SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		authenticationResponse.setIspmpAllowed(currentUser.getIsPmpAllowed());
		authenticationResponse.setIsSahajmargAllowed(currentUser.getIsSahajmargAllowed());
		return authenticationResponse;
	}
}
