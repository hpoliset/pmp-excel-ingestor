package org.srcm.heartfulness.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.repository.WelcomeMailRepository;

/**
 * This class is the service Implementation for the subscription based services.
 * 
 * @author himasreev
 *
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	@Autowired
	private WelcomeMailRepository welcomeMailRepository;

	/**
	 * To unsubscribe a participant from receiving any mail from PMP
	 * 
	 */
	@Override
	public void unsubscribe(String mailID, String name) {
		WelcomeMailDetails sendySubscriber = new WelcomeMailDetails();
		sendySubscriber.setEmail(mailID);
		sendySubscriber.setPrintName(name);
		sendySubscriber.setUnsubscribed(1);
		sendySubscriber.setEmailStatus("USER_UNSUBSCRIBED");
		welcomeMailRepository.updateUserUnsubscribed(sendySubscriber);
	}

	/**
	 * Method to unsubscribe the user to stop receiving emails.
	 * 
	 * @param mail
	 * @return
	 */
	@Override
	public int checkForMailSubcription(String mail) {
		return welcomeMailRepository.checkForMailSubcription(mail);
	}

	/**
	 * Method to subscribe the given mail ID and receive emails.
	 * 
	 * @param mail
	 * @param name
	 */
	@Override
	public void subscribetoMailAlerts(String mail, String name) {
		WelcomeMailDetails sendySubscriber = new WelcomeMailDetails();
		sendySubscriber.setEmail(mail);
		sendySubscriber.setPrintName(name);
		sendySubscriber.setSubscribed(1);
		welcomeMailRepository.updateUserSubscribedStatus(sendySubscriber);
	}

	/**
	 * Method to check whether the given mail ID is subscribed or not.
	 * 
	 * @param mail
	 * @return
	 */
	@Override
	public int checkMailSubscribedStatus(String mail) {
		return welcomeMailRepository.checkMailSubscribedStatus(mail);
	}

	/**
	 * Method to update confirmed status for the given mail ID.
	 * 
	 * @param mailID
	 */
	@Override
	public void updateconfirmSubscribedStatus(String mailID) {
		welcomeMailRepository.updateconfirmSubscribedStatus(mailID);

	}

	/**
	 * Method to check whether the given mail ID is confirmed their subscription
	 * or not.
	 * 
	 * @param mailID
	 * @return
	 */
	@Override
	public int checkForconfirmStatusOfSubscription(String mailID) {
		return welcomeMailRepository.checkForconfirmStatusOfSubscription(mailID);
	}

}
