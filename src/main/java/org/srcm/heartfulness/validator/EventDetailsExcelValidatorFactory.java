package org.srcm.heartfulness.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.enumeration.ExcelType;

/**
 * 
 * @author balajid
 * 
 */
public class EventDetailsExcelValidatorFactory {

	/**
	 * 
	 * @param workBook
	 * @param version
	 * @return
	 */
	public static List<String> validateExcel(Workbook workBook, ExcelType version) {
		List<String> response = new ArrayList<String>();
		EventDetailsExcelValidator validator = version.getValidator();
		response = validator.validate(workBook);
		return response;
	}

}
