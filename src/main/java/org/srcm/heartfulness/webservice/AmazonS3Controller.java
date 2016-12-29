package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.AmazonS3Service;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.AmazonS3RequestValidator;

/**
 * Rest Controller - For managing file objects in AWS S3.
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/aws/")
public class AmazonS3Controller {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3Controller.class);

	@Autowired
	AmazonS3Service amazonS3Service;

	@Autowired
	AmazonS3RequestValidator amazonS3RequestValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@RequestMapping(value = "/permissionletter/upload", method = RequestMethod.POST)
	public ResponseEntity<?> uploadPermissionLetterForEvent(@RequestHeader(value = "Authorization") String token,
			@RequestParam String eventId, @RequestParam("file") MultipartFile multipartFile,
			@Context HttpServletRequest httpRequest) throws ParseException, IOException {

		LOGGER.info("Stated uploading to S3.Event Id : {} , FileName : {}", eventId,
				multipartFile.getOriginalFilename());

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson("eventId : " + eventId + " , fileName : "
						+ multipartFile.getOriginalFilename()));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		try {

			Response eResponse = amazonS3RequestValidator.uploadPermissionLetterRequest(eventId, multipartFile,
					accessLog, token);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.uploadObjectInAWSAndUpdateEvent(eventId, multipartFile, accessLog);

		} catch (Exception e) {
			LOGGER.error("Intenal server error : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/permissionletter/download", method = RequestMethod.POST)
	public ResponseEntity<?> createPresignedURLForPermissionLetter(
			@RequestHeader(value = "Authorization") String token, @RequestParam("eventId") String eventId,
			@RequestParam("fileName") String fileName, @Context HttpServletRequest httpRequest) throws ParseException,
			IOException {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson("eventId : " + eventId + " , fileName : " + fileName));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			Response eResponse = amazonS3RequestValidator.downloadPermissionLetterRequest(fileName, eventId,
					accessLog, token);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.createPresignedURL(eventId, fileName, accessLog);
		} catch (Exception e) {
			LOGGER.error("Intenal server error : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/session/upload", method = RequestMethod.POST)
	public ResponseEntity<?> uploadImagesForSession(@RequestHeader(value = "Authorization") String token,
			@RequestParam String eventId, @RequestParam String sessionId,
			@RequestParam("file") MultipartFile multipartFiles[], @Context HttpServletRequest httpRequest)
			throws ParseException, IOException {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson("eventId : " + eventId + " , fileCount : " + multipartFiles.length));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			Response eResponse = amazonS3RequestValidator.uploadSessionFilesRequest(eventId, sessionId,
					multipartFiles, accessLog, token);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.uploadListOfObjectsInAWSForSession(eventId, sessionId, multipartFiles, accessLog);
		} catch (Exception e) {
			LOGGER.error("Intenal server error : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/session/download", method = RequestMethod.POST)
	public ResponseEntity<?> createPresignedURLForSessionImages(@RequestHeader(value = "Authorization") String token,
			@RequestParam String eventId, @RequestParam String sessionId, @Context HttpServletRequest httpRequest)
			throws ParseException, IOException {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson("eventId : " + eventId + " , sessionId " + sessionId));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		try {
			Response eResponse = amazonS3RequestValidator.downloadSessionImagesRequest(sessionId, eventId,
					accessLog, token);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.createPresignedURLForSessionImages(eventId, sessionId, accessLog);
		} catch (Exception e) {
			LOGGER.error("Intenal server error : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
