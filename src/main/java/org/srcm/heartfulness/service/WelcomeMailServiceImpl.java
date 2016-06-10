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
import java.util.Set;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EventConstants;
import org.srcm.heartfulness.helper.FTPConnectionHelper;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.model.json.response.EmailverificationResponse;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.WelcomeMailRepository;
import org.srcm.heartfulness.rest.template.SendyRestTemplate;

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
	SendMail sendEmailNotification;

	/**
	 * To fetch the participants from database and subscribe to the welcome mail
	 * subscribers sendy API list at scheduled time
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
		LOGGER.debug("partcipant size {}" + participants.size());
		Set<Integer> invalidParticipantSet = new HashSet<Integer>();
		if (participants.size() >= 1) {
			for (Participant participant : participants) {
				sendySubscriber = new SendySubscriber();
				participantCount = welcomeMailRepository.getIntroducedParticipantCount(participant.getPrintName(),
						participant.getEmail());
				if (participantCount < 1 && null != participant.getEmail() && !participant.getEmail().isEmpty()
						&& participant.getEmail().matches(EventConstants.EMAIL_REGEX)) {
					sendySubscriber.setNameToSendMail(getName(participant.getPrintName()));
					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					sendySubscriber.setParticipantId(participant.getId());
					subscriberSet.add(sendySubscriber);
					response = sendyRestTemplate.addNewSubscriber(sendySubscriber);
					flag = response.equals("1") ? true : response.equals("Already subscribed.") ? true : false;
					if (!flag) {
						LOGGER.debug("Error while adding subscriber - " + response);
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
			LOGGER.debug("Valid mail id count - " + validEmailSubscribersCount);
		} else {
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
			if (participant.getEmail().matches(EventConstants.EMAIL_REGEX)) {
				if (!invalidParticipantSet.contains(participant.getId()))
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
		if (null != printName && !printName.isEmpty()) {
			printName = printName.replace(".", " ");
			String[] name = printName.split(" ");
			if (name.length > 0) {
				for (int i = 0; i < name.length; i++) {
					if (name[i].length() > 2) {
						return name[i].substring(0, 1).toUpperCase() + name[i].substring(1);
					}
				}
			}
		} else {
			return "Friend";
		}
		return printName;
	}

	/**
	 * To fetch the participants from database and unsubscribe from the welcome
	 * mail subscribers sendy API list at scheduled time
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
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd_MMM_mm");
		String currentDate = dateTime.format(format);

		SendySubscriber sendySubscriber = null;
		List<SendySubscriber> subscriberList = new ArrayList<SendySubscriber>();
		List<Participant> participants = new ArrayList<Participant>();
		participants = welcomeMailRepository.getParticipantsToSendWelcomeEmails();
		int validEmailSubscribersCount = 0;
		StringBuilder sb = new StringBuilder();
		LOGGER.debug("partcipant size {}" + participants.size());
		if (null != participants && participants.size() >= 1) {
			for (Participant participant : participants) {
				if (null != participant.getEmail() && !participant.getEmail().isEmpty()
						&& participant.getEmail().matches(EventConstants.EMAIL_REGEX)) {
					sendySubscriber = new SendySubscriber();
					validEmailSubscribersCount++;
					sb.append(participant.getEmail() + System.lineSeparator());
					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					subscriberList.add(sendySubscriber);
				}
			}
			FileOutputStream fop = new FileOutputStream(welcomeMailidsLocalFilepath + currentDate + "_"
					+ welcomeMailidsFileName);
			fop.write(sb.toString().getBytes());
			fop.close();
			LOGGER.debug("File copied to  " + welcomeMailidsLocalFilepath + currentDate + "_" + welcomeMailidsFileName);
			LOGGER.debug("Valid email count- " + validEmailSubscribersCount);
			ftpConnectionHelper.processUpload(welcomeMailidsLocalFilepath, welcomeMailidsRemoteFilepath,
					welcomeMailidsFileName);

			ftpConnectionHelper.sendNotificationForWelcomeEmails(validEmailSubscribersCount);
			if (null != subscriberList && subscriberList.size() >= 1) {
				for (SendySubscriber subscriber : subscriberList) {
					welcomeMailRepository.updateWelcomeMailLog(subscriber.getUserName(), subscriber.getEmail());
					welcomeMailRepository.updateParticipantByMailId(subscriber.getEmail());
				}
				LOGGER.debug("Details updated to participant and welcome email log table.");
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
		LOGGER.debug("START		:Getting coordinator list to send email noticafications");

		try{
			Map<String,List<String>> details = welcomeMailRepository.getCoordinatorWithEmailDetails();
			LOGGER.debug("				Total number of coordinators to send email is : "+details.size());
			if(!details.isEmpty()){
				LOGGER.debug("START		:Sending email notifications to the coordinator list");
				for(Map.Entry<String,List<String>> map:details.entrySet()){
					sendEmailNotification.sendMailNotificationToCoordinator(map.getKey(),map.getValue().get(0),map.getValue().get(1),map.getValue().get(2));
				}
				LOGGER.debug("END		:Completed sending email notifications to the coordinator list");
				LOGGER.debug("START		:Updating is_coordinator_informed column for the participants who have received welcome email");
				int upadateStatus = welcomeMailRepository.updateCoordinatorInformedStatus();
				if(upadateStatus > 0){
					LOGGER.debug("END		:Completed updating is_coordinator_informed column for the participant who have received welcome email");
				}else{
					LOGGER.debug("Failed to update is_coordinator_informed column for the participants who have received welcome email");
				}
			}else{
				LOGGER.debug("END		:No new participants found who have received welcome email");
			}

		}catch(EmptyResultDataAccessException ex){
			LOGGER.debug("EmptyResultDataAccessException		:No new participants found who have received welcome email");
		}catch(Exception ex){
			LOGGER.debug("EXCEPTION		:Failed to get the list of coordinators");
		}
	}
}