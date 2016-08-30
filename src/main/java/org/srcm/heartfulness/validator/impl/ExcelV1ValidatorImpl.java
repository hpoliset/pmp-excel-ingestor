/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.RegularExpressionConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.enumeration.V1ParticipantCols;
import org.srcm.heartfulness.enumeration.V1ProgramCols;
import org.srcm.heartfulness.enumeration.V2ProgramCols2;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.EventDetailsExcelValidator;

/**
 * Implementation for validating the altered 1.0 excel version.
 * 
 * @author Koustav Dutta
 *
 */
public class ExcelV1ValidatorImpl implements EventDetailsExcelValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelV1ValidatorImpl.class);

	@Override
	public List<String> validate(Workbook workBook) {

		List<String> errorList = new ArrayList<String>();
		if (workBook == null) {
			errorList.add("Workbook is not valid or empty.");
			return errorList;
		}
		Sheet sheet = workBook.getSheet(EventDetailsUploadConstants.V1_SHEET_NAME);
		if (sheet == null) {
			errorList.add("Sheet is not present/invalid or empty.");
			return errorList;
		}
		validateProgramDetails(sheet, errorList);
		validateParticipantDetails(sheet, errorList);
		checkEventMandatoryFields(sheet, errorList);
		checkParticipantMandatoryFields(sheet, errorList);
		return errorList;
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateProgramDetails(Sheet sheet, List<String> errorList) {

		LOGGER.debug("INFO : Started validating Event Details structure for altered 1.0 template.");
		int row, col;
		for (V1ProgramCols column : V1ProgramCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Program Header:  " + column.getHeader()
				+ " is not present as per the template.");
			}
		}
		LOGGER.debug("INFO : Event Details structure validation completed for altered 1.0 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateParticipantDetails(Sheet sheet, List<String> errorList) {

		LOGGER.debug("INFO : Started validating Participation Details structure for altered 1.0 template.");
		int row, col;
		for (V1ParticipantCols column : V1ParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Participant Header " + column.getHeader()
				+ " is not present as per the template.");
			}
		}
		LOGGER.debug("INFO : Participants Details structure validation completed for altered 1.0 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param eventErrorList
	 * @return
	 * @throws IOException
	 */
	public void checkEventMandatoryFields(Sheet sheet, List<String> eventErrorList) {

		LOGGER.debug("Started validating Event Details fields for altered 1.0 template.");
		String eventDateStr = sheet.getRow(11).getCell(2, Row.CREATE_NULL_AS_BLANK).toString();
		try {
			DateUtils.parseDate(eventDateStr);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse program start date:[" + eventDateStr + "]");
			eventErrorList.add("Not able to parse program start date:[" + eventDateStr + "]");
		}

		String coordinatorEmail = sheet.getRow(5).getCell(2, Row.CREATE_NULL_AS_BLANK).toString();
		if(null != coordinatorEmail && !coordinatorEmail.isEmpty()){
			
			if(coordinatorEmail.contains(";")){
				String[] emails = coordinatorEmail.split(";");
				if(emails != null && emails.length > 0){
					for(String email:emails){
						if(!email.matches(RegularExpressionConstants.EMAIL_REGEX)){
							eventErrorList.add(V1ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid at row number 6");
						}
					}
				}
			}
			
		}

		LOGGER.debug("Event Details fields  validation completed for altered 1.0 template.");
	}

	public void checkParticipantMandatoryFields(Sheet sheet, List<String> eventErrorList) {

		LOGGER.debug("INFO : Started validating participant detail fields for altered 1.0 template.");
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int i = 15; i < rowCount; i++) {
			Row currentRow = sheet.getRow(i);
			eventErrorList.addAll(parseParticipantData(currentRow, i + 1));
		}
		LOGGER.debug("INFO : Participants detail field validation completed for altered 1.0 template.");
	}

	private List<String> parseParticipantData(Row currentRow, int rowNumber) {

		List<String> errorList = new ArrayList<String>();
		if (!currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
			
			String ptncptEmail = currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString();
			if(null != ptncptEmail && !ptncptEmail.isEmpty()){
				if(!ptncptEmail.matches(RegularExpressionConstants.EMAIL_REGEX)){
					errorList.add(V1ParticipantCols.EMAIL_ADDRESS.getHeader() + "is invalid at row number "+rowNumber);
				}
			}

			String introducedDateStr = currentRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString();
			try {
				DateUtils.parseDate(introducedDateStr);
			} catch (ParseException e) {
				LOGGER.error("Not able to parse Introduced date:[" + introducedDateStr + "] at row number " + rowNumber);
				errorList.add("Not able to parse Introduced date:[" + introducedDateStr + "] at row number " + rowNumber);
			}

		}
		return errorList;
	}
}
