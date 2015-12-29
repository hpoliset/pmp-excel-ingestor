/**
 * 
 */
package org.srcm.heartfulness.util;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * @author koustavd
 *
 */
public interface ExcelSheetValidator {
	
	public List<String> validateEventDetails() throws IOException;
	public List<String> validateParticipantDetails() throws IOException;
	public void initializeAll(byte[] excelContent)throws InvalidFormatException,IOException;
	public List<String> checkEventMandatoryFields() throws IOException;
	public List<String> checkParticipantMandatoryFields() throws IOException;
	
}
