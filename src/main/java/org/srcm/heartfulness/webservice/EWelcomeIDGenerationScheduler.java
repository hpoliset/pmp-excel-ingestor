package org.srcm.heartfulness.webservice;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.helper.EWelcomeIDGenerationHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.PmpIngestionServiceImpl;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * 
 * @author himasreev
 *
 */
@RestController
public class EWelcomeIDGenerationScheduler {

	private static Logger LOGGER = LoggerFactory.getLogger(PmpIngestionServiceImpl.class);

	@Autowired
	PmpParticipantService participantService;

	@Autowired
	ParticipantRepository participantRepository;

	@Autowired
	EWelcomeIDGenerationHelper eWelcomeIDGenerationHelper;

	@Autowired
	ProgramService programService;
	
	@Autowired
	APIAccessLogService apiAccessLogService;

	/**
	 * Cron to generate EWelcomeIDs for the participants.
	 */
	//@RequestMapping(value = "generateewelcomeid", method = RequestMethod.POST)
	@Scheduled(cron = "${welcome.mailids.generation.cron.time}")
	public void generateEWelcomeIDsForTheParticipants() {
		LOGGER.debug("START : Scheduler to generate EwelcomeID's for the participants started at - " + new Date());
		List<Participant> participants = participantService.getParticipantListToGenerateEWelcomeID();
		LOGGER.debug("Scheduler to generate EwelcomeID's : Total no. of partcipants to generate eWelcomeID : {} ",
				participants.size());
		PMPAPIAccessLog accessLog =null;
		for (Participant participant : participants) {
			try {
				accessLog = new PMPAPIAccessLog(null, null, "cron-to-generate-ewelcomeID",
						DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
						StackTraceUtils.convertPojoToJson(participant), null);
				int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
				Program program= programService.getProgramById(participant.getProgramId());
				participant.setProgram(program);
				String eWelcomeID = programService.generateeWelcomeID(participant, id);
				if ("success".equalsIgnoreCase(eWelcomeID)) {
					participant.setIntroducedBy(program.getCoordinatorEmail());
					participant.setIntroductionDate(new Date());
					participant.setIntroduced(1);
					participant.setEwelcomeIdRemarks(null);
					participant.setIsEwelcomeIdInformed(0);
					participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_COMPLETED_STATE);
					participantRepository.save(participant);
					LOGGER.debug(
							"Scheduler to generate EwelcomeID's : eWelcomeID generated successfully to the participant : {} ",
							participant.getPrintName());
					accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(participant));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				}else{
					participant.setEwelcomeIdRemarks(eWelcomeID);
					participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
					participant.setIsEwelcomeIdInformed(0);
					participantRepository.save(participant);
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setErrorMessage(eWelcomeID);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(participant));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				}
			} catch (Exception e) {
				LOGGER.debug(
						"Scheduler to generate EwelcomeID's : Error while generating EWelcomeID for the participant :"
								+ " SeqID : {} , Name : {} , Email : {} ", participant.getSeqId(),
						participant.getPrintName(), participant.getEmail());
				LOGGER.debug("Scheduler to generate EwelcomeID's : Exception :  {} ",
						StackTraceUtils.convertStackTracetoString(e));
				participant.setEwelcomeIdRemarks(e.getMessage());
				participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
				participant.setIsEwelcomeIdInformed(0);
				participantRepository.save(participant);
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson(e));
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(participant));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			}
		}
		LOGGER.debug("END : Scheduler to generate EwelcomeID's for the participants completed at - " + new Date());
	}

}
