package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.enumeration.ExcelType;
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

	private static Logger LOGGER = LoggerFactory.getLogger(IngestionRestController.class);

	@Autowired
	private PmpIngestionService pmpIngestionService;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	ExcelIngestionValidator excelIngestionValidator;

	@RequestMapping(value = "/excelupload", method = RequestMethod.POST)
	public ResponseEntity<?> processSingleFileUpload(
			@RequestHeader(value = "Authorization") String token,
			@RequestParam(name = "file", required = true) MultipartFile excelDataFile,
			@RequestParam(name = "jiraissuenumber", required = true) String jiraIssueNumber,
			@RequestParam(required = true) String eWelcomeIdCheckbox,
			@Context HttpServletRequest httpRequest) {
		
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson("Filename = "+excelDataFile.getOriginalFilename() + "\n Jira issue number = "+jiraIssueNumber));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		ExcelUploadResponse excelResponse = new ExcelUploadResponse(excelDataFile.getOriginalFilename(),ExcelType.UNDEFINED,EventDetailsUploadConstants.FAILURE_STATUS,null);

		try{
			//token validation
			Response eResponse = excelIngestionValidator.validateExcelUploadRequest(accessLog, token);
			if(null != eResponse){
				List<String> errorList = new ArrayList<>(1);
				errorList.add(eResponse.getDescription());
				excelResponse.setErrorMsg(errorList);
				return new ResponseEntity<ExcelUploadResponse>(excelResponse,HttpStatus.PRECONDITION_FAILED);
			}

			//service layer call for data validation and extraction
			//MultipartFile[] uploadedFile = new MultipartFile[] { excelDataFile };
			excelResponse = pmpIngestionService.parseAndPersistExcelFile(excelDataFile.getOriginalFilename(),excelDataFile.getBytes(),eWelcomeIdCheckbox,jiraIssueNumber,accessLog.getUsername());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);

		} catch(Exception ex){
			LOGGER.error("Error while fetching token from MYSRCM for single upload {}",ex);
			List<String> errorList = new ArrayList<>(1);
			errorList.add(EventDetailsUploadConstants.INVALID_UPLOAD_REQUEST);
			excelResponse.setErrorMsg(errorList);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}
		
		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelResponse));
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		return new ResponseEntity<ExcelUploadResponse>(excelResponse, HttpStatus.OK);

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
	public ResponseEntity<?> processBulkFileUpload(
			@RequestHeader(value = "Authorization") String token,
			@RequestParam(name = "files", required = true ) MultipartFile uploadedExcelFiles[],
			@RequestParam(name = "jiraissuenumbers", required = true) String jiraIssueNumbers[],
			@RequestParam(required = true) String eWelcomeIdCheckbox,
			@Context HttpServletRequest httpRequest) {

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(uploadedExcelFiles.length));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		
		List<ExcelUploadResponse> excelUploadResponseList = new ArrayList<ExcelUploadResponse>();
		
		try {
			//token validation
			Response eResponse = excelIngestionValidator.validateExcelUploadRequest(accessLog, token);
			if (null != eResponse) {
				List<String> errorList = new ArrayList<>(1);
				errorList.add(eResponse.getDescription());
				for(MultipartFile files : uploadedExcelFiles){
					excelUploadResponseList.add(new ExcelUploadResponse(files.getOriginalFilename(),ExcelType.UNDEFINED,EventDetailsUploadConstants.FAILURE_STATUS,errorList));
				}
				return new ResponseEntity<List<ExcelUploadResponse>>(excelUploadResponseList,HttpStatus.PRECONDITION_FAILED);
			}
			//file upload vs jira issue count validation
			excelIngestionValidator.validateFilesWithJiraIssuesCount(uploadedExcelFiles,jiraIssueNumbers,excelUploadResponseList,accessLog);
			if(excelUploadResponseList.size() > 0){
				return new ResponseEntity<List<ExcelUploadResponse>>(excelUploadResponseList,HttpStatus.PRECONDITION_FAILED);
			}
			
			//service layer call for data validation and extraction
			Map<String, MultipartFile> uploadedFileDetails = IntStream.range(0, jiraIssueNumbers.length).boxed()
				    .collect(Collectors.toMap(i -> jiraIssueNumbers[i], i -> uploadedExcelFiles[i]));
			
			/*for(Map.Entry<String, MultipartFile> map : uploadedFileDetails.entrySet()){
				System.out.println("key=="+map.getKey()+"..value=="+map.getValue().getOriginalFilename());
			}*/
			excelUploadResponseList = pmpIngestionService.parseAndPersistExcelFile(uploadedFileDetails,eWelcomeIdCheckbox,accessLog.getUsername());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);

		} catch (Exception ex) {
			LOGGER.error("Error while fetching token from MYSRCM for bulk upload {}",ex);
			List<String> errorList = new ArrayList<>(1);
			errorList.add(EventDetailsUploadConstants.INVALID_UPLOAD_REQUEST);
			for(MultipartFile files : uploadedExcelFiles){
				excelUploadResponseList.add(new ExcelUploadResponse(files.getOriginalFilename(),ExcelType.UNDEFINED,EventDetailsUploadConstants.FAILURE_STATUS,errorList));
			}
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}
		
		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponseList));
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		return new ResponseEntity<List<ExcelUploadResponse>>(excelUploadResponseList, HttpStatus.OK);
	}

}
