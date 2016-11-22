package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.helper.MySRCMIntegrationHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.CreateUserRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.ProgramRepository;
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
	ProgramRepository programRepository;

	@Autowired
	MySRCMIntegrationHelper mysrcmIntegrationHelper;

	/**
	 * method to save the user details
	 * org.srcm.heartfulness.service.UserService#
	 * save(org.srcm.heartfulness.model.User)
	 */
	@Override
	public void save(User user) {
		if (null == user.getRole() && user.getId() == 0)
			user.setRole((programRepository.isEventCoordinatorExistsWithUserEmailId(user.getEmail())) ? PMPConstants.LOGIN_ROLE_COORDINATOR
					: PMPConstants.LOGIN_ROLE_SEEKER);
		if (null == user.getIsPmpAllowed() && user.getId() == 0)
			user.setIsPmpAllowed(PMPConstants.REQUIRED_NO);
		if (null == user.getIsSahajmargAllowed() && user.getId() == 0)
			user.setIsSahajmargAllowed(PMPConstants.REQUIRED_NO);
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
	public Result getUserProfile(String accessToken, int id) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException, ParseException {
		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id, EndpointConstants.GET_USER_PROFILE,
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(accessToken), null);
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
		User user = userRepository.findByEmail(srcmProfile.getEmail());
		if (null == user) {
			user = new User();
			user.setName(srcmProfile.getName());
			user.setFirst_name(srcmProfile.getFirst_name());
			user.setLast_name(srcmProfile.getLast_name());
			user.setEmail(srcmProfile.getEmail());
			user.setAbyasiId(srcmProfile.getRef());
			user.setGender(srcmProfile.getGender());
			user.setMobile(srcmProfile.getMobile());
			user.setAgeGroup(String.valueOf(srcmProfile.getAge()));
			user.setZipcode(srcmProfile.getPostal_code());
			user.setAddress(srcmProfile.getStreet());
			if (null == user.getRole() && user.getId() == 0)
				user.setRole((programRepository.isEventCoordinatorExistsWithUserEmailId(user.getEmail())) ? PMPConstants.LOGIN_ROLE_COORDINATOR
						: PMPConstants.LOGIN_ROLE_SEEKER);
			if (null == user.getIsPmpAllowed() && user.getId() == 0)
				user.setIsPmpAllowed(PMPConstants.REQUIRED_NO);
			if (null == user.getIsSahajmargAllowed() && user.getId() == 0)
				user.setIsSahajmargAllowed(PMPConstants.REQUIRED_NO);
			userRepository.save(user);
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.UserProfileService#createUser(org.srcm.heartfulness.model.json.request.CreateUserRequest, int, java.lang.String)
	 */
	@Override
	public User createUser(CreateUserRequest user, int id, String requestURL) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id, EndpointConstants.CREATE_USER_PROFILE,
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(user), null);
		int accessdetailsID = apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		accessLogDetails.setId(accessdetailsID);
		User newUser = mysrcmIntegrationHelper.getClientCredentialsandCreateUser(user, requestURL);
		if (null != user.getName() && !user.getName().isEmpty()) {
			newUser.setName(user.getName());
		} else {
			newUser.setName(user.getFirstName() + " " + user.getLastName());
		}
		if (null != user.getGender() && !user.getGender().isEmpty())
			newUser.setGender(user.getGender());
		if (null != user.getAbyasiId() && !user.getAbyasiId().isEmpty())
			newUser.setAbyasiId(user.getAbyasiId());
		if (null != user.getCity() && !user.getCity().isEmpty())
			newUser.setCity(user.getCity());
		if (null != user.getCountry() && !user.getCountry().isEmpty())
			newUser.setCountry(user.getCountry());
		if (null != user.getState() && !user.getState().isEmpty())
			newUser.setState(user.getState());
		if (null != user.getMobile() && !user.getMobile().isEmpty())
			newUser.setMobile(user.getMobile());
		if (null != user.getAgeGroup() && !user.getAgeGroup().isEmpty())
			newUser.setAgeGroup(user.getAgeGroup());
		if (null != user.getLanguagePreference() && !user.getLanguagePreference().isEmpty())
			newUser.setLanguagePreference(user.getLanguagePreference());
		if (null != user.getZipcode() && !user.getZipcode().isEmpty())
			newUser.setZipcode(user.getZipcode());
			newUser.setRole((programRepository.isEventCoordinatorExistsWithUserEmailId(user.getEmail())) ? PMPConstants.LOGIN_ROLE_COORDINATOR
					: PMPConstants.LOGIN_ROLE_SEEKER);
			newUser.setIsPmpAllowed(PMPConstants.REQUIRED_NO);
			newUser.setIsSahajmargAllowed(PMPConstants.REQUIRED_NO);
		userRepository.save(newUser);
		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(newUser));
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
		return newUser;
	}

}
