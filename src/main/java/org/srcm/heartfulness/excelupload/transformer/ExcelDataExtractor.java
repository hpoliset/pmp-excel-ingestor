package org.srcm.heartfulness.excelupload.transformer;

import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Extractor class to parse the excel file and populate the program and participant data.
 * 
 * Created by Koustav Dutta.
 */
public interface ExcelDataExtractor {

	/**
	 * This method is used to get all the program details from the corresponding excel file.
	 * 
	 * @return Program details
	 */
	public Program extractExcel(Workbook workbook,String eWelcomeIdCheckbox,String jiraIssueNumber) throws InvalidExcelFileException ;


}
