package org.srcm.heartfulness.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
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

	/**
	 * method to save the user details
	 * org.srcm.heartfulness.service.UserService#
	 * save(org.srcm.heartfulness.model.User)
	 */
	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	/**
	 * method to load the user details with reference to email
	 */
	@Override
	public User loadUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * method to validate the user with srcm
	 */
	@Override
	public SrcmAuthenticationResponse ValidateLogin(AuthenticationRequest authenticationRequest)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		return srcmRest.authenticate(authenticationRequest);
	}

	/**
	 * method to get the user profile from srcm
	 *
	 */
	@Override
	public Result getUserProfile(String accessToken) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		return srcmRest.getUserProfile(accessToken);
	}

}
