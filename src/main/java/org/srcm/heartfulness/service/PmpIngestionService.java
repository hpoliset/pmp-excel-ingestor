package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;

/**
 * Created by vsonnathi on 11/19/15.
 */
public interface PmpIngestionService {

	ExcelUploadResponse parseAndPersistExcelFile(String fileName, byte[] fileContent,String eWelcomeIdCheckbox,String jiraIssueNumber);

	List<ExcelUploadResponse> parseAndPersistExcelFile(MultipartFile[] excels,String eWelcomeIdCheckbox) throws IOException;
	
	List<ExcelUploadResponse> parseAndPersistExcelFile(Map<String, MultipartFile> uploadedFileDetails,String eWelcomeIdCheckbox)throws IOException;

	void normalizeStagingRecords();

	// void syncRecordsToAims(Date aimsSyncTime);

}
