package org.srcm.heartfulness.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is the implementation of service provider for the validate login and generation of token.
 * 
 * @author himasreev
 *
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService{
	
	@Autowired
	SrcmRestTemplate srcmRest;
	
	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	Environment env;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	
	/**
	 * Method to validate the user with MySRCM.
	 * 
	 * @param AuthenticationRequest
	 */
	@Override
	public SrcmAuthenticationResponse authenticate(AuthenticationRequest authenticationRequest)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		SrcmAuthenticationResponse authenticationResponse = srcmRest.authenticate(authenticationRequest);
		authenticationResponse.setAccess_token(encryptDecryptAES.encrypt(authenticationResponse.getAccess_token(),
				env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)));
		authenticationResponse.setRefresh_token(encryptDecryptAES.encrypt(authenticationResponse.getRefresh_token(),
				env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)));
		LOGGER.debug("User:{} is validated and token is generated", authenticationRequest.getUsername());
		return authenticationResponse;
	}
	
}
