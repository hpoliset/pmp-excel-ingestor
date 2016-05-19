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
	 * method to get the user profile from srcm
	 *
	 */
	@Override
	public Result getUserProfile(String accessToken) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		return srcmRest.getUserProfile(accessToken);
	}
	
	/**
	 * Method to create the user in MySRCM & PMP and to persist user details in
	 * PMP.
	 * 
	 */
	@Override
	public User createUser(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException {

		User newUser = srcmRest.createUserProfile(user);
		// srcm will return null values for the city,state,country
		if (null != user.getName())
			newUser.setName(user.getName());
		if (null != user.getCity())
			newUser.setCity(user.getCity());
		if (null != user.getCountry())
			newUser.setCountry(user.getCountry());
		if (null != user.getState())
			newUser.setState(user.getState());
		if (null != user.getMobile())
			newUser.setMobile(user.getMobile());

		// need to confirm and need to change code
		newUser.setMembershipId(user.getMembershipId() == null ? "0" : user.getMembershipId());
		newUser.setAbyasiId(user.getMembershipId() == null ? 0 : Integer.valueOf(user.getMembershipId()));
		userRepository.save(newUser);
		return newUser;
	}

}
