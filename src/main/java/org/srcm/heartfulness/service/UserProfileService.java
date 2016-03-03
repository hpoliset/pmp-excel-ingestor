package org.srcm.heartfulness.service;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.IntroductionDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is service provider for the user profile based actions
 * @author HimaSree
 *
 */
public interface UserProfileService {

	/**
	 * method to validate the user based on username and password
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
	 * method to update the user profile in pmp
	 * @param user
	 * @param token
	 */
	void UpdateUserProfile(UserProfile user, String token);

	/**
	 * method to retrieve <code>User</code> from the data store by email.
	 * 
	 * @param user
	 */
	void save(User user);

	/**
	 * method to create user in srcm and pmp
	 * @param user
	 */
	User createUser(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;

	/**
	 * method to save <code>User</code> to the data store.
	 * 
	 * @param email
	 * @return
	 */
	User loadUserByEmail(String email);
	
	/**
	 * method to get refresh token details from srcm
	 * @param refreshtoken
	 * @param authenticationRequest 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	SrcmAuthenticationResponse getRefreshToken(String refreshtoken) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;

	/**
	 * method to update the introduction details
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	IntroductionDetails updateIntroductionDetails(User user) throws SQLException;

}
