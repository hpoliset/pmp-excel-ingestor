/**
 * 
 */
package org.srcm.heartfulness.util;

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
import org.srcm.heartfulness.constants.HeartfulnessConstants;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

/**
 * @author koustavd
 *
 */
public class ExcelDataExtractorV1Impl implements ExcelDataExtractor{
	
	static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataExtractorV1Impl.class);
	private  String fileName;
    private  byte[] fileContent;
    private  Workbook workbook;
    private  Sheet sheet;
    private Program program;

    public ExcelDataExtractorV1Impl(String fileName, byte[] fileContent) throws InvalidExcelFileException {
		this.fileName = fileName;
	    this.fileContent = fileContent;
	    this.workbook = ExcelParserUtils.getWorkbook(this.fileName, this.fileContent);
	    this.sheet = workbook.getSheet(HeartfulnessConstants.V1_SHEET_NAME);
	    this.program = parseProgram();
	}

	@Override
	public Program getProgram() throws InvalidExcelFileException {
		 return program;
	}

	@Override
	public List<Participant> getParticipantList() throws InvalidExcelFileException {
		List<Participant> participantList = new ArrayList<Participant>();
        int totalRows = sheet.getPhysicalNumberOfRows();
        for (int i=15; i < totalRows; i++) {
	    	 Row currentRow = sheet.getRow(i);
	         Participant participant = parseParticipantRow(currentRow);
	         if(participant.getExcelSheetSequenceNumber() == 0){
	        	 participant.setExcelSheetSequenceNumber(i-14);
	         }
	         if ("".equals(participant.getPrintName())) {
	             break;
	         }
	         participantList.add(participant);
        }
		return participantList;
	}

	
	private Participant parseParticipantRow(Row currentRow) throws InvalidExcelFileException {
		Participant participant = new Participant();
		if(!currentRow.getCell(0,Row.CREATE_NULL_AS_BLANK).toString().isEmpty()){
			double seqNo = Double.valueOf(currentRow.getCell(0,Row.CREATE_NULL_AS_BLANK).toString());
			int integerSeq = (int)seqNo;
			participant.setExcelSheetSequenceNumber(integerSeq);
		}
		participant.setPrintName(currentRow.getCell(1,Row.CREATE_NULL_AS_BLANK).toString());
		participant.setCity(currentRow.getCell(2,Row.CREATE_NULL_AS_BLANK).toString());
		participant.setState(currentRow.getCell(3,Row.CREATE_NULL_AS_BLANK).toString());
		participant.setEmail(currentRow.getCell(4,Row.CREATE_NULL_AS_BLANK).toString());
		Cell phoneCell = currentRow.getCell(5, Row.CREATE_NULL_AS_BLANK);
        try {
            Double numbericMobilePhone = phoneCell.getNumericCellValue();
            participant.setMobilePhone(String.valueOf(numbericMobilePhone.longValue()));
        } catch (Exception e) {
            LOGGER.error("Participant mobile phone number is not numeric, trying as string");
            participant.setMobilePhone(phoneCell.toString());
        }
		participant.setProfession(currentRow.getCell(6,Row.CREATE_NULL_AS_BLANK).toString());
		if(currentRow.getCell(7,Row.CREATE_NULL_AS_BLANK).toString().equals("YES")){
			participant.setIntroduced(1);
		}else{
			participant.setIntroduced(0);
		}
		String introducedDateStr = currentRow.getCell(8,Row.CREATE_NULL_AS_BLANK).toString();
		Date introducedDate = null;
		try {
			introducedDate = DateUtils.parseDate(introducedDateStr);
		} catch (ParseException e) {
			LOGGER.error("Not able to parse program event date:[" + introducedDateStr + "]");
			throw new InvalidExcelFileException("Not able to parse program event date:[" + introducedDateStr + "]");
		}
		participant.setIntroductionDate(introducedDate);
		participant.setIntroducedBy(currentRow.getCell(9,Row.CREATE_NULL_AS_BLANK).toString());
		participant.setRemarks(currentRow.getCell(10,Row.CREATE_NULL_AS_BLANK).toString());
		return participant;
	}


	private Program parseProgram() throws InvalidExcelFileException {
		System.out.println("INFO : Started to parse data for altered 1.0 template.");
		Program program = new Program();
		program.setProgramChannel(sheet.getRow(3).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorName(sheet.getRow(4).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setCoordinatorEmail(sheet.getRow(5).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventPlace(sheet.getRow(6).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventState(sheet.getRow(7).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setEventCountry(sheet.getRow(8).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationName(sheet.getRow(9).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		program.setOrganizationWebSite(sheet.getRow(10).getCell(2, Row.CREATE_NULL_AS_BLANK).toString());
		String eventDateStr = sheet.getRow(11).getCell(2, Row.CREATE_NULL_AS_BLANK).toString();
        Date eventDate = null;
        try {
           eventDate = DateUtils.parseDate(eventDateStr);
        } catch (ParseException e) {
           LOGGER.error("Not able to parse program event date:[" + eventDateStr + "]");
           throw new InvalidExcelFileException("Not able to parse program event date:[" + eventDateStr + "]");
        }
        program.setProgramStartDate(eventDate);
        System.out.println("INFO : Parsing complete for altered 1.0 template.");
		return program;
	}
}
