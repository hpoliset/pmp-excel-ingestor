package org.srcm.heartfulness.service;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


public interface WelcomeMailService {

	public void addNewSubscriber() throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException, MessagingException;
	
	public void unsubscribeUsers() throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException;

}