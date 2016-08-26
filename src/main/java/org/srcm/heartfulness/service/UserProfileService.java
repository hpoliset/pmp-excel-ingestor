package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Result;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is service provider for the user profile based actions
 * 
 * @author HimaSree
 *
 */
public interface UserProfileService {

	/**
	 * method to save <code>User</code> to the data store.
	 * 
	 * @param email
	 * @return
	 */
	User loadUserByEmail(String email);

	/**
	 * method to get the user profile from MYSRCM
	 * 
	 * @param token
	 * @param id 
	 * @return Result object
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws HttpClientErrorException
	 * @throws ParseException 
	 * @throws Exception
	 */
	Result getUserProfile(String token, int id) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException, ParseException;

	/**
	 * method to retrieve <code>User</code> from the data store by email.
	 * 
	 * @param user
	 */
	void save(User user);
}
