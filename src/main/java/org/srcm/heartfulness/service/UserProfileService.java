package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Result;

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
	 * Method to get the user profile from MYSRCM
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
	 * Method to update the user details.
	 * 
	 * @param token
	 * @param accesslogId
	 * @param user
	 * @param id
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	User updateUserDetails(String token, int accesslogId, User user, int id) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException, ParseException;

	/**
	 * Method to fetch the user profile from the MYSRCM by passing token and if
	 * user doesn't exists in DB, store the details in HFN backend.
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	User getUserProfileAndCreateUser(String token, int id) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException, ParseException;

	/**
	 * Method to retrieve <code>User</code> from the data store by email.
	 * 
	 * @param user
	 */
	void save(User user);
	
	/**
	 * This method is used to get the email Ids associated with 
	 * an Abhyasi Id.
	 * @param abyasiId, is used to get all the emails associated 
	 * eith a specific Abhyasi Id. 
	 * @return List<String> email Ids for a given Abhyasi Id.
	 */
	List<String> getEmailsWithAbhyasiId(String abyasiId);
	
	public User getUserMailWithId(int userId);
	
}
