package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.service.WelcomeMailService;

/**
 * 
 * @author rramesh
 *
 */
@RestController
@RequestMapping("/api/welcomemail/")
public class WelcomeMailController {

	@Autowired
	private WelcomeMailService WelcomeMailService;

	@Autowired
	private SendMail sendMail;

	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeMailController.class);

	/* @Scheduled(cron = "${welcome.mail.subscribe.cron.time}") */
	public void subscribeUser() {
		try {
			LOGGER.debug("Scheduler started at - " + new Date());
			WelcomeMailService.addNewSubscriber();
		} catch (HttpClientErrorException | IOException | MessagingException e) {
			// e.printStackTrace();
			LOGGER.error("Exception while Subscribe - {} " + e.getMessage());
		}
	}

	/* @Scheduled(cron = "${welcome.mail.unsubscribe.cron.time}") */
	public void unsubscribeUser() {
		try {
			LOGGER.debug("Unsubcribe user called.");
			WelcomeMailService.unsubscribeUsers();
		} catch (HttpClientErrorException | IOException e) {
			// e.printStackTrace();
			LOGGER.error("Exception while Unsubscribe - {} " + e.getMessage());
		}
	}

	@Scheduled(cron = "${welcome.mailids.file.upload.cron.time}") 
	public void uploadDailyWelcomeMailidsToFTP() {
		try {
			LOGGER.debug("Upload File to FTP called.");
			WelcomeMailService.uploadParticipantEmailidsToFTP();
			LOGGER.debug("Process Completed.");
		} catch (Exception e) {
			LOGGER.error("Exception while uploading file - {} " + e.getMessage());
		}
	}

	/**
	 * Controller is used to send email to the coordinators with event details
	 * about the participants who have received welcome emails.It is a crob job
	 * running at a scheduled time.
	 */
	@Scheduled(cron = "${welcome.mailids.coordinator.inform.cron.time}") 
	public void sendEmailToCoordinator() {
		LOGGER.debug("START		:Cron job started to fetch participants to whom welcome mail already sent");
		WelcomeMailService.getCoordinatorListAndSendMail();
		LOGGER.debug("END		:Cron job completed to fetch participants to whom welcome mail already sent");
	}

	//@RequestMapping(value = "informcoordinatorswithewelcomeids", method = RequestMethod.POST)
	/*@Scheduled(cron = "${ewelcomeid.generate.coordinator.inform.cron.time}") */
	public void sendGeneratedEwelcomeIdToCoordinators() {
		try {
			LOGGER.debug("Sending mail to co-ordinator for e-welcome id generation called.");
			WelcomeMailService.getGeneratedEwelcomeIdAndSendToCoordinators();
		} catch (Exception e) {
			LOGGER.error("Exception while sending file - {} " + e.getMessage());
		}
	}
	
	//@RequestMapping(value = "sendwelcomemail", method = RequestMethod.POST)
	@Scheduled(cron = "${welcome.mail.to.hfnlist.cron.time}") 
	public void sendWelcomeMail() {
		try {
			LOGGER.debug("Sending mail to hfn list called.");
			WelcomeMailService.sendWelcomeMailToHfnList();
		} catch (Exception e) {
			LOGGER.error("Exception while sending mail - {} " + e.getMessage());
		}
	}

}
