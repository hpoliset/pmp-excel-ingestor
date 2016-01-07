package org.srcm.heartfulness.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
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
		String versionName = null;
		Sheet sheet = null;
		//v2.1 
		sheet = uploadedExcelWorkbook.getSheet(EventDetailsUploadConstants.V2_EVENT_SHEET_NAME);
		versionName = sheet != null ? sheet.getRow(1).getCell(0, Row.CREATE_NULL_AS_BLANK).toString() : null;
		//v3
		if (versionName == null) {
			sheet = uploadedExcelWorkbook.getSheet(EventDetailsUploadConstants.V3_SHEET_NAME);
			versionName = sheet != null ? sheet.getRow(0).getCell(8, Row.CREATE_NULL_AS_BLANK).toString() : null;
		}else{
			
		}
		// identify the version
		if (versionName != null) {
			version = versionName.contains("/V2.1/") ? ExcelType.V2_1 : (versionName.contains("/v3/") ? ExcelType.V3
					: ExcelType.INVALID);
		} else if (uploadedExcelWorkbook.getNumberOfSheets() == 2) {
			version = ExcelType.V1;
		}
		return version;
	}
}
