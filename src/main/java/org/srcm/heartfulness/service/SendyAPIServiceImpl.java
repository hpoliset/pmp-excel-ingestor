package org.srcm.heartfulness.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.SendyMailRepository;
import org.srcm.heartfulness.rest.template.SendyAPIRestTemplate;

/**
 * 
 * @author rramesh
 *
 */
@Service
public class SendyAPIServiceImpl implements SendyAPIService {

	private static Logger LOGGER = LoggerFactory.getLogger(SendyAPIServiceImpl.class);

	@Autowired
	SendyAPIRestTemplate sendyAPIRestTemplate;

	@Autowired
	private SendyMailRepository sendyMailRepository;

	/* @Scheduled(cron = "0 17 14 * * *") */
	public void addNewSubscriber() {

		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyy");
		String currentDateTime = dateTime.format(format);

		SendySubscriber sendySubscriber = null;

		List<Participant> participants = new ArrayList<Participant>();
		participants = sendyMailRepository.getParticipantsToSendWelcomeMail();
		Set<SendySubscriber> subscriberSet = new HashSet<SendySubscriber>();
		int participantCount = 0;

		if (participants.size() >= 1) {
			for (Participant participant : participants) {
				Map<String, String> fields = new HashMap<String, String>();
				sendySubscriber = new SendySubscriber();
				participantCount = sendyMailRepository.getIntroducedParticipantCount(participant.getPrintName(),
						participant.getEmail());
				System.out.println("participant count " + participantCount);
				if (participantCount < 1) {
					fields.put("Date", currentDateTime);
					sendySubscriber.setNameToSendMail(getName(participant.getPrintName()));
					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					sendySubscriber.setfields(fields);
					// subscribersList.put(participant.getPrintName(),
					// participant.getEmail());
					subscriberSet.add(sendySubscriber);
					try {
						sendyAPIRestTemplate.addNewSubscriber(sendySubscriber);
						sendyAPIRestTemplate.addSubscriberToMonthlyNewsletterList(sendySubscriber);
					} catch (HttpClientErrorException | IOException e) {
						LOGGER.debug("Error while adding subscriber - " + e.getMessage());
					}
				}
			}
		} else {
			LOGGER.debug("No participant found.");
		}
		// System.out.println("SET SIZE "+subscriberSet.size());
		if (subscriberSet.size() >= 1) {
			try {
				sendyAPIRestTemplate.sendMail();
			} catch (HttpClientErrorException | IOException e) {
				LOGGER.debug("Error while sending Mail - " + e.getMessage());
				try {
					sendyAPIRestTemplate.sendErrorAlertMail();
				} catch (MessagingException ex) {
					LOGGER.debug("Error while sending SMTP Mail - " + ex.getMessage());
				}
			}
			for (SendySubscriber subscriber : subscriberSet) {
				WelcomeMailDetails welcomeMailDetails = new WelcomeMailDetails();
				welcomeMailDetails.setPrintName(subscriber.getUserName());
				welcomeMailDetails.setEmail(subscriber.getEmail());
				welcomeMailDetails.setCreateTime(new Date());
				sendyMailRepository.save(welcomeMailDetails);
			}
			try {
				sendyAPIRestTemplate.executeCronJob();
			} catch (HttpClientErrorException | IOException e) {
				LOGGER.debug("Error while executing cron job - " + e.getMessage());
			}
			sendyMailRepository.updateParticipant();
		}
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
				if (name[i].length() > 2) {
					return name[i].substring(0, 1).toUpperCase() + name[i].substring(1);
				}
			}
		}
		return printName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.SendyAPIService#unsubscribeUsers()
	 */
	/*@Scheduled(cron = "0 18 14 * * *")*/
	public void unsubscribeUsers() {
		List<WelcomeMailDetails> subscribers = new ArrayList<WelcomeMailDetails>();
		subscribers = sendyMailRepository.getSubscribersToUnsubscribe();
		if (subscribers.size() >= 1) {
			for (WelcomeMailDetails subscriber : subscribers) {
				try {
					sendyAPIRestTemplate.unsubscribeUser(subscriber);
				} catch (HttpClientErrorException | IOException e) {
					LOGGER.debug("Error while unsubscribe - " + e.getMessage());
				}
			}
		}
	}

	@Override
	public String subscribe(String printName, String mailID) {
		String response = null;
		int subscriberCount;
		SendySubscriber sendySubscriber = new SendySubscriber();
		WelcomeMailDetails welcomeMailDetails = new WelcomeMailDetails();
		welcomeMailDetails.setPrintName(printName);
		welcomeMailDetails.setEmail(mailID);
		welcomeMailDetails.setCreateTime(new Date());
		sendySubscriber.setNameToSendMail(getName(printName));
		sendySubscriber.setUserName(printName);
		sendySubscriber.setEmail(mailID);
		subscriberCount = sendyMailRepository.getIntroducedParticipantCount(printName, mailID);
		if (subscriberCount < 1) {
			try {
				sendyMailRepository.save(welcomeMailDetails);
				response = sendyAPIRestTemplate.addSubscriberToMonthlyNewsletterList(sendySubscriber);
			} catch (HttpClientErrorException | IOException e) {
				e.printStackTrace();
			}
		} else if (subscriberCount == 1) {
			response = sendyMailRepository.updateUserSubscribed(printName, mailID);
		}
		return response;
	}

	@Override
	public String unsubscribe(String mailID) {
		String response = null;
		WelcomeMailDetails sendySubscriber = new WelcomeMailDetails();
		sendySubscriber.setEmail(mailID);
		System.out.println("unsubs service " + mailID);
		sendyMailRepository.updateUserUnsubscribed(mailID);
		try {
			response = sendyAPIRestTemplate.unsubscribeUserFromMonthlyNewsletterList(sendySubscriber);
		} catch (HttpClientErrorException | IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
