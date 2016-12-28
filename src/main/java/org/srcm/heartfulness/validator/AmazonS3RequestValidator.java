package org.srcm.heartfulness.validator;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.ErrorResponse;

public interface AmazonS3RequestValidator {

	ErrorResponse uploadPermissionLetterRequest(String eventId, MultipartFile multipartFile, PMPAPIAccessLog accessLog,
			String token);

	ErrorResponse downloadPermissionLetterRequest(String fileName, String eventId, PMPAPIAccessLog accessLog,
			String token);

	ErrorResponse uploadSessionFilesRequest(String eventId, String sessionId, MultipartFile[] multipartFiles,
			PMPAPIAccessLog accessLog, String token, String fileType);

}
