package org.srcm.heartfulness.service;

import java.io.IOException;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author HimaSree
 *
 */
public interface UserProfileService {

	/**
	 * 
	 * @param authenticationRequest
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	SrcmAuthenticationResponse ValidateLogin(AuthenticationRequest authenticationRequest)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;

	/**
	 * method to get the user profile from MYSRCM
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
	 * 
	 * @param user
	 * @param token
	 */
	void UpdateUserProfile(UserProfile user, String token);

	/**
	 * Retrieve a <code>User</code> from the data store by email.
	 * 
	 * @param user
	 */
	void save(User user);

	/**
	 * 
	 * @param user
	 */
	User createUser(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;

	/**
	 * save <code>User</code> to the data store.
	 * 
	 * @param email
	 * @return
	 */
	User loadUserByEmail(String email);
	
	/**
	 * 
	 * @param refreshtoken
	 * @param authenticationRequest 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	SrcmAuthenticationResponse getRefreshToken(String refreshtoken) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;

}
