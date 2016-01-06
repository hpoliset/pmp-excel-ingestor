package org.srcm.heartfulness.validator;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Validator to streamline all excel type validation implementation.
 * 
 * @author Koustav Dutta
 *
 */
public interface EventDetailsExcelValidator {

	/**
	 * Main validation method.
	 * 
	 * @param workBook
	 * @see {@link Workbook}
	 * @return error messages if any.
	 */
	public List<String> validate(Workbook workBook);

}
