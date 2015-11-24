package org.srcm.heartfulness;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StreamUtils;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelDataExtractor;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.io.IOException;
import java.util.List;

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
        String fileName = "v21ValidEventDate.xlsm";
        Resource v2ValidResource = resourceLoader.getResource("classpath:" + fileName);

        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        ExcelDataExtractor v21Extractor = ExcelParserUtils.getExcelDataExtractor(fileName, fileContent);
        Program validV21Program = v21Extractor.getProgram();
        List<Participant> participantList = v21Extractor.getParticipantList();
        validV21Program.setParticipantList(participantList);

        programRepository.save(validV21Program);

        //Validate that is stored.
        Program newProgram = programRepository.findById(validV21Program.getProgramId());
        Assert.assertNotNull("Could not find newly created object", newProgram);

        Assert.assertEquals("Should be same number of participants", newProgram.getParticipantList().size(), participantList.size());

        //Let's insert the excel file one more time.
        ExcelDataExtractor v21ExtractorDuplicate = ExcelParserUtils.getExcelDataExtractor(fileName, fileContent);
        Program validV21ProgramDuplicate = v21Extractor.getProgram();
        List<Participant> participantListDuplicate = v21Extractor.getParticipantList();
        validV21ProgramDuplicate.setParticipantList(participantListDuplicate);
        programRepository.save(validV21ProgramDuplicate);

        Assert.assertEquals("Should not have create a new row", validV21ProgramDuplicate.getProgramId(),
                validV21Program.getProgramId());

        // delete it ...
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "program", "participant");

    }

}
