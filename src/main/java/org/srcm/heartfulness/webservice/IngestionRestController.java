package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.ExcelIngestionValidator;

/**
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/ingest/")
public class IngestionRestController {

	@Autowired
	private PmpIngestionService pmpIngestionService;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	ExcelIngestionValidator excelIngestionValidator;

	@RequestMapping(value = "/excelupload", method = RequestMethod.POST)
	public ResponseEntity<?> processFileUpload(@RequestHeader(value = "Authorization") String token,
			@RequestParam("file") MultipartFile excelDataFile, @RequestParam String eWelcomeIdCheckbox,
			@Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(excelDataFile.getOriginalFilename()));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		try {
			Response eResponse = excelIngestionValidator.validateExcelUploadRequest(accessLog, token);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			MultipartFile[] uploadedFile = new MultipartFile[] { excelDataFile };
			List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(uploadedFile,
					eWelcomeIdCheckbox);
			return new ResponseEntity<List<ExcelUploadResponse>>(responseList, HttpStatus.OK);
		} catch (Exception ex) {
			Response response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server error.");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}

	}

	/**
	 * This method is used to process multiple excel file upload.
	 * 
	 * @param uploadedExcelFiles
	 * @param modelMap
	 * @return bulkUploadResponse.jsp
	 * @throws IOException
	 */
	@RequestMapping(value = "bulkexcelupload", method = RequestMethod.POST)
	public ResponseEntity<?> processFileUpload(@RequestHeader(value = "Authorization") String token,
			@RequestParam("files") MultipartFile uploadedExcelFiles[], @RequestParam String eWelcomeIdCheckbox,
			@Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(uploadedExcelFiles.length));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		try {
			Response eResponse = excelIngestionValidator.validateExcelUploadRequest(accessLog, token);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}

			List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(uploadedExcelFiles,
					eWelcomeIdCheckbox);
			return new ResponseEntity<List<ExcelUploadResponse>>(responseList, HttpStatus.OK);
		} catch (Exception ex) {
			Response response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server error.");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}

	}

}
