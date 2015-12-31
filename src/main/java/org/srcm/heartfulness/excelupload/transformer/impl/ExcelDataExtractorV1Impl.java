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
	public Program extractExcel(Workbook workbook) throws InvalidExcelFileException {
		Sheet sheet = workbook.getSheet(EventDetailsUploadConstants.V1_SHEET_NAME);
		Program program = new Program();
		program = parseProgram(sheet);
		program.setParticipantList(getParticipantList(sheet));
		return program;
	}

	
	/**
     * This method is used to parse the Participant details and fill all the data in Participant POJO class.
     *
     * @return List of Participant details
     */
	private List<Participant> getParticipantList(Sheet sheet) throws InvalidExcelFileException {
		LOGGER.debug("Started to parse participant data for altered 1.0 template.");
		List<Participant> participantList = new ArrayList<Participant>();
		int totalRows = sheet.getPhysicalNumberOfRows();
		for (int i = 15; i < totalRows; i++) {
			Row currentRow = sheet.getRow(i);
			Participant participant = parseParticipantRow(currentRow);
			if (participant.getExcelSheetSequenceNumber() == 0) {
				participant.setExcelSheetSequenceNumber(i - 14);
			}
			if ("".equals(participant.getPrintName())) {
				break;
			}
			participantList.add(participant);
		}
		LOGGER.debug("Parsing participant data completed for altered 1.0 template.");
		return participantList;
	}

	
	/**
     * Parses the Participant details from the excel sheet and populates Participant class.
     *
     * @param currentRow
     * @return Participant Details
     * @throws InvalidExcelFileException
     */
	private Participant parseParticipantRow(Row currentRow) throws InvalidExcelFileException {
		Participant participant = new Participant();
		if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
			double seqNo = Double.valueOf(currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString());
			int integerSeq = (int) seqNo;
			participant.setExcelSheetSequenceNumber(integerSeq);
		}
		participant.setPrintName(currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setCity(currentRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setState(currentRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setEmail(currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString());
		Cell phoneCell = currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericMobilePhone = phoneCell.getNumericCellValue();
			participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()));
		} catch (NumberFormatException | ClassCastException e) {
			LOGGER.error("Participant mobile phone number is not numeric, trying as string");
			participant.setMobilePhone(phoneCell.toString());
		}
		participant.setProfession(currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString());
		if (currentRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().equals("YES")) {
			participant.setIntroduced(1);
		} else {
			participant.setIntroduced(0);
		}
		String introducedDateStr = currentRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString();
		Date introducedDate = null;
		try {
			introducedDate = DateUtils.parseDate(introducedDateStr);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse program event date:[" + introducedDateStr + "]");
			throw new InvalidExcelFileException("Not able to parse program event date:[" + introducedDateStr + "]");
		}
		participant.setIntroductionDate(introducedDate);
		participant.setIntroducedBy(currentRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setRemarks(currentRow.getCell(10, Row.CREATE_NULL_AS_BLANK).toString());
		return participant;
	}
	
	
	/**
     * This method is used to parse the Event details and populate the Program class
     *
     * @return Program details.
     * @throws InvalidExcelFileException
     */
	private Program parseProgram(Sheet sheet) throws InvalidExcelFileException {
		LOGGER.debug("Started to parse program data for altered 1.0 template.");
		Program program = new Program();
		program.setProgramChannel(sheet.getRow(3).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorName(sheet.getRow(4).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorEmail(sheet.getRow(5).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventPlace(sheet.getRow(6).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventState(sheet.getRow(7).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCountry(sheet.getRow(8).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationName(sheet.getRow(9).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationWebSite(sheet.getRow(10).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		String eventDateStr = sheet.getRow(11).getCell(2, Row.CREATE_NULL_AS_BLANK).toString();
		Date eventDate = null;
		try {
			eventDate = DateUtils.parseDate(eventDateStr);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse program event date:[" + eventDateStr + "]");
			throw new InvalidExcelFileException("Not able to parse program event date:[" + eventDateStr + "]");
		}
		program.setProgramStartDate(eventDate);
		LOGGER.debug("Parsing program data completed for altered 1.0 template.");
		return program;
	}

}
