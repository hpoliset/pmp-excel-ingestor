/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.enumeration.V2ParticipantCols;
import org.srcm.heartfulness.enumeration.V2ProgramCols2;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.EventDetailsExcelValidator;

/**
 * Implementation for validating the v2 excel.
 * 
 * @author Koustav Dutta
 *
 */
public class ExcelV2ValidatorImpl implements EventDetailsExcelValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelV2ValidatorImpl.class);

	@Override
	public List<String> validate(Workbook workbook) {
		List<String> errorList = new ArrayList<String>();

		if (workbook == null) {
			errorList.add("Workbook is not valid or empty.");
			return errorList;
		}

		Sheet eventSheet = workbook.getSheet(EventDetailsUploadConstants.EVENT_SHEET_NAME);
		Sheet participantSheet = workbook.getSheet(EventDetailsUploadConstants.PARTICIPANT_SHEET_NAME);

		if (eventSheet == null) {
			errorList.add("Event Details Sheet is not present/invalid or empty.");
			return errorList;
		}
		if (participantSheet == null) {
			errorList.add("Participants Details Sheet is not present/invalid or empty.");
			return errorList;
		}
		validateProgramDetails(eventSheet, errorList);
		validateParticipantDetails(participantSheet, errorList);
		checkProgramMandatoryFields(eventSheet,errorList);
		checkParticipantMandatoryFields(participantSheet, errorList);
		return errorList;
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateProgramDetails(Sheet sheet, List<String> errorList) {

		LOGGER.debug("Started validating Event Details structure for v2 template.");
		int row, col;
		for (V2ProgramCols2 column : V2ProgramCols2.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Program Header:  " + column.getHeader()
						+ " is not present as per the template.");
			}
		}
		LOGGER.debug("Event Details structure validation completed for v2 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateParticipantDetails(Sheet sheet, List<String> errorList) {

		LOGGER.debug("INFO : Started validating Participants Details sheet structure for v2.1 template.");
		int row, col;
		for (V2ParticipantCols column : V2ParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Participant Header " + column.getHeader()
						+ " is not present as per the template.");
			}
		}
		LOGGER.debug("INFO : Participants Details sheet structure validation completed for v2.1 template.");
	}

	public void checkProgramMandatoryFields(Sheet eventSheet, List<String> errorList){
		LOGGER.debug("INFO : Started validating Program Details sheet structure for v2.1 template.");

		if(eventSheet.getRow(2).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_TYPE.getHeader() + " is a mandatory field and cannot be empty at rownumber 3");
		}

		if(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_PLACE.getHeader() + " is a mandatory field and cannot be empty at rownumber 4");
		}

		String programStartDate = eventSheet.getRow(3).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(programStartDate.isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_DATE.getHeader() + " is a mandatory field and cannot be empty at rownumber 4");
		}else{
			try {
				DateUtils.parseDate(programStartDate);
			} catch (ParseException e) {
				errorList.add(V2ProgramCols2.EVENT_DATE.getHeader() + " is invalid at row number 4");
			}
		}

		if(eventSheet.getRow(4).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_COUNTRY.getHeader() + " is a mandatory field and cannot be empty at rownumber 5");
		}

		if(eventSheet.getRow(4).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_STATE.getHeader() + " is a mandatory field and cannot be empty at rownumber 5");
		}

		if(eventSheet.getRow(5).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_CITY.getHeader() + " is a mandatory field and cannot be empty at rownumber 6");
		}

		if(eventSheet.getRow(6).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_COORDINATORNAME.getHeader() + " is a mandatory field and cannot be empty at rownumber 7");
		}

		if(eventSheet.getRow(7).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_COORDINATOR_MOBILE.getHeader() + " is a mandatory field and cannot be empty at rownumber 8");
		}

		String coordinatorEmail = eventSheet.getRow(7).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(coordinatorEmail.isEmpty()){
			errorList.add(V2ProgramCols2.EVENT_COORDINATOR_MAIL.getHeader() + " is a mandatory field and cannot be empty at rownumber 8");
		}else{

			if(coordinatorEmail.contains(";")){

				String[] emails = coordinatorEmail.split(";");
				if(emails != null && emails.length > 0){
					for(String email:emails){
						if(!email.matches(ExpressionConstants.EMAIL_REGEX)){
							errorList.add(V2ProgramCols2.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid at row number 8");
							break;
						}
					}
				}
			}



		}

		if(eventSheet.getRow(9).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.ORGANIZATION_NAME.getHeader() + " is a mandatory field and cannot be empty at rownumber 10");
		}

		if(eventSheet.getRow(9).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.ORGANIZATION_CONTACT_PERSON.getHeader() + " is a mandatory field and cannot be empty at rownumber 10");
		}

		String orgCntctEmail = eventSheet.getRow(10).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();

		if(orgCntctEmail.isEmpty()){
			errorList.add(V2ProgramCols2.ORGANIZATION_CONTACT_MAILID.getHeader() + " is a mandatory field and cannot be empty at rownumber 11");
		}else{
			if(!orgCntctEmail.matches(ExpressionConstants.EMAIL_REGEX)){
				errorList.add(V2ProgramCols2.ORGANIZATION_CONTACT_MAILID.getHeader() + " is invalid at row number 11");
			}
		}

		if(eventSheet.getRow(11).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.ORGANIZATION_CONTACT_MOBILE.getHeader() + " is a mandatory field and cannot be empty at row number 12");
		}

		if(eventSheet.getRow(13).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.PRECEPTOR_NAME.getHeader() + " is a mandatory field and cannot be empty at row number 14");
		}

		if(eventSheet.getRow(14).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols2.PRECEPTOR_ID.getHeader() + " is a mandatory field and cannot be empty at row number 15");
		}

		LOGGER.debug("INFO : Completed validating Program Details sheet structure for v2.1 template.");
	}

	public void checkParticipantMandatoryFields(Sheet participantSheet, List<String> errorList) {

		LOGGER.debug("Started validating Participation Details sheet mandatory fields for v2.1 template.");
		int rowCount = participantSheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowCount; i++) {
			Row currentRow = participantSheet.getRow(i);
			if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				errorList.addAll(parseParticipantData(currentRow, i + 1));
			}
		}
		LOGGER.debug("Participation Details sheet mandatory fields validation completed for v2.1 template.");
	}

	private List<String> parseParticipantData(Row currentRow, int rowNumber) {

		List<String> errorList = new ArrayList<String>();

		if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {

			String firstSittingStr =  currentRow.getCell(1,
					Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(!firstSittingStr.isEmpty() && null != firstSittingStr){
				if (!firstSittingStr.equals("Y")) {
					if(!firstSittingStr.equals("N")){
						try {
							DateUtils.parseDate(firstSittingStr);
						} catch (ParseException e) {
							errorList.add(V2ParticipantCols.FIRST_SITTING.getHeader()
									+ " is invalid at row number " + rowNumber );
						}
					}
				}
			}

			String secondSittingStr =  currentRow.getCell(2,
					Row.CREATE_NULL_AS_BLANK).toString().trim();

			if(!secondSittingStr.isEmpty()  && null != secondSittingStr){
				if (!secondSittingStr.equals("Y")) {
					if(!secondSittingStr.equals("N")){
						try {
							DateUtils.parseDate(secondSittingStr);
						} catch (ParseException e) {
							errorList.add(V2ParticipantCols.SECONND_SITTING.getHeader()
									+ " is invalid at row number " + rowNumber );
						}
					}
				}
			}

			String thirdSittingStr =  currentRow.getCell(3,
					Row.CREATE_NULL_AS_BLANK).toString().trim();

			if(!thirdSittingStr.isEmpty() && null != thirdSittingStr){
				if (!thirdSittingStr.equals("Y")) {
					if(!thirdSittingStr.equals("N")){
						try {
							DateUtils.parseDate(thirdSittingStr);
						} catch (ParseException e) {
							errorList.add(V2ParticipantCols.THIRD_SITTING.getHeader()
									+ " is invalid at row number " + rowNumber);
						}
					}
				}
			}

			if (currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				errorList.add(V2ParticipantCols.COUNTRY.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}

			if (currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				errorList.add(V2ParticipantCols.STATE.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}

			if (currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				errorList.add(V2ParticipantCols.CITY.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}

			String ptcpntEmail = currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(null != ptcpntEmail && !ptcpntEmail.isEmpty()){
				if(!ptcpntEmail.matches(ExpressionConstants.EMAIL_REGEX)){
					errorList.add(V2ParticipantCols.EMAIL.getHeader() 
							+ " is invalid at row number "+rowNumber);
				}
			}
/*
			String welcomeCardNumber = currentRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(!welcomeCardNumber.isEmpty()){
				if(!welcomeCardNumber.matches(ExpressionConstants.EWELCOME_ID_REGEX)){
					errorList.add(V2ParticipantCols.WELCOME_CARD_NUMBER.getHeader() + "is invalid at row number "+rowNumber);
				}
			}

			String wlcmCardIssueDate = currentRow.getCell(17, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(null != wlcmCardIssueDate && !wlcmCardIssueDate.isEmpty()){
				try {
					DateUtils.parseDate(wlcmCardIssueDate);
				} catch (ParseException e) {
					errorList.add(V2ParticipantCols.WELCOME_CARD_ISSUE_DATE.getHeader()
							+ " is invalid at row number " + rowNumber);
				}
			}*/

		}
		return errorList;
	}

}
