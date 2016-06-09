package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
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

	/* @Scheduled(cron = "${welcome.mailids.file.upload.cron.time}") */
	@RequestMapping(value = "/uploadtoftp", method = RequestMethod.POST)
	public String uploadDailyWelcomeMailidsToFTP() {
		try {
			LOGGER.debug("Upload File to FTP called.");
			WelcomeMailService.uploadParticipantEmailidsToFTP();
			return "Process Completed.";
		} catch (Exception e) {
			LOGGER.error("Exception while uploading file - {} " + e.getMessage());
			return "Error Occurred.";
		}
	}

}