package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EventConstants;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.WelcomeMailRepository;
import org.srcm.heartfulness.rest.template.SendyRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class WelcomeMailServiceImpl implements WelcomeMailService {

	private static Logger LOGGER = LoggerFactory.getLogger(WelcomeMailServiceImpl.class);

	@Autowired
	SendyRestTemplate sendyRestTemplate;

	@Autowired
	private WelcomeMailRepository welcomeMailRepository;

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
			if(participant.getEmail().matches(EventConstants.EMAIL_REGEX)){
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
		if(null!=printName && !printName.isEmpty()){
			printName = printName.replace(".", " ");
			String[] name = printName.split(" ");
			if (name.length > 0) {
				for (int i = 0; i < name.length; i++) {
					if (name[i].length() > 2) {
						return name[i].substring(0, 1).toUpperCase() + name[i].substring(1);
					}
				}
			}
		}else{
			return "Friend";
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
}