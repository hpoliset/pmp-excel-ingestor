package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Result;
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
	
}
