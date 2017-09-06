package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface AuthenticationService {

	/**
	 * method to validate the user based on username and password
	 * 
	 * @param authenticationRequest
	 * @param session 
	 * @param id 
	 * @param model 
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public SrcmAuthenticationResponse validateLogin(AuthenticationRequest authenticationRequest, HttpSession session, int id)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException, ParseException;

	public ResponseEntity<?> validateUserAndFetchProfile(AuthenticationRequest authenticationRequest, int id);

	
}
