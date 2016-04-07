package org.srcm.heartfulness.service;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.srcm.heartfulness.model.IntroductionDetails;
import org.srcm.heartfulness.model.User;

/**
 * 
 * @author himasreev
 *
 */
public interface PMPMailService {

	/**
	 * method to send mail to the seeker and HFN team after seeker request for sitting
	 * @param newUser
	 * @param introdet
	 * @throws MessagingException
	 */
	void sendMail(User newUser, IntroductionDetails introdet) throws MessagingException, AuthenticationFailedException,AddressException;

}
