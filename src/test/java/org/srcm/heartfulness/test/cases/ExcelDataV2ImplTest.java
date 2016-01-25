package org.srcm.heartfulness.test.cases;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.PmpApplication;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.assertion.PmpAssertTest;
import org.srcm.heartfulness.helper.PmpApplicationHelper;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * @author Koustav Dutta
 *
 */

@SpringApplicationConfiguration(classes = PmpApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelDataV2ImplTest {
	@Autowired
	PmpApplicationHelper helper;

	@Autowired
	PmpAssertTest pmpAssertTest;

	@Autowired
	PmpIngestionService pmpIngestionService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * Validates template v2.1 and persists the extracted data into database 
	 * and also checks for data de-duplicaton.
	 * @throws InvalidExcelFileException
	 * @throws IOException
	 */
	@Test
	@Transactional
	@Rollback(true)
	public void parseAndPersistValidV2ExcelData() throws InvalidExcelFileException, IOException{
		String fileName = "Validv2ExcelFile.xlsx";
		byte[] fileContent = helper.getValidExcelWorkbook(fileName);
		ExcelUploadResponse response = pmpIngestionService.parseAndPersistExcelFile(fileName, fileContent);
		pmpAssertTest.assertValidExcelFile(response.getErrorMsg());
		int initialParticipantCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "participant");
		//Lets extract and persist the same excel again
		pmpIngestionService.parseAndPersistExcelFile(fileName, fileContent);
		int finalParticipantCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "participant");
		pmpAssertTest.validateDataDeduplication(initialParticipantCount,finalParticipantCount);
	}

	/**
	 * Validates the structure for invalid v2.1 template and returns the error responses.
	 * @throws InvalidExcelFileException
	 * @throws IOException
	 */
	@Test
	public void invalidV2StructureValidation() throws InvalidExcelFileException, IOException{
		String fileName = "InvalidStructure_v2.xlsx";
		byte[] fileContent = helper.getValidExcelWorkbook(fileName);
		ExcelUploadResponse response = pmpIngestionService.parseAndPersistExcelFile(fileName, fileContent);
		List<String> errorResponse = response.getErrorMsg();
		pmpAssertTest.validateV2Structure(errorResponse);
	}

	/**
	 * Validates the mandatory parameters for invalid v1.0 template and returns the error responses.
	 * @throws InvalidExcelFileException
	 * @throws IOException
	 */
	@Test
	public void invalidV2MandatoryParametersValidation() throws InvalidExcelFileException, IOException{
		String fileName = "InvalidMandatoryParams_v2.xlsx";
		byte[] fileContent = helper.getValidExcelWorkbook(fileName);
		ExcelUploadResponse response = pmpIngestionService.parseAndPersistExcelFile(fileName, fileContent);
		List<String> errorResponse = response.getErrorMsg();
		pmpAssertTest.validateV2MandatoryParameters(errorResponse);
	}
}
