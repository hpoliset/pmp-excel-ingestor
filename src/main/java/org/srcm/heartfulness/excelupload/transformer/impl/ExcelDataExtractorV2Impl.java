package org.srcm.heartfulness.excelupload.transformer.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public Program extractExcel(Workbook workbook) throws InvalidExcelFileException {
		Program program =  new Program();
		Sheet eventSheet = workbook.getSheet("Event Details");
		Sheet participantSheet =  workbook.getSheet("Participants Details");
		program = parseProgram(eventSheet);
		program.setParticipantList(getParticipantList(participantSheet));
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


	private List<Participant> getParticipantList(Sheet participantsSheet) throws InvalidExcelFileException {
		List<Participant> participantList = new ArrayList<Participant>();
		int totalRows = participantsSheet.getPhysicalNumberOfRows();
		// skip first two
		int j = 1;
		for (int i=1; i < totalRows; i++) {
			Row currentRow = participantsSheet.getRow(i);
			Participant participant = parseParticipantRow(currentRow);
			// this will go away
			if(!currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
				if (participant.getExcelSheetSequenceNumber() == 0) {
					participant.setExcelSheetSequenceNumber(j);
				}
				participantList.add(participant);
				j++;
			}
		}

		return participantList;
	}

	private Participant parseParticipantRow(Row participantRow) throws InvalidExcelFileException {
		Participant participant = new Participant();
		if(!participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){		
			participant.setPrintName(participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString());

			String firstSittingStr = participantRow.getCell(1,
					Row.CREATE_NULL_AS_BLANK).toString();
			if (!"Y".equals(firstSittingStr) && !"N".equals(firstSittingStr)) {
				try {
					Date firstSittingDate = DateUtils.parseDate(firstSittingStr);
					participant.setFirstSittingDate(firstSittingDate);
				} catch (ParseException e) {
					throw new InvalidExcelFileException("Not able to parse first sitting date:[" + firstSittingStr + "]");
				}
			}else if("Y".equals(firstSittingStr)){
				participant.setFirstSitting(1);
			}else if("N".equals(firstSittingStr)){
				participant.setFirstSitting(0);
			}

			String secondSittingStr = participantRow.getCell(2,
					Row.CREATE_NULL_AS_BLANK).toString();
			if (!"Y".equals(secondSittingStr) && !"N".equals(secondSittingStr)) {
				try {
					Date secondSittingDate = DateUtils.parseDate(secondSittingStr);
					participant.setSecondSittingDate(secondSittingDate);
				} catch (ParseException e) {
					throw new InvalidExcelFileException("Not able to parse second sitting date:[" + secondSittingStr + "]");
				}
			} else if("Y".equals(secondSittingStr)){
				participant.setSecondSitting(1);
			}else if("N".equals(secondSittingStr)){
				participant.setSecondSitting(0);
			}

			String thirdSittingStr = participantRow.getCell(3,
					Row.CREATE_NULL_AS_BLANK).toString();
			if (!"Y".equals(thirdSittingStr) && !"N".equals(thirdSittingStr)) {
				try {
					Date thirdSittingDate = DateUtils.parseDate(thirdSittingStr);
					participant.setThirdSittingDate(thirdSittingDate);
				} catch (ParseException e) {
					throw new InvalidExcelFileException("Not able to parse third sitting date:[" + thirdSittingStr + "]");
				}
			} else if("Y".equals(thirdSittingStr)){
				participant.setThirdSitting(1);
			}else if("N".equals(thirdSittingStr)){
				participant.setThirdSitting(0);
			}

			participant.setCountry(participantRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setState(participantRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setCity(participantRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setEmail(participantRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString());
			Cell mobilePhoneCell = participantRow.getCell(8, Row.CREATE_NULL_AS_BLANK);
			try {
				Double numbericMobilePhone = mobilePhoneCell.getNumericCellValue();
				participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()));
			} catch (NumberFormatException | ClassCastException | IllegalStateException  e) {
				LOGGER.info("Participant mobile phone number is not numeric, trying as string");
				participant.setMobilePhone(String.valueOf(mobilePhoneCell));
			} catch (Exception e) {
				LOGGER.info("Participant mobile phone number is not numeric, trying as string");
				participant.setMobilePhone(String.valueOf(mobilePhoneCell));
			}
			participant.setProfession(participantRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setDepartment(participantRow.getCell(10, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setBatch(participantRow.getCell(11, Row.CREATE_NULL_AS_BLANK).toString());
			String receiveUpdateStr = participantRow.getCell(12, Row.CREATE_NULL_AS_BLANK).toString();
			if ("Y".equalsIgnoreCase(receiveUpdateStr)) {
				participant.setReceiveUpdates(1);
			} else {
				participant.setReceiveUpdates(0);
			}

			participant.setGender(participantRow.getCell(13, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setAgeGroup(participantRow.getCell(14, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setLanguage(participantRow.getCell(15, Row.CREATE_NULL_AS_BLANK).toString());
			participant.setWelcomeCardNumber(participantRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString());
			String welcomeCardDateStr = participantRow.getCell(17, Row.CREATE_NULL_AS_BLANK).toString();
			Date welcomeCardDate = null;
			try {
				welcomeCardDate = DateUtils.parseDate(welcomeCardDateStr);
			} catch (ParseException e) {
				throw new InvalidExcelFileException(
						"Unable to part v2 file: welcome card date: [" + welcomeCardDateStr + "]");
			}

			participant.setWelcomeCardDate(welcomeCardDate);
			participant.setRemarks(participantRow.getCell(18, Row.CREATE_NULL_AS_BLANK).toString());
		}
		return participant;
	}

	private Program parseProgram(Sheet eventSheet) throws InvalidExcelFileException {

		Program program = new Program();
		program.setProgramChannel(eventSheet.getRow(2).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventPlace(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setProgramName(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCountry(eventSheet.getRow(4).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCity(eventSheet.getRow(5).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorName(eventSheet.getRow(6).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		Cell coordinatorMobileNumber = eventSheet.getRow(7).getCell(1, Row.CREATE_NULL_AS_BLANK);
		try{
			Double numericCoorntrMobNbr = coordinatorMobileNumber.getNumericCellValue();
			program.setCoordinatorMobile(String.valueOf(numericCoorntrMobNbr.longValue()));
		}catch (NumberFormatException | ClassCastException | IllegalStateException  e) {
			LOGGER.info("Coordinator mobile number is not numeric, trying as string");
			program.setCoordinatorMobile(String.valueOf(coordinatorMobileNumber));
		}catch(Exception ex){
			LOGGER.info("Coordinator mobile number is not numeric, trying as string");
			program.setCoordinatorMobile(String.valueOf(coordinatorMobileNumber));
		}

		program.setOrganizationName(eventSheet.getRow(9).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationWebSite(eventSheet.getRow(10).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());

		program.setPreceptorName(eventSheet.getRow(13).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setPreceptorIdCardNumber(eventSheet.getRow(14).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());

		program.setRemarks(eventSheet.getRow(17).getCell(0, Row.CREATE_NULL_AS_BLANK).toString());

		String eventDateStr = eventSheet.getRow(3).getCell(3, Row.CREATE_NULL_AS_BLANK).toString();
		Date eventDate = null;
		try {
			eventDate = DateUtils.parseDate(eventDateStr);
		} catch (ParseException e) {
			throw new InvalidExcelFileException("Not able to parse program event date:[" + eventDateStr + "]");
		}
		program.setProgramStartDate(eventDate);

		program.setEventState(eventSheet.getRow(4).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());

		program.setCoordinatorEmail(eventSheet.getRow(7).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationContactName(eventSheet.getRow(9).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationContactEmail(eventSheet.getRow(10).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
		Cell organizationContactMobile = eventSheet.getRow(11).getCell(3, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericPhoneNumber = organizationContactMobile.getNumericCellValue();
			program.setOrganizationContactMobile(String.valueOf(numbericPhoneNumber.longValue()));
		} catch (NumberFormatException | ClassCastException | IllegalStateException  e) {
			LOGGER.info("OrganizationPhoneNumber is not numeric, trying as string");
			program.setOrganizationContactMobile(String.valueOf(organizationContactMobile));
		} catch (Exception e) {
			LOGGER.info("OrganizationPhoneNumber is not numeric, trying as string");
			program.setOrganizationContactMobile(String.valueOf(organizationContactMobile));
		}
		program.setWelcomeCardSignedByName(eventSheet.getRow(13).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
		program.setWelcomeCardSignerIdCardNumber(eventSheet.getRow(14).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());

		return program;
	}

}
