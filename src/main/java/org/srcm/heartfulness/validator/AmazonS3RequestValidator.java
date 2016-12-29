package org.srcm.heartfulness.validator;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;

public interface AmazonS3RequestValidator {

	Response uploadPermissionLetterRequest(String eventId, MultipartFile multipartFile, PMPAPIAccessLog accessLog,
			String token);

	Response downloadPermissionLetterRequest(String fileName, String eventId, PMPAPIAccessLog accessLog,
			String token);

	Response uploadSessionFilesRequest(String eventId, String sessionId, MultipartFile[] multipartFiles,
			PMPAPIAccessLog accessLog, String token);

	Response downloadSessionImagesRequest(String sessionId, String eventId, PMPAPIAccessLog accessLog, String token);


}
