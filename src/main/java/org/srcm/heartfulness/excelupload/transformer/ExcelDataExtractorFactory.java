package org.srcm.heartfulness.excelupload.transformer;

import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.util.InvalidExcelFileException;
/**
 * Factory class to extract all necessary data from the uploaded excel sheet.
 * 
 * @author Koustav Dutta
 *
 */
public class ExcelDataExtractorFactory {

	/**
	 * Extracting all the program and participant details from the uploaded
	 * excel file.
	 * 
	 * @param workBook
	 *            @see {@link Workbook}
	 * @param version
	 *            @see {@link ExcelType}
	 * @return Extracted details from the excel in form of <code>Program</code>.
	 * @throws InvalidExcelFileException
	 *             when the given excel does not adhere to the template defined.
	 */
	public static Program extractProgramDetails(Workbook workBook, ExcelType version) throws InvalidExcelFileException {
		Program program = new Program();
		ExcelDataExtractor extractor = version.getExtractor();
		program = extractor.extractExcel(workBook);
		return program;

	}

}
