package org.srcm.heartfulness.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.srcm.heartfulness.model.Program;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
