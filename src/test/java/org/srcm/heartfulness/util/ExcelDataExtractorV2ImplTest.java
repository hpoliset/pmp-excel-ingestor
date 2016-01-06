package org.srcm.heartfulness.util;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.srcm.heartfulness.PmpApplication;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.excelupload.transformer.impl.ExcelDataExtractorV2Impl;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by vsonnathi on 11/17/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PmpApplication.class)
public class ExcelDataExtractorV2ImplTest {
    static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataExtractorV2ImplTest.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void testValidV2ExcelFile() throws IOException, InvalidExcelFileException {
        String fileName = "HFN-DATA-MH-PUNE-INPSAD332-20151016.xlsx";

        String resourceName = "classpath:" + fileName;
        parseFile(resourceName);
    }

    private void parseFile(String resourceName) throws IOException, InvalidExcelFileException {
        Resource v2ValidResource = resourceLoader.getResource(resourceName);

        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        Workbook workbook = ExcelParserUtils.getWorkbook(resourceName, fileContent);
        Program program =  ExcelDataExtractorFactory.extractProgramDetails(workbook,ExcelType.V2_1);
        Assert.notNull(program, "Not able to parse valid V21 file: [" + resourceName + "]");
        System.out.println("compute hash: " + program.computeHashCode());
        System.out.println("program = " + program);
        List<Participant> participantList = program.getParticipantList();
       // Assert.notEmpty(participantList, "Not able read participants");
    }

    @Test(expected = InvalidExcelFileException.class)
    public void testInValidV2ExcelFile() throws IOException, InvalidExcelFileException {
        String invalidFileName = "v21InvalidEventDate.xlsm";
        Resource v2ValidResource = resourceLoader.getResource("classpath:" + invalidFileName);
        byte[] fileContent = StreamUtils.copyToByteArray(v2ValidResource.getInputStream());
        Workbook workbook = ExcelParserUtils.getWorkbook(invalidFileName, fileContent);
        ExcelDataExtractor v2Extractor = new ExcelDataExtractorV2Impl();
        v2Extractor.extractExcel(workbook);
    }

   /* @Test
    public void testValidSample1FromField () throws IOException, InvalidExcelFileException {
        // Read from the directory.

        Files.walk(Paths.get("/Users/vsonnathi/final/testHFNExcelFiles")).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                try {
                    parseFile("file://"+ filePath.toString());
                } catch (IOException e) {
                    LOGGER.error("Could not parse file: [" + filePath + "]", e);
                } catch (InvalidExcelFileException e) {
                    LOGGER.error("Could not parse file: [" + filePath + "]", e);
                }
                LOGGER.info("Done with: [" + filePath + "]");
            }
        });

    }*/
}
