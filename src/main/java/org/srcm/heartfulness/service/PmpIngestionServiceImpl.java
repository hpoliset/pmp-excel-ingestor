package org.srcm.heartfulness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelDataExtractor;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.util.Date;
import java.util.List;

/**
 * Created by vsonnathi on 11/19/15.
 */
@Service
public class PmpIngestionServiceImpl implements PmpIngestionService {

    @Autowired
    private ProgramRepository programRepository;

    @Override
    @Transactional
    public void parseAndPersistExcelFile(String fileName, byte[] fileContent) throws InvalidExcelFileException {

        //Validate and Parse the excel file
        ExcelDataExtractor dataExtractor = ExcelParserUtils.getExcelDataExtractor(fileName, fileContent);

        //Persist the program
        Program program = dataExtractor.getProgram();
        List<Participant> participantList = dataExtractor.getParticipantList();
        program.setParticipantList(participantList);

        programRepository.save(program);
    }

    @Override
    public void normalizeStagingRecords(Date batchProcessingTime) {

    }
}
