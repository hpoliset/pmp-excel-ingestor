package org.srcm.heartfulness.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;

/**
 * Service provider class for upload and download images from AWS S3.
 * 
 * @author himasreev
 *
 */
public interface AmazonS3Service {

	/**
	 * To upload images to AWS S3 and update the image path in
	 * <code>program</code> table.
	 * 
	 * @param eventId
	 * @param multipartFile
	 * @param accessLog
	 * @return <code>ResponseEntity<Response></code>
	 */
	ResponseEntity<List<Response>> uploadObjectInAWSAndUpdateEvent(String eventId, MultipartFile[] multipartFile,
			PMPAPIAccessLog accessLog);

	/**
	 * To create a presigned URL, which is valid for particular time to access
	 * the given image.
	 * 
	 * @param eventId
	 * @param fileName
	 * @param accessLog
	 * @return <code>ResponseEntity<Response></code>
	 */
	ResponseEntity<Map<String, String>> createPresignedURL(String eventId, PMPAPIAccessLog accessLog);

	/**
	 * To upload multiple images to the session and update the image path in
	 * <code>session_images</code> table.
	 * 
	 * @param eventId
	 * @param sessionId
	 * @param multipartFiles
	 * @param accessLog
	 * @return <code>ResponseEntity<Response></code>
	 */
	ResponseEntity<List<Response>> uploadListOfObjectsInAWSForSession(String eventId, String sessionId,
			MultipartFile[] multipartFiles, PMPAPIAccessLog accessLog);

	/**
	 * To generate presegined URL's for all the available images of a session
	 * and return a map of image name and URL's.
	 * 
	 * @param eventId
	 * @param sessionId
	 * @param accessLog
	 * @return <code>ResponseEntity<?></code>
	 */
	ResponseEntity<?> createPresignedURLForSessionImages(String eventId, String sessionId, PMPAPIAccessLog accessLog);

}
