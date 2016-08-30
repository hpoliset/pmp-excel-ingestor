package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpSession;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface AuthenticationService {

	/**
	 * Method to validate the user based on username and password.
	 * 
	 * @param authenticationRequest
	 * @param session
	 * @param id
	 * @param userAgent
	 * @param model
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	SrcmAuthenticationResponse validateLogin(AuthenticationRequest authenticationRequest, HttpSession session, int id,
			String userAgent) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException,
			ParseException;
	
	/**
	 * Method to validate the user based on username and password.
	 * 
	 * @param authenticationRequest
	 * @param session
	 * @param id
	 * @param requestURI 
	 * @param userAgent
	 * @param model
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	SrcmAuthenticationResponse validateUser(AuthenticationRequest authenticationRequest, HttpSession session, int id, String requestURI) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException,
			ParseException;

}
