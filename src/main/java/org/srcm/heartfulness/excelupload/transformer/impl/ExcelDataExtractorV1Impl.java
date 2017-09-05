/**
 * 
 */
package org.srcm.heartfulness.excelupload.transformer.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Implementation class to extract excel data for altered v1.0 template.
 * 
 * @author Koustav Dutta
 *
 */
public class ExcelDataExtractorV1Impl implements ExcelDataExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataExtractorV1Impl.class);


	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor#extractExcel(org.apache.poi.ss.usermodel.Workbook)
	 */
	@Override
	public Program extractExcel(Workbook workbook,String eWelcomeIdCheckbox,String jiraIssueNumber) throws InvalidExcelFileException {
		Sheet sheet = workbook.getSheet(EventDetailsUploadConstants.V1_SHEET_NAME);
		Program program = new Program();
		boolean disableEwelcomeIdGeneration = false;
		if(eWelcomeIdCheckbox.equals("on")){
			disableEwelcomeIdGeneration = true;
		}
		program = parseProgram(sheet,disableEwelcomeIdGeneration);
		program.setParticipantList(getParticipantList(sheet, disableEwelcomeIdGeneration));
		program.setJiraIssueNumber(jiraIssueNumber);
		return program;
	}


	/**
	 * This method is used to parse the Participant details and fill all the data in Participant POJO class.
	 *
	 * @return List of Participant details
	 */
	private List<Participant> getParticipantList(Sheet sheet, boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		LOGGER.info("Started to parse participant data for altered 1.0 template.");
		List<Participant> participantList = new ArrayList<Participant>();
		int totalRows = sheet.getPhysicalNumberOfRows(),j=15;
		for (int i = 15; i < totalRows; i++) {
			Row currentRow = sheet.getRow(i);
			if(!currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
				Participant participant = parseParticipantRow(currentRow,disableEwelcomeIdGeneration);
				if (participant.getExcelSheetSequenceNumber() == 0) {
					participant.setExcelSheetSequenceNumber(j - 14);
				}
				participantList.add(participant);
				j++;
			}
		}
		LOGGER.info("Parsing participant data completed for altered 1.0 template.");
		return participantList;
	}


	/**
	 * Parses the Participant details from the excel sheet and populates Participant class.
	 *
	 * @param currentRow
	 * @return Participant Details
	 * @throws InvalidExcelFileException
	 */
	private Participant parseParticipantRow(Row currentRow, boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		Participant participant = new Participant();
		if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()) {
			double seqNo = Double.valueOf(currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim());
			int integerSeq = (int) seqNo;
			participant.setExcelSheetSequenceNumber(integerSeq);
		}
		participant.setPrintName(currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setCity(currentRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setState(currentRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setEmail(currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim());
		Cell phoneCell = currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericMobilePhone = phoneCell.getNumericCellValue();
			participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()).trim());
			if(participant.getMobilePhone().equals("0")){
				participant.setMobilePhone("");
			}
		} catch (NumberFormatException | ClassCastException | IllegalStateException e) {
			LOGGER.error("Participant mobile phone number is not numeric, trying as string");
			participant.setMobilePhone(String.valueOf(phoneCell).trim());
		}catch(Exception ex){
			LOGGER.error("Participant mobile phone number is not numeric, trying as string");
			participant.setMobilePhone(String.valueOf(phoneCell).trim());
		}
		participant.setProfession(currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim());
		String isIntroduced = currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if (isIntroduced.equalsIgnoreCase("YES") || isIntroduced.equalsIgnoreCase("Y")) {
			participant.setIntroduced(1);
		} else {
			participant.setIntroduced(0);
		}
		String introducedDateStr = currentRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim();
		Date introducedDate = null;
		try {
			introducedDate = DateUtils.parseDate(introducedDateStr);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse program event date:[" + introducedDateStr + "]");
			throw new InvalidExcelFileException("Not able to parse program event date:[" + introducedDateStr + "]");
		}
		participant.setIntroductionDate(introducedDate);
		participant.setIntroducedBy(currentRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setRemarks(currentRow.getCell(10, Row.CREATE_NULL_AS_BLANK).toString().trim());
		if(disableEwelcomeIdGeneration){
			participant.setEwelcomeIdState(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
		}
		participant.setReceiveUpdates(1);
		return participant;
	}


	/**
	 * This method is used to parse the Event details and populate the Program class
	 *
	 * @return Program details.
	 * @throws InvalidExcelFileException
	 */
	private Program parseProgram(Sheet sheet, boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		LOGGER.info("Started to parse program data for altered 1.0 template.");
		Program program = new Program();
		program.setProgramChannel(sheet.getRow(3).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setCoordinatorName(sheet.getRow(4).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setCoordinatorEmail(sheet.getRow(5).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventPlace(sheet.getRow(6).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setProgramName(sheet.getRow(6).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventState(sheet.getRow(7).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventCountry(sheet.getRow(8).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationName(sheet.getRow(9).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationWebSite(sheet.getRow(10).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim());
		String eventDateStr = sheet.getRow(11).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim();
		Date eventDate = null;
		try {
			eventDate = DateUtils.parseDate(eventDateStr);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse program event date:[" + eventDateStr + "]");
			throw new InvalidExcelFileException("Not able to parse program event date:[" + eventDateStr + "]");
		}
		program.setProgramStartDate(eventDate);
		if(disableEwelcomeIdGeneration){
			program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
		}else{
			program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_ENABLED_STATE);
		}
		LOGGER.info("Parsing program data completed for altered 1.0 template.");
		return program;
	}

}
