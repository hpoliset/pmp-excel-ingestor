package org.srcm.heartfulness.webservice;

import java.io.IOException;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
@RequestMapping("/api/sendy/")
public class SendyAPIController {

	@Autowired
	private WelcomeMailService sendyAPIService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SendyAPIController.class);

	@Scheduled(cron = "0 55 2 * * *")
	public void subscribeUser(){
		try {
			sendyAPIService.addNewSubscriber();
		} catch (HttpClientErrorException | IOException | MessagingException e) {
			//e.printStackTrace();
			LOGGER.error("Exception while Subscribe - {} "+ e.getMessage());
		}
	}
	//@Scheduled(cron = "0 28 19 * * *")
	public void unsubscribeUser(){
		try {
			sendyAPIService.unsubscribeUsers();
		} catch (HttpClientErrorException | IOException e) {
			//e.printStackTrace();
			LOGGER.error("Exception while Unsubscribe - {} "+ e.getMessage());
		}
	}
	
	@RequestMapping(value = "/testWelcomeMail", method = RequestMethod.GET)
	public String testWelcomeMail() {
		subscribeUser();
		return "completed";
	}
	
	@RequestMapping(value = "/testUnsubcribe", method = RequestMethod.GET)
	public String testUnsubcribe() {
		unsubscribeUser();
		return "completed";
		
	}
}