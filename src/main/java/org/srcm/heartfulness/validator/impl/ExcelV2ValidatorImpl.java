/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

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
import org.srcm.heartfulness.enumeration.V2ParticipantCols;
import org.srcm.heartfulness.enumeration.V2ProgramCols;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.EventDetailsExcelValidator;

import ch.qos.logback.core.util.SystemInfo;

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
		checkProgramCharacterLength(eventSheet, errorList);
		return errorList;
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateProgramDetails(Sheet sheet, List<String> errorList) {

		LOGGER.info("Started validating Event Details structure for v2 template.");
		int row, col;
		for (V2ProgramCols column : V2ProgramCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col,Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
				errorList.add(" Invalid Program Header:  " + column.getHeader()
				+ " is not present as per the template.");
			}
		}
		LOGGER.info("Event Details structure validation completed for v2 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateParticipantDetails(Sheet sheet, List<String> errorList) {

		LOGGER.info("INFO : Started validating Participants Details sheet structure for v2.1 template.");
		int row, col;
		for (V2ParticipantCols column : V2ParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col,Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim())) {
				errorList.add(" Invalid Participant Header " + column.getHeader()
				+ " is not present as per the template.");
			}
		}
		LOGGER.info("INFO : Participants Details sheet structure validation completed for v2.1 template.");
	}

	public void checkProgramMandatoryFields(Sheet eventSheet, List<String> errorList){
		LOGGER.info("INFO : Started validating Program Details sheet structure for v2.1 template.");

		if(eventSheet.getRow(2).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_TYPE.getHeader() + " is a mandatory field and cannot be empty at rownumber 3");
		}

		if(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_PLACE.getHeader() + " is a mandatory field and cannot be empty at rownumber 4");
		}

		String programStartDate = eventSheet.getRow(3).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(programStartDate.isEmpty()){
			errorList.add(V2ProgramCols.EVENT_DATE.getHeader() + " is a mandatory field and cannot be empty at rownumber 4");
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(programStartDate));
				if(DateUtils.parseDate(programStartDate).after(sdf.parse(sdf.format(new Date())))){
					LOGGER.error("Program start date cannot be a future date :[" + programStartDate + "] at row number 4");
					errorList.add("Program start date cannot be a future date:[" + programStartDate + "] at row number 4");
				}
			} catch (Exception e) {
				errorList.add(V2ProgramCols.EVENT_DATE.getHeader() + " is invalid at row number 4");
			}
		}

		if(eventSheet.getRow(4).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_COUNTRY.getHeader() + " is a mandatory field and cannot be empty at rownumber 5");
		}

		if(eventSheet.getRow(4).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_STATE.getHeader() + " is a mandatory field and cannot be empty at rownumber 5");
		}

		if(eventSheet.getRow(5).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_CITY.getHeader() + " is a mandatory field and cannot be empty at rownumber 6");
		}

		if(eventSheet.getRow(6).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_COORDINATORNAME.getHeader() + " is a mandatory field and cannot be empty at rownumber 7");
		}

		if(eventSheet.getRow(7).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.EVENT_COORDINATOR_MOBILE.getHeader() + " is a mandatory field and cannot be empty at rownumber 8");
		}

		String coordinatorEmail = eventSheet.getRow(7).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(coordinatorEmail.isEmpty()){
			errorList.add(V2ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is a mandatory field and cannot be empty at rownumber 8");
		}else{

			if(coordinatorEmail.contains(";")){

				String[] emails = coordinatorEmail.split(";");
				if(emails != null && emails.length > 0){
					for(String email:emails){
						if(!email.matches(ExpressionConstants.EMAIL_REGEX)){
							errorList.add(V2ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid at row number 8");
							break;
						}
					}
				}
			}else if(!coordinatorEmail.matches(ExpressionConstants.EMAIL_REGEX)){
				errorList.add(V2ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " is invalid at row number 8");	
			}
		}

		if(eventSheet.getRow(9).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.ORGANIZATION_NAME.getHeader() + " is a mandatory field and cannot be empty at rownumber 10");
		}

		/*if(eventSheet.getRow(9).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_PERSON.getHeader() + " is a mandatory field and cannot be empty at rownumber 10");
		}*/

		String orgCntctEmail = eventSheet.getRow(10).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();

		if(orgCntctEmail.isEmpty()){
			//errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_MAILID.getHeader() + " is a mandatory field and cannot be empty at rownumber 11");
		}else{
			if(!orgCntctEmail.matches(ExpressionConstants.EMAIL_REGEX)){
				errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_MAILID.getHeader() + " is invalid at row number 11");
			}
		}

		/*if(eventSheet.getRow(11).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_MOBILE.getHeader() + " is a mandatory field and cannot be empty at row number 12");
		}*/

		if(eventSheet.getRow(13).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.PRECEPTOR_NAME.getHeader() + " is a mandatory field and cannot be empty at row number 14");
		}

		if(eventSheet.getRow(14).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
			errorList.add(V2ProgramCols.PRECEPTOR_ID.getHeader() + " is a mandatory field and cannot be empty at row number 15");
		}

		LOGGER.info("INFO : Completed validating Program Details sheet structure for v2.1 template.");
	}

	public void checkParticipantMandatoryFields(Sheet participantSheet, List<String> errorList) {

		LOGGER.info("Started validating Participation Details sheet mandatory fields for v2.1 template.");
		int rowCount = participantSheet.getPhysicalNumberOfRows();
		// List<String> participantNames = new LinkedList<>();
		for (int i = 1; i < rowCount; i++) {
			Row currentRow = participantSheet.getRow(i);
			if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
				errorList.addAll(parseParticipantData(currentRow, i + 1));
				//participantNames.add(currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim());

			}
		}

		/*Set<String> participantSet = new HashSet<String>(participantNames);
        for(String pctptName:participantSet){
            int count = Collections.frequency(participantNames, pctptName);
            if(count > 1){
                errorList.add("Uploaded excel sheet contains "+count+" participants with same name '"+ pctptName+"'. Please make the changes and reupload");
            }
        }*/

		LOGGER.info("Participation Details sheet mandatory fields validation completed for v2.1 template.");
	}

	private List<String> parseParticipantData(Row currentRow, int rowNumber) {

		List<String> errorList = new ArrayList<String>();

		if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {

			String firstSittingStr =  currentRow.getCell(1,
					Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(!firstSittingStr.isEmpty() && null != firstSittingStr){
				if (!firstSittingStr.equalsIgnoreCase("Y")) {
					if(!firstSittingStr.equalsIgnoreCase("N")){
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(firstSittingStr));
							if(DateUtils.parseDate(firstSittingStr).after(sdf.parse(sdf.format(new Date())))){
								LOGGER.error("First sitting date cannot be a future date :[" + firstSittingStr + "] at row number " + rowNumber);
								errorList.add("First sitting date cannot be a future date:[" + firstSittingStr + "] at row number " + rowNumber);
							}
						} catch (Exception e) {
							errorList.add(V2ParticipantCols.FIRST_SITTING.getHeader()
									+ " is invalid at row number " + rowNumber );
						}
					}
				}
			}

			String secondSittingStr =  currentRow.getCell(2,
					Row.CREATE_NULL_AS_BLANK).toString().trim();

			if(!secondSittingStr.isEmpty()  && null != secondSittingStr){
				if (!secondSittingStr.equalsIgnoreCase("Y")) {
					if(!secondSittingStr.equalsIgnoreCase("N")){
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(secondSittingStr));
							if(DateUtils.parseDate(secondSittingStr).after(sdf.parse(sdf.format(new Date())))){
								LOGGER.error("Second sitting date cannot be a future date :[" + secondSittingStr + "] at row number " + rowNumber);
								errorList.add("Second sitting date cannot be a future date:[" + secondSittingStr + "] at row number " + rowNumber);
							}
						} catch (Exception e) {
							errorList.add(V2ParticipantCols.SECONND_SITTING.getHeader()
									+ " is invalid at row number " + rowNumber );
						}
					}
				}
			}

			String thirdSittingStr =  currentRow.getCell(3,
					Row.CREATE_NULL_AS_BLANK).toString().trim();

			if(!thirdSittingStr.isEmpty() && null != thirdSittingStr){
				if (!thirdSittingStr.equalsIgnoreCase("Y")) {
					if(!thirdSittingStr.equalsIgnoreCase("N")){
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(thirdSittingStr));
							if(DateUtils.parseDate(thirdSittingStr).after(sdf.parse(sdf.format(new Date())))){
								LOGGER.error("Third sitting date cannot be a future date :[" + thirdSittingStr + "] at row number " + rowNumber);
								errorList.add("Third sitting date cannot be a future date:[" + thirdSittingStr + "] at row number " + rowNumber);
							}
						} catch (Exception e) {
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

			/*String welcomeCardNumber = currentRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(!welcomeCardNumber.isEmpty()){
				if(!welcomeCardNumber.matches(ExpressionConstants.EWELCOME_ID_REGEX)){
					errorList.add(V2ParticipantCols.WELCOME_CARD_NUMBER.getHeader() + "is invalid at row number "+rowNumber);
				}
			}*/

			String wlcmCardIssueDate = currentRow.getCell(17, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(null != wlcmCardIssueDate && !wlcmCardIssueDate.isEmpty()){
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.determineDateFormat(wlcmCardIssueDate));
					if(DateUtils.parseDate(wlcmCardIssueDate).after(sdf.parse(sdf.format(new Date())))){
						LOGGER.error("Welcome card issue date cannot be a future date :[" + wlcmCardIssueDate + "] at row number " + rowNumber);
						errorList.add("Welcome card issue date cannot be a future date:[" + wlcmCardIssueDate + "] at row number " + rowNumber);
					}
				} catch (Exception e) {
					errorList.add(V2ParticipantCols.WELCOME_CARD_ISSUE_DATE.getHeader()
							+ " is invalid at row number " + rowNumber);
				}
			}

			if (V2ParticipantCols.NAME.getLength() < currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.NAME.getHeader() + " should not contain more than " 
						+ V2ParticipantCols.NAME.getLength() +" characters at row number " + rowNumber);
			}


			if (V2ParticipantCols.COUNTRY.getLength() < currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.COUNTRY.getHeader() + " should not contain more than " + V2ParticipantCols.COUNTRY.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.STATE.getLength() < currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.STATE.getHeader() + " should not contain more than " + V2ParticipantCols.STATE.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.CITY.getLength() < currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.CITY.getHeader() + " should not contain more than " + V2ParticipantCols.CITY.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.EMAIL.getLength() < currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.EMAIL.getHeader() + " should not contain more than " + V2ParticipantCols.EMAIL.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.MOBILE.getLength() < currentRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.MOBILE.getHeader() + " should not contain more than " + V2ParticipantCols.MOBILE.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.PROFFESION.getLength() < currentRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.PROFFESION.getHeader() + " should not contain more than " + V2ParticipantCols.PROFFESION.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.RECEIVE_UPDATES.getLength() < currentRow.getCell(12, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.RECEIVE_UPDATES.getHeader() + " should not contain more than " + V2ParticipantCols.RECEIVE_UPDATES.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.GENDER.getLength() < currentRow.getCell(13, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.GENDER.getHeader() + " should not contain more than " + V2ParticipantCols.GENDER.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.AGE_GROUP.getLength() < currentRow.getCell(12, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.AGE_GROUP.getHeader() + " should not contain more than " + V2ParticipantCols.AGE_GROUP.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.PREF_LANGUAGE.getLength() < currentRow.getCell(13, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.PREF_LANGUAGE.getHeader() + " should not contain more than " + V2ParticipantCols.PREF_LANGUAGE.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.WELCOME_CARD_NUMBER.getLength() < currentRow.getCell(14, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.WELCOME_CARD_NUMBER.getHeader() + " should not contain more than " + V2ParticipantCols.WELCOME_CARD_NUMBER.getLength() +" characters at row number " + rowNumber);
			}

			if (V2ParticipantCols.REMARKS.getLength() < currentRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim().length()) {
				errorList.add(V2ParticipantCols.REMARKS.getHeader() + " should not contain more than " + V2ParticipantCols.REMARKS.getLength() +" characters at row number " + rowNumber);
			}

		}
		return errorList;
	}

	public void checkProgramCharacterLength(Sheet eventSheet, List<String> errorList) {


		LOGGER.info("INFO : Started validating Program Details character length for v2.1 template.");

		if(V2ProgramCols.EVENT_TYPE.getLength() < eventSheet.getRow(2).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_TYPE.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_TYPE.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_PLACE.getLength() < eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_PLACE.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_PLACE.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_COUNTRY.getLength() < eventSheet.getRow(4).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_COUNTRY.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_COUNTRY.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_STATE.getLength() < eventSheet.getRow(4).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_STATE.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_STATE.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_CITY.getLength() < eventSheet.getRow(5).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_CITY.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_CITY.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_COORDINATORNAME.getLength() < eventSheet.getRow(6).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_COORDINATORNAME.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_COORDINATORNAME.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_COORDINATOR_MOBILE.getLength() < eventSheet.getRow(7).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_COORDINATOR_MOBILE.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_COORDINATOR_MOBILE.getLength() +" characters ");
		}

		if(V2ProgramCols.EVENT_COORDINATOR_MAIL.getLength() < eventSheet.getRow(7).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.EVENT_COORDINATOR_MAIL.getHeader() + " should not contain more than " + V2ProgramCols.EVENT_COORDINATOR_MAIL.getLength() +" characters ");
		}

		if(V2ProgramCols.ORGANIZATION_NAME.getLength() < eventSheet.getRow(9).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.ORGANIZATION_NAME.getHeader() + " should not contain more than " + V2ProgramCols.ORGANIZATION_NAME.getLength() +" characters ");
		}

		if(V2ProgramCols.ORGANIZATION_CONTACT_PERSON.getLength() < eventSheet.getRow(9).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_PERSON.getHeader() + " should not contain more than " + V2ProgramCols.ORGANIZATION_CONTACT_PERSON.getLength() +" characters ");
		}


		if(V2ProgramCols.ORGANIZATION_WEBSITE.getLength() < eventSheet.getRow(10).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.ORGANIZATION_WEBSITE.getHeader() + " should not contain more than " + V2ProgramCols.ORGANIZATION_WEBSITE.getLength() +" characters ");
		}

		if(V2ProgramCols.ORGANIZATION_CONTACT_MAILID.getLength() < eventSheet.getRow(10).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_MAILID.getHeader() + " should not contain more than " + V2ProgramCols.ORGANIZATION_CONTACT_MAILID.getLength() +" characters ");
		}

		if(V2ProgramCols.ORGANIZATION_CONTACT_MOBILE.getLength() < eventSheet.getRow(11).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.ORGANIZATION_CONTACT_MOBILE.getHeader() + " should not contain more than " + V2ProgramCols.ORGANIZATION_CONTACT_MOBILE.getLength() +" characters ");
		}

		if(V2ProgramCols.PRECEPTOR_NAME.getLength() < eventSheet.getRow(13).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.PRECEPTOR_NAME.getHeader() + " should not contain more than " + V2ProgramCols.PRECEPTOR_NAME.getLength() +" characters ");
		}

		if(V2ProgramCols.WELCOME_CARD_SIGNEDBY.getLength() < eventSheet.getRow(13).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.WELCOME_CARD_SIGNEDBY.getHeader() + " should not contain more than " + V2ProgramCols.WELCOME_CARD_SIGNEDBY.getLength() +" characters ");
		}

		if(V2ProgramCols.PRECEPTOR_ID.getLength() < eventSheet.getRow(14).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.PRECEPTOR_ID.getHeader() + " should not contain more than " + V2ProgramCols.PRECEPTOR_ID.getLength() +" characters ");
		}

		if(V2ProgramCols.WELCOME_CARD_SIGNER_ID.getLength() < eventSheet.getRow(14).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.WELCOME_CARD_SIGNER_ID.getHeader() + " should not contain more than " + V2ProgramCols.WELCOME_CARD_SIGNER_ID.getLength() +" characters ");
		}

		if(V2ProgramCols.REMARKS.getLength() < eventSheet.getRow(17).getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().length()){
			errorList.add(V2ProgramCols.REMARKS.getHeader() + " should not contain more than " + V2ProgramCols.REMARKS.getLength() +" characters ");
		}
		LOGGER.info("Event Details character length validation completed for v2 template.");
	}

}
