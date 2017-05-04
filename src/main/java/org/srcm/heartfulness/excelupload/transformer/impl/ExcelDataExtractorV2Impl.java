package org.srcm.heartfulness.excelupload.transformer.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;


/**
 * Created by vsonnathi on 11/16/15.
 */
public class ExcelDataExtractorV2Impl implements ExcelDataExtractor {

	/*private Sheet eventSheet;
	private Sheet participantsSheet;
	private Program program;*/

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataExtractorV2Impl.class);

	public ExcelDataExtractorV2Impl() {
		super();
	}

	@Override
	public Program extractExcel(Workbook workbook,String eWelcomeIdCheckbox,String jiraIssueNumber) throws InvalidExcelFileException {
		Program program =  new Program();
		Sheet eventSheet = workbook.getSheet("Event Details");
		Sheet participantSheet =  workbook.getSheet("Participants Details");
		boolean disableEwelcomeIdGeneration = false;
		if(eWelcomeIdCheckbox.equals("on")){
			disableEwelcomeIdGeneration = true;
		}
		program = parseProgram(eventSheet,disableEwelcomeIdGeneration);
		program.setParticipantList(getParticipantList(participantSheet,disableEwelcomeIdGeneration));
		program.setJiraIssueNumber(jiraIssueNumber);
		return program;
	}


	/*public Program getProgram(Workbook workbook) throws InvalidExcelFileException {
		this.eventSheet = workbook.getSheet("Event Details");
		this.participantsSheet = workbook.getSheet("Participants Details");
		if (this.eventSheet == null) {
			throw new InvalidExcelFileException("Event Details sheet not found");
		}
		// Read Program
		this.program = parseProgram();
		return program;
	}*/


	public List<Participant> getParticipantList(Sheet participantsSheet,boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		LOGGER.info("Started extracting participant for V2 template");
		List<Participant> participantList = new ArrayList<Participant>();
		int totalRows = participantsSheet.getPhysicalNumberOfRows();
		// skip first two
		int j = 1;
		for (int i=1; i < totalRows; i++) {
			Row currentRow = participantsSheet.getRow(i);
			// this will go away
			if(!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){
				Participant participant = parseParticipantRow(currentRow,disableEwelcomeIdGeneration);
				if (participant.getExcelSheetSequenceNumber() == 0) {
					participant.setExcelSheetSequenceNumber(j);
				}
				participantList.add(participant);
				j++;
			}
		}
		LOGGER.info("Completed extracting participant for V2 template");
		return participantList;
	}

	private Participant parseParticipantRow(Row participantRow,boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		Participant participant = new Participant();
		if(!participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim().isEmpty()){		
			participant.setPrintName(participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().trim());

			String firstSittingStr = participantRow.getCell(1,
					Row.CREATE_NULL_AS_BLANK).toString().trim();
			if (!"Y".equalsIgnoreCase(firstSittingStr) && !"N".equalsIgnoreCase(firstSittingStr)) {
				try {
					Date firstSittingDate = DateUtils.parseDate(firstSittingStr);
					participant.setFirstSittingDate(firstSittingDate);
				} catch (ParseException e) {
					throw new InvalidExcelFileException("Not able to parse first sitting date:[" + firstSittingStr + "]");
				}
			}else if("Y".equalsIgnoreCase(firstSittingStr)){
				participant.setFirstSitting(1);
			}else if("N".equalsIgnoreCase(firstSittingStr)){
				participant.setFirstSitting(0);
			}

			String secondSittingStr = participantRow.getCell(2,
					Row.CREATE_NULL_AS_BLANK).toString().trim();
			if (!"Y".equalsIgnoreCase(secondSittingStr) && !"N".equalsIgnoreCase(secondSittingStr)) {
				try {
					Date secondSittingDate = DateUtils.parseDate(secondSittingStr);
					participant.setSecondSittingDate(secondSittingDate);
				} catch (ParseException e) {
					throw new InvalidExcelFileException("Not able to parse second sitting date:[" + secondSittingStr + "]");
				}
			} else if("Y".equalsIgnoreCase(secondSittingStr)){
				participant.setSecondSitting(1);
			}else if("N".equalsIgnoreCase(secondSittingStr)){
				participant.setSecondSitting(0);
			}

			String thirdSittingStr = participantRow.getCell(3,
					Row.CREATE_NULL_AS_BLANK).toString().trim();
			if (!"Y".equalsIgnoreCase(thirdSittingStr) && !"N".equalsIgnoreCase(thirdSittingStr)) {
				try {
					Date thirdSittingDate = DateUtils.parseDate(thirdSittingStr);
					participant.setThirdSittingDate(thirdSittingDate);
				} catch (ParseException e) {
					throw new InvalidExcelFileException("Not able to parse third sitting date:[" + thirdSittingStr + "]");
				}
			} else if("Y".equalsIgnoreCase(thirdSittingStr)){
				participant.setThirdSitting(1);
			}else if("N".equalsIgnoreCase(thirdSittingStr)){
				participant.setThirdSitting(0);
			}

			participant.setCountry(participantRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setState(participantRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setCity(participantRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setEmail(participantRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString().trim());
			Cell mobilePhoneCell = participantRow.getCell(8, Row.CREATE_NULL_AS_BLANK);
			try {
				Double numbericMobilePhone = mobilePhoneCell.getNumericCellValue();
				participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()).trim());
			} catch (NumberFormatException | ClassCastException | IllegalStateException  e) {
				LOGGER.error("Participant mobile phone number is not numeric, trying as string");
				participant.setMobilePhone(String.valueOf(mobilePhoneCell).trim());
			} catch (Exception e) {
				LOGGER.error("Participant mobile phone number is not numeric, trying as string");
				participant.setMobilePhone(String.valueOf(mobilePhoneCell).trim());
			}
			participant.setProfession(participantRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setDepartment(participantRow.getCell(10, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setBatch(participantRow.getCell(11, Row.CREATE_NULL_AS_BLANK).toString().trim());
			String receiveUpdateStr = participantRow.getCell(12, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if ("Y".equalsIgnoreCase(receiveUpdateStr)) {
				participant.setReceiveUpdates(1);
			} else {
				participant.setReceiveUpdates(0);
			}

			participant.setGender(participantRow.getCell(13, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setAgeGroup(participantRow.getCell(14, Row.CREATE_NULL_AS_BLANK).toString().trim());
			participant.setLanguage(participantRow.getCell(15, Row.CREATE_NULL_AS_BLANK).toString().trim());

			String welcomeCardNo = participantRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim();
			if(welcomeCardNo.isEmpty()){
				if(disableEwelcomeIdGeneration){
					participant.setEwelcomeIdState(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
				}
			}else{
				participant.setWelcomeCardNumber(welcomeCardNo);
				String welcomeCardDateStr = participantRow.getCell(17, Row.CREATE_NULL_AS_BLANK).toString().trim();
				Date welcomeCardDate = null;
				try {
					welcomeCardDate = DateUtils.parseDate(welcomeCardDateStr);
				} catch (ParseException e) {
					throw new InvalidExcelFileException(
							"Unable to part v2 file: welcome card date: [" + welcomeCardDateStr + "]");
				}
				participant.setWelcomeCardDate(welcomeCardDate);
			}

			/*participant.setWelcomeCardNumber(participantRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString().trim());
			String welcomeCardDateStr = participantRow.getCell(17, Row.CREATE_NULL_AS_BLANK).toString().trim();
			Date welcomeCardDate = null;
			try {
				welcomeCardDate = DateUtils.parseDate(welcomeCardDateStr);
			} catch (ParseException e) {
				throw new InvalidExcelFileException(
						"Unable to part v2 file: welcome card date: [" + welcomeCardDateStr + "]");
			}

			participant.setWelcomeCardDate(welcomeCardDate);*/


			participant.setRemarks(participantRow.getCell(18, Row.CREATE_NULL_AS_BLANK).toString().trim());
		}
		return participant;
	}

	private Program parseProgram(Sheet eventSheet,boolean disableEwelcomeIdGeneration) throws InvalidExcelFileException {
		LOGGER.info("Started extracting program for V2 template");
		Program program = new Program();
		program.setProgramChannel(eventSheet.getRow(2).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventPlace(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setProgramName(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setEventCountry(eventSheet.getRow(4).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCity(eventSheet.getRow(5).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setCoordinatorName(eventSheet.getRow(6).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		Cell coordinatorMobileNumber = eventSheet.getRow(7).getCell(1, Row.CREATE_NULL_AS_BLANK);
		try{
			Double numericCoorntrMobNbr = coordinatorMobileNumber.getNumericCellValue();
			program.setCoordinatorMobile(String.valueOf(numericCoorntrMobNbr.longValue()).trim());
		}catch (NumberFormatException | ClassCastException | IllegalStateException  e) {
			LOGGER.error("Coordinator mobile number is not numeric, trying as string");
			program.setCoordinatorMobile(String.valueOf(coordinatorMobileNumber).trim());
		}catch(Exception ex){
			LOGGER.error("Coordinator mobile number is not numeric, trying as string");
			program.setCoordinatorMobile(String.valueOf(coordinatorMobileNumber).trim());
		}

		program.setOrganizationName(eventSheet.getRow(9).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationWebSite(eventSheet.getRow(10).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());

		program.setPreceptorName(eventSheet.getRow(13).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setPreceptorIdCardNumber(eventSheet.getRow(14).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());
		
		program.setRemarks(eventSheet.getRow(16).getCell(1, Row.CREATE_NULL_AS_BLANK).toString().trim());

		String eventDateStr = eventSheet.getRow(3).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim();
		Date eventDate = null;
		try {
			eventDate = DateUtils.parseDate(eventDateStr);
		} catch (Exception e) {
			throw new InvalidExcelFileException("Not able to parse program event date:[" + eventDateStr + "]");
		}
		program.setProgramStartDate(eventDate);

		program.setEventState(eventSheet.getRow(4).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());

		program.setCoordinatorEmail(eventSheet.getRow(7).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationContactName(eventSheet.getRow(9).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setOrganizationContactEmail(eventSheet.getRow(10).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		Cell organizationContactMobile = eventSheet.getRow(11).getCell(3, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericPhoneNumber = organizationContactMobile.getNumericCellValue();
			program.setOrganizationContactMobile(String.valueOf(numbericPhoneNumber.longValue()).trim());
		} catch (NumberFormatException | ClassCastException | IllegalStateException  e) {
			LOGGER.error("OrganizationPhoneNumber is not numeric, trying as string");
			program.setOrganizationContactMobile(String.valueOf(organizationContactMobile).trim());
		} catch (Exception e) {
			LOGGER.error("OrganizationPhoneNumber is not numeric, trying as string");
			program.setOrganizationContactMobile(String.valueOf(organizationContactMobile).trim());
		}
		program.setWelcomeCardSignedByName(eventSheet.getRow(13).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		program.setWelcomeCardSignerIdCardNumber(eventSheet.getRow(14).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		if(disableEwelcomeIdGeneration){
			program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
		}else{
			program.setIsEwelcomeIdGenerationDisabled(EventDetailsUploadConstants.EWELCOME_ID_ENABLED_STATE);
		}
		program.setSendersEmailAddress(eventSheet.getRow(16).getCell(3, Row.CREATE_NULL_AS_BLANK).toString().trim());
		
		LOGGER.info("Completed extracting program for V2 template");
		return program;
	}

}
