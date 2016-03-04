package org.srcm.heartfulness.service;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.helper.PmpMailHelper;
import org.srcm.heartfulness.model.IntroductionDetails;
import org.srcm.heartfulness.model.User;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class PMPMailServiceImpl implements PMPMailService {
	
	@Autowired
	PmpMailHelper mailHelper;

	/**
	 * method to send mail to the seeker and HFN team after seeker request for sitting
	 * @param newUser
	 * @param introdet
	 * @throws MessagingException 
	 */
	@Override
	public void sendMail(User newUser, IntroductionDetails introdet) throws MessagingException {
		mailHelper.sendEmailtoSeeker(newUser.getEmail());
		mailHelper.sendEmailtoHfnTeam(newUser,introdet);
	}

}
