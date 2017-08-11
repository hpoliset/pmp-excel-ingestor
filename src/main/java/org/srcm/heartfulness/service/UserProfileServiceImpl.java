package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.UserRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is service implementation for the user profile based actions.
 * @author HimaSree
 *
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

	@Autowired
	SrcmRestTemplate srcmRest;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	APIAccessLogService apiAccessLogService;

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.UserProfileService#save(org.srcm.heartfulness.model.User)
	 */
	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.UserProfileService#loadUserByEmail(java.lang.String)
	 */
	@Override
	public User loadUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.UserProfileService#getUserProfile(java.lang.String, int)
	 */
	@Override
	public Result getUserProfile(String accessToken, int id)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException, ParseException {
		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id, EndpointConstants.GET_USER_PROFILE,
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,StackTraceUtils.convertPojoToJson(accessToken),null);
		int accessdetailsID = apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		accessLogDetails.setId(accessdetailsID);
		Result result = srcmRest.getUserProfile(accessToken);
		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(result));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
		return result;
	}

	@Override
	public User updateUserDetails(String token, int accesslogId, User user, int id) throws HttpClientErrorException,
	JsonParseException, JsonMappingException, IOException, ParseException {
		Result result = getUserProfile(token, accesslogId);
		UserProfile srcmProfile = result.getUserProfile()[0];
		User pmpUser = userRepository.findByEmail(srcmProfile.getEmail());
		if (pmpUser != null && id == pmpUser.getId()) {
			if (id == pmpUser.getId()) {
				pmpUser.setName(user.getName());
				pmpUser.setAbyasiId(user.getAbyasiId());
				pmpUser.setAddress(user.getAddress());
				pmpUser.setCity(user.getCity());
				pmpUser.setState(user.getState());
				pmpUser.setCountry(user.getCountry());
				pmpUser.setEmail(user.getEmail());
				pmpUser.setFirst_name(user.getFirst_name());
				pmpUser.setGender(user.getGender());
				pmpUser.setLast_name(user.getLast_name());
				pmpUser.setLanguagePreference(user.getLanguagePreference());
				pmpUser.setMobile(user.getMobile());
				pmpUser.setZipcode(user.getZipcode());
				pmpUser.setAgeGroup(user.getAgeGroup());
				userRepository.save(pmpUser);
			}
		}
		return user;
	}

	@Override
	public User getUserProfileAndCreateUser(String token, int id) throws HttpClientErrorException, JsonParseException,
	JsonMappingException, IOException, ParseException {
		Result result = getUserProfile(token, id);
		UserProfile srcmProfile = result.getUserProfile()[0];
		LOGGER.info("Profile email is {}",srcmProfile.getEmail());
		LOGGER.info("Profile user_email is {}",srcmProfile.getUser_email());
		//User user = userRepository.findByEmail(srcmProfile.getEmail());
		User user = userRepository.findByEmail(null == srcmProfile.getUser_email() ? srcmProfile.getEmail() 
				: srcmProfile.getUser_email().isEmpty() ? srcmProfile.getEmail() : srcmProfile.getUser_email());

		if (null == user) {
			user = new User();
			user.setName(srcmProfile.getName());
			user.setFirst_name(srcmProfile.getFirst_name());
			user.setLast_name(srcmProfile.getLast_name());
			//user.setEmail(srcmProfile.getEmail());
			user.setEmail(null == srcmProfile.getUser_email() ? srcmProfile.getEmail() 
					: srcmProfile.getUser_email().isEmpty() ? srcmProfile.getEmail() : srcmProfile.getUser_email());
			user.setAbyasiId(srcmProfile.getRef());
			user.setGender(srcmProfile.getGender());
			user.setMobile(srcmProfile.getMobile());
			user.setAgeGroup(String.valueOf(srcmProfile.getAge()));
			user.setZipcode(srcmProfile.getPostal_code());
			user.setAddress(srcmProfile.getStreet());
			if (null == user.getRole() && user.getId() == 0)
				user.setRole(PMPConstants.LOGIN_ROLE_SEEKER);
			if (null == user.getIsPmpAllowed() && user.getId() == 0)
				user.setIsPmpAllowed(PMPConstants.REQUIRED_NO);
			if (null == user.getIsSahajmargAllowed() && user.getId() == 0)
				user.setIsSahajmargAllowed(PMPConstants.REQUIRED_NO);
			userRepository.save(user);
		}
		return user;
	}

	/**
	 * This method is used to get the email Ids associated with 
	 * an Abhyasi Id.
	 * @param abyasiId, is used to get all the emails associated 
	 * eith a specific Abhyasi Id. 
	 * @return List<String> email Ids for a given Abhyasi Id.
	 */
	@Override
	public List<String> getEmailsWithAbhyasiId(String abyasiId) {
		return userRepository.getEmailsWithAbhyasiId(abyasiId);
	}
	
	@Override
	public User getUserMailWithId(int userId) {
		return userRepository.getUserMailWithId(userId);
	}


}
