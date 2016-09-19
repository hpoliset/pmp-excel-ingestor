package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.helper.MySRCMIntegrationHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.CreateUserRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.repository.UserRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

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

	@Autowired
	APIAccessLogService apiAccessLogService;
	
	@Autowired
	MySRCMIntegrationHelper mysrcmIntegrationHelper;

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
	 * @throws ParseException
	 *
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
	
	/**
	 * Method to create the user in srcm & pmp and to persist user details in
	 * pmp
	 */
	@Override
	public User createUser(CreateUserRequest user, int id, String requestURL) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id,
				EndpointConstants.CREATE_USER_PROFILE, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(user), null);
		int accessdetailsID = apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		accessLogDetails.setId(accessdetailsID);
		User newUser =  mysrcmIntegrationHelper.getClientCredentialsandCreateUser(user, requestURL);
		if (null != user.getName() && ! user.getName().isEmpty())
			newUser.setName(user.getName());
		if (null != user.getGender()&& ! user.getGender().isEmpty())
			newUser.setGender(user.getGender());
		if (null != user.getAbyasiId()&& ! user.getAbyasiId().isEmpty())
			newUser.setAbyasiId(user.getAbyasiId());
		if (null != user.getCity()&& ! user.getCity().isEmpty())
			newUser.setCity(user.getCity());
		if (null != user.getCountry()&& ! user.getCountry().isEmpty())
			newUser.setCountry(user.getCountry());
		if (null != user.getState()&& ! user.getState().isEmpty())
			newUser.setState(user.getState());
		if (null != user.getMobile()&& ! user.getMobile().isEmpty())
			newUser.setMobile(user.getMobile());
		if(null != user.getAgeGroup()&& ! user.getAgeGroup().isEmpty())
			newUser.setAgeGroup(user.getAgeGroup());
		if(null != user.getLanguagePreference()&& ! user.getLanguagePreference().isEmpty())
			newUser.setLanguagePreference(user.getLanguagePreference());
		if(null != user.getZipcode()&& ! user.getZipcode().isEmpty())
			newUser.setZipcode( user.getZipcode());
		userRepository.save(newUser);
		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(newUser));
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
		return newUser;
	}


}
