/**
 * 
 */
package org.srcm.heartfulness.service;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.MobileDataIngestionValidator;

/**
 * @author Koustav Dutta
 *
 */
@Service
public class MobileDataIngestionServiceImpl implements MobileDataIngestionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MobileDataIngestionServiceImpl.class);
	
	@Autowired
	private MobileDataIngestionValidator mobileDataIngsValidator;

	@Override
	public ResponseEntity<?> parseAndPersistExcelFile(MultipartFile multipartFile,
			ExcelUploadResponse excelUploadResponse, PMPAPIAccessLog accessLog) {

		LOGGER.info("Started parsing excel file sent from mobile device ");
		//workbook validation
		Workbook workBook = null;
		try {
			workBook = ExcelParserUtils.getWorkbook(multipartFile.getOriginalFilename(), multipartFile.getBytes());
		} catch (InvalidExcelFileException iefe) {
			LOGGER.info("Excel file must end with .xlsm/.xlsx/.xls");
			excelUploadResponse.getErrorMsg().add("Excel file name must end with .xlsm/.xlsm/.xls");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(iefe));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
		} catch (IOException ioe) {
			LOGGER.info("File {} is not having a proper workbook ",multipartFile.getOriginalFilename());
			excelUploadResponse.getErrorMsg().add("File " + multipartFile.getOriginalFilename() + "is not having a proper workbook ");
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
		}

		if(null == workBook){
			return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.PRECONDITION_FAILED);
		}

		//sheet validation
		Sheet eventSheet = workBook.getSheet(EventDetailsUploadConstants.EVENT_SHEET_NAME);
		Sheet participantSheet = workBook.getSheet(EventDetailsUploadConstants.PARTICIPANT_SHEET_NAME);
		if(null == eventSheet){
			excelUploadResponse.getErrorMsg().add("Event Details sheet is not present/invalid or empty");
			accessLog.setErrorMessage("Event Details sheet is not present/invalid or empty");
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
            return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.PRECONDITION_FAILED);
		}
		if(null == participantSheet){
			excelUploadResponse.getErrorMsg().add("Participant Details sheet is not present/invalid or empty");
			accessLog.setErrorMessage("Participant Details sheet is not present/invalid or empty");
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
            return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.PRECONDITION_FAILED);
		}
		
		//sheet structure validation and mandatory fields validation
		mobileDataIngsValidator.validateExcelSheetStructureAndMandatoryParams(eventSheet,participantSheet,excelUploadResponse.getErrorMsg());
		if(excelUploadResponse.getErrorMsg().size() > 0){
			accessLog.setErrorMessage("Excel Sheet structure/Mandatory params validation failed");
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
			return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.PRECONDITION_FAILED);
		}

		//extract data and save and return success response.
		
		/*Program program = ExcelDataExtractorFactory.extractProgramDetails(workBook, ExcelType.V2_1,"off");
		program.setCreatedSource(PMPConstants.CREATED_SOURCE_EXCEL);
		
		//valiadte coord emial and preceptor id with mysrcm and send mails if required.
		 
		//  
		 
		programRepository.save(program);*/
		
		//
		//save primary coordinator and preceptor details in program_coordinatiors table
		
		
		//ewelcome id status for every participant we need to update
		
		return null;
	}


}
