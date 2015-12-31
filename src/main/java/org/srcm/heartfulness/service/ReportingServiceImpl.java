package org.srcm.heartfulness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractor;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.OrganisationRepository;
import org.srcm.heartfulness.repository.ParticipantFullDetailsRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by vsonnathi on 11/19/15.
 */
@Service
public class ReportingServiceImpl implements ReportingService {

    static Logger LOGGER = LoggerFactory.getLogger(ReportingServiceImpl.class);

    @Autowired
    private ParticipantFullDetailsRepository participantFullDetailsRepository ;

    @Override
    @Transactional
    public Collection<ParticipantFullDetails> getParticipantsByChannel(String channel) {

    	
    	return participantFullDetailsRepository.findByChannel(channel);
    	
        //Validate and Parse the excel file
        //ExcelDataExtractor dataExtractor = ExcelParserUtils.getExcelDataExtractor(fileName, fileContent);

        //Persist the program
        //Program program = dataExtractor.getProgram();
        //List<Participant> participantList = dataExtractor.getParticipantList();
        //program.setParticipantList(participantList);

        //programRepository.save(program);
    }
}
