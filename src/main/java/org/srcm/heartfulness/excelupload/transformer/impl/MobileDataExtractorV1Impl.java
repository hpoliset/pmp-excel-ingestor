/**
 * 
 */
package org.srcm.heartfulness.excelupload.transformer.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * @author Koustav Dutta
 *
 */
public class MobileDataExtractorV1Impl implements ExcelDataExtractor{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MobileDataExtractorV1Impl.class);

	@Override
	public Program extractExcel(Workbook workbook, String eWelcomeIdCheckbox) throws InvalidExcelFileException {
		
		Program program = null;
		Sheet eventSheet = workbook.getSheet("Event Details");
		Sheet participantSheet =  workbook.getSheet("Participants Details");
		boolean disableEwelcomeIdGeneration = false;
		if(eWelcomeIdCheckbox.equals("on")){
			disableEwelcomeIdGeneration = true;
		}
		program = parseProgram(eventSheet,disableEwelcomeIdGeneration);
		program.setParticipantList(getParticipantList(participantSheet,disableEwelcomeIdGeneration));
		return program;
	}

	private Program parseProgram(Sheet eventSheet, boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		LOGGER.info("Started extracting program for M1.0 template");
		Program program = new Program();
		program.setProgramChannel(eventSheet.getRow(1).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		
		String eventDateStr = eventSheet.getRow(1).getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim();
		Date eventDate = null;
		try {
			eventDate = DateUtils.parseDate(eventDateStr);
		} catch (Exception e) {
			throw new InvalidExcelFileException("Not able to parse event date:[" + eventDateStr + "]");
		}
		program.setProgramStartDate(eventDate);
		
		String eventToDateStr = eventSheet.getRow(1).getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!eventToDateStr.isEmpty()){
			Date eventToDate = null;
			try {
				eventToDate = DateUtils.parseDate(eventToDateStr);
			} catch (Exception e) {
				throw new InvalidExcelFileException("Not able to parse to date:[" + eventToDateStr + "]");
			}
			program.setProgramEndDate(eventToDate);
		}
		
		program.setEventPlace(eventSheet.getRow(1).getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventCity(eventSheet.getRow(1).getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventState(eventSheet.getRow(1).getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventCountry(EventDetailsUploadConstants.M1_0_EVENT_COUNTRY);
		program.setCoordinatorName(eventSheet.getRow(1).getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setCoordinatorEmail(eventSheet.getRow(1).getCell(10, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationName(eventSheet.getRow(1).getCell(11, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationContactName(eventSheet.getRow(1).getCell(12, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationContactEmail(eventSheet.getRow(1).getCell(13, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationContactMobile(eventSheet.getRow(1).getCell(14, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationWebSite(eventSheet.getRow(1).getCell(15, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setPreceptorName(eventSheet.getRow(1).getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setPreceptorIdCardNumber(eventSheet.getRow(1).getCell(17, Row.CREATE_NULL_AS_BLANK).toString().trim());
		
		String autoGenEventId = eventSheet.getRow(1).getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim();
		if(!autoGenEventId.isEmpty() && !autoGenEventId.equals("1")){
			program.setAutoGeneratedEventId(autoGenEventId);
		}
		program.setProgramName(eventSheet.getRow(1).getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_ENABLED_STATE);
		
		if(disableEwelcomeIdGeneration){
			program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
		}
		LOGGER.info("Completed extracting program for M1.0 template");
		return program;
	}
	
	
	private List<Participant> getParticipantList(Sheet participantSheet, boolean disableEwelcomeIdGeneration) {
		
		LOGGER.info("Started extracting participant for M1.0 template");
		List<Participant> participantList = new ArrayList<Participant>();
		int totalRows = participantSheet.getPhysicalNumberOfRows();
		int j = 1;
		for (int i=1; i < totalRows; i++) {
			Row currentRow = participantSheet.getRow(i);
			if(!currentRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
				Participant participant = parseParticipantRow(currentRow,disableEwelcomeIdGeneration);
				if (participant.getExcelSheetSequenceNumber() == 0) {
					participant.setExcelSheetSequenceNumber(j);
				}
				participantList.add(participant);
				j++;
			}
		}
		LOGGER.info("Completed extracting participant for M1.0 template");
		return participantList;
	}

	private Participant parseParticipantRow(Row participantRow, boolean disableEwelcomeIdGeneration) {
		
		Participant participant = new Participant();
		participant.setPrintName(participantRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setEmail(participantRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setMobilePhone(participantRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString().trim());
		String gender = participantRow.getCell(10, Row.CREATE_NULL_AS_BLANK).toString().trim();
		participant.setGender(gender.equalsIgnoreCase(PMPConstants.MALE)?PMPConstants.GENDER_MALE : gender.equalsIgnoreCase(PMPConstants.FEMALE) ? PMPConstants.GENDER_FEMALE : "");
		participant.setCity(participantRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setState(participantRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setCountry(EventDetailsUploadConstants.M1_0_EVENT_COUNTRY);
		participant.setRemarks(participantRow.getCell(14, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setLanguage(participantRow.getCell(12, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setAgeGroup(participantRow.getCell(11, Row.CREATE_NULL_AS_BLANK).toString().trim());
		participant.setFirstSitting(Integer.parseInt(participantRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString().trim()));
		participant.setSecondSitting(Integer.parseInt(participantRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim()));
		participant.setThirdSitting(Integer.parseInt(participantRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim()));
		participant.setReceiveUpdates(participantRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim().equalsIgnoreCase("Y") ? 1:0 );
		if(disableEwelcomeIdGeneration){
			participant.setEwelcomeIdState(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
		}
		participant.setTotalDays(Integer.parseInt(participantRow.getCell(13, Row.CREATE_NULL_AS_BLANK).toString().trim()));
		return participant;
	}

}
