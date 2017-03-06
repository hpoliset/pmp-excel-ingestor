package org.srcm.heartfulness.validator;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.vo.ReportVO;

public interface ReportsValidator {

	Response validateGenerateReportsRequest(PMPAPIAccessLog accessLog, String token, ReportVO reportVO);

}
