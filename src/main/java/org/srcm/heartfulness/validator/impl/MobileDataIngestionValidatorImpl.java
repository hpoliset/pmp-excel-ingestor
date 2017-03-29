/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.enumeration.MobileDataParticipantCols;
import org.srcm.heartfulness.enumeration.MobileDataProgramCols;
import org.srcm.heartfulness.enumeration.V2ParticipantCols;
import org.srcm.heartfulness.enumeration.V2ProgramCols;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.MobileDataIngestionValidator;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class MobileDataIngestionValidatorImpl implements MobileDataIngestionValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MobileDataIngestionValidatorImpl.class);

	@Override
	public void validateExcelSheetStructureAndMandatoryParams(Sheet eventSheet, Sheet participantSheet,
			List<String> errorMsg) {

		//Sheet structure validation
		validateProgramSheetStructure(eventSheet,errorMsg);
		validateParticipantSheetStructure(participantSheet,errorMsg);

		//Mandatory params validation
		validateProgramMandatoryParams(eventSheet,errorMsg);
		validateParticipantMandatoryParams(participantSheet,errorMsg);

	}

	private void validateProgramSheetStructure(Sheet eventSheet, List<String> errorMsg) {
		LOGGER.info("INFO : Started validating Event Details structure for mobile excel template.");
		int row, col;
		for (MobileDataProgramCols column : MobileDataProgramCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(eventSheet.getRow(row).getCell(col,Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
				errorMsg.add(" Invalid Program Header " + column.getHeader() + " is not present as per the template.");
			}
		}

		LOGGER.info("INFO : Completed validating Event Details structure validation for mobile excel template.");
	}


	private void validateParticipantSheetStructure(Sheet participantSheet, List<String> errorMsg) {
		LOGGER.info("INFO : Started validating Participant Details structure for mobile excel template.");
		int row, col;
		for (MobileDataParticipantCols column : MobileDataParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(participantSheet.getRow(row).getCell(col,Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
				errorMsg.add(" Invalid Participant Header " + column.getHeader() + " is not present as per the template.");

			}
		}
		LOGGER.info("INFO : Completed validating Participant Details structure validation for mobile excel template.");
	}

	private void validateProgramMandatoryParams(Sheet eventSheet, List<String> errorMsg) {
		LOGGER.info("INFO : Started validating Program Details mandatory params for mobile excel template.");

		if(eventSheet.getRow(1).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_TYPE.getHeader() + " is a mandatory field and cannot be empty ");
		}

		String eventFromDate = eventSheet.getRow(1).getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim();
		Date eventDate = null;
		if(eventFromDate.isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_FROM_DATE.getHeader() + " is a mandatory field and cannot be empty ");
		}else{
			try {
				if((eventDate = DateUtils.parseDate(eventFromDate)).after(new Date())){
					errorMsg.add(" Event date cannot be a future date [" + eventFromDate + "] ");
				}
			} catch (ParseException e) {
				errorMsg.add(" Event date [" + eventFromDate + "] is invalid. You can use yyyyMMdd/dd-MMM-yy/dd.MM.yyyy/dd-MM-yyyy/yyyy-MM-dd or any other valid date formats ");
			}
		}

		String eventToDate = eventSheet.getRow(1).getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim();
		Date toDate = null;
		if(!eventToDate.isEmpty()){
			try {
				toDate = DateUtils.parseDate(eventToDate);
			} catch (ParseException e) {
				errorMsg.add(" To date [" + eventToDate + "] is invalid. You can use yyyyMMdd/dd-MMM-yy/dd.MM.yyyy/dd-MM-yyyy/yyyy-MM-dd or any other valid date formats ");
			}

		}
		
		if((null != eventDate && null != toDate) && eventDate.after(toDate) ){
			errorMsg.add(" Event date [" + eventFromDate + "] cannot be greater than To date [" + eventToDate + "]");
		}
		
		if(eventSheet.getRow(1).getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_CITY.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_STATE.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_COORDINATORNAME.getHeader() + " is a mandatory field and cannot be empty ");
		}
		
		String coordinatorEmail = eventSheet.getRow(1).getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(coordinatorEmail.isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is a mandatory field and cannot be empty ");
		}else if(!coordinatorEmail.matches(ExpressionConstants.EMAIL_REGEX)){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid ");
		}
		if(eventSheet.getRow(1).getCell(10, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.ORGANIZATION_NAME.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(11, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.PRECEPTOR_NAME.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(12, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.PRECEPTOR_ID.getHeader() + " is a mandatory field and cannot be empty ");
		}

		LOGGER.info("INFO : Completed validating Program Details mandatory params for mobile excel template.");
	}


	private void validateParticipantMandatoryParams(Sheet participantSheet, List<String> errorMsg) {
		LOGGER.info("INFO : Started validating Participant Details mandatory params for mobile excel template.");
		
		int rowCount = participantSheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowCount; i++) {
			Row currentRow = participantSheet.getRow(i);
			if (!currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				//errorMsg.addAll(parseParticipantData(currentRow, i + 1));
				parseParticipantData(currentRow, i + 1,errorMsg);
			}
		}
		LOGGER.info("INFO : Completed validating Participant Details mandatory params for mobile excel template.");
	}

	private void parseParticipantData(Row currentRow, int rowNumber,List<String> errorMsg) {
		
		//need to test 1st,2nd,3rd sitting validation
		int firstSittingDate = Integer.parseInt(currentRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		System.out.println("First sitting date=="+firstSittingDate);
		/*if(!firstSittingDate.isEmpty() ? firstSittingDate.equals("1") ? false : firstSittingDate.equals("0") ? false : true : false){
			errorMsg.add(" "+ MobileDataParticipantCols.FIRST_SITTING.getHeader() + " date should be 1/0 at row number " + rowNumber);
		}*/
		
		if(currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataParticipantCols.STATE.getHeader() + " is a mandatory field and cannot be empty at row number " + rowNumber);
		}
		if(currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataParticipantCols.CITY.getHeader() + " is a mandatory field and cannot be empty at row number " + rowNumber);
		}
		
		String participantEmail = currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!participantEmail.isEmpty()){
			if(!participantEmail.matches(ExpressionConstants.EMAIL_REGEX)){
				errorMsg.add(" "+MobileDataParticipantCols.EMAIL.getHeader() + " is invalid at row number "+rowNumber);
			}
		}
		
		//need to validate mobile number
		//need to validate total days.
		
	}



}
