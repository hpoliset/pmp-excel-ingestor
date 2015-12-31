/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.HeartfulnessConstants;
import org.srcm.heartfulness.enumeration.V2ParticipantCols;
import org.srcm.heartfulness.enumeration.V2ProgramCols2;
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

		Sheet eventSheet = workbook.getSheet(HeartfulnessConstants.EVENT_SHEET_NAME);
		Sheet participantSheet = workbook.getSheet(HeartfulnessConstants.PARTICIPANT_SHEET_NAME);

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

		LOGGER.info("INFO : Started validating Participants Details sheet structure for v2.1 template.");
		int row, col;
		for (V2ParticipantCols column : V2ParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Participant Header " + column.getHeader()
						+ " is not present as per the template.");
			}
		}
		LOGGER.info("INFO : Participants Details sheet structure validation completed for v2.1 template.");
	}

	public void checkParticipantMandatoryFields(Sheet participantSheet, List<String> errorList) {

		LOGGER.debug("Started validating Participation Details sheet mandatory fields for v2.1 template.");
		int rowCount = participantSheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowCount; i++) {
			Row currentRow = participantSheet.getRow(i);
			errorList.addAll(parseParticipantData(currentRow, i + 1));
		}
		LOGGER.debug("Participation Details sheet mandatory fields validation completed for v2.1 template.");
	}

	private List<String> parseParticipantData(Row currentRow, int rowNumber) {

		List<String> errorList = new ArrayList<String>();
		if (!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
			if (currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
				errorList.add(V2ParticipantCols.NAME.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}
			if (currentRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
				errorList.add(V2ParticipantCols.COUNTRY.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}
			if (currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
				errorList.add(V2ParticipantCols.STATE.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}
			if (currentRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
				errorList.add(V2ParticipantCols.CITY.getHeader()
						+ " is a mandatory field and cannot be empty at row number " + rowNumber);
			}
		}
		return errorList;
	}

}
