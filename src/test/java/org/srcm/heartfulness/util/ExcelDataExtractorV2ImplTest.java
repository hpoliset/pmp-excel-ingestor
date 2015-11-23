package org.srcm.heartfulness.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.srcm.heartfulness.PmpApplication;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

import java.io.IOException;
import java.util.List;

/**
 * Created by vsonnathi on 11/17/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PmpApplication.class)
public class ExcelDataExtractorV2ImplTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void testValidV2ExcelFile() throws IOException, InvalidExcelFileException {
        String fileName = "v21ValidEventDate.xlsm";
        Resource v2ValidResource = resourceLoader.getResource("classpath:" + fileName);

        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        ExcelDataExtractor v2Extractor = new ExcelDataExtractorV2Impl(fileName, fileContent);
        Program program = v2Extractor.getProgram();
        Assert.notNull(program, "Not able to parse valid V21 file: [" + fileName + "]");
        System.out.println("compute hash: " + program.computeHashCode());
        System.out.println("program = " + program);

        List<Participant> participantList = v2Extractor.getParticipantList();
        Assert.notEmpty(participantList, "Not able read particpants");
    }

    @Test(expected = InvalidExcelFileException.class)
    public void testInValidV2ExcelFile() throws IOException, InvalidExcelFileException {
        String fileName = "v21InValidEventDate.xlsm";
        Resource v2ValidResource = resourceLoader.getResource("classpath:" + fileName);

        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        ExcelDataExtractor v2Extractor = new ExcelDataExtractorV2Impl(fileName, fileContent);
    }
}
