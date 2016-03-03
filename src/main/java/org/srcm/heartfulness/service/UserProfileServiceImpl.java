package org.srcm.heartfulness.service;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.IntroductionDetails;
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

	/**
	 * method to save the user details
	 * org.srcm.heartfulness.service.UserService#save(org.srcm.heartfulness.model.User)
	 */
	@Override
	public void save(User user) {
		userRepository.save(user);
	}
	
	/**
	 * method to create the user in srcm & pmp and to persist user details in pmp
	 */
	@Override
	public User createUser(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException {
		User newUser = srcmRest.createUserProfile(user);
		if(null != user.getName()) newUser.setName(user.getName());
		if (null !=user.getCity()) newUser.setCity(user.getCity());
		if (null !=user.getCountry())  newUser.setCountry(user.getCountry());
		if (null !=user.getState())  newUser.setState(user.getState());
		if (null !=user.getMobile()) newUser.setMobile(user.getMobile());
		if ( 0 != user.getAbyasiId()) newUser.setAbyasiId(user.getAbyasiId());
		userRepository.save(newUser);
		return newUser;
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

	
	@Override
	public void UpdateUserProfile(UserProfile user, String token) {
		// srcmRest.UpdateUserProfile(user,token);
	}

	/**
	 * method to get the refresh token details from the srcm
	 */
	@Override
	public SrcmAuthenticationResponse getRefreshToken(String refreshtoken) 
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		return srcmRest.getRefreshTokenDetails(refreshtoken);
	}
	
	/**
	 * method to update the introductory details of the seeker
	 */
	@Override
	public IntroductionDetails updateIntroductionDetails(User user) throws SQLException{
		IntroductionDetails intrDet=new IntroductionDetails();
		userRepository.getUserID(user);
		intrDet=userRepository.updateIntroductionDetails(user);
		return intrDet;
	}
}
