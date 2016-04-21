package org.srcm.heartfulness.webservice;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

	//@Scheduled(cron = "0 18 19 * * *")
	public void subscribeUser(){
		try {
			sendyAPIService.addNewSubscriber();
		} catch (HttpClientErrorException | IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
	//@Scheduled(cron = "0 28 19 * * *")
	public void unsubscribeUser(){
		try {
			sendyAPIService.unsubscribeUsers();
		} catch (HttpClientErrorException | IOException e) {
			e.printStackTrace();
		}
	}
}