package org.srcm.heartfulness.service;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface WelcomeMailService {

	/**
	 * Method to add new subscriber to the welcome mail list.
	 * 
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void addNewSubscriber() throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException, MessagingException;

	/**
	 * Method unsubscribe the user from the welcome mail list.
	 * 
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void unsubscribeUsers() throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException;

	/**
	 * Method to unsubscribe the given mail ID and stop sending emails to the
	 * respective participant.
	 * 
	 * @param mailID
	 * @param name
	 */
	void unsubscribe(String mailID, String name);

}