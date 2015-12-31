package org.srcm.heartfulness.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Factory to do all necessary validations on the uploaded excel sheet.
 * 
 * @author Koustav Dutta.
 * 
 */
public class EventDetailsExcelValidatorFactory {

	/**
	 * 
	 * @param workBook
	 * @param version
	 * @return
	 * @throws InvalidExcelFileException
	 */
	public static List<String> validateExcel(Workbook workBook, ExcelType version) throws InvalidExcelFileException {
		List<String> response = new ArrayList<String>();
		try {

			EventDetailsExcelValidator validator = version.getValidator();
			response = validator.validate(workBook);
		} catch (NullPointerException e) {
			throw new InvalidExcelFileException("Invalid File");
		}
		return response;
	}

}
