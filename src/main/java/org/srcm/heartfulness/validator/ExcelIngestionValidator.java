package org.srcm.heartfulness.validator;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;

public interface ExcelIngestionValidator {

	Response validateExcelUploadRequest(PMPAPIAccessLog accessLog, String token);

	void validateFilesWithJiraIssuesCount(MultipartFile[] uploadedExcelFiles, String[] jiraIssueNumbers,List<ExcelUploadResponse> excelUploadResponseList, PMPAPIAccessLog accessLog);
	

}
