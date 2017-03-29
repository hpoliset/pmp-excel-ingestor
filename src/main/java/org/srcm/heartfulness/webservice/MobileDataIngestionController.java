/**
 * 
 */
package org.srcm.heartfulness.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.MobileUploadConstants;
import org.srcm.heartfulness.helper.PMPTokenValidator;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.MobileDataIngestionService;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.DateUtils;

/**
 * @author Koustav Dutta
 *
 */
@RestController
@RequestMapping("/api/ingest/mobile")
public class MobileDataIngestionController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MobileDataIngestionController.class);
	
	@Autowired
	private APIAccessLogService apiAccessLogService;
	
	@Autowired
	private PMPTokenValidator pmpTokenValidator;
	
	@Autowired
	private MobileDataIngestionService mobileDataService;

	@RequestMapping(value = "/upload/exceldata", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createParticipant(@RequestParam("file") MultipartFile multipartFile,
			@RequestHeader(value = "Authorization") String token, @Context HttpServletRequest httpRequest ) {

		String fileDetails =   "Filename : " + multipartFile.getOriginalFilename() + "File Size :" + multipartFile.getSize()
		+ "Content-Type :" + multipartFile.getContentType();

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,fileDetails);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		
		ExcelUploadResponse excelUploadResponse = new ExcelUploadResponse();
		List<String> errorList = new ArrayList<String>();
		excelUploadResponse.setFileName(multipartFile.getOriginalFilename());
		excelUploadResponse.setStatus(ErrorConstants.STATUS_FAILED);
		excelUploadResponse.setErrorMsg(errorList);
		
		PMPResponse tokenValidationResponse = pmpTokenValidator.validateAuthToken(token, accessLog);
		
		if (tokenValidationResponse instanceof ErrorResponse) {
			errorList.add(MobileUploadConstants.INVALID_AUTH_TOKEN);
			excelUploadResponse.setErrorMsg(errorList);
			return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse, HttpStatus.OK);
		}
		
		//service layer call	
		ResponseEntity<?> serviceResponse = mobileDataService.parseAndPersistExcelFile(multipartFile,excelUploadResponse,accessLog);
		
		//need to update accesslog or can do it in service layer also.
		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		return serviceResponse;

	
	}

}
