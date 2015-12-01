package org.srcm.heartfulness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.OrganisationRepository;
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

    @Autowired
    private OrganisationRepository organisationRepository;

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

    //every 15 minutes
    @Override
    @Scheduled(cron = "0 0/15 * * * *")
    public void normalizeStagingRecords() {

        // Find out all the program records that are updated after the batchProcessingTime
        LOGGER.info("normalizedStagingRecords ... invoked at:[" + new Date() + "]");

        // Find all program objects that have been modified since last normalized run.
        List<Integer> programIds = programRepository.findUpdatedProgramIdsSince(new Date());
        for (Integer programId : programIds) {
            Program program = programRepository.findById(programId);
            normalizeProgram(program);
        }
    }

    private void normalizeProgram(Program program) {
        // Look up Organisation based on name and address_line1
        Organisation organisation = organisationRepository.findByNameAndWebsite(program.getOrganizationName(),
                program.getOrganizationWebSite());
    }
}
