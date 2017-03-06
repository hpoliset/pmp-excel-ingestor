package org.srcm.heartfulness.validator;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;

public interface ExcelIngestionValidator {

	Response validateExcelUploadRequest(PMPAPIAccessLog accessLog, String token);

}
