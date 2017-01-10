package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.SessionDetailsService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.AmazonS3RequestValidator;
import org.srcm.heartfulness.validator.EventDashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Validator to streamline all requests to upload and download objects through
 * Amazon S3.
 * 
 * @author himasreev
 *
 */
@Component
public class AmazonS3RequestValidatorImpl implements AmazonS3RequestValidator {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SessionDetailsService sessionDetailsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.validator.AmazonS3RequestValidator#
	 * uploadPermissionLetterRequest(java.lang.String,
	 * org.springframework.web.multipart.MultipartFile,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.lang.String)
	 */
	@Override
	public Response validateUploadPermissionLetterRequest(String eventId, MultipartFile multipartFile,
			PMPAPIAccessLog accessLog, String token) {
		Response eResponse = new Response(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
		} catch (HttpClientErrorException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (JsonParseException | JsonMappingException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (IOException | ParseException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (Exception e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setDescription("Invalid client credentials");
			return eResponse;
		} else {
			accessLog.setUsername(userProfile.getEmail());
		}
		User user = userProfileService.loadUserByEmail(userProfile.getEmail());
		if (null == user) {
			eResponse.setDescription("User doesnot exists");
			return eResponse;
		}
		Map<String, String> errors = new HashMap<String, String>();
		if (null == multipartFile.getOriginalFilename() || multipartFile.getOriginalFilename().isEmpty()) {
			errors.put("fileName", "File Name is required.");
		}
		if (null == eventId || eventId.isEmpty()) {
			errors.put("eventId", "Event Id is required");
		} else {
			Program program = programRepository.findByAutoGeneratedEventId(eventId);
			if (null == program || null == program.getAutoGeneratedEventId()
					|| program.getAutoGeneratedEventId().isEmpty())
				errors.put("eventId", "Invalid Event Id");
		}
		if (!errors.isEmpty()) {
			eResponse.setDescription(errors.toString());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.validator.AmazonS3RequestValidator#
	 * downloadPermissionLetterRequest(java.lang.String, java.lang.String,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.lang.String)
	 */
	@Override
	public Response validateDownloadPermissionLetterRequest(String fileName, String eventId, PMPAPIAccessLog accessLog,
			String token) {
		Response eResponse = new Response(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
		} catch (HttpClientErrorException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (JsonParseException | JsonMappingException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (IOException | ParseException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (Exception e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setDescription("Invalid client credentials");
			return eResponse;
		} else {
			accessLog.setUsername(userProfile.getEmail());
		}
		User user = userProfileService.loadUserByEmail(userProfile.getEmail());
		if (null == user) {
			eResponse.setDescription("User doesnot exists");
			return eResponse;
		}
		Map<String, String> errors = new HashMap<String, String>();
		if (null == fileName || fileName.isEmpty())
			errors.put("fileName", "File Name is required.");
		if (null == eventId || eventId.isEmpty()) {
			errors.put("eventId", "Event Id is required");
		} else {
			Program program = programRepository.findByAutoGeneratedEventId(eventId);
			if (null == program || null == program.getAutoGeneratedEventId()
					|| program.getAutoGeneratedEventId().isEmpty())
				errors.put("eventId", "Invalid Event Id");
		}
		if (!errors.isEmpty()) {
			eResponse.setDescription(errors.toString());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.validator.AmazonS3RequestValidator#
	 * uploadSessionFilesRequest(java.lang.String, java.lang.String,
	 * org.springframework.web.multipart.MultipartFile[],
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.lang.String)
	 */
	@Override
	public Response validateUploadSessionFilesRequest(String eventId, String sessionId, MultipartFile[] multipartFiles,
			PMPAPIAccessLog accessLog, String token) {
		Response eResponse = new Response(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
		} catch (HttpClientErrorException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (JsonParseException | JsonMappingException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (IOException | ParseException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (Exception e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setDescription("Invalid client credentials");
			return eResponse;
		} else {
			accessLog.setUsername(userProfile.getEmail());
		}
		User user = userProfileService.loadUserByEmail(userProfile.getEmail());
		if (null == user) {
			eResponse.setDescription("User doesnot exists");
			return eResponse;
		}
		Map<String, String> errors = new HashMap<String, String>();
		if (0 == multipartFiles.length) {
			errors.put("file", "Files are required.");
		}
		if (null == sessionId || sessionId.isEmpty()) {
			errors.put("sessionId", "Sessiont Id is required");
		}
		if (null == eventId || eventId.isEmpty()) {
			errors.put("eventId", "Event Id is required");
		} else {
			Program program = programRepository.findByAutoGeneratedEventId(eventId);
			if (null == program || 0 == program.getProgramId()) {
				errors.put("eventId", "Invalid Event Id");
			} else if (!(null == sessionId || sessionId.isEmpty())) {
				int sessionDetailsId = sessionDetailsService.getSessionDetailsIdBySessionIdandProgramId(sessionId,
						program.getProgramId());
				if (0 == sessionDetailsId) {
					errors.put("sessionId", "Invalid Session Id");
				}
			}
		}
		if (!errors.isEmpty()) {
			eResponse.setDescription(errors.toString());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.validator.AmazonS3RequestValidator#
	 * downloadSessionImagesRequest(java.lang.String, java.lang.String,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog, java.lang.String)
	 */
	@Override
	public Response validateDownloadSessionImagesRequest(String sessionId, String eventId, PMPAPIAccessLog accessLog,
			String token) {
		Response eResponse = new Response(ErrorConstants.STATUS_FAILED, "");
		UserProfile userProfile = null;
		try {
			userProfile = eventDashboardValidator.validateToken(token, accessLog.getId());
		} catch (HttpClientErrorException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (JsonParseException | JsonMappingException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
			eResponse.setDescription("Invalid authorization token");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (IOException | ParseException e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		} catch (Exception e) {
			eResponse.setDescription("Error while fetching profile from MySRCM");
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		if (null == userProfile) {
			eResponse.setDescription("Invalid client credentials");
			return eResponse;
		} else {
			accessLog.setUsername(userProfile.getEmail());
		}
		User user = userProfileService.loadUserByEmail(userProfile.getEmail());
		if (null == user) {
			eResponse.setDescription("User doesnot exists");
			return eResponse;
		}
		Map<String, String> errors = new HashMap<String, String>();
		if (null == sessionId || sessionId.isEmpty()) {
			errors.put("sessionId", "Session Id is required");
		}
		if (null == eventId || eventId.isEmpty()) {
			errors.put("eventId", "Event Id is required");
		} else {
			Program program = programRepository.findByAutoGeneratedEventId(eventId);
			if (null == program || 0 == program.getProgramId()) {
				errors.put("eventId", "Invalid Event Id");
			} else if (null == sessionId || sessionId.isEmpty()) {
				int sessionDetailsId = sessionDetailsService.getSessionDetailsIdBySessionIdandProgramId(sessionId,
						program.getProgramId());
				if (0 == sessionDetailsId) {
					errors.put("sessionId", "Invalid Session Id");
				} else if (0 == sessionDetailsService.getCountOfSessionImages(sessionDetailsId)) {
					errors.put("sessionId", "No Images available for the session");
				}
			}
		}
		if (!errors.isEmpty()) {
			eResponse.setDescription(errors.toString());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(eResponse.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return eResponse;
		}
		return null;
	}

}
