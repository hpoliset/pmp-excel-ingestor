package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;

/**
 * Created by vsonnathi on 11/19/15.
 */
public interface PmpIngestionService {

	ExcelUploadResponse parseAndPersistExcelFile(String fileName, byte[] fileContent);

	List<ExcelUploadResponse> parseAndPersistExcelFile(MultipartFile[] excels) throws IOException;

	void normalizeStagingRecords();

	// void syncRecordsToAims(Date aimsSyncTime);

}
