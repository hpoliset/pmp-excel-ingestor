package org.srcm.heartfulness.excelupload.transformer;

import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.util.List;

/**
 * Extractor class to parse the excel file and populate the program and participant data.
 * 
 * Created by vsonnathi on 11/16/15.
 */
public interface ExcelDataExtractor {
	
	/**
	 * This method is used to get all event details.
	 * 
	 * @return @see {@link Program}
	 * @throws InvalidExcelFileException
	 */
	public Program getProgram(Workbook workbook) throws InvalidExcelFileException;
	
/**
 * This method is used to get all the participant details from the corresponding excel file.
 * 
 * @return List of participant details.
 * @throws InvalidExcelFileException
 */
	public List<Participant> getParticipantList(Workbook workbook) throws InvalidExcelFileException;
	
	
}
