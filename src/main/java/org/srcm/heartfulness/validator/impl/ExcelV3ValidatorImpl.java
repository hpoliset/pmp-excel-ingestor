/**
 * 
 */
package org.srcm.heartfulness.validator.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.enumeration.V3ParticipantCols;
import org.srcm.heartfulness.enumeration.V3ProgramCols;
import org.srcm.heartfulness.validator.EventDetailsExcelValidator;

/**
 * Implementation for validating the V3 excel.
 * 
 * @author Goutham
 *
 */
public class ExcelV3ValidatorImpl implements EventDetailsExcelValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelV3ValidatorImpl.class);

	/**
	 * This method is used to validate the V3 template 
	 * 
	 * @param workbook @see {@link Workbook}
	 * @return List<String>
	 */
	@Override
	public List<String> validate(Workbook workbook) {
		List<String> errorList = new ArrayList<String>();

		if (workbook == null) {
			errorList.add("Workbook is not valid or empty.");
			return errorList;
		}

		Sheet sheet = workbook.getSheet(EventDetailsUploadConstants.V3_SHEET_NAME);
		if (sheet == null) {
			errorList.add("Event Details Sheet is not present/invalid or empty.");
			return errorList;
		}
		validateProgramDetails(sheet, errorList);
		validateParticipantDetails(sheet, errorList);
		return errorList;
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateProgramDetails(Sheet sheet, List<String> errorList) {

		LOGGER.debug("Started validating Event Details structure for V3 template.");
		int row, col;
		for (V3ProgramCols column : V3ProgramCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Program Header:  " + column.getHeader()
						+ " is not present as per the template.");
			}
		}
		LOGGER.debug("Event Details structure validation completed for V3 template.");
	}

	/**
	 * 
	 * @param sheet
	 * @param errorList
	 */
	public void validateParticipantDetails(Sheet sheet, List<String> errorList) {

		LOGGER.info("INFO : Started validating Participants Details sheet structure for V3 template.");
		int row, col;
		for (V3ParticipantCols column : V3ParticipantCols.values()) {
			row = column.getRow();
			col = column.getCell();
			if (!column.getHeader().equalsIgnoreCase(sheet.getRow(row).getCell(col).getStringCellValue().trim())) {
				errorList.add(" Invalid Participant Header " + column.getHeader()
						+ " is not present as per the template.");
			}
		}
		LOGGER.info("INFO : Participants Details sheet structure validation completed for V3 template.");
	}

}
