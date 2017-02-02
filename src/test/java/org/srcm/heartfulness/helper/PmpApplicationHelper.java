/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.validator.EventDetailsExcelValidatorFactory;

/**
 * @author koustavd
 *
 */
@Service
public class PmpApplicationHelper {

	@Autowired
	private ResourceLoader resourceLoader;
	
	
	/**
	 * This method will return Excel file content.
	 * @param fileName
	 * @return the extracted file content for a valid excel file.
	 * @throws IOException
	 */
	public byte[] getExcelContent(String fileName) throws IOException{
		
		Resource ValidResource = resourceLoader.getResource("classpath:" + fileName);
		byte[] fileContent = StreamUtils.copyToByteArray(ValidResource.getInputStream());
		return fileContent;
	}
	
	/**
	 * 
	 * @param workBook Excel sheet workbook type XSSFWorkbook or HSSFWorkbook
	 * @param version Defines the excel sheet version 
	 * @return list of error if exists
	 */
	public List<String> validateExcelStructure(Workbook workBook,ExcelType version){
		
		return EventDetailsExcelValidatorFactory.validateExcel(workBook, version);
		
	}

}
