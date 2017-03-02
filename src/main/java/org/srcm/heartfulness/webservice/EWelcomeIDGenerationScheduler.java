package org.srcm.heartfulness.webservice;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.helper.EWelcomeIDGenerationHelper;
import org.srcm.heartfulness.mail.SendMail;
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
 * Controller for managing ewelcomeId related functionalities.
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
	
	@Autowired
	SendMail sendMail;

	/**
	 * Cron to generate EWelcomeIDs for the participants.
	 */
	@Scheduled(cron = "${welcome.mailids.generation.cron.time}") 
	public void generateEWelcomeIDsForParticipants() {
		LOGGER.info("START : CRON : EWELCOMEID GENERATION : Scheduler to generate EwelcomeID's for the participants started at - "
				+ new Date());
		sendMail.sendNotificationToInformProcessExecution(EmailLogConstants.EWELCOME_ID_GENERATION);
		List<Integer> programIds = participantRepository.getProgramIDsToGenerateEwelcomeIds();
		LOGGER.info("CRON : EWELCOMEID GENERATION : Total no. of Events to generate eWelcomeID : {} ",
				programIds.size());
		if (!programIds.isEmpty() && programIds.size() > 0) {
			for (Integer programId : programIds) {
				List<Participant> participants = participantRepository
						.getParticipantwithProgramIdToGenerateEwelcomeId(programId);
				Program program = programService.getProgramDetailsToGenerateEwelcomeIDById(programId);
				LOGGER.info(
						"CRON : EWELCOMEID GENERATION : EventID: {} - Total no. of participants to generate eWelcomeID : {} ",
						program.getAutoGeneratedEventId(), participants.size());
				for (Participant participant : participants) {
					participant.setProgram(program);
					PMPAPIAccessLog accessLog = null;
					accessLog = new PMPAPIAccessLog(null, null, "cron-to-generate-ewelcomeID",
							DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
							StackTraceUtils.convertPojoToJson(participant), null);
					int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
					try {
						accessLog.setUsername(program.getCoordinatorEmail());
						participant.setProgram(program);
						String isvalid = null;
						if (0 == program.getFirstSittingBy()) {
							isvalid = programService.validatePreceptorIDCardNumber(program, id);
						}
						if (null == isvalid) {
							String eWelcomeID = programService.generateeWelcomeID(participant, id);
							if ("success".equalsIgnoreCase(eWelcomeID)) {
								accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
								accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
										+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
										+ participant.getSeqId() + " ,EwelcomeID:" + participant.getWelcomeCardNumber()
										+ " ,Issued Date:" + participant.getWelcomeCardDate() + " ,EwelcomeID State:"
										+ participant.getEwelcomeIdState() + " ,EwelcomeID informed:"
										+ participant.getIsEwelcomeIdInformed());
								accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
								apiAccessLogService.updatePmpAPIAccessLog(accessLog);
							} else {
								eWelcomeID = transformErrorMsg(eWelcomeID);
								try {
									participant.setEwelcomeIdRemarks(eWelcomeID);
									participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
									participant.setIsEwelcomeIdInformed(0);
									participantRepository.updateParticipantEwelcomeIDDetails(participant);
								} catch (Exception e) {
									LOGGER.error("Exception while persisting participant details : {} ",
											StackTraceUtils.convertPojoToJson(e));
								}
								accessLog.setStatus(ErrorConstants.STATUS_FAILED);
								accessLog.setErrorMessage(eWelcomeID);
								accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
										+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
										+ participant.getSeqId() + " ,EwelcomeID State:"
										+ participant.getEwelcomeIdState() + " ,EwelcomeID informed:"
										+ participant.getIsEwelcomeIdInformed());
								accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
								apiAccessLogService.updatePmpAPIAccessLog(accessLog);
							}
						} else {
							try {
								for (Participant participant2 : participants) {
									participant2.setEwelcomeIdRemarks(isvalid);
									participant2.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
									participant2.setIsEwelcomeIdInformed(0);
									participantRepository.updateParticipantEwelcomeIDDetails(participant2);
								}
								break;
							} catch (Exception e) {
								LOGGER.error("Exception while persisting participant details : {} ",
										StackTraceUtils.convertPojoToJson(e));
							}
							accessLog.setStatus(ErrorConstants.STATUS_FAILED);
							accessLog.setErrorMessage(isvalid);
							accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
									+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
									+ participant.getSeqId() + " ,EwelcomeID State:" + participant.getEwelcomeIdState()
									+ " ,EwelcomeID informed:" + participant.getIsEwelcomeIdInformed());
							accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
							apiAccessLogService.updatePmpAPIAccessLog(accessLog);
						}
					} catch (Exception e) {
						LOGGER.error("Scheduler to generate EwelcomeID's : Exception :  {} ",
								StackTraceUtils.convertStackTracetoString(e));
						accessLog.setStatus(ErrorConstants.STATUS_FAILED);
						accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson(e));
						accessLog.setResponseBody("Email:" + participant.getEmail() + " ,EventID:"
								+ participant.getProgram().getAutoGeneratedEventId() + " ,SeqId:"
								+ participant.getSeqId() + " ,EwelcomeID State:" + participant.getEwelcomeIdState()
								+ " ,EwelcomeID informed:" + participant.getIsEwelcomeIdInformed());
						accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
						apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					}

				}
			}
			LOGGER.info("END : CRON : EWELCOMEID GENERATION : Scheduler to generate EwelcomeID's for the participants completed at - "
					+ new Date());
		} else {
			LOGGER.info("END : CRON : EWELCOMEID GENERATION : No participants available to generate EwelcomeID's on - "
					+ new Date());
		}
	}

	/**
	 * Method to parse and transform MYSRCM error message.
	 * 
	 * @param eWelcomeID
	 * @return
	 */
	private String transformErrorMsg(String eWelcomeID) {
		String[] eWelcomeIDParts = eWelcomeID.split(" - ");
		if (eWelcomeIDParts[0].equalsIgnoreCase(ErrorConstants.EWELCOMEID_DUPLICATE_RECORD_RESPONSE_FROM_MYSRCM)) {
			eWelcomeIDParts[0] = ErrorConstants.EWELCOMEID_DUPLICATE_RECORD_CUSTOMIZED_RESPONSE;
			eWelcomeID = eWelcomeIDParts[0] + eWelcomeIDParts[1];
		}
		return eWelcomeID;
	}

}
