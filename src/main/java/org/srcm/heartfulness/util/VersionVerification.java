/**
 * 
 */
package org.srcm.heartfulness.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.constants.HeartfulnessConstants;

/**
 * @author koustavd
 *
 */
@Service
public class VersionVerification {

	public String validateExcelVersion(byte[] excelContent){
		Workbook workbook = null;
		Sheet sheet;
		boolean check =false,recheck =false; 
		try(ByteArrayInputStream inputStream = new ByteArrayInputStream(excelContent)){
			workbook = WorkbookFactory.create(inputStream);
			if(workbook.getNumberOfSheets() == 2){
				check =true;
			}
			sheet = workbook.getSheet(HeartfulnessConstants.EVENT_SHEET_NAME);
			if(sheet != null){
				String versionName = sheet.getRow(1).getCell(0,Row.CREATE_NULL_AS_BLANK).toString();
				String[] array = versionName.split("/");
				for(String value:array){
					if(value.contains("V2.1")){
						recheck = true;
						break;
					}
				}
			}
			if(check && !recheck){
				return HeartfulnessConstants.VERSION_ONE;
			}else if(!check && recheck){
				return HeartfulnessConstants.VERSION_TWO;
			}else{
				return HeartfulnessConstants.INVALID_VERSION;
			}
		}catch(IOException | InvalidFormatException ex){
			return HeartfulnessConstants.INVALID_VERSION;
		}catch (Exception e) {
			return HeartfulnessConstants.INVALID_VERSION;
		}
	}

}
