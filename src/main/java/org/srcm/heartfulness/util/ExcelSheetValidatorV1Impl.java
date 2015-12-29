/**
 * 
 */
package org.srcm.heartfulness.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.HeartfulnessConstants;

/**
 * @author koustavd
 *
 */
public class ExcelSheetValidatorV1Impl implements ExcelSheetValidator{
	static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetValidatorV1Impl.class);
	private  Workbook workbook;
	private  Sheet sheet;
	
	@Override
	@SuppressWarnings("static-access")
	public void initializeAll(byte[] excelContent) throws InvalidFormatException, IOException {
		Workbook workbook = null;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(excelContent);
		workbook = new WorkbookFactory().create(inputStream);
		this.workbook = workbook;
		this.sheet = workbook.getSheet(HeartfulnessConstants.V1_SHEET_NAME);
	}
	
	@Override
	public List<String> validateEventDetails() throws IOException {
		System.out.println("INFO : Started validating Event Details structure for altered 1.0 template.");
		List<String> eventErrorList = new ArrayList<String>();
		if(this.workbook == null){
			eventErrorList.add("Workbook is not valid or empty.");
		}else if(this.sheet == null){
			eventErrorList.add("Sheet is not present/invalid or empty.");
		}else{
			if(!sheet.getRow(3).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_PROGRAM_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_PROGRAM_NAME +" is not present or invalid.");
			}else if(!sheet.getRow(3).getCell(4).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_OTHER)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_OTHER +" is not present or invalid.");
			}
			if(!sheet.getRow(4).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_COORDINATOR_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_COORDINATOR_NAME +" is not present or invalid.");
			}
			if(!sheet.getRow(5).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_COORDINATOR_EMAIL_ID)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_COORDINATOR_EMAIL_ID +" is not present or invalid.");
			}
			if(!sheet.getRow(6).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_CENTER_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_CENTER_NAME +" is not present or invalid.");
			}
			if(!sheet.getRow(7).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_STATE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_STATE +" is not present or invalid.");
			}
			if(!sheet.getRow(8).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_COUNTRY)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_COUNTRY +" is not present or invalid.");
			}
			if(!sheet.getRow(9).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_INSTITUTION_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_INSTITUTION_NAME +" is not present or invalid.");
			}
			if(!sheet.getRow(10).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_WEBSITE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_WEBSITE +" is not present or invalid.");
			}
			if(!sheet.getRow(11).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_PROGRAM_DATE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.V1_PROGRAM_DATE +" is not present or invalid.");
			}
		}
		System.out.println("INFO : Event Details structure validation completed for altered 1.0 template.");
		return eventErrorList;
	}

	@Override
	public List<String> validateParticipantDetails() throws IOException {
		List<String> participantErrorList = new ArrayList<String>();
		System.out.println("INFO : Started validating Participation Details structure for altered 1.0 template.");
		if(sheet == null){
			 participantErrorList.add("Sheet is not present/invalid or empty.");
		 }else{
			 Row headerRow = sheet.getRow(14);
			 int cellCount = headerRow.getPhysicalNumberOfCells();
		     if(cellCount != 11){
		    	 participantErrorList.add("Participants Details structure in template version  altered 1.0 must contain 11 cells but is having "+cellCount+" cells");
			 }	 
		     
		     if(!headerRow.getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.V1_SERIAL_NO)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_SERIAL_NO+" is not present or invalid.");
		     }
		     if(!headerRow.getCell(1).getStringCellValue().trim().equals(HeartfulnessConstants.V1_FULLNAME)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_FULLNAME+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.V1_CITY)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_CITY+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(3).getStringCellValue().trim().equals(HeartfulnessConstants.V1_STATE)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_STATE+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(4).getStringCellValue().trim().equals(HeartfulnessConstants.V1_EMAIL_ADDRESS)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_EMAIL_ADDRESS+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(5).getStringCellValue().trim().equals(HeartfulnessConstants.V1_PHONE_NUMBER)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_PHONE_NUMBER+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(6).getStringCellValue().trim().equals(HeartfulnessConstants.V1_OCCUPATION)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_OCCUPATION+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(7).getStringCellValue().trim().equals(HeartfulnessConstants.V1_INTRODUCTION)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_EMAILID+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(8).getStringCellValue().trim().equals(HeartfulnessConstants.V1_INTRODUCED_DATE)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_INTRODUCED_DATE+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(9).getStringCellValue().trim().equals(HeartfulnessConstants.V1_INTRODUCED_BY)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_INTRODUCED_BY+" is not present or invalid.");	 
			 }
		     if(!headerRow.getCell(10).getStringCellValue().trim().equals(HeartfulnessConstants.V1_REMARKS)){
		    	 participantErrorList.add("Header name mismatch "+HeartfulnessConstants.V1_REMARKS+" is not present or invalid.");	 
			 }
		 }
		System.out.println("INFO : Participants Details structure validation completed for altered 1.0 template.");
		return participantErrorList;
	}

	@Override
	public List<String> checkEventMandatoryFields() throws IOException {
		List<String> errorList = new ArrayList<String>();
		System.out.println("INFO : Started validating Event Details fields for altered 1.0 template.");
		String eventDateStr = sheet.getRow(11).getCell(2, Row.CREATE_NULL_AS_BLANK).toString();
        Date eventDate = null;
        try {
           eventDate = DateUtils.parseDate(eventDateStr);
        } catch (ParseException e) {
           LOGGER.error("Not able to parse program event date:[" + eventDateStr + "]");
           errorList.add("Not able to parse program event date:[" + eventDateStr + "]");
        }
		System.out.println("INFO : Event Details fields  validation completed for altered 1.0 template.");
		return errorList;
	}

	@Override
	public List<String> checkParticipantMandatoryFields() throws IOException {
		List<String> participantErrorList = new ArrayList<String>();
		System.out.println("INFO : Started validating participant detail fields for altered 1.0 template.");
		int rowCount = sheet.getPhysicalNumberOfRows();
		for(int i =15;i<rowCount;i++){
			Row currentRow = sheet.getRow(i);
			participantErrorList.addAll(parseParticipantData(currentRow,i+1));
		}
		System.out.println("INFO : Participants detail field validation completed for altered 1.0 template.");
		return participantErrorList;
	}
	
	private List<String> parseParticipantData(Row currentRow, int rowNumber) {
		List<String> errorList = new ArrayList<String>();
		String introducedDateStr = currentRow.getCell(8,Row.CREATE_NULL_AS_BLANK).toString();
		Date introducedDate = null;
		try{
			introducedDate = DateUtils.parseDate(introducedDateStr);
		}catch(ParseException e){
			LOGGER.error("Not able to parse Introduced date:[" + introducedDateStr + "] at row number "+rowNumber);
			errorList.add("Not able to parse Introduced date:[" + introducedDateStr + "] at row number "+rowNumber);
		}
		return errorList;
	}
}
