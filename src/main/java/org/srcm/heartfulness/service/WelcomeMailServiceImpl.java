package org.srcm.heartfulness.service;

import java.io.FileOutputStream;
import java.io.IOException;
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
		int validEmailSubscribersCount=0;
		String response = null;
		boolean flag=true;
		LOGGER.debug("partcipant size {}"+participants.size());
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
						LOGGER.debug("Error while adding subscriber - " + response);
						//sendyRestTemplate.sendErrorAlertMail();
						invalidParticipantSet.add(participant.getId());
					}
					else {
						validEmailSubscribersCount++;
						WelcomeMailDetails welcomeMailDetails = new WelcomeMailDetails();
						if(null!=sendySubscriber.getUserName() && !sendySubscriber.getUserName().isEmpty()){
							welcomeMailDetails.setPrintName(sendySubscriber.getUserName());
						}else{
							welcomeMailDetails.setPrintName("Friend");
						}
						welcomeMailDetails.setEmail(sendySubscriber.getEmail());
						welcomeMailDetails.setCreateTime(new Date());
						welcomeMailRepository.save(welcomeMailDetails);
					}
				}
			} 
			LOGGER.debug("Valid mail id count - "+validEmailSubscribersCount);
		}else {
			LOGGER.debug("No participant found.");
		}
		if (subscriberSet.size() >= 1) {
			LOGGER.debug("sending mail");
			response = sendyRestTemplate.sendWelcomeMail();
			if (response.equals("Campaign created and now sending")) {
				try {
					sendyRestTemplate.executeCronJob();
				} catch (HttpClientErrorException | IOException e) {
					LOGGER.debug("Error while executing cron job - " + e.getMessage());
				}
			} else {
				LOGGER.debug("Error while sending Mail - " + response);
				sendyRestTemplate.sendErrorAlertMail();
			}
		}
		for (Participant participant : participants) {
			if(participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)){
				if(!invalidParticipantSet.contains(participant.getId()))
					welcomeMailRepository.updateParticipantMailSentById(participant.getId());
			}
		}
		LOGGER.debug("Mail sent successfully.");
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
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd_MMM");
		String currentDate = dateTime.format(format);

		SendySubscriber sendySubscriber = null;
		List<SendySubscriber> subscriberList = new ArrayList<SendySubscriber>();
		List<Participant> participants = new ArrayList<Participant>();
		participants = welcomeMailRepository.getParticipantsToSendWelcomeEmails();
		int validEmailSubscribersCount = 0;
		StringBuilder sb = new StringBuilder();
		LOGGER.debug("Total partcipant size {}" + participants.size());
		if (null != participants && participants.size() >= 1) {
			for (Participant participant : participants) {
				if (null != participant.getEmail() && !participant.getEmail().isEmpty()
						&& participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
					int countOfEmailAvailableInWelcomeLog = welcomeMailRepository.checkForMailIdInWelcomeLog(participant.getEmail());
					sendySubscriber = new SendySubscriber();
					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					if (countOfEmailAvailableInWelcomeLog < 1) {
						sb.append(participant.getEmail() + System.lineSeparator());
						sendySubscriber.setIsCoOrdinatorInformed(0);
						validEmailSubscribersCount++;
					}else{
						sendySubscriber.setIsCoOrdinatorInformed(1);
					}
					subscriberList.add(sendySubscriber);
				}
			}
			LOGGER.debug("{} participants already received welcome mail.",participants.size()-validEmailSubscribersCount);
			LOGGER.debug("{} new participants." + validEmailSubscribersCount);
			if(validEmailSubscribersCount>0){
				FileOutputStream fop = new FileOutputStream(welcomeMailidsLocalFilepath + currentDate + "_"
						+ welcomeMailidsFileName);
				fop.write(sb.toString().getBytes());
				fop.close();
				LOGGER.debug("File copied to  " + welcomeMailidsLocalFilepath + currentDate + "_" + welcomeMailidsFileName);
				ftpConnectionHelper.processUpload(welcomeMailidsLocalFilepath, welcomeMailidsRemoteFilepath,
						welcomeMailidsFileName);
			}
			ftpConnectionHelper.sendNotificationForWelcomeEmails(validEmailSubscribersCount);
			if (null != subscriberList && subscriberList.size() >= 1) {
				for (SendySubscriber subscriber : subscriberList) {
					welcomeMailRepository.updateWelcomeMailLog(subscriber.getUserName(), subscriber.getEmail());
					welcomeMailRepository.updateParticipantByMailId(subscriber);
				}
				LOGGER.debug("Details updated to participant and welcome email log table for {} participants." ,subscriberList.size());
			}
		} else {
			LOGGER.debug("No participant found.");
			ftpConnectionHelper.sendNotificationForWelcomeEmails(0);
		}
	}

	/**
	 * Service method that will get the list of coordinators with 
	 * details of the participant count who have received welcome 
	 * emails,event name,coordinator name and send mails to the respective
	 *  coordinators with the details.
	 */
	@Override
	public void getCoordinatorListAndSendMail() {
		LOGGER.debug("START        :Getting coordinator list to send email noticafications");
		try{
			Map<String,List<String>> details = welcomeMailRepository.getCoordinatorWithEmailDetails();
			LOGGER.debug("            Total number of coordinators to send email is : "+details.size());
			if(!details.isEmpty()){
				LOGGER.debug("START        :Sending email notifications to the coordinator list");
				for(Map.Entry<String,List<String>> map:details.entrySet()){
					if(null != map.getKey()){
						if(!map.getKey().isEmpty()){
							if(map.getValue().get(3)!=null && !map.getValue().get(3).isEmpty()){
								try{
									int pctptCount = welcomeMailRepository.getPctptCountByPgrmId(map.getKey());
									int wlcmEmailRcvdPctptCount = welcomeMailRepository.wlcmMailRcvdPctptCount(map.getKey());
									LOGGER.debug("              :Total count of participant for event id "+map.getValue().get(3)+" is "+pctptCount );
									LOGGER.debug("              :Total count of participant who have received welcome email already "+wlcmEmailRcvdPctptCount);
									LOGGER.debug("START        :Sending email to "+map.getValue().get(3));
									CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
									coordinatorEmail.setCoordinatorEmail(map.getValue().get(3));
									coordinatorEmail.setCoordinatorName(map.getValue().get(2));
									coordinatorEmail.setEventName(map.getValue().get(1));
									coordinatorEmail.setTotalParticipantCount(String.valueOf(pctptCount));
									coordinatorEmail.setPctptAlreadyRcvdWlcmMailCount(String.valueOf(wlcmEmailRcvdPctptCount));
									coordinatorEmail.setPctptRcvdWlcmMailYstrdayCount(map.getValue().get(0));
									coordinatorEmail.setProgramCreateDate(map.getValue().get(4));
									sendEmailNotification.sendMailNotificationToCoordinator(coordinatorEmail);
									try{
										LOGGER.debug("START        :Inserting mail log details in table");
										PMPMailLog pmpMailLog = 
												new PMPMailLog(map.getKey(),
														map.getValue().get(3),EmailLogConstants.PCTPT_EMAIL_DETAILS,
														EmailLogConstants.STATUS_SUCCESS,null);
										mailLogRepository.createMailLog(pmpMailLog);
										LOGGER.debug("END        :Completed inserting mail log details in table");
									}catch(Exception ex){
										LOGGER.debug("END        :Exception while inserting mail log details in table");
									}
									LOGGER.debug("END        :Completed sending email to "+map.getValue().get(3));
								}catch(AddressException aex){
									try{
										PMPMailLog pmpMailLog = 
												new PMPMailLog(map.getKey(),
														map.getValue().get(3),EmailLogConstants.PCTPT_EMAIL_DETAILS,
														EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(aex));
										mailLogRepository.createMailLog(pmpMailLog);
									}catch(Exception ex){
										LOGGER.debug("EXCEPTION  :Failed to update mail log table");
									}
									LOGGER.debug("ADDRESS_EXCEPTION  :Failed to sent mail to" + map.getValue().get(3));
									LOGGER.debug("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
								}catch(MessagingException mex){
									try{
										PMPMailLog pmpMailLog = 
												new PMPMailLog(map.getKey(),
														map.getValue().get(3),EmailLogConstants.PCTPT_EMAIL_DETAILS,
														EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(mex));
										mailLogRepository.createMailLog(pmpMailLog);
									}catch(Exception ex){
										LOGGER.debug("EXCEPTION  :Failed to update mail log table");
									}
									LOGGER.debug("MESSAGING_EXCEPTION  :Failed to sent mail to" + map.getValue().get(3));
									LOGGER.debug("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
								}catch(Exception ex){
									try{
										PMPMailLog pmpMailLog = 
												new PMPMailLog(map.getKey(),
														map.getValue().get(3),EmailLogConstants.PCTPT_EMAIL_DETAILS,
														EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(ex));
										mailLogRepository.createMailLog(pmpMailLog);
									}catch(Exception exx){
										LOGGER.debug("EXCEPTION  :Failed to update mail log table");
									}
									LOGGER.debug("EXCEPTION  :Failed to sent mail to" + map.getValue().get(3));
									LOGGER.debug("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
								}
							}else{
								LOGGER.debug("MESSAGE: Coordinator email is empty,so email not triggered for the given programID : "+map.getKey());
							}
						}

					}
					LOGGER.debug("START        :Updating database column for the participants who have received welcome email for coordinator "+map.getValue().get(3));
					int upadateStatus = welcomeMailRepository.updateCoordinatorInformedStatus(map.getKey());
					if(upadateStatus > 0){
						LOGGER.debug("END        :Completed updating database column for the participant who have received welcome email for coordinator "+map.getValue().get(3));
					}else{
						LOGGER.debug("Failed to update database column for the participants who have received welcome email for coordinator"+map.getValue().get(3));
					}
				}
				LOGGER.debug("END        :Completed sending email notifications to the coordinator list");
			}else{
				LOGGER.debug("END        :No new participants found who have received welcome email");
			}

		}catch(EmptyResultDataAccessException ex){
			LOGGER.debug("EmptyResultDataAccessException        :No new participants found who have received welcome email");
			try{
				LOGGER.debug("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = 
						new PMPMailLog("",
								"",EmailLogConstants.PCTPT_EMAIL_DETAILS,
								EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(ex));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.debug("END        :Completed inserting mail log details in table");
			}catch(Exception e){
				LOGGER.debug("END        :Exception while inserting mail log details in table");
			}
		}catch(Exception ex){
			LOGGER.debug("EXCEPTION        :Failed to get the list of coordinators");
			try{
				LOGGER.debug("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = 
						new PMPMailLog("",
								"",EmailLogConstants.PCTPT_EMAIL_DETAILS,
								EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(ex));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.debug("END        :Completed inserting mail log details in table");
			}catch(Exception e){
				LOGGER.debug("END        :Exception while inserting mail log details in table");
			}
		}
	}

	@Override
	public void getGeneratedEwelcomeIdAndSendToCoordinators() {
		LOGGER.debug("Fetching co-ordinator details and e-welcomeID details..!");
		List<CoordinatorEmail> coordinatorEmails = new ArrayList<>();
		List<Integer> listOfParticipantId = new ArrayList<>();
		try{
			Map<CoordinatorEmail, List<Participant>> eWelcomeIdDetails = welcomeMailRepository.getGeneratedEwelcomeIdDetails();
			LOGGER.debug("Count of coordinators to send email - "+eWelcomeIdDetails.size());
			if(!eWelcomeIdDetails.isEmpty()){
				for(Entry<CoordinatorEmail, List<Participant>> map:eWelcomeIdDetails.entrySet()){
					//System.out.println("Iterating for - "+map.getKey().getCoordinatorEmail());
					if(null != map.getKey()){
						if(map.getKey().getCoordinatorEmail()!=null && !map.getKey().getCoordinatorEmail().isEmpty()){
							try{
								CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
								coordinatorEmail.setEventName(map.getKey().getEventName());
								coordinatorEmail.setCoordinatorName(map.getKey().getCoordinatorName());
								coordinatorEmail.setCoordinatorEmail(map.getKey().getCoordinatorEmail());
								//List<Participant> failedParticipants = participantRepository.getEWelcomeIdGenerationFailedPartcicipants(map.getKey().getProgramId());
								List<Participant> failedParticipants = new ArrayList<Participant>();
								LOGGER.debug("Failed participants : "+failedParticipants.size() + ", programID : "+map.getKey().getProgramId());
								List<Participant> eWelcomeIDParticipants = participantRepository.getEWelcomeIdGeneratedPartcicipants(map.getKey().getProgramId());
								LOGGER.debug("eWelcomeIDParticipants : "+eWelcomeIDParticipants.size() + ", programID : "+map.getKey().getProgramId());
								sendEmailNotification.sendGeneratedEwelcomeIdDetailslToCoordinator(coordinatorEmail,eWelcomeIDParticipants,failedParticipants);
								try{
									LOGGER.debug("Inserting log details in table.");
									PMPMailLog pmpMailLog = 
											new PMPMailLog(map.getKey().getProgramId(),
													map.getKey().getCoordinatorEmail(),EmailLogConstants.WLCMID_EMAIL_DETAILS,
													EmailLogConstants.STATUS_SUCCESS,null);
									mailLogRepository.createMailLog(pmpMailLog);
									LOGGER.debug("Completed inserting log details in table.");
								}catch(Exception ex){
									LOGGER.debug("Exception while inserting log details in table.");
								}
								for(Participant participant : map.getValue()){
									listOfParticipantId.add(participant.getId());
									//System.out.println("participant id "+participant.getId()+" inserted");
								}
								LOGGER.debug("E-mail sent to - "+map.getKey().getCoordinatorEmail());
							}catch(AddressException aex){
								try{
									PMPMailLog pmpMailLog = 
											new PMPMailLog(map.getKey().getProgramId(),
													map.getKey().getCoordinatorEmail(),EmailLogConstants.WLCMID_EMAIL_DETAILS,
													EmailLogConstants.STATUS_FAILED,aex.toString());
									mailLogRepository.createMailLog(pmpMailLog);
								}catch(Exception ex){
									LOGGER.debug("EXCEPTION  :Failed to update mail log table");
								}
								LOGGER.debug("ADDRESS_EXCEPTION  :Failed to sent mail to" + map.getKey().getCoordinatorEmail());
								LOGGER.debug("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
							}catch(MessagingException mex){
								try{
									PMPMailLog pmpMailLog = 
											new PMPMailLog(map.getKey().getProgramId(),
													map.getKey().getCoordinatorEmail(),EmailLogConstants.WLCMID_EMAIL_DETAILS,
													EmailLogConstants.STATUS_FAILED,mex.toString());
									mailLogRepository.createMailLog(pmpMailLog);
								}catch(Exception ex){
									LOGGER.debug("EXCEPTION  :Failed to update mail log table");
								}
								LOGGER.debug("MESSAGING_EXCEPTION  :Failed to sent mail to" + map.getKey().getCoordinatorEmail());
								LOGGER.debug("ADDRESS_EXCEPTION  :Looking for next coordinator if available");
							}catch(Exception ex){
								try{
									PMPMailLog pmpMailLog = 
											new PMPMailLog(map.getKey().getProgramId(),
													map.getKey().getCoordinatorEmail(),EmailLogConstants.WLCMID_EMAIL_DETAILS,
													EmailLogConstants.STATUS_FAILED,ex.toString());
									mailLogRepository.createMailLog(pmpMailLog);
								}catch(Exception exx){
									LOGGER.debug("EXCEPTION  :Failed to update mail log table");
								}
								LOGGER.debug("EXCEPTION - Failed to sent mail to" + map.getKey().getCoordinatorEmail());
								LOGGER.debug("ADDRESS_EXCEPTION - Looking for next coordinator if available");
							}
						}else{
							LOGGER.debug("Coordinator email is empty. Hence email not triggered for the programID : "+map.getKey().getProgramId());
						}
					}
					try {
						if (listOfParticipantId!=null && listOfParticipantId.size()>0) {
							for (Integer id : listOfParticipantId) {
								welcomeMailRepository.updateEwelcomeIDInformedStatus(id.toString());
							}
							LOGGER.debug("Details updated to participant table for {} participants." ,listOfParticipantId.size());
						}
					} catch (Exception e) {
						LOGGER.debug("Error while updating the database for participant- "+e.getMessage());
					}
				}
				LOGGER.debug("Completed sending eWelcome ID email notifications to the coordinator list.");
			}else{
				LOGGER.debug("No new e-welcome ID generated for participants.");
			}
		}catch(EmptyResultDataAccessException ex){
			LOGGER.debug("EmptyResultDataAccessException - No new e-welcome ID generated for participants."+ex.getMessage());
			try{
				LOGGER.debug("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = 
						new PMPMailLog("",
								"",EmailLogConstants.WLCMID_EMAIL_DETAILS,
								EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(ex));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.debug("END        :Completed inserting mail log details in table");
			}catch(Exception e){
				LOGGER.debug("END        :Exception while inserting mail log details in table");
			}
		}catch(Exception ex){
			LOGGER.debug("Exception while processing - "+ex.getMessage());
			try{
				LOGGER.debug("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = 
						new PMPMailLog("",
								"",EmailLogConstants.WLCMID_EMAIL_DETAILS,
								EmailLogConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(ex));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.debug("END        :Completed inserting mail log details in table");
			}catch(Exception e){
				LOGGER.debug("END        :Exception while inserting mail log details in table");
			}
		}
	}

}