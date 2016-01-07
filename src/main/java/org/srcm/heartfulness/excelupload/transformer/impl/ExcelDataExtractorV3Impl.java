package org.srcm.heartfulness.excelupload.transformer.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
 * Implementation class to extract excel data for altered V3 template.
 * 
 * @author Goutham
 *
 */
public class ExcelDataExtractorV3Impl implements ExcelDataExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataExtractorV3Impl.class);
	private FormulaEvaluator formulaEval;

	public ExcelDataExtractorV3Impl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor#extractExcel(org.apache.poi.ss.usermodel.Workbook)
	 */
	@Override
	public Program extractExcel(Workbook workbook) throws InvalidExcelFileException {

        formulaEval = workbook.getCreationHelper().createFormulaEvaluator();
		Program program = new Program();
		Sheet sheet = workbook.getSheet(EventDetailsUploadConstants.V3_SHEET_NAME);
		program = parseProgram(sheet);
		program.setParticipantList(getParticipantList(sheet));
		return program;
	}

	/**
	 * This method is used to get the list of participants 
	 * 
	 * @param sheet @see {@link Sheet}
	 * @return List<Participant>
	 * @throws InvalidExcelFileException @see {@link InvalidExcelFileException}
	 */
	private List<Participant> getParticipantList(Sheet sheet) throws InvalidExcelFileException {
		List<Participant> participantList = new ArrayList<Participant>();
		int totalRows = sheet.getPhysicalNumberOfRows();
		// skip first six
		for (int i = 7; i < totalRows; i++) {
			Row currentRow = sheet.getRow(i);
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

	/**
	 * This method is used to parse the participant single row 
	 * 
	 * @param participantRow @see {@link Row}
	 * @return participant @see {@link Participant}
	 * @throws InvalidExcelFileException @see {@link InvalidExcelFileException}
	 */
	private Participant parseParticipantRow(Row participantRow) throws InvalidExcelFileException {
		Participant participant = new Participant();
		if (!participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString().isEmpty()) {
			double seqNo = Double.valueOf(participantRow.getCell(0, Row.CREATE_NULL_AS_BLANK).toString());
			int integerSeq = (int) seqNo;
			participant.setExcelSheetSequenceNumber(integerSeq);
		}
		String printName = participantRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString() + " "
				+ participantRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString() + " "
				+ participantRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString();
		participant.setPrintName(printName.trim());
		participant.setFirstName(participantRow.getCell(1, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setMiddleName(participantRow.getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setLastName(participantRow.getCell(3, Row.CREATE_NULL_AS_BLANK).toString());

		String firstSittingStr = participantRow.getCell(4, Row.CREATE_NULL_AS_BLANK).toString();
		try {
			Date firstSittingDate = DateUtils.parseDate(firstSittingStr);
			participant.setFirstSittingDate(firstSittingDate);
		} catch (ParseException e) {
			// ignore
		}
		String secondSittingStr = participantRow.getCell(5, Row.CREATE_NULL_AS_BLANK).toString();
		try {
			Date secondSittingDate = DateUtils.parseDate(secondSittingStr);
			participant.setSecondSittingDate(secondSittingDate);
		} catch (ParseException e) {
			// ignore
		}
		String thirdSittingStr = participantRow.getCell(6, Row.CREATE_NULL_AS_BLANK).toString();
		try {
			Date thirdSittingDate = DateUtils.parseDate(thirdSittingStr);
			participant.setThirdSittingDate(thirdSittingDate);
		} catch (ParseException e) {
			// ignore
		}
		Cell phoneCell = participantRow.getCell(7, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericMobilePhone = phoneCell.getNumericCellValue();
			participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()));
		} catch (NumberFormatException | ClassCastException | IllegalStateException e) {
			LOGGER.error("Participant mobile phone number is not numeric, trying as string");
			participant.setMobilePhone(phoneCell.toString());
		}
		participant.setEmail(participantRow.getCell(8, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setAddressLine1(participantRow.getCell(9, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setCity(participantRow.getCell(10, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setState(participantRow.getCell(11, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setCountry(participantRow.getCell(12, Row.CREATE_NULL_AS_BLANK).toString());
		Cell pincodeCell = participantRow.getCell(13, Row.CREATE_NULL_AS_BLANK);
		try {
			Double pincode = pincodeCell.getNumericCellValue();
			participant.setPincode(pincode.intValue());
		} catch (NumberFormatException | ClassCastException | IllegalStateException e) {
			LOGGER.error("Participant pincode is not numeric, trying as string");
			participant.setMobilePhone(phoneCell.toString());
		}
		participant.setGender(participantRow.getCell(14, Row.CREATE_NULL_AS_BLANK).toString());
		String dobStr = participantRow.getCell(15, Row.CREATE_NULL_AS_BLANK).toString();
		try {
			Date dobDate = DateUtils.parseDate(dobStr);
			participant.setDateOfBirth(dobDate);
		} catch (ParseException e) {
			// ignore
		}
		participant.setProfession(participantRow.getCell(16, Row.CREATE_NULL_AS_BLANK).toString());
		// organisation TODO
		// participant.setOrignization(participantRow.getCell(17,Row.CREATE_NULL_AS_BLANK).toString());
		participant.setDepartment(participantRow.getCell(18, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setBatch(participantRow.getCell(19, Row.CREATE_NULL_AS_BLANK).toString());

		participant.setReceiveUpdates(participantRow.getCell(20, Row.CREATE_NULL_AS_BLANK).toString()
				.equalsIgnoreCase("Yes") ? 1 : 0);
		participant.setLanguage(participantRow.getCell(21, Row.CREATE_NULL_AS_BLANK).toString());
		// other language TODO
		// participant.setOtherLangugate(participantRow.getCell(22,Row.CREATE_NULL_AS_BLANK).toString());
		participant.setRemarks(participantRow.getCell(23, Row.CREATE_NULL_AS_BLANK).toString());
		participant.setWelcomeCardNumber(participantRow.getCell(24, Row.CREATE_NULL_AS_BLANK).toString());
		String welcomeCardStr = participantRow.getCell(25, Row.CREATE_NULL_AS_BLANK).toString();
		Date welcomeCardDate = null;
		try {
			welcomeCardDate = DateUtils.parseDate(welcomeCardStr);
			participant.setWelcomeCardDate(welcomeCardDate);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse welcome card date:[" + welcomeCardDate + "]");
		}
		// welcome card signed by
		// participant.setWelcomeCard(participantRow.getCell(26,Row.CREATE_NULL_AS_BLANK).toString());
		return participant;
	}

	/**
	 * This method is used to parse the program for the V3.0 template 
	 * 
	 * @param sheet @see {@link Sheet}
	 * @return program @see {@link Program}
	 * @throws InvalidExcelFileException @see {@link InvalidExcelFileException}
	 */
	private Program parseProgram(Sheet sheet) throws InvalidExcelFileException {

		LOGGER.debug("Started to parse data for V3 template.");
		Program program = new Program();
		program.setProgramChannel(sheet.getRow(1).getCell(10, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventPlace(sheet.getRow(1).getCell(8, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCountry(sheet.getRow(2).getCell(10, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCity(sheet.getRow(2).getCell(8, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorName(sheet.getRow(1).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorId(sheet.getRow(4).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());

		Cell eventCoordinatorMobile = sheet.getRow(2).getCell(2, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericPhoneNumber = eventCoordinatorMobile.getNumericCellValue();
			program.setCoordinatorMobile(String.valueOf(numbericPhoneNumber.longValue()));
		} catch (Exception e) {
			LOGGER.info("OrganizationPhoneNumber is not numeric, trying as string");
			program.setCoordinatorMobile(eventCoordinatorMobile.toString());
		}

		program.setOrganizationName(sheet.getRow(1).getCell(6, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationWebSite(sheet.getRow(3).getCell(6, Row.CREATE_NULL_AS_BLANK).toString());

		program.setPreceptorName(sheet.getRow(7).getCell(27, Row.CREATE_NULL_AS_BLANK).toString());
		program.setPreceptorIdCardNumber(sheet.getRow(7).getCell(28, Row.CREATE_NULL_AS_BLANK).toString());
		//program.setRemarks(sheet.getRow(17).getCell(0, Row.CREATE_NULL_AS_BLANK).toString());
		//program.setWelcomeCardSignedByName(sheet.getRow(7).getCell(26, Row.CREATE_NULL_AS_BLANK).toString());
		program.setWelcomeCardSignerIdCardNumber(sheet.getRow(7).getCell(26, Row.CREATE_NULL_AS_BLANK).toString());
		String eventDateStr = sheet.getRow(3).getCell(10, Row.CREATE_NULL_AS_BLANK).toString();
		Date eventDate = null;
		try {
			eventDate = DateUtils.parseDate(eventDateStr);
		} catch (ParseException e) {
			throw new InvalidExcelFileException("Not able to parse program event date:[" + eventDateStr + "]");
		}
		program.setProgramStartDate(eventDate);
		program.setEventState(sheet.getRow(3).getCell(8, Row.CREATE_NULL_AS_BLANK).toString());
		String eventId=formulaEval.evaluate(sheet.getRow(4).getCell(10, Row.CREATE_NULL_AS_BLANK)).formatAsString();
		program.setEventId(eventId);
		program.setCoordinatorEmail(sheet.getRow(3).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationContactName(sheet.getRow(1).getCell(4, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationContactEmail(sheet.getRow(3).getCell(4, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationDepartment(sheet.getRow(4).getCell(6, Row.CREATE_NULL_AS_BLANK).toString());

		Cell organizationContactMobile = sheet.getRow(2).getCell(4, Row.CREATE_NULL_AS_BLANK);
		try {
			Double numbericPhoneNumber = organizationContactMobile.getNumericCellValue();
			program.setOrganizationContactMobile(String.valueOf(numbericPhoneNumber.longValue()));
		} catch (Exception e) {
			LOGGER.debug("OrganizationPhoneNumber is not numeric, trying as string");
			program.setOrganizationContactMobile(organizationContactMobile.toString());
		}
		return program;
	}

}
