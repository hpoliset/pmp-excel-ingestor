/**
 * 
 */
package org.srcm.heartfulness.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
public class ExcelSheetValidatorV2Impl implements ExcelSheetValidator {
	private  Workbook workbook;
	private  Sheet eventSheet;
	private  Sheet participantSheet;
	static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetValidatorV2Impl.class);

	@SuppressWarnings("static-access")
	public void initializeAll(byte[] excelContent) throws InvalidFormatException, IOException {
		Workbook workbook = null;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(excelContent);
		workbook = new WorkbookFactory().create(inputStream);
		this.workbook = workbook;
		this.eventSheet = workbook.getSheet(HeartfulnessConstants.EVENT_SHEET_NAME);
		this.participantSheet = workbook.getSheet(HeartfulnessConstants.PARTICIPANT_SHEET_NAME);
	}

	@Override
	public List<String> validateEventDetails() throws IOException { 
		List<String> eventErrorList = new ArrayList<String>();
		System.out.println("INFO : Started validating Event Details sheet structure for v2.1 template.");
		if(this.workbook == null){
			eventErrorList.add("Workbook is not valid or empty.");
		}else if(this.eventSheet == null){
			eventErrorList.add("Event Details sheet is not found.");
		}else{
			int cellCount = eventSheet.getRow(2).getPhysicalNumberOfCells();
			if(cellCount != 4){
				eventErrorList.add("Event Details sheet must contain 4 cells but is having "+cellCount +" cells.");
			}
			if(!eventSheet.getRow(2).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_TYPE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_TYPE +" is not present or invalid.");
			}else if(!eventSheet.getRow(2).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_OTHER)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_OTHER +" is not present or invalid.");
			}
			if(!eventSheet.getRow(3).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_PLACE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_PLACE +" is not present or invalid.");
			}else if(!eventSheet.getRow(3).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_DATE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_DATE +" is not present or invalid.");
			}
			if(!eventSheet.getRow(4).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_COUNTRY)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_COUNTRY +" is not present or invalid.");
			}else if(!eventSheet.getRow(4).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_STATE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_STATE +" is not present or invalid.");
			}
			if(!eventSheet.getRow(5).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_CITY)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_CITY +" is not present or invalid.");
			}
			if(!eventSheet.getRow(6).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_COORDINATOR_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_COORDINATOR_NAME +" is not present or invalid.");
			}
			if(!eventSheet.getRow(7).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_COORDINATOR_MOBILE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_COORDINATOR_MOBILE +" is not present or invalid.");
			}else if(!eventSheet.getRow(7).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_COORDINATOR_EMAIL_ID)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_COORDINATOR_EMAIL_ID +" is not present or invalid.");
			}
			if(!eventSheet.getRow(9).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.ORGANISATION_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.ORGANISATION_NAME +" is not present or invalid.");
			}else if(!eventSheet.getRow(9).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.ORGANISATION_CONTACT_PERSON)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.ORGANISATION_CONTACT_PERSON +" is not present or invalid.");
			}
			if(!eventSheet.getRow(10).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.ORGANISATION_WEBSITE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.ORGANISATION_WEBSITE +" is not present or invalid.");
			}else if(!eventSheet.getRow(10).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.ORGANISATION_CONTACT_EMAILID)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.ORGANISATION_CONTACT_EMAILID +" is not present or invalid.");
			}
			if(!eventSheet.getRow(11).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.ORGANISATION_CONTACT_MOBILE)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.ORGANISATION_CONTACT_MOBILE +" is not present or invalid.");
			}
			if(!eventSheet.getRow(13).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.PRECEPTOR_NAME)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.PRECEPTOR_NAME +" is not present or invalid.");
			}else if(!eventSheet.getRow(13).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.WELCOME_CARD_SIGNED_BY)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.WELCOME_CARD_SIGNED_BY +" is not present or invalid.");
			}
			if(!eventSheet.getRow(14).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.PRECEPTOR_ID)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.PRECEPTOR_ID +" is not present or invalid.");
			}else if(!eventSheet.getRow(14).getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.WELCOME_CARD_SIGNED_BY_ID)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.WELCOME_CARD_SIGNED_BY_ID +" is not present or invalid.");
			}
			if(!eventSheet.getRow(16).getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.EVENT_REMARKS)){
				eventErrorList.add("Parsing mismatch "+HeartfulnessConstants.EVENT_REMARKS +" is not present or invalid.");
			}
		}
		System.out.println("INFO : Event Details sheet structure validation completed for v2.1 template.");  
		return eventErrorList;
	}

	@Override
	public List<String> validateParticipantDetails() throws IOException {
		List<String> participantErrorList = new ArrayList<String>();
		System.out.println("INFO : Started validating Participation Details sheet structure for v2.1 template.");
		if(participantSheet == null){
			participantErrorList.add("Participants Details sheet is not found.");
		}else{
			Row headerRow = participantSheet.getRow(0);
			int cellCount = headerRow.getPhysicalNumberOfCells();
			if(cellCount != 19){
				participantErrorList.add("Participants Details sheet must contain 19 cells but is having "+cellCount+" cells");
			}
			if(!headerRow.getCell(0).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_NAME)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_NAME+" is not present or invalid.");
			}
			if(!headerRow.getCell(1).getStringCellValue().trim().equals(HeartfulnessConstants.FIRST_SITTING)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.FIRST_SITTING+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(2).getStringCellValue().trim().equals(HeartfulnessConstants.SECOND_SITTING)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.SECOND_SITTING+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(3).getStringCellValue().trim().equalsIgnoreCase(HeartfulnessConstants.THIRD_SITTING)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.THIRD_SITTING+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(4).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_COUNTRY)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_COUNTRY+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(5).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_STATE)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_STATE+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(6).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_CITY)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_CITY+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(7).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_EMAILID)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_EMAILID+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(8).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_MOBILE)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_MOBILE+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(9).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_PROFESSION)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_PROFESSION+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(10).getStringCellValue().trim().equals(HeartfulnessConstants.DEPTARTMENT_STREAM)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.DEPTARTMENT_STREAM+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(11).getStringCellValue().trim().equals(HeartfulnessConstants.BATCH_YEAR)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.BATCH_YEAR+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(12).getStringCellValue().trim().equals(HeartfulnessConstants.RECEIVE_UPDATES)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.RECEIVE_UPDATES+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(13).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_GENDER)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_GENDER+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(14).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_AGE_GROUP)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_AGE_GROUP+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(15).getStringCellValue().trim().equals(HeartfulnessConstants.PREFERRED_LANGUAGE)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PREFERRED_LANGUAGE+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(16).getStringCellValue().trim().equalsIgnoreCase(HeartfulnessConstants.WELCOME_CARD_NUMBER)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.WELCOME_CARD_NUMBER+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(17).getStringCellValue().trim().equals(HeartfulnessConstants.WELCOME_CARD_ISSUE_DATE)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.WELCOME_CARD_ISSUE_DATE+" is not present or invalid.");	 
			}
			if(!headerRow.getCell(18).getStringCellValue().trim().equals(HeartfulnessConstants.PARTICIPANT_REMARKS)){
				participantErrorList.add("Header name mismatch "+HeartfulnessConstants.PARTICIPANT_REMARKS+" is not present or invalid.");	 
			}
		}
		System.out.println("INFO : Participants Details sheet structure validation completed for v2.1 template.");
		return participantErrorList;
	}

	@Override
	public List<String> checkEventMandatoryFields() throws IOException {
		System.out.println("INFO : Started validating Event Details sheet mandatory fields for v2.1 template.");
		List<String> eventErrorList = new ArrayList<String>();
		if(eventSheet.getRow(2).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_TYPE +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(3).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_PLACE +" is a mandatory field and cannot be empty.");
		}else if(eventSheet.getRow(3).getCell(3).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_DATE +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(4).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_COUNTRY +" is a mandatory field and cannot be empty.");
		}else if(eventSheet.getRow(4).getCell(3).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_STATE +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(5).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_CITY +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(6).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_COORDINATOR_NAME +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(7).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_COORDINATOR_MOBILE +" is a mandatory field and cannot be empty.");
		}else if(eventSheet.getRow(7).getCell(3).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.EVENT_COORDINATOR_EMAIL_ID +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(9).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.ORGANISATION_NAME +" is a mandatory field and cannot be empty.");
		}else if(eventSheet.getRow(9).getCell(3).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.ORGANISATION_CONTACT_PERSON +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(10).getCell(3).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.ORGANISATION_CONTACT_EMAILID +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(11).getCell(3).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.ORGANISATION_CONTACT_MOBILE +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(13).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.PRECEPTOR_NAME +" is a mandatory field and cannot be empty.");
		}
		if(eventSheet.getRow(14).getCell(1).toString().isEmpty()){
			eventErrorList.add(HeartfulnessConstants.PRECEPTOR_ID +" is a mandatory field and cannot be empty.");
		}
		System.out.println("INFO : Event Details sheet mandatory fields validation completed for v2.1 template.");
		return eventErrorList;
	}


	@Override
	public List<String> checkParticipantMandatoryFields() throws IOException {
		System.out.println("INFO : Started validating Participation Details sheet mandatory fields for v2.1 template.");
		List<String> participantErrorList = new ArrayList<String>();
		int rowCount = participantSheet.getPhysicalNumberOfRows();
		for(int i =1;i<rowCount;i++){
			Row currentRow = participantSheet.getRow(i);
			participantErrorList.addAll(parseParticipantData(currentRow,i+1));
		}
		System.out.println("INFO : Participation Details sheet mandatory fields validation completed for v2.1 template.");
		return participantErrorList;
	}

	private List<String> parseParticipantData(Row currentRow, int rowNumber) {
		List<String> errorList = new ArrayList<String>();
		if(!currentRow.getCell(0,Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
			if(currentRow.getCell(0,Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
				errorList.add(HeartfulnessConstants.PARTICIPANT_NAME +" is a mandatory field and cannot be empty at row number "+rowNumber);
			}
			if(currentRow.getCell(4,Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
				errorList.add(HeartfulnessConstants.PARTICIPANT_COUNTRY +" is a mandatory field and cannot be empty at row number "+rowNumber);
			}
			if(currentRow.getCell(5,Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
				errorList.add(HeartfulnessConstants.PARTICIPANT_STATE +" is a mandatory field and cannot be empty at row number "+rowNumber);
			}
			if(currentRow.getCell(6,Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
				errorList.add(HeartfulnessConstants.PARTICIPANT_CITY +" is a mandatory field and cannot be empty at row number "+rowNumber);
			}
		}
		return errorList;
	}

}
