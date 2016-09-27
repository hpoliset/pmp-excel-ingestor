package org.srcm.heartfulness.webservice;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.helper.EWelcomeIDGenerationHelper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.service.APIAccessLogService;
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

	private static Logger LOGGER = LoggerFactory.getLogger(EWelcomeIDGenerationScheduler.class);

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
	// @RequestMapping(value = "generateewelcomeid", method =
	// RequestMethod.POST)
	@Scheduled(cron = "${welcome.mailids.generation.cron.time}")
	public void generateEWelcomeIDsForTheParticipants() {
		LOGGER.debug("START : CRON : EWELCOMEID GENERATION : Scheduler to generate EwelcomeID's for the participants started at - "
				+ new Date());
		List<Participant> participants = participantService.getParticipantListToGenerateEWelcomeID();
		LOGGER.debug("CRON : EWELCOMEID GENERATION : Total no. of partcipants to generate eWelcomeID : {} ",
				participants.size());
		PMPAPIAccessLog accessLog = null;
		int id = 0;
		for (Participant participant : participants) {
			try {
				accessLog = new PMPAPIAccessLog(null, null, "cron-to-generate-ewelcomeID",
						DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
						StackTraceUtils.convertPojoToJson(participant), null);
				id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
			} catch (Exception e) {

			}
			try {
				Program program = programService.getProgramById(participant.getProgramId());
				participant.setProgram(program);
				String eWelcomeID = programService.generateeWelcomeID(participant, id);
				if ("success".equalsIgnoreCase(eWelcomeID)) {
					try {
						LOGGER.debug(
								"CRON : EWELCOMEID GENERATION : eWelcomeID generated successfully to the participant : {}, SeqID:{}, EventID: {}, EwelcomeID: {} ",
								participant.getPrintName(), participant.getSeqId(), participant.getProgram()
										.getAutoGeneratedEventId(), participant.getWelcomeCardNumber());
						participant.setIntroducedBy(program.getCoordinatorEmail());
						participant.setIntroductionDate(new Date());
						participant.setIntroduced(1);
						participant.setEwelcomeIdRemarks(null);
						participant.setIsEwelcomeIdInformed(0);
						participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_COMPLETED_STATE);
						participantRepository.save(participant);
						LOGGER.debug("CRON : EWELCOMEID GENERATION :Participant details persisted successfully");
					} catch (Exception e) {
						LOGGER.debug("Exception while persisting participant details : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
					try {
						accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
						accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
								+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
								+ participant.getSeqId() + " ,EwelcomeID:" + participant.getWelcomeCardNumber()
								+ " ,Issued Date:" + participant.getWelcomeCardDate() + " ,EwelcomeID State:"
								+ participant.getEwelcomeIdState() + " ,EwelcomeID informed:"
								+ participant.getIsEwelcomeIdInformed());
						accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
						apiAccessLogService.updatePmpAPIAccessLog(accessLog);
						LOGGER.debug("CRON : EWELCOMEID GENERATION :Updated log successfully");
					} catch (Exception e) {
						LOGGER.debug("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
				} else {
					try {
						LOGGER.debug(
								"CRON : EWELCOMEID GENERATION : eWelcomeID generation failed to the participant : {} ,SeqID:{}, EventID: {},EwelcomeID remarks: {} ",
								participant.getPrintName(), participant.getSeqId(), participant.getProgram()
										.getAutoGeneratedEventId(), eWelcomeID);
						participant.setEwelcomeIdRemarks(eWelcomeID);
						participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
						participant.setIsEwelcomeIdInformed(0);
						participantRepository.save(participant);
						LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Participant details persisted successfully");
					} catch (Exception e) {
						LOGGER.debug("Exception while persisting participant details : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
					try {
						accessLog.setStatus(ErrorConstants.STATUS_FAILED);
						accessLog.setErrorMessage(eWelcomeID);
						accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
								+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
								+ participant.getSeqId() + " ,EwelcomeID State:" + participant.getEwelcomeIdState()
								+ " ,EwelcomeID informed:" + participant.getIsEwelcomeIdInformed());
						accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
						apiAccessLogService.updatePmpAPIAccessLog(accessLog);
						LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Updated log successfully");
					} catch (Exception e) {
						LOGGER.debug("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
				}

			} catch (Exception e) {
				LOGGER.debug("Scheduler to generate EwelcomeID's : Exception :  {} ",
						StackTraceUtils.convertStackTracetoString(e));
				/*try {
					LOGGER.debug(
							"CRON : EWELCOMEID GENERATION : Error while generating EWelcomeID for the participant :"
									+ " SeqID : {} , Name : {} , Email : {} ", participant.getSeqId(),
							participant.getPrintName(), participant.getEmail());
					LOGGER.debug("Scheduler to generate EwelcomeID's : Exception :  {} ",
							StackTraceUtils.convertStackTracetoString(e));
					participant.setEwelcomeIdRemarks(e.getMessage());
					participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
					participant.setIsEwelcomeIdInformed(0);
					participantRepository.save(participant);
					LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Participant details persisted successfully");
				} catch (Exception ex) {
					LOGGER.debug("Exception while persisting participant details : {} ",
							StackTraceUtils.convertPojoToJson(ex));
				}*/
				try {
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson(e));
					accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
							+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:" + participant.getSeqId()
							+ " ,EwelcomeID State:" + participant.getEwelcomeIdState() + " ,EwelcomeID informed:"
							+ participant.getIsEwelcomeIdInformed());
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Updated log successfully");
					LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Updated log successfully");
				} catch (Exception ex) {
					LOGGER.debug("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(ex));
				}
			}

		}
		LOGGER.debug("END : CRON : EWELCOMEID GENERATION : Scheduler to generate EwelcomeID's for the participants completed at - "
				+ new Date());
	}

}
