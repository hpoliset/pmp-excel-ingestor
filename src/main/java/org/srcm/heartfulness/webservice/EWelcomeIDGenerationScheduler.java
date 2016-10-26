package org.srcm.heartfulness.webservice;

import java.util.ArrayList;
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
	//@RequestMapping(value = "generateewelcomeid", method = RequestMethod.POST)
	@Scheduled(cron = "${welcome.mailids.generation.cron.time}")
	public void generateEWelcomeIDsForTheParticipants() {
		LOGGER.info("START : CRON : EWELCOMEID GENERATION : Scheduler to generate EwelcomeID's for the participants started at - "
				+ new Date());
		List<Participant> participants=new ArrayList<Participant>();
		try{
			participants = participantService.getParticipantListToGenerateEWelcomeID();
		}catch(Exception e){
			LOGGER.error("Error While fetching participants from DB : Exception : {} ",StackTraceUtils.convertStackTracetoString(e));
		}
		LOGGER.info("CRON : EWELCOMEID GENERATION : Total no. of partcipants to generate eWelcomeID : {} ",
				participants.size());
		for (Participant participant : participants) {
			PMPAPIAccessLog accessLog = null;
			int id = 0;
			try {
				accessLog = new PMPAPIAccessLog(null, null, "cron-to-generate-ewelcomeID",
						DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
						StackTraceUtils.convertPojoToJson(participant), null);
				id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
			} catch (Exception e) {
				LOGGER.error("Error while inserting data into pmp api access log table. Exception: {} ",StackTraceUtils.convertStackTracetoString(e));
			}
			LOGGER.info("CRON : EWELCOMEID GENERATION : partcipant {} : loggerID:{} ",participant.getPrintName(),id);
			try {
				Program program = programService.getProgramById(participant.getProgramId());
				participant.setProgram(program);
				accessLog.setUsername(program.getCoordinatorEmail());
				String isvalid=programService.validatePreceptorIDCardNumber(program, id);
				if (null == isvalid) {
					String eWelcomeID = programService.generateeWelcomeID(participant, id);
					if ("success".equalsIgnoreCase(eWelcomeID)) {
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
							LOGGER.error("Exception while inserting PMP API log details in table : {} ",
									StackTraceUtils.convertPojoToJson(e));
						}
					} else {
						eWelcomeID = transformErrorMsg(eWelcomeID);
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
							LOGGER.error("Exception while persisting participant details : {} ",
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
							LOGGER.error("Exception while inserting PMP API log details in table : {} ",
									StackTraceUtils.convertPojoToJson(e));
						}
					}
				}else{
					try {
						LOGGER.debug(
								"CRON : EWELCOMEID GENERATION : eWelcomeID generation failed to the participant : {} ,SeqID:{}, EventID: {},EwelcomeID remarks: {} ",
								participant.getPrintName(), participant.getSeqId(), participant.getProgram()
								.getAutoGeneratedEventId(), isvalid);
						participant.setEwelcomeIdRemarks(isvalid);
						participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
						participant.setIsEwelcomeIdInformed(0);
						participantRepository.save(participant);
						LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Participant details persisted successfully");
					} catch (Exception e) {
						LOGGER.error("Exception while persisting participant details : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
					try {
						accessLog.setStatus(ErrorConstants.STATUS_FAILED);
						accessLog.setErrorMessage(isvalid);
						accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
								+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
								+ participant.getSeqId() + " ,EwelcomeID State:" + participant.getEwelcomeIdState()
								+ " ,EwelcomeID informed:" + participant.getIsEwelcomeIdInformed());
						accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
						apiAccessLogService.updatePmpAPIAccessLog(accessLog);
						LOGGER.debug("CRON : EWELCOMEID GENERATION : Failed case: Updated log successfully");
					} catch (Exception e) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
				}
			} catch (Exception e) {
				LOGGER.error("Scheduler to generate EwelcomeID's : Exception :  {} ",
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
				} catch (Exception ex) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(ex));
				}
			}

		}
		LOGGER.info("END : CRON : EWELCOMEID GENERATION : Scheduler to generate EwelcomeID's for the participants completed at - "
				+ new Date());
	}
	
	
	
	private String transformErrorMsg(String eWelcomeID) {
		String[] eWelcomeIDParts = eWelcomeID.split(" - ");
		if(eWelcomeIDParts[0].equalsIgnoreCase(ErrorConstants.EWELCOMEID_DUPLICATE_RECORD_RESPONSE_FROM_MYSRCM)){
			eWelcomeIDParts[0]=ErrorConstants.EWELCOMEID_DUPLICATE_RECORD_CUSTOMIZED_RESPONSE;
			eWelcomeID=eWelcomeIDParts[0]+eWelcomeIDParts[1];
		}
		return eWelcomeID;
	}

}
