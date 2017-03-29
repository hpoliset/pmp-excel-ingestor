/**
 * 
 */
package org.srcm.heartfulness.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface MobileDataIngestionService {

	public ResponseEntity<?> parseAndPersistExcelFile(MultipartFile multipartFile, ExcelUploadResponse excelUploadResponse,PMPAPIAccessLog accessLog);

}
