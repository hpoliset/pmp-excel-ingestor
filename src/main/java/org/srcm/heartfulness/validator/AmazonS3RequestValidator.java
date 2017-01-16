package org.srcm.heartfulness.validator;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;

/**
 * Validator to streamline all requests to upload and download objects through
 * Amazon S3.
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
	Response validateUploadPermissionLetterRequest(String eventId, MultipartFile multipartFile,
			PMPAPIAccessLog accessLog, String token);

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
	Response validateDownloadPermissionLetterRequest(String fileName, String eventId, PMPAPIAccessLog accessLog,
			String token);

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
	Response validateUploadSessionImagesRequest(String eventId, String sessionId, MultipartFile[] multipartFiles,
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
	Response validateDownloadSessionImagesRequest(String sessionId, String eventId, PMPAPIAccessLog accessLog,
			String token);

}
