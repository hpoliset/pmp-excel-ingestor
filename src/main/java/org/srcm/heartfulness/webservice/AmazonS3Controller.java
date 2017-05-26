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
import org.srcm.heartfulness.model.json.response.PMPResponse;
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

	/**
	 * Webservice endpoint to upload the coordinator permission letter to the
	 * event.
	 * 
	 * @param token
	 * @param eventId
	 * @param multipartFile
	 * @param httpRequest
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/upload/event/permissionletter", method = RequestMethod.POST)
	public ResponseEntity<?> uploadPermissionLetterForEvent(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam String eventId, 
			@RequestParam("file") MultipartFile multipartFiles[], 
			@Context HttpServletRequest httpRequest) {

		LOGGER.info("Stated uploading to S3.Event Id : {} , File Count : {}", eventId,	multipartFiles.length);

		//save request details in PMP
		String requestBody = "eventId : " + eventId + " , file Count : " + multipartFiles.length;
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,requestBody);

		try {
			Response eResponse = amazonS3RequestValidator.validateUploadPermissionLetterRequest(eventId, multipartFiles,accessLog, authToken);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.uploadObjectInAWSAndUpdateEvent(eventId, multipartFiles, accessLog);

		} catch (Exception e) {
			LOGGER.error("Error while uploading permission letter : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(response));
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Webservice endpoint to get the permission letter download path by
	 * creating a presigned URL which is valid for a particular time.
	 * 
	 * @param token
	 * @param eventId
	 * @param fileName
	 * @param httpRequest
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/download/event/permissionletter", method = RequestMethod.POST)
	public ResponseEntity<?> createPresignedURLForPermissionLetter(
			@RequestHeader(value = "Authorization") String authToken, 
			@RequestParam("eventId") String eventId,
			@Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,StackTraceUtils.convertPojoToJson("eventId : " + eventId ));

		try {
			Response eResponse = amazonS3RequestValidator.validateDownloadPermissionLetterRequest(eventId, accessLog,authToken);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.createPresignedURL(eventId, accessLog);
		} catch (Exception e) {
			LOGGER.error("Error while downloading permission letter : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(response));
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Webservice endpoint to upload multiple images to a session.
	 * 
	 * @param token
	 * @param eventId
	 * @param sessionId
	 * @param multipartFiles
	 * @param httpRequest
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/upload/session/images", method = RequestMethod.POST)
	public ResponseEntity<?> uploadImagesForSession(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam String eventId, 
			@RequestParam String sessionId,
			@RequestParam("file") MultipartFile multipartFiles[], 
			@Context HttpServletRequest httpRequest) {

		//save request details in PMP
		String requestBody = "eventId : " + eventId + " , fileCount : " + multipartFiles.length;
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,requestBody);

		try {

			Response eResponse = amazonS3RequestValidator.validateUploadSessionImagesRequest(eventId, sessionId, multipartFiles,accessLog, authToken);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			return amazonS3Service.uploadListOfObjectsInAWSForSession(eventId, sessionId, multipartFiles, accessLog);

		} catch (Exception e) {
			LOGGER.error("Error while uploading files/images for session  : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(response));
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Webservice endpoint to get the list of images of a session along with
	 * their presigned URL which is valid for a particular time.
	 * 
	 * @param token
	 * @param eventId
	 * @param sessionId
	 * @param httpRequest
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/download/session/images", method = RequestMethod.POST)
	public ResponseEntity<?> createPresignedURLForSessionImages(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam String eventId, @RequestParam String sessionId, @Context HttpServletRequest httpRequest)
					throws ParseException, IOException {

		//save request details in PMP
		String requestBody = "eventId : " + eventId + " , sessionId " + sessionId;
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,requestBody);

		try {

			Response eResponse = amazonS3RequestValidator.validateDownloadSessionImagesRequest(sessionId, eventId, accessLog,authToken);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			return amazonS3Service.createPresignedURLForSessionImages(eventId, sessionId, accessLog);

		} catch (Exception e) {
			LOGGER.error("Error while generating download url for session files/images : {}", e);
			Response response = new Response(ErrorConstants.STATUS_FAILED, e.getMessage());
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(response));
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private PMPAPIAccessLog createPMPAPIAccessLog(String username,HttpServletRequest httpRequest,String requestBody){

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(username, httpRequest.getRemoteAddr(), 
				httpRequest.getRequestURI(),DateUtils.getCurrentTimeInMilliSec(), null, 
				ErrorConstants.STATUS_FAILED, null,requestBody);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		return accessLog;
	}


	private void updatePMPAPIAccessLog(PMPAPIAccessLog pmpApiAccessLog, String status, String errorMessage, String responseBody){

		pmpApiAccessLog.setStatus(status);
		pmpApiAccessLog.setErrorMessage(errorMessage);
		pmpApiAccessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		pmpApiAccessLog.setResponseBody(responseBody);
		apiAccessLogService.updatePmpAPIAccessLog(pmpApiAccessLog);
	}


}
