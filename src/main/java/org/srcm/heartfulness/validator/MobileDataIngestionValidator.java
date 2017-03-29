/**
 * 
 */
package org.srcm.heartfulness.validator;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author Koustav Dutta
 *
 */
public interface MobileDataIngestionValidator {

	void validateExcelSheetStructureAndMandatoryParams(Sheet eventSheet, Sheet participantSheet, List<String> errorMsg);
	
	

}
