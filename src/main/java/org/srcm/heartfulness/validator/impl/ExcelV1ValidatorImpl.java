/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.enumeration.V1ParticipantCols;
import org.srcm.heartfulness.enumeration.V1ProgramCols;
import org.srcm.heartfulness.enumeration.V2ParticipantCols;
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
		checkProgramCharacterLength(sheet, errorList);
		return errorList;
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateProgramDetails(Sheet sheet, List<String> errorList) {

		LOGGER.info("INFO : Started validating Event Details structure for altered 1.0 template.");
		int row, col;
		for (V1ProgramCols column : V1ProgramCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col,Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
				errorList.add(" Invalid Program Header:  " + column.getHeader()
				+ " is not present as per the template.");
			}
		}
		LOGGER.info("INFO : Event Details structure validation completed for altered 1.0 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateParticipantDetails(Sheet sheet, List<String> errorList) {

		LOGGER.info("INFO : Started validating Participation Details structure for altered 1.0 template.");
		int row, col;
		for (V1ParticipantCols column : V1ParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col,Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
				errorList.add(" Invalid Participant Header " + column.getHeader()
				+ " is not present as per the template.");
			}
		}
		LOGGER.info("INFO : Participants Details structure validation completed for altered 1.0 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param eventErrorList
	 * @return
	 * @throws IOException
	 */
	public void checkEventMandatoryFields(Sheet sheet, List<String> eventErrorList) {

		LOGGER.info("Started validating Event Details fields for altered 1.0 template.");
		String eventDateStr = sheet.getRow(11).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(eventDateStr));
			if(DateUtils.parseDate(eventDateStr).after(sdf.parse(sdf.format(new Date())))){
				LOGGER.error("Program start date cannot be a future date :[" + eventDateStr + "]");
				eventErrorList.add("Program start date cannot be a future date:[" + eventDateStr + "]");
			}
		} catch (Exception e) {
			LOGGER.error("Not able to parse program start date:[" + eventDateStr + "]");
			eventErrorList.add("Not able to parse program start date:[" + eventDateStr + "]");
		}

		String coordinatorEmail = sheet.getRow(5).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(null != coordinatorEmail && !coordinatorEmail.isEmpty()){
			
			if(coordinatorEmail.contains(";")){
				String[] emails = coordinatorEmail.split(";");
				if(emails != null && emails.length > 0){
					for(String email:emails){
						if(!email.matches(ExpressionConstants.EMAIL_REGEX)){
							eventErrorList.add(V1ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid at row number 6");
						}
					}
				}
			}else if(!coordinatorEmail.matches(ExpressionConstants.EMAIL_REGEX)){
				eventErrorList.add(V1ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid at row number 6");
			}
			
		}

		LOGGER.info("Event Details fields  validation completed for altered 1.0 template.");
	}

	public void checkParticipantMandatoryFields(Sheet sheet, List<String> eventErrorList) {

		LOGGER.info("INFO : Started validating participant detail fields for altered 1.0 template.");
		int rowCount = sheet.getPhysicalNumberOfRows();
       // List<String> participantNames = new LinkedList<>();
		for (int i = 15; i < rowCount; i++) {
			Row currentRow = sheet.getRow(i);
			if (!currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				eventErrorList.addAll(parseParticipantData(currentRow, i + 1));
                //participantNames.add(currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim());

			}
		}
		
        /*Set<String> participantSet = new HashSet<String>(participantNames);
        for(String pctptName:participantSet){
            int count = Collections.frequency(participantNames, pctptName);
            if(count > 1){
                eventErrorList.add("Uploaded excel sheet contains "+count+" participants with same name '"+ pctptName+"'. Please make the changes and reupload");
            }
        }*/

		LOGGER.info("INFO : Participants detail field validation completed for altered 1.0 template.");
	}

	private List<String> parseParticipantData(Row currentRow, int rowNumber) {

		List<String> errorList = new ArrayList<String>();
		if (!currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
			
			String ptncptEmail = currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(null != ptncptEmail && !ptncptEmail.isEmpty()){
				if(!ptncptEmail.matches(ExpressionConstants.EMAIL_REGEX)){
					errorList.add(V1ParticipantCols.EMAIL_ADDRESS.getHeader() + "is invalid at row number "+rowNumber);
				}
			}
			
			String ptcpntMob = currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(null != ptcpntMob && !ptcpntMob.isEmpty()){
				
				try{
					
					Double numbericMobilePhone = currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
					String pctptMobNumber = String.valueOf(numbericMobilePhone.longValue()).trim();
					if(!pctptMobNumber.matches(ExpressionConstants.MOBILE_REGEX)){
						errorList.add(V1ParticipantCols.PHONE.getHeader() 
								+ " is invalid at row number "+rowNumber);
					}
				} catch(Exception ex){
					errorList.add(V1ParticipantCols.PHONE.getHeader() 
							+ " is invalid at row number "+rowNumber);
				}
			}

			String introducedDateStr = currentRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(introducedDateStr));
				if(DateUtils.parseDate(introducedDateStr).after(sdf.parse(sdf.format(new Date())))){
					LOGGER.error("Introduced date cannot be a future date :[" + introducedDateStr + "] at row number " + rowNumber);
					errorList.add("Introduced date date cannot be a future date:[" + introducedDateStr + "] at row number " + rowNumber);
				}
			} catch (Exception e) {
				LOGGER.error("Not able to parse Introduced date:[" + introducedDateStr + "] at row number " + rowNumber);
				errorList.add("Not able to parse Introduced date:[" + introducedDateStr + "] at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.FULL_NAME.getLength() < currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.FULL_NAME.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.FULL_NAME.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.CITY.getLength() < currentRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.CITY.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.CITY.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.STATE.getLength() < currentRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.STATE.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.STATE.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.EMAIL_ADDRESS.getLength() < currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.EMAIL_ADDRESS.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.EMAIL_ADDRESS.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.PHONE.getLength() < currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.PHONE.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.PHONE.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.OCCUPATION.getLength() < currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.OCCUPATION.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.OCCUPATION.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.INTRODUCED.getLength() < currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.INTRODUCED.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.INTRODUCED.getLength() +" characters at row number " + rowNumber);
			}
			
			if (V1ParticipantCols.INTRODUCED_BY.getLength() < currentRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V1ParticipantCols.INTRODUCED_BY.getHeader() + " should not contain more than " 
							+ V1ParticipantCols.INTRODUCED_BY.getLength() +" characters at row number " + rowNumber);
			}

		}
		return errorList;
	}
	
	/**
	 * 
	 * @param sheet
	 * @param eventErrorList
	 * @return
	 * @throws IOException
	 */
	public void checkProgramCharacterLength(Sheet sheet, List<String> eventErrorList) {
		LOGGER.info("Started validating Event Details field length for altered 1.0 template.");
		
		if(V1ProgramCols.EVENT_TYPE.getLength() < sheet.getRow(3).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.EVENT_TYPE.getHeader() + " should not contain more than " + V1ProgramCols.EVENT_TYPE.getLength() +" characters ");
		}
		
		if(V1ProgramCols.EVENT_COORDINATORNAME.getLength() < sheet.getRow(4).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.EVENT_COORDINATORNAME.getHeader() + " should not contain more than " + V1ProgramCols.EVENT_COORDINATORNAME.getLength() +" characters ");
		}
		
		if(V1ProgramCols.EVENT_COORDINATOR_MAIL.getLength() < sheet.getRow(5).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " should not contain more than " + V1ProgramCols.EVENT_COORDINATOR_MAIL.getLength() +" characters ");
		}
		
		if(V1ProgramCols.CENTER_NAME.getLength() < sheet.getRow(6).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.CENTER_NAME.getHeader() + " should not contain more than " + V1ProgramCols.CENTER_NAME.getLength() +" characters ");
		}
		
		if(V1ProgramCols.EVENT_STATE.getLength() < sheet.getRow(7).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.EVENT_STATE.getHeader() + " should not contain more than " + V1ProgramCols.EVENT_STATE.getLength() +" characters ");
		}
		
		if(V1ProgramCols.EVENT_COUNTRY.getLength() < sheet.getRow(8).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.EVENT_COUNTRY.getHeader() + " should not contain more than " + V1ProgramCols.EVENT_COUNTRY.getLength() +" characters ");
		}
		
		if(V1ProgramCols.INSTITUTION_NAME.getLength() < sheet.getRow(9).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.INSTITUTION_NAME.getHeader() + " should not contain more than " + V1ProgramCols.INSTITUTION_NAME.getLength() +" characters ");
		}
		
		if(V1ProgramCols.WEBSITE.getLength() < sheet.getRow(10).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			eventErrorList.add(V1ProgramCols.WEBSITE.getHeader() + " should not contain more than " + V1ProgramCols.WEBSITE.getLength() +" characters ");
		}
	}
}
