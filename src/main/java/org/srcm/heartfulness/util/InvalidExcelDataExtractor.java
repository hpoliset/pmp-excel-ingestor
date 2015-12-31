/**
 * 
 */
package org.srcm.heartfulness.util;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

/**
 * This class is used for Invalid Excel file format.
 * @author Koustav Dutta
 *
 */
public class InvalidExcelDataExtractor implements ExcelDataExtractor{

	@Override
	public Program getProgram(Workbook workbook) throws InvalidExcelFileException{
		throw new InvalidExcelFileException("Template version is invalid");
	}

	@Override
	public List<Participant> getParticipantList(Workbook workbook) throws InvalidExcelFileException {
		throw new InvalidExcelFileException("Template version is invalid");
	}

}
