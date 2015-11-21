package org.srcm.heartfulness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelDataExtractor;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Created by vsonnathi on 11/19/15.
 */
@Service
public class PmpIngestionServiceImpl implements PmpIngestionService {

    @Autowired
    ProgramRepository programRepository;

    @Override
    @Transactional
    public void parseAndPersistExcelFile(String fileName, byte[] fileContent) throws InvalidExcelFileException {

        //Validate and Parse the excel file
        ExcelDataExtractor dataExtractor = ExcelParserUtils.getExcelDataExtractor(fileName, fileContent);

        //Persist the program
        programRepository.save(dataExtractor.getProgram());

    }
}
