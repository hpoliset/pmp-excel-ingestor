package org.srcm.heartfulness.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;

public interface AmazonS3Service {

	ResponseEntity<Response> uploadObjectInAWSAndUpdateEvent(String eventId, MultipartFile multipartFile, PMPAPIAccessLog accessLog);

	ResponseEntity<Response> createPresignedURL(String eventId, String fileName, PMPAPIAccessLog accessLog);

	ResponseEntity<List<Response>> uploadListOfObjectsInAWSForSession(String eventId, String sessionId, String fileType,
			MultipartFile[] multipartFiles, PMPAPIAccessLog accessLog);


}
