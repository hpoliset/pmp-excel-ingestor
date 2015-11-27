package org.srcm.heartfulness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    static Logger LOGGER = LoggerFactory.getLogger(PmpIngestionServiceImpl.class);

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
//    every 15 minutes
    @Scheduled(cron = "0 0/15 * * * *")
//    @Scheduled(cron = "0/5 * * * * *")
    public void normalizeStagingRecords() {

        // Find out all the program records that are updated after the batchProcessingTime
        LOGGER.info("normalizedStagingRecords ... invoked at:[" + new Date() + "]");

        // Find all program objects that have been modified since last normalized run.



    }
}
