package org.srcm.heartfulness.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.constants.HeartfulnessConstants;
import org.srcm.heartfulness.model.ExcelMetaData;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.OrganisationRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.util.ExcelDataExtractor;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.ExcelSheetValidator;
import org.srcm.heartfulness.util.ExcelSheetValidatorV1Impl;
import org.srcm.heartfulness.util.ExcelSheetValidatorV2Impl;
import org.srcm.heartfulness.util.InvalidExcelFileException;
import org.srcm.heartfulness.util.VersionVerification;

import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    private VersionVerification versionVerification;
    
    
    
    @Override
    @Transactional
	public ExcelMetaData parseAndPersistExcelFile(String fileName, byte[] fileContent){
		ExcelMetaData excelMetaData = new ExcelMetaData();
		excelMetaData.setFileName(fileName);
		excelMetaData.setFileSize(fileContent.length);
		List<String> errorResponse = new ArrayList<String>();
	    //Validate and Parse the excel file
		String version = versionVerification.validateExcelVersion(fileContent);
		errorResponse = validateExcelFile(fileContent,version);
		if(!errorResponse.isEmpty()){
			excelMetaData.setExcelVersion(version);
			excelMetaData.setErrorMsg(errorResponse);
			excelMetaData.setStatus(HeartfulnessConstants.FAILURE_STATUS);
			return excelMetaData;
		}else{
			try{
	    		ExcelDataExtractor dataExtractor = ExcelParserUtils.getExcelDataExtractor(fileName, fileContent,version);
		        //Persist the program
		        Program program = dataExtractor.getProgram();
		        List<Participant> participantList = dataExtractor.getParticipantList();
		        program.setParticipantList(participantList);
		        programRepository.save(program);
		        excelMetaData.setExcelVersion(version);
				excelMetaData.setErrorMsg(errorResponse);
				excelMetaData.setStatus(HeartfulnessConstants.SUCCESS_STATUS);
				return excelMetaData;
			}catch(InvalidExcelFileException ex){
				LOGGER.error(ex.getMessage());
				errorResponse.add(ex.getMessage());
				excelMetaData.setExcelVersion(version);
				excelMetaData.setErrorMsg(errorResponse);
				excelMetaData.setStatus(HeartfulnessConstants.FAILURE_STATUS);
				return excelMetaData;
			}
		}
	}

    @Override
//    every 15 minutes
//    @Scheduled(cron = "0 0/15 * * * *")
//    @Scheduled(cron = "0/5 * * * * *")
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
    
    @Override
	public List<String> validateExcelFile(byte[] excelContent,String version) {
		List<String> finalResponse = new ArrayList<String>();
		List<String> eventResponse = null ;  
		List<String> participantResponse = null;
		ExcelSheetValidator excelSheetValidator = null;
		if(version.equals(HeartfulnessConstants.VERSION_ONE)){
			try{
				excelSheetValidator = new ExcelSheetValidatorV1Impl();
				excelSheetValidator.initializeAll(excelContent);
				eventResponse	= excelSheetValidator.validateEventDetails();
				participantResponse = excelSheetValidator.validateParticipantDetails();
			}catch(IOException | InvalidFormatException ex){
				LOGGER.error(ex.getMessage());
				finalResponse.add(ex.getMessage());
			}
			if(!eventResponse.isEmpty()){
				finalResponse.addAll(eventResponse);
			} 
			if(!participantResponse.isEmpty()){
				finalResponse.addAll(participantResponse);
			}
			if(eventResponse.isEmpty() && participantResponse.isEmpty()){
				try{
					eventResponse = excelSheetValidator.checkEventMandatoryFields();
					participantResponse = excelSheetValidator.checkParticipantMandatoryFields();
				}catch(IOException ex){
					LOGGER.error(ex.getMessage());
					finalResponse.add(ex.getMessage());
				}
				if(!eventResponse.isEmpty()){
					finalResponse.addAll(eventResponse);
				} 
				if(!participantResponse.isEmpty()){
					finalResponse.addAll(participantResponse);
				}
			}
			return finalResponse;
		}else if(version.equals(HeartfulnessConstants.VERSION_TWO)){
			try {
				excelSheetValidator = new ExcelSheetValidatorV2Impl();
				excelSheetValidator.initializeAll(excelContent);
				eventResponse	= excelSheetValidator.validateEventDetails();
				participantResponse = excelSheetValidator.validateParticipantDetails();
			} catch (IOException | InvalidFormatException e) {
				LOGGER.error(e.getMessage());
				finalResponse.add(e.getMessage());
			}
			if(!eventResponse.isEmpty()){
				finalResponse.addAll(eventResponse);
			} 
			if(!participantResponse.isEmpty()){
				finalResponse.addAll(participantResponse);
			}
			if(eventResponse.isEmpty() && participantResponse.isEmpty()){
				try{
					eventResponse = excelSheetValidator.checkEventMandatoryFields();
					participantResponse = excelSheetValidator.checkParticipantMandatoryFields();
				}catch(IOException ex){
					LOGGER.error(ex.getMessage());
					finalResponse.add(ex.getMessage());
				}
				if(!eventResponse.isEmpty()){
					finalResponse.addAll(eventResponse);
				} 
				if(!participantResponse.isEmpty()){
					finalResponse.addAll(participantResponse);
				}
			}
			return finalResponse;
		}else{
			LOGGER.error(HeartfulnessConstants.INVALID_TEMPLATE_MSG);
			finalResponse.add(HeartfulnessConstants.INVALID_TEMPLATE_MSG);
			System.out.println("FL=="+finalResponse.size());
			return finalResponse;
		}
	}
    
}
