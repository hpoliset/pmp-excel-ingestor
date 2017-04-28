/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.enumeration.MobileDataParticipantCols;
import org.srcm.heartfulness.enumeration.MobileDataProgramCols;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.EventDetailsExcelValidator;

/**
 * @author Koustav Dutta
 *
 */
@Component
public class MobileDataIngestionValidatorV1Impl implements EventDetailsExcelValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MobileDataIngestionValidatorV1Impl.class);

	@Override
	public List<String> validate(Workbook workBook) {
		List<String> errorList = new ArrayList<String>();

		Sheet eventSheet = workBook.getSheet(EventDetailsUploadConstants.EVENT_SHEET_NAME);
		Sheet participantSheet = workBook.getSheet(EventDetailsUploadConstants.PARTICIPANT_SHEET_NAME);

		if (eventSheet == null) {
			errorList.add("Event Details Sheet is not present/invalid or empty.");
			return errorList;
		}
		if (participantSheet == null) {
			errorList.add("Participants Details Sheet is not present/invalid or empty.");
			return errorList;
		}
		validateProgramSheetStructure(eventSheet, errorList);
		validateParticipantSheetStructure(participantSheet, errorList);
		validateProgramMandatoryParams(eventSheet,errorList);
		validateParticipantMandatoryParams(participantSheet, errorList);
		return errorList;
	}

	/*@Override
	public void validateExcelSheetStructureAndMandatoryParams(Sheet eventSheet, Sheet participantSheet,
			List<String> errorMsg) {

		//Sheet structure validation
		validateProgramSheetStructure(eventSheet,errorMsg);
		validateParticipantSheetStructure(participantSheet,errorMsg);

		//Mandatory params validation
		validateProgramMandatoryParams(eventSheet,errorMsg);
		validateParticipantMandatoryParams(participantSheet,errorMsg);

	}
	 */
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

		if(eventSheet.getRow(1).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_TYPE.getHeader() + " is a mandatory field and cannot be empty ");
		}

		String eventFromDate = eventSheet.getRow(1).getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(eventFromDate.isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_FROM_DATE.getHeader() + " is a mandatory field and cannot be empty ");
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(eventFromDate));
				if(DateUtils.parseDate(eventFromDate).after(sdf.parse(sdf.format(new Date())))){
					errorMsg.add(" Event date cannot be a future date [" + eventFromDate + "] ");
				}
			} catch (Exception e) {
				errorMsg.add(" Event date [" + eventFromDate + "] is invalid. You can use dd-MM-yyyy,dd.MM.yyyy,dd/MM/yyyy or any other valid date formats ");
			}
		}

		String eventToDate = eventSheet.getRow(1).getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!eventToDate.isEmpty()){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(eventToDate));
				if(DateUtils.parseDate(eventToDate).after(sdf.parse(sdf.format(new Date())))){
					LOGGER.error("To date[" + eventToDate + "] cannot be a future date");
					errorMsg.add("To date[" + eventToDate + "] cannot be a future date");
				}
			} catch (Exception e) {
				errorMsg.add(" To date [" + eventToDate + "] is invalid. You can use dd-MM-yyyy,dd.MM.yyyy,dd/MM/yyyy or any other valid date formats  ");
			}

		}

		/*if((DateUtils.parseDate(eventToDate) && null != toDate) && eventDate.after(toDate) ){
			errorMsg.add(" Event date [" + eventFromDate + "] cannot be greater than To date [" + eventToDate + "]");
		}*/

		if(eventSheet.getRow(1).getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_CITY.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_STATE.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_COORDINATOR_NAME.getHeader() + " is a mandatory field and cannot be empty ");
		}

		String coordinatorEmail = eventSheet.getRow(1).getCell(10, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(coordinatorEmail.isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is a mandatory field and cannot be empty ");
		}else if(!coordinatorEmail.matches(ExpressionConstants.EMAIL_REGEX)){
			errorMsg.add(" "+ MobileDataProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid ");
		}
		if(eventSheet.getRow(1).getCell(11, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.ORGANIZATION_NAME.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataProgramCols.PRECEPTOR_NAME.getHeader() + " is a mandatory field and cannot be empty ");
		}
		if(eventSheet.getRow(1).getCell(17, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
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
				parseParticipantData(currentRow, i + 1,errorMsg);
			}
		}
		LOGGER.info("INFO : Completed validating Participant Details mandatory params for mobile excel template.");
	}

	private void parseParticipantData(Row currentRow, int rowNumber,List<String> errorMsg) {

		String firstSittingValue = currentRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!firstSittingValue.isEmpty() ? firstSittingValue.equalsIgnoreCase("1") ? false : !firstSittingValue.equalsIgnoreCase("0")   : false){
			errorMsg.add(" "+ MobileDataParticipantCols.FIRST_SITTING.getHeader() + " value entered ["+firstSittingValue+"] should be 1 or 0 at row number " + rowNumber);
		}

		String secondSittingValue = currentRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!secondSittingValue.isEmpty() ? secondSittingValue.equalsIgnoreCase("1") ? false : !secondSittingValue.equalsIgnoreCase("0") : false){
			errorMsg.add(" "+ MobileDataParticipantCols.SECONND_SITTING.getHeader() + " value entered ["+secondSittingValue+"] should be 1 or 0 at row number " + rowNumber);
		}

		String thirdSittingValue = currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!thirdSittingValue.isEmpty() ? thirdSittingValue.equalsIgnoreCase("1") ? false : !thirdSittingValue.equalsIgnoreCase("0") : false){
			errorMsg.add(" "+ MobileDataParticipantCols.THIRD_SITTING.getHeader() + " value entered ["+thirdSittingValue +"] should be 1 or 0 at row number " + rowNumber);
		}

		if(currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataParticipantCols.STATE.getHeader() + " is a mandatory field and cannot be empty at row number " + rowNumber);
		}
		if(currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorMsg.add(" "+ MobileDataParticipantCols.CITY.getHeader() + " is a mandatory field and cannot be empty at row number " + rowNumber);
		}

		String participantEmail = currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!participantEmail.isEmpty() && !participantEmail.matches(ExpressionConstants.EMAIL_REGEX)){
			errorMsg.add(" "+MobileDataParticipantCols.EMAIL.getHeader() + " is invalid at row number "+rowNumber);
		}

		//mobile number validation
		/*String phoneNumber = currentRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!phoneNumber.isEmpty() && !phoneNumber.matches(ExpressionConstants.MOBILE_V1_0_REGEX)){
			errorMsg.add(" "+MobileDataParticipantCols.MOBILE.getHeader() + " number is invalid at row number "+rowNumber);
		}*/

		try{
			Integer.parseInt(currentRow.getCell(13, Row.CREATE_NULL_AS_BLANK).toString().trim());
		} catch(Exception ex){
			errorMsg.add(" "+MobileDataParticipantCols.TOTAL_DAYS.getHeader() + " should be numeric value at row number "+rowNumber);
		}

	}

}
