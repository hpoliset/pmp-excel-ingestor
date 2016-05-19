package org.srcm.heartfulness.service;

import java.io.IOException;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is service provider for the user profile based actions.
 * 
 * @author HimaSree
 *
 */
public interface UserProfileService {

	/**
	 * Method to save <code>User</code> to the data store.
	 * 
	 * @param email
	 * @return
	 */
	User loadUserByEmail(String email);

	/**
	 * method to get the user profile from MYSRCM.
	 * 
	 * @param token
	 * @return Result object
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws HttpClientErrorException
	 * @throws Exception
	 */
	Result getUserProfile(String token) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException;

	/**
	 * method to retrieve <code>User</code> from the data store by email.
	 * 
	 * @param user
	 */
	void save(User user);
	
	/**
	 * Method to create user in MySRCM and PMP.
	 * 
	 * @param user
	 */
	User createUser(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;
	
}
