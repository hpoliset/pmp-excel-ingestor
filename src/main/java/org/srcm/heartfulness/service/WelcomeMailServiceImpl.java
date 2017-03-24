package org.srcm.heartfulness.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.helper.FTPConnectionHelper;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.repository.WelcomeMailRepository;
import org.srcm.heartfulness.rest.template.SendyRestTemplate;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

@Service
public class WelcomeMailServiceImpl implements WelcomeMailService {

	private static Logger LOGGER = LoggerFactory.getLogger(WelcomeMailServiceImpl.class);

	@Autowired
	SendyRestTemplate sendyRestTemplate;

	@Autowired
	private WelcomeMailRepository welcomeMailRepository;

	@Autowired
	private SendMail sendEmailNotification;

	@Value("${welcome.mailids.filename}")
	private String welcomeMailidsFileName;

	@Value("${welcome.mailids.local.filepath}")
	private String welcomeMailidsLocalFilepath;

	@Value("${welcome.mailids.remote.filepath}")
	private String welcomeMailidsRemoteFilepath;

	@Autowired
	private FTPConnectionHelper ftpConnectionHelper;

	@Autowired
	private ParticipantRepository participantRepository;

	@Autowired
	private MailLogRepository mailLogRepository;

	@Autowired
	private ProgramRepository programRepository;

	/**
	 * To fetch the participants from database and subscribe to the welcome mail
	 * subscribers list at scheduled time
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws HttpClientErrorException
	 * @throws MessagingException
	 */
	@Override
	public void addNewSubscriber() throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException, MessagingException {

		SendySubscriber sendySubscriber = null;
		List<Participant> participants = new ArrayList<Participant>();
		participants = welcomeMailRepository.getParticipantsToSendWelcomeMail();
		Set<SendySubscriber> subscriberSet = new HashSet<SendySubscriber>();
		int participantCount = 0;
		int validEmailSubscribersCount = 0;
		String response = null;
		boolean flag = true;
		LOGGER.info("partcipant size {}" + participants.size());
		Set<Integer> invalidParticipantSet = new HashSet<Integer>();
		if (participants.size() >= 1) {
			for (Participant participant : participants) {
				sendySubscriber = new SendySubscriber();
				participantCount = welcomeMailRepository.getIntroducedParticipantCount(participant.getPrintName(),
						participant.getEmail());
				if (participantCount < 1 && null != participant.getEmail() && !participant.getEmail().isEmpty()
						&& participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
					sendySubscriber.setNameToSendMail(getName(participant.getPrintName()));
					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					sendySubscriber.setParticipantId(participant.getId());
					subscriberSet.add(sendySubscriber);
					response = sendyRestTemplate.addNewSubscriber(sendySubscriber);
					flag = response.equals("1") ? true : response.equals("Already subscribed.") ? true : false;
					if (!flag) {
						LOGGER.info("Error while adding subscriber - " + response);
						// sendyRestTemplate.sendErrorAlertMail();
						invalidParticipantSet.add(participant.getId());
					} else {
						validEmailSubscribersCount++;
						WelcomeMailDetails welcomeMailDetails = new WelcomeMailDetails();
						if (null != sendySubscriber.getUserName() && !sendySubscriber.getUserName().isEmpty()) {
							welcomeMailDetails.setPrintName(sendySubscriber.getUserName());
						} else {
							welcomeMailDetails.setPrintName("Friend");
						}
						welcomeMailDetails.setEmail(sendySubscriber.getEmail());
						welcomeMailDetails.setCreateTime(new Date());
						welcomeMailRepository.save(welcomeMailDetails);
					}
				}
			}
			LOGGER.info("Valid mail id count - " + validEmailSubscribersCount);
		} else {
			LOGGER.info("No participant found.");
		}
		if (subscriberSet.size() >= 1) {
			LOGGER.info("sending mail");
			response = sendyRestTemplate.sendWelcomeMail();
			if (response.equals("Campaign created and now sending")) {
				try {
					sendyRestTemplate.executeCronJob();
				} catch (HttpClientErrorException | IOException e) {
					LOGGER.info("Error while executing cron job - " + e.getMessage());
				}
			} else {
				LOGGER.info("Error while sending Mail - " + response);
				sendyRestTemplate.sendErrorAlertMail();
			}
		}
		for (Participant participant : participants) {
			if (participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
				if (!invalidParticipantSet.contains(participant.getId()))
					welcomeMailRepository.updateParticipantMailSentById(participant.getId());
			}
		}
		LOGGER.info("Mail sent successfully.");
	}

	/**
	 * To add salitation to the print name
	 * 
	 * @param printName
	 * @return the salitatetd print name
	 */
	private String getName(String printName) {
		printName = printName.replace(".", " ");
		String[] name = printName.split(" ");
		if (name.length > 0) {
			for (int i = 0; i < name.length; i++) {
				if (name[i].length() > 2 && !name[i].equalsIgnoreCase("mrs") && !name[i].equalsIgnoreCase("smt")) {
					return name[i].substring(0, 1).toUpperCase() + name[i].substring(1).toLowerCase();
				}
			}
		}
		return printName;
	}

	/**
	 * To fetch the participants from database and unsubscribe from the welcome
	 * mail subscribers list at scheduled time
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws HttpClientErrorException
	 */
	@Override
	public void unsubscribeUsers() throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException {
		List<WelcomeMailDetails> subscribers = new ArrayList<WelcomeMailDetails>();
		subscribers = welcomeMailRepository.getSubscribersToUnsubscribe();
		if (subscribers.size() >= 1) {
			for (WelcomeMailDetails subscriber : subscribers) {
				sendyRestTemplate.unsubscribeUser(subscriber);
			}
		}
	}

	/**
	 * To upload list of welcome mail ids as a file to the FTP
	 * 
	 * @throws IOException
	 * @throws JSchException
	 * @throws SftpException
	 */
	@Override
	public void uploadParticipantEmailidsToFTP() throws IOException, JSchException, SftpException {
		sendEmailNotification.sendNotificationToInformProcessExecution(EmailLogConstants.FTP_UPLOAD_DETAILS);
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd_MMM");
		String currentDate = dateTime.format(format);

		SendySubscriber sendySubscriber = null;
		List<SendySubscriber> subscriberList = new ArrayList<SendySubscriber>();
		List<Participant> participants = new ArrayList<Participant>();
		participants = welcomeMailRepository.getParticipantsToSendWelcomeEmails();
		int validEmailSubscribersCount = 0;
		StringBuilder sb = new StringBuilder();
		LOGGER.info("Total partcipant size {}", participants.size());
		if (null != participants && participants.size() >= 1) {
			for (Participant participant : participants) {
				if (null != participant.getEmail() && !participant.getEmail().isEmpty()
						&& participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
					int countOfEmailAvailableInWelcomeLog = welcomeMailRepository
							.checkForMailIdInWelcomeLog(participant.getEmail());
					sendySubscriber = new SendySubscriber();
					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					if (countOfEmailAvailableInWelcomeLog < 1) {
						sb.append(participant.getEmail() + System.lineSeparator());
						sendySubscriber.setIsCoOrdinatorInformed(0);
						validEmailSubscribersCount++;
					} else {
						sendySubscriber.setIsCoOrdinatorInformed(1);
					}
					subscriberList.add(sendySubscriber);
				}
			}
			LOGGER.info("{} participants already received welcome mail.", participants.size()
					- validEmailSubscribersCount);
			LOGGER.info("{} new participants.", validEmailSubscribersCount);
			if (validEmailSubscribersCount > 0) {
				FileOutputStream fop = new FileOutputStream(welcomeMailidsLocalFilepath + currentDate + "_"
						+ welcomeMailidsFileName);
				fop.write(sb.toString().getBytes());
				fop.close();
				LOGGER.info("File copied to  " + welcomeMailidsLocalFilepath + currentDate + "_"
						+ welcomeMailidsFileName);
				ftpConnectionHelper.processUpload(welcomeMailidsLocalFilepath, welcomeMailidsRemoteFilepath,
						welcomeMailidsFileName);
			}
			ftpConnectionHelper.sendNotificationForWelcomeEmails(validEmailSubscribersCount);
			if (null != subscriberList && subscriberList.size() >= 1) {
				for (SendySubscriber subscriber : subscriberList) {
					welcomeMailRepository.updateWelcomeMailLog(subscriber.getUserName(), subscriber.getEmail());
					welcomeMailRepository.updateParticipantByMailId(subscriber);
				}
				LOGGER.info("Details updated to participant and welcome email log table for {} participants.",
						subscriberList.size());
			}
		} else {
			LOGGER.info("No participant found.");
			ftpConnectionHelper.sendNotificationForWelcomeEmails(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.WelcomeMailService#
	 * getCoordinatorListAndSendMail()
	 */
	@Override
	public void getCoordinatorListAndSendMail() {
		LOGGER.info("START        :Getting coordinator list to send email noticafications");
		sendEmailNotification.sendNotificationToInformProcessExecution(EmailLogConstants.WELCOME_MAIL_DETAILS);
		try {
			Map<String, List<String>> details = welcomeMailRepository.getCoordinatorWithEmailDetails();
			LOGGER.info("            Total number of coordinators to send email is : " + details.size());
			if (!details.isEmpty()) {
				LOGGER.info("START        :Sending email notifications to the coordinator list");
				Session session = sendEmailNotification.getSession();
				for (Map.Entry<String, List<String>> map : details.entrySet()) {
					if (null != map.getKey()) {
						if (!map.getKey().isEmpty()) {
							try{
								if (map.getValue().get(3) != null && !map.getValue().get(3).isEmpty()) {
									int pctptCount = welcomeMailRepository.getPctptCountByPgrmId(map.getKey());
									int wlcmEmailRcvdPctptCount = welcomeMailRepository.wlcmMailRcvdPctptCount(map
											.getKey());
									LOGGER.info("              :Total count of participant for event id "
											+ map.getValue().get(3) + " is " + pctptCount);
									LOGGER.info("              :Total count of participant who have received welcome email already "
											+ wlcmEmailRcvdPctptCount);
									LOGGER.info("START        :Sending email to " + map.getValue().get(3));
									CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
									coordinatorEmail.setCoordinatorEmail(map.getValue().get(3));
									coordinatorEmail.setCoordinatorName(map.getValue().get(2));
									coordinatorEmail.setEventName(map.getValue().get(1));
									coordinatorEmail.setTotalParticipantCount(String.valueOf(pctptCount));
									coordinatorEmail.setPctptAlreadyRcvdWlcmMailCount(String
											.valueOf(wlcmEmailRcvdPctptCount));
									coordinatorEmail.setPctptRcvdWlcmMailYstrdayCount(map.getValue().get(0));
									SimpleDateFormat inputsdf = new SimpleDateFormat(ExpressionConstants.SQL_DATE_FORMAT);
									coordinatorEmail.setProgramCreateDate(null != map.getValue().get(4) ?  inputsdf.parse(map.getValue().get(4)) :null);
									coordinatorEmail.setEventPlace(map.getValue().get(5));
									coordinatorEmail.setEventCity(map.getValue().get(6));
									coordinatorEmail.setProgramCreationDate(null != map.getValue().get(7) ? inputsdf.parse(map.getValue().get(7)) :null);
									try{
										sendEmailNotification.sendMailNotificationToCoordinator(coordinatorEmail,session);
										LOGGER.debug("START        :Inserting mail log details in table");
										PMPMailLog pmpMailLog = new PMPMailLog(map.getKey(), map.getValue().get(3),
												EmailLogConstants.PCTPT_EMAIL_DETAILS, EmailLogConstants.STATUS_SUCCESS,
												null);
										mailLogRepository.createMailLog(pmpMailLog);
										LOGGER.info("END        :Completed sending email to " + map.getValue().get(3));
									} catch (AddressException aex) {
										PMPMailLog pmpMailLog = new PMPMailLog(map.getKey(), map.getValue().get(3),
												EmailLogConstants.PCTPT_EMAIL_DETAILS, EmailLogConstants.STATUS_FAILED,
												StackTraceUtils.convertStackTracetoString(aex));
										mailLogRepository.createMailLog(pmpMailLog);
										LOGGER.error("ADDRESS_EXCEPTION  :Failed to sent mail to" + map.getValue().get(3) +" :Exception : {}",  aex.getMessage());
										LOGGER.error("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
									} 
								} else {
									LOGGER.info("MESSAGE: Coordinator email is empty,so email not triggered for the given programID : "
											+ map.getKey());
								}

								LOGGER.info("START        :Updating database column for the participants who have received welcome email for coordinator "
										+ map.getValue().get(3));
								int upadateStatus = welcomeMailRepository.updateCoordinatorInformedStatus(map.getKey());
								if (upadateStatus > 0) {
									LOGGER.info("END        :Completed updating database column for the participant who have received welcome email for coordinator "
											+ map.getValue().get(3));
								} else {
									LOGGER.info("Failed to update database column for the participants who have received welcome email for coordinator"
											+ map.getValue().get(3));
								}
							} catch(Exception ex){
								PMPMailLog pmpMailLog = new PMPMailLog(map.getKey(), map.getValue().get(3),
										EmailLogConstants.PCTPT_EMAIL_DETAILS, EmailLogConstants.STATUS_FAILED,
										StackTraceUtils.convertStackTracetoString(ex));
								mailLogRepository.createMailLog(pmpMailLog);
								LOGGER.error("EXCEPTION  :Failed to sent mail to" + map.getValue().get(3)+" :Exception : {}",  ex.getMessage());
								LOGGER.error("EXCEPTION  :Looking for next coordinator if available");
							}
							 Thread.sleep(5000);
						}
					}
				}
				LOGGER.info("END        :Completed sending email notifications to the coordinator list");
			} else {
				LOGGER.info("END        :No new participants found who have received welcome email");
			}

		} catch (EmptyResultDataAccessException ex) {
			LOGGER.error("EmptyResultDataAccessException        :No new participants found who have received welcome email");
			PMPMailLog pmpMailLog = new PMPMailLog("", "", EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception ex) {
			LOGGER.error("EXCEPTION        :Failed to send mail to coordinators : {} ",ex);
			PMPMailLog pmpMailLog = new PMPMailLog("", "", EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
			mailLogRepository.createMailLog(pmpMailLog);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.WelcomeMailService#
	 * getGeneratedEwelcomeIdAndSendToCoordinators()
	 */
	@Override
	public void getGeneratedEwelcomeIdAndSendToCoordinators() {
		LOGGER.info("Fetching co-ordinator details and e-welcomeID details..!");
		sendEmailNotification.sendNotificationToInformProcessExecution(EmailLogConstants.WLCMID_EMAIL_DETAILS);
		try {
			LOGGER.info("Total count of coordinators available in DB with is ewelcome id informed as active - "
					+ welcomeMailRepository.getCountofIsWelcomeIdInformedcordinators());
			Map<CoordinatorEmail, List<Participant>> eWelcomeIdDetails = welcomeMailRepository
					.getGeneratedEwelcomeIdDetails();
			LOGGER.info("Count of coordinators to send email - " + eWelcomeIdDetails.size());
			if (!eWelcomeIdDetails.isEmpty()) {
				Session session = sendEmailNotification.getSession();
				for (Entry<CoordinatorEmail, List<Participant>> map : eWelcomeIdDetails.entrySet()) {
					LOGGER.info("Event: {} ,Coordinatoremail : {} ", map.getKey().getEventID(), map.getKey()
							.getCoordinatorEmail());
					List<Integer> listOfParticipantId = new ArrayList<>();
					if (null != map.getKey()) {
						try{
							if (map.getKey().getCoordinatorEmail() != null && !map.getKey().getCoordinatorEmail().isEmpty()) {
								CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
								coordinatorEmail.setEventName(map.getKey().getEventName());
								coordinatorEmail.setCoordinatorName(map.getKey().getCoordinatorName());
								coordinatorEmail.setCoordinatorEmail(map.getKey().getCoordinatorEmail());
								coordinatorEmail.setProgramId(map.getKey().getProgramId());
								coordinatorEmail.setEventID(map.getKey().getEventID());
								coordinatorEmail.setEventCity(map.getKey().getEventCity());
								coordinatorEmail.setEventPlace(map.getKey().getEventPlace());
								coordinatorEmail.setProgramCreateDate(map.getKey().getProgramCreateDate());
								coordinatorEmail.setProgramCreationDate(map.getKey().getProgramCreationDate());
								List<Participant> failedParticipants = participantRepository
										.getEWelcomeIdGenerationFailedParticipants(map.getKey().getProgramId());
								LOGGER.info("Failed participants : " + failedParticipants.size() + ", EventID : "
										+ map.getKey().getEventID());
								List<Participant> eWelcomeIDParticipants = participantRepository
										.getEWelcomeIdGeneratedParticipants(map.getKey().getProgramId());
								LOGGER.info("eWelcomeIDParticipants : " + eWelcomeIDParticipants.size()
										+ ", EventID : " + map.getKey().getEventID());
								for (Participant failedParticipant : failedParticipants) {
									listOfParticipantId.add(failedParticipant.getId());
									// System.out.println("participant id "+participant.getId()+" inserted");
								}
								for (Participant eWelcomeIDParticipant : eWelcomeIDParticipants) {
									listOfParticipantId.add(eWelcomeIDParticipant.getId());
									// System.out.println("participant id "+participant.getId()+" inserted");
								}
								try {
									sendEmailNotification.sendGeneratedEwelcomeIdDetailslToCoordinator(coordinatorEmail,
											eWelcomeIDParticipants, failedParticipants,session);
									PMPMailLog pmpMailLog = new PMPMailLog(map.getKey().getProgramId(), map.getKey()
											.getCoordinatorEmail(), EmailLogConstants.WLCMID_EMAIL_DETAILS,
											EmailLogConstants.STATUS_SUCCESS, null);
									mailLogRepository.createMailLog(pmpMailLog);
									LOGGER.info("E-mail sent to - " + map.getKey().getCoordinatorEmail());
								} catch (AddressException aex) {
									PMPMailLog pmpMailLog = new PMPMailLog(map.getKey().getProgramId(), map.getKey()
											.getCoordinatorEmail(), EmailLogConstants.WLCMID_EMAIL_DETAILS,
											EmailLogConstants.STATUS_FAILED, aex.toString());
									mailLogRepository.createMailLog(pmpMailLog);
									LOGGER.error("ADDRESS_EXCEPTION  :Failed to sent mail to {} :Exception : {}", map
											.getKey().getCoordinatorEmail(), aex.getMessage());
									LOGGER.error("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
								}
							} else {
								LOGGER.info("Coordinator email is empty. Hence email not triggered for the programID : ",
										map.getKey().getProgramId());
							}
							try {
								if (listOfParticipantId != null && listOfParticipantId.size() > 0) {
									for (Integer id : listOfParticipantId) {
										welcomeMailRepository.updateEwelcomeIDInformedStatus(id.toString());
									}
									LOGGER.info("Details updated to participant table for {} participants.",
											listOfParticipantId.size());
								}
							} catch (Exception e) {
								LOGGER.error("Error while updating the database for participant- " + e.getMessage());
							}
						}catch (Exception ex) {
							PMPMailLog pmpMailLog = new PMPMailLog(map.getKey().getProgramId(), map.getKey()
									.getCoordinatorEmail(), EmailLogConstants.WLCMID_EMAIL_DETAILS,
									EmailLogConstants.STATUS_FAILED, ex.toString());
							mailLogRepository.createMailLog(pmpMailLog);
							LOGGER.error("EXCEPTION  :Failed to sent mail to {} :Exception : {} ", map
									.getKey().getCoordinatorEmail(), ex.getMessage());
							LOGGER.error("EXCEPTION - Looking for next coordinator if available");
						}
						 Thread.sleep(5000);
					}
				}
				LOGGER.info("Completed sending eWelcome ID email notifications to the coordinator list.");
			} else {
				LOGGER.info("No new e-welcome ID generated for participants.");
			}
		} catch (EmptyResultDataAccessException ex) {
			LOGGER.error("EmptyResultDataAccessException - No new e-welcome ID generated for participants - {}" ,  StackTraceUtils.convertStackTracetoString(ex));
			PMPMailLog pmpMailLog = new PMPMailLog("", "", EmailLogConstants.WLCMID_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception ex) {
			LOGGER.error("Exception while processing - {} " ,  StackTraceUtils.convertStackTracetoString(ex));
			PMPMailLog pmpMailLog = new PMPMailLog("", "", EmailLogConstants.WLCMID_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
			mailLogRepository.createMailLog(pmpMailLog);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.WelcomeMailService#sendWelcomeMailToHfnList
	 * ()
	 */
	@Override
	public void sendWelcomeMailToHfnList() {
		sendEmailNotification.sendNotificationToInformProcessExecution(EmailLogConstants.WELCOME_MAIL_TO_PARTICIPANTS);
		try {
			sendEmailNotification.sendWelcomeMail();
			LOGGER.info("Welcome mail sent successfully to the list.");
		} catch (UnsupportedEncodingException | MessagingException | ParseException e) {
			LOGGER.error("Error while sending mail to list - " + e.getMessage());
		}
	}

}