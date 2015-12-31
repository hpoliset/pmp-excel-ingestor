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
		for (int i=1; i < totalRows; i++) {
			Row currentRow = participantsSheet.getRow(i);
			Participant participant = parseParticipantRow(currentRow);
			// this will go away
			if (participant.getExcelSheetSequenceNumber() == 0) {
				participant.setExcelSheetSequenceNumber(i);
			}
			if ("".equals(participant.getPrintName())) {
				break; // Not able to figure out how to get correct rows.
			}
			participantList.add(participant);
		}

		return participantList;
	}

	private Participant parseParticipantRow(Row participantRow) throws InvalidExcelFileException {
		Participant participant = new Participant();
		participant.setPrintName(participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString());
		String firstSittingStr = participantRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString();

		SimpleDateFormat mmddyy = new SimpleDateFormat("mm/dd/yy");
		if (!"Y".equals(firstSittingStr)) {
			try {
				Date firstSittingDate = mmddyy.parse(firstSittingStr);
				participant.setFirstSittingDate(firstSittingDate);
			} catch (ParseException e) {
				// ignore
			}
		} else {
			participant.setFirstSittingTaken(1);
		}

		String secondSittingStr = participantRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString();
		if (!"Y".equals(firstSittingStr)) {
			try {
				Date firstSittingDate = mmddyy.parse(firstSittingStr);
				participant.setSecondSittingDate(firstSittingDate);
			} catch (ParseException e) {
				// ignore
			}
		} else {
			participant.setSecondSittingTaken(1);
		}

		String thirdSittingStr = participantRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString();
		if (!"Y".equals(firstSittingStr)) {
			try {
				Date sittingDate = mmddyy.parse(thirdSittingStr);
				participant.setThirdSittingDate(sittingDate);
			} catch (ParseException e) {
				// ignore
			}
		} else {
			participant.setThirdSittingTaken(1);
		}

		participant.setCountry(participantRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setState(participantRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setCity(participantRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setEmail(participantRow.getCell(7, Row.CREATE_NULL_AS_BLANK).toString());
		Cell mobilePhoneCell = participantRow.getCell(8, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericMobilePhone = mobilePhoneCell.getNumericCellValue();
			participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()));
		} catch (Exception e) {
			LOGGER.info("Participant mobile phone number is not numeric, trying as string");
			participant.setMobilePhone(mobilePhoneCell.toString());
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
		/*SimpleDateFormat mmDDDYYformat = new SimpleDateFormat("dd-MMM-yy");
        Date welcomeCardDateFromStr = null;
        try {
            if (!"".equals(welcomeCardDateStr)) {
                welcomeCardDateFromStr = mmDDDYYformat.parse(welcomeCardDateStr);
            }
        } catch (ParseException e) {
            throw new InvalidExcelFileException("Unable to parse v2 file:[" + fileName + "] and Abhyasi Sheet ", e);
        }*/
		participant.setWelcomeCardDate(welcomeCardDate);
		participant.setRemarks(participantRow.getCell(18, Row.CREATE_NULL_AS_BLANK).toString());

		return participant;
	}

	private Program parseProgram(Sheet eventSheet) throws InvalidExcelFileException {

		Program program = new Program();

		program.setProgramChannel(eventSheet.getRow(2).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventPlace(eventSheet.getRow(3).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCountry(eventSheet.getRow(4).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCity(eventSheet.getRow(5).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorName(eventSheet.getRow(6).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorMobile(eventSheet.getRow(7).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());

		program.setOrganizationName(eventSheet.getRow(9).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationWebSite(eventSheet.getRow(10).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());

		program.setPreceptorName(eventSheet.getRow(13).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		program.setPreceptorIdCardNumber(eventSheet.getRow(14).getCell(1, Row.CREATE_NULL_AS_BLANK).toString());

		program.setRemarks(eventSheet.getRow(17).getCell(0, Row.CREATE_NULL_AS_BLANK).toString());

		String eventDateStr = eventSheet.getRow(3).getCell(3, Row.CREATE_NULL_AS_BLANK).toString();
		//        SimpleDateFormat mmDDDYYformat = new SimpleDateFormat("dd-MMM-yy");
		Date eventDate = null;
		/*try {
            eventDate = mmDDDYYformat.parse(eventDateStr);
        } catch (ParseException e) {
            throw new InvalidExcelFileException("Unable to parse v2 file:[" + fileName + "]", e);
        }*/
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
		} catch (Exception e) {
			LOGGER.info("OrganizationPhoneNumber is not numeric, trying as string");
			program.setOrganizationContactMobile(organizationContactMobile.toString());
		}

		program.setWelcomeCardSignedByName(eventSheet.getRow(13).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
		program.setWelcomeCardSignerIdCardNumber(eventSheet.getRow(14).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());

		return program;
	}

}
