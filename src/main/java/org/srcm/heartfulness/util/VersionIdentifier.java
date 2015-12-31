package org.srcm.heartfulness.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.HeartfulnessConstants;
import org.srcm.heartfulness.enumeration.ExcelType;

/**
 * This is a helper class to identify the version of the Heartfulness Event
 * Participant details excel.
 * 
 * @author Koustav Dutta
 *
 */

@Component
public class VersionIdentifier {

	/**
	 * Utility method to identify the uploaded excel type.
	 * 
	 * @param uploadedExcelWorkbook
	 * @see{@link Workbook}
	 * @return The version type in the form of Enum. @see {@link ExcelType}
	 */
	public ExcelType findVersion(Workbook uploadedExcelWorkbook) {
		ExcelType version = ExcelType.INVALID;
		Sheet sheet = uploadedExcelWorkbook.getSheet(HeartfulnessConstants.EVENT_SHEET_NAME);
		if (sheet != null) {
			String versionName = sheet.getRow(1).getCell(0, Row.CREATE_NULL_AS_BLANK).toString();
			// TODO Change to use Regular Expression
			String[] array = versionName.split("/");
			for (String value : array) {
				if (value.contains("V2.1")) {
					version = ExcelType.V2_1;
					break;
				}
			}
		} else if (uploadedExcelWorkbook.getNumberOfSheets() == 2) {
			version = ExcelType.V1;
		}
		return version;
	}
}
