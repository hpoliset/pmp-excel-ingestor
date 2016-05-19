package org.srcm.heartfulness.service;

import java.io.IOException;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is the service provider for the validate login and generation of token.
 * 
 * @author himasreev
 *
 */
public interface AuthenticationService {
	
	/**
	 * Method to validate the user based on username and password.
	 * 
	 * @param authenticationRequest
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	SrcmAuthenticationResponse authenticate(AuthenticationRequest authenticationRequest)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;
	
}
