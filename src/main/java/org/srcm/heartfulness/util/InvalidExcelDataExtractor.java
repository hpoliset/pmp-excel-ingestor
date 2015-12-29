/**
 * 
 */
package org.srcm.heartfulness.util;

import java.util.List;

import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

/**
 * @author koustavd
 *
 */
public class InvalidExcelDataExtractor implements ExcelDataExtractor{

	@Override
	public Program getProgram() throws InvalidExcelFileException{
		throw new InvalidExcelFileException("Template version is invalid");
	}

	@Override
	public List<Participant> getParticipantList() throws InvalidExcelFileException {
		throw new InvalidExcelFileException("Template version is invalid");
	}

}
