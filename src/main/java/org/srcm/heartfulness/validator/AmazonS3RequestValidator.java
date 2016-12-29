package org.srcm.heartfulness.validator;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;

/**
 * 
 * @author himasreev
 *
 */
public interface AmazonS3RequestValidator {

	/**
	 * Method to validate the request to upload permission letter to the event.
	 * 
	 * @param eventId
	 * @param multipartFile
	 * @param accessLog
	 * @param token
	 * @return
	 */
	Response uploadPermissionLetterRequest(String eventId, MultipartFile multipartFile, PMPAPIAccessLog accessLog,
			String token);

	/**
	 * Method to validate the request to download the permission letter to the
	 * event.
	 * 
	 * @param fileName
	 * @param eventId
	 * @param accessLog
	 * @param token
	 * @return
	 */
	Response downloadPermissionLetterRequest(String fileName, String eventId, PMPAPIAccessLog accessLog, String token);

	/**
	 * Method to validate the request to upload multiple images to the session.
	 * 
	 * @param eventId
	 * @param sessionId
	 * @param multipartFiles
	 * @param accessLog
	 * @param token
	 * @return
	 */
	Response uploadSessionFilesRequest(String eventId, String sessionId, MultipartFile[] multipartFiles,
			PMPAPIAccessLog accessLog, String token);

	/**
	 * Method to validate the request to download multiple images to the
	 * session.
	 * 
	 * @param sessionId
	 * @param eventId
	 * @param accessLog
	 * @param token
	 * @return
	 */
	Response downloadSessionImagesRequest(String sessionId, String eventId, PMPAPIAccessLog accessLog, String token);

}
