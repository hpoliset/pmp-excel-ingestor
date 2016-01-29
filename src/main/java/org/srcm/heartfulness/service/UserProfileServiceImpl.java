package org.srcm.heartfulness.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.UserRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author HimaSree
 *
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

	@Autowired
	SrcmRestTemplate srcmRest;

	@Autowired
	private UserRepository userRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.UserService#save(org.srcm.heartfulness.
	 * model.User)
	 */
	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	public User createUser(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException {
		User newUser = srcmRest.createUserProfile(user);
		userRepository.save(newUser);
		return newUser;
	}

	@Override
	public User loadUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.UserProfileService#ValidateLogin(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public SrcmAuthenticationResponse ValidateLogin(AuthenticationRequest authenticationRequest)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		return srcmRest.authenticate(authenticationRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.UserProfileService#getUserProfile(java.
	 * lang.String)
	 */
	@Override
	public Result getUserProfile(String accessToken) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		return srcmRest.getUserProfile(accessToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.UserProfileService#UpdateUserProfile(org
	 * .srcm.heartfulness.service.response.UserProfile, java.lang.String)
	 */
	@Override
	public void UpdateUserProfile(UserProfile user, String token) {
		// srcmRest.UpdateUserProfile(user,token);
	}

}
