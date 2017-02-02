/**
 * 
 */
package org.srcm.heartfulness.test.cases;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.PmpApplication;
import org.srcm.heartfulness.assertion.PmpAssertTest;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.helper.PmpApplicationHelper;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * @author Koustav Dutta
 *
 */

@SpringApplicationConfiguration(classes = PmpApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParticipantDeduplicationTest {

	@Autowired
	PmpApplicationHelper helper;

	@Autowired
	PmpAssertTest pmpAssertTest;

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	private String eWelcomeIdCheckbox = "off";

	/**
	 * This method is used to create new records in db and reupload again
	 * to match the count of participants which should be same.
	 * @throws InvalidExcelFileException
	 * @throws IOException
	 */
	@Test
	@Transactional
	public void parseAndPersistValidV2ExcelData() throws InvalidExcelFileException, IOException{

		// start with clean slate.
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "program", "participant");

		String fileName = "v2excelsheet.xlsx";
		byte[] fileContent = helper.getExcelContent(fileName);
		Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
		List<String> listOfErrors = helper.validateExcelStructure(workbook,ExcelType.V2_1);

		//All the mandatory fields should be filled and excel sheet structure should be ok.
		pmpAssertTest.assertValidExcelFile(listOfErrors);

		//Extract event and participant details and persist
		Program program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1,eWelcomeIdCheckbox);
		List<Participant> participantList = program.getParticipantList();
		programRepository.save(program);

		//get the uploaded event
		Program uploadedProgram = programRepository.findById(program.getProgramId());

		//Count of participants should match what I have uploaded
		pmpAssertTest.validateParticipantCount(participantList.size(), uploadedProgram.getParticipantList().size());

		//Let's insert the excel file one more time.
		Program sameProgram = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1,eWelcomeIdCheckbox);
		programRepository.save(sameProgram);

		//Get the latest participant count
		int pctptCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "participant");

		//Count of participants with similar name,email and mobile number should be same 
		pmpAssertTest.validateParticipantCount(sameProgram.getParticipantList().size(), pctptCount);
		
		updatePctptWithSameNameAndRowNumber();
		updatePctptWithSameNameAndEmail();
		updatePctptWithSameNameAndMobile();
		createNewRecords();
	}


	/**
	 * In this method we have modified email and mobile number of two participants,
	 * but since the row number and name is same the participant count should be same.
	 * Instead of adding new participant , it should update existing records.
	 * @throws IOException 
	 * @throws InvalidExcelFileException
	 */
	//@Test
	public void updatePctptWithSameNameAndRowNumber() throws IOException, InvalidExcelFileException{

		String fileName = "ParticipantWithSameNameAndRownumber.xlsx";

		byte[] fileContent = helper.getExcelContent(fileName);
		Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
		List<String> listOfErrors = helper.validateExcelStructure(workbook,ExcelType.V2_1);

		//All the mandatory fields should be filled and excel sheet structure should be ok.
		pmpAssertTest.assertValidExcelFile(listOfErrors);

		//Extract event and participant details and persist
		Program program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1,eWelcomeIdCheckbox);
		List<Participant> participantList = program.getParticipantList();
		programRepository.save(program);
		
		//We have modified email and mobile number of two participants but since row number is same so participant count should be same
		Program updatedProgram = programRepository.findById(program.getProgramId());
		
		//Expected participant count should match
		pmpAssertTest.validateParticipantCount(updatedProgram.getParticipantList().size(), 5);

	}


	/**
	 * In this method we have updated the mobile number of 3 participants and interchanged rows also
	 * but since the name and email is same so participant record should be updated instead of 
	 * a new record.
	 * @throws IOException
	 * @throws InvalidExcelFileException
	 */
	//@Test
	public void updatePctptWithSameNameAndEmail() throws IOException, InvalidExcelFileException{

		String fileName = "ParticipantWithSameNameAndEmail.xlsx";

		byte[] fileContent = helper.getExcelContent(fileName);
		Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
		List<String> listOfErrors = helper.validateExcelStructure(workbook,ExcelType.V2_1);

		//All the mandatory fields should be filled and excel sheet structure should be ok.
		pmpAssertTest.assertValidExcelFile(listOfErrors);

		//Extract event and participant details and persist
		Program program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1,eWelcomeIdCheckbox);
		List<Participant> participantList = program.getParticipantList();
		programRepository.save(program);

		//We have modified mobile number of three participants but since name and email same so participant count should be same
		Program updatedProgram = programRepository.findById(program.getProgramId());

		//Expected participant count should match
		pmpAssertTest.validateParticipantCount(updatedProgram.getParticipantList().size(), 5);


	}


	/**
	 * In this method we have modified email of some 2 participant and interchanged the rows 
	 * also but since name and mobile number are same so it should update existing participants.
	 * @throws IOException
	 * @throws InvalidExcelFileException
	 */
	//@Test
	public void updatePctptWithSameNameAndMobile() throws IOException, InvalidExcelFileException{

		String fileName = "ParticipantWithSameNameAndMobile.xlsx";

		byte[] fileContent = helper.getExcelContent(fileName);
		Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
		List<String> listOfErrors = helper.validateExcelStructure(workbook,ExcelType.V2_1);

		//All the mandatory fields should be filled and excel sheet structure should be ok.
		pmpAssertTest.assertValidExcelFile(listOfErrors);

		//Extract event and participant details and persist
		Program program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1,eWelcomeIdCheckbox);
		programRepository.save(program);

		//We have modified email of two participants but since name and mobile number are same so participant count should be same
		Program updatedProgram = programRepository.findById(program.getProgramId());

		//Expected participant count should match
		pmpAssertTest.validateParticipantCount(updatedProgram.getParticipantList().size(), 5);

	}

	/**
	 * Here the excel sheet contains a new record, with new name,email,mobile number so the 
	 * count of participant should increase by one.
	 * @throws IOException
	 * @throws InvalidExcelFileException
	 */
	//@Test
	public void createNewRecords() throws IOException, InvalidExcelFileException{
		
		String fileName = "v2excelsheet_newrecord.xlsx";

		byte[] fileContent = helper.getExcelContent(fileName);
		Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
		List<String> listOfErrors = helper.validateExcelStructure(workbook,ExcelType.V2_1);

		//All the mandatory fields should be filled and excel sheet structure should be ok.
		pmpAssertTest.assertValidExcelFile(listOfErrors);

		//Extract event and participant details and persist
		Program program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1,eWelcomeIdCheckbox);
		programRepository.save(program);

		//We have added a new record in the same excel sheet so count should increase by 1
		Program updatedProgram = programRepository.findById(program.getProgramId());

		//Expected participant count should match
		pmpAssertTest.validateParticipantCount(updatedProgram.getParticipantList().size(), 6);
	}


}
