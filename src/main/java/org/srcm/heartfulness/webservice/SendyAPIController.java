package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.service.WelcomeMailService;

/**
 * 
 * @author rramesh
 *
 */
@RestController
@RequestMapping("/api/sendy/")
public class SendyAPIController {

	@Autowired
	private WelcomeMailService sendyAPIService;

	private static final Logger LOGGER = LoggerFactory.getLogger(SendyAPIController.class);

	/* @Scheduled(cron = "${welcome.mail.subscribe.cron.time}") */
	public void subscribeUser() {
		try {
			LOGGER.debug("Scheduler started at - " + new Date());
			sendyAPIService.addNewSubscriber();
		} catch (HttpClientErrorException | IOException | MessagingException e) {
			// e.printStackTrace();
			LOGGER.error("Exception while Subscribe - {} " + e.getMessage());
		}
	}

	/* @Scheduled(cron = "${welcome.mail.unsubscribe.cron.time}") */
	public void unsubscribeUser() {
		try {
			LOGGER.debug("Unsubcribe user called.");
			sendyAPIService.unsubscribeUsers();
		} catch (HttpClientErrorException | IOException e) {
			// e.printStackTrace();
			LOGGER.error("Exception while Unsubscribe - {} " + e.getMessage());
		}
	}

	@RequestMapping(value = "unsubscribe", method = { RequestMethod.POST, RequestMethod.GET })
	public String sendMail(@RequestBody SendySubscriber sendySubcriber) {
		sendyAPIService.unsubscribe(sendySubcriber.getEmail(),sendySubcriber.getUserName());
		return "";
	}
}