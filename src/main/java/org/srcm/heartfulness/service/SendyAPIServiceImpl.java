package org.srcm.heartfulness.service;

import java.io.IOException;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.SendyMailRepository;
import org.srcm.heartfulness.rest.template.SendyAPIRestTemplate;

@Service
public class SendyAPIServiceImpl implements SendyAPIService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SendyAPIServiceImpl.class);

	@Autowired
	SendyAPIRestTemplate sendyAPIRestTemplate;

	@Autowired
	private SendyMailRepository sendyMailRepository;


	@Override
	@Scheduled(cron = "0 58 17 * * *")
	public void addNewSubscriber() {

		/*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -6);
		String date = dateFormat.format(cal.getTime());
		System.out.println(dateFormat.format(new Date()));*/
		
		SendySubscriber sendySubscriber = null;
		
		List<Participant> participants = new ArrayList<Participant>();
		participants = sendyMailRepository.getParticipantsToSendWelcomeMail();
		
		//Map<String, String> subscribersList = new HashMap<String, String>();
		Set<SendySubscriber> subscriberSet = new HashSet<SendySubscriber>();
		int participantCount = 0;
		
		if(participants.size()>=1){
			for (Participant participant : participants) {
				Map<String, String> fields = new HashMap<String, String>();
				sendySubscriber = new SendySubscriber();
				
				participantCount = sendyMailRepository.getIntroducedParticipantCount(participant.getPrintName(),
						participant.getEmail());
				//System.out.println("participant count "+participantCount);
				if (participantCount < 1) {
					fields.put("Event", "HTC");
					fields.put("EventDate", "29-03-2016");

					sendySubscriber.setUserName(participant.getPrintName());
					sendySubscriber.setEmail(participant.getEmail());
					sendySubscriber.setfields(fields);

					//subscribersList.put(participant.getPrintName(), participant.getEmail());
					subscriberSet.add(sendySubscriber);
					
					try {
						sendyAPIRestTemplate.addNewSubscriber(sendySubscriber);
					} catch (HttpClientErrorException | IOException e) {
						LOGGER.debug("Error while adding subscriber - "+e.getMessage());
					}
				}

			}
		}
		else{
			LOGGER.debug("No participant found.");
		}
		
		//System.out.println("SET SIZE "+subscriberSet.size());
		
		if(subscriberSet.size()>=1){
			try {
				sendyAPIRestTemplate.sendMail();
				
			} catch (HttpClientErrorException | IOException e) {
				LOGGER.debug("Error while sending Mail - " + e.getMessage());
				try {
					sendyAPIRestTemplate.sendErrorAlertMail();
				} catch (MessagingException ex) {
					LOGGER.debug("Error while sending Mail - " + ex.getMessage());
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
	
	@Override
	@Scheduled(cron = "0 0 18 * * *")
	public void unsubscribeUsers() {
		List<WelcomeMailDetails> subscribers = new ArrayList<WelcomeMailDetails>();
		subscribers = sendyMailRepository.getSubscribersToUnsubscribe();
		
		if(subscribers.size() >= 1){
			for (WelcomeMailDetails subscriber : subscribers) {
				try {
					sendyAPIRestTemplate.unsubscribeUser(subscriber);
				} catch (HttpClientErrorException | IOException e) {
					LOGGER.debug("Error while unsubscribe - "+e.getMessage());
				}
			}
		}
	}
	
}
