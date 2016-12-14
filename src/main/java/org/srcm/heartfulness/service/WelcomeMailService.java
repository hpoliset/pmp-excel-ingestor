package org.srcm.heartfulness.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * Service Provider class to handle welcome mail related actions.
 * 
 * @author himasreev
 *
 */
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
	 * Method to add welcome mail list in a file and upload it to a FTP.
	 * 
	 * @throws FileNotFoundException
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public void uploadParticipantEmailidsToFTP() throws FileNotFoundException, IOException, JSchException,
			SftpException;

	/**
	 * Service method to fetch the list of coordinators who will receive emails
	 * for those participants who have received welcome emails.
	 */
	public void getCoordinatorListAndSendMail();

	/**
	 * Method to get the list of event to which ewelcome Id generation processed
	 * and send mails to the respective event coordinator.
	 */
	public void getGeneratedEwelcomeIdAndSendToCoordinators();

	/**
	 * Method to trigger mail to the HFN new list to send the welcome mails to
	 * the new participnats.
	 */
	public void sendWelcomeMailToHfnList();

}