package org.srcm.heartfulness;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StreamUtils;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PmpApplication.class)
@WebAppConfiguration
public class PmpApplicationTests {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProgramRepository programRepository;

	@Test
	public void parseAndPersistValidV21ExcelFile() throws IOException, InvalidExcelFileException {

        // start with clean slate.
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "program", "participant");

        String fileName = "v21ValidEventDate.xlsm";
        Resource v2ValidResource = resourceLoader.getResource("classpath:" + fileName);

        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
        Program validV21Program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1);
        List<Participant> participantList = validV21Program.getParticipantList();
        programRepository.save(validV21Program);

        //Validate that is stored.
        Program newProgram = programRepository.findById(validV21Program.getProgramId());
        Assert.assertNotNull("Could not find newly created object", newProgram);

        Assert.assertEquals("Should be same number of participants", newProgram.getParticipantList().size(), participantList.size());

        //Let's insert the excel file one more time.
        Program validV21ProgramDuplicate = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1);
        programRepository.save(validV21ProgramDuplicate);

        Assert.assertEquals("Should not have create a new row", validV21ProgramDuplicate.getProgramId(),
                validV21Program.getProgramId());

        // delete it ...
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "program", "participant");

    }

    @Test
    public void parseAndPersistDay1AndDay3() throws IOException, InvalidExcelFileException {
        // delete it ... start with a clean slate
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "program", "participant");

        String fileName = "HFN-DATA-MH-PUNE-INPSAD332-20151016.xlsx";
        Resource v2ValidResource = resourceLoader.getResource("classpath:" + fileName);

        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        Workbook workbook = ExcelParserUtils.getWorkbook(fileName, fileContent);
        Program validV21Program = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1);
        List<Participant> participantList = validV21Program.getParticipantList();
        validV21Program.setParticipantList(participantList);

        programRepository.save(validV21Program);

        //Validate that is stored.
        Program newProgram = programRepository.findById(validV21Program.getProgramId());
        Assert.assertNotNull("Could not find newly created object", newProgram);

        Assert.assertEquals("Should be same number of participants", newProgram.getParticipantList().size(), participantList.size());

        //Let's insert the excel file one more time.
        String day3FileName = "HFN-DATA-MH-PUNE-INPSAD332-20151018.xlsx";
        Resource v21Day3Resource = resourceLoader.getResource("classpath:" + day3FileName);
        byte[] v21Day3FileContent = StreamUtils.copyToByteArray(v21Day3Resource.getInputStream());
        workbook = ExcelParserUtils.getWorkbook(fileName, v21Day3FileContent);
        Program validV21ProgramDuplicate = ExcelDataExtractorFactory.extractProgramDetails(workbook, ExcelType.V2_1);
        List<Participant> participantListDuplicate = validV21ProgramDuplicate.getParticipantList();
        validV21ProgramDuplicate.setParticipantList(participantListDuplicate);
        programRepository.save(validV21ProgramDuplicate);

        Assert.assertEquals("Should not have create a new row", validV21ProgramDuplicate.getProgramId(),
                validV21Program.getProgramId());
        //Validate that is updated;
        Program updatedProgram = programRepository.findById(validV21Program.getProgramId());
        Participant participant = updatedProgram.getParticipantList().get(14);
        System.out.println("participant.getWelcomeCardDate() = " + participant.getWelcomeCardDate());

        // delete it ...
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "program", "participant");
    }

}
