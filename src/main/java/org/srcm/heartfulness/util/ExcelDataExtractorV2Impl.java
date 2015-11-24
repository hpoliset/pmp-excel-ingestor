package org.srcm.heartfulness.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vsonnathi on 11/16/15.
 */
public class ExcelDataExtractorV2Impl implements ExcelDataExtractor {

    private final String fileName;
    private final byte[] fileContent;
    private final Workbook workbook;
    private final Sheet eventSheet;
    private final Sheet participantsSheet;
    private Program program;

    public ExcelDataExtractorV2Impl(String fileName, byte[] fileContent) throws InvalidExcelFileException {
        this.fileName = fileName;
        this.fileContent = fileContent;

        this.workbook = ExcelParserUtils.getWorkbook(this.fileName, this.fileContent);

        this.eventSheet = workbook.getSheet("Event Details");
        this.participantsSheet = workbook.getSheet("Participants Details");

        if (this.eventSheet == null) {
            throw new InvalidExcelFileException("Event Details sheet not found");
        }

        // Read Program
        this.program = parseProgram();

       /* if (this.participantsSheet == null) {
            throw new InvalidExcelFileException("Participant Details sheet not found");
        }*/
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public List<Participant> getParticipantList() throws InvalidExcelFileException {
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
        Double numbericMobilePhone = participantRow.getCell(8, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
        participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()));
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
        SimpleDateFormat mmDDDYYformat = new SimpleDateFormat("dd-MMM-yy");
        Date welcomeCardDate = null;
        try {
            if (!"".equals(welcomeCardDateStr)) {
                welcomeCardDate = mmDDDYYformat.parse(welcomeCardDateStr);
            }
        } catch (ParseException e) {
            throw new InvalidExcelFileException("Unable to parse v2 file:[" + fileName + "] and Abhyasi Sheet ", e);
        }
        participant.setWelcomeCardDate(welcomeCardDate);
        participant.setRemarks(participantRow.getCell(18, Row.CREATE_NULL_AS_BLANK).toString());

        return participant;
    }

    private Program parseProgram() throws InvalidExcelFileException {

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
        SimpleDateFormat mmDDDYYformat = new SimpleDateFormat("dd-MMM-yy");
        Date eventDate = null;
        try {
            eventDate = mmDDDYYformat.parse(eventDateStr);
        } catch (ParseException e) {
            throw new InvalidExcelFileException("Unable to parse v2 file:[" + fileName + "]", e);
        }
        program.setProgramStartDate(eventDate);

        program.setEventState(eventSheet.getRow(4).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());

        program.setCoordinatorEmail(eventSheet.getRow(7).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
        program.setOrganizationContactName(eventSheet.getRow(9).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
        program.setOrganizationContactEmail(eventSheet.getRow(10).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
        Double numbericPhoneNumber = eventSheet.getRow(11).getCell(3, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
        program.setOrganizationContactMobile(String.valueOf(numbericPhoneNumber.longValue()));

        program.setWelcomeCardSignedByName(eventSheet.getRow(13).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());
        program.setWelcomeCardSignerIdCardNumber(eventSheet.getRow(14).getCell(3, Row.CREATE_NULL_AS_BLANK).toString());

        return program;
    }

}
