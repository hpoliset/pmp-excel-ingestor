package org.srcm.heartfulness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.WelcomeMailDetails;
import org.srcm.heartfulness.model.json.request.SubscriptionRequest;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.repository.WelcomeMailRepository;

/**
 * This class is the service Implementation for the subscription based services.
 * 
 * @author himasreev
 *
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

	@Autowired
	private WelcomeMailRepository welcomeMailRepository;

	@Autowired
	private SendMail sendMail;

	/**
	 * To unsubscribe a participant from receiving any mail from PMP
	 * 
	 */
	@Override
	public Response unsubscribe(String emailID, String name) {
		if (1 == welcomeMailRepository.checkForMailSubcription(emailID)) {
			LOGGER.debug("Already unsubscribed - mail : {} , name : {}", emailID, name);
			return new Response("Success", "You've already unsubscribed.");
		} else {
			LOGGER.debug("unsubscription - mail : {} , name : {}", emailID, name);
			WelcomeMailDetails sendySubscriber = new WelcomeMailDetails();
			sendySubscriber.setEmail(emailID);
			sendySubscriber.setPrintName(name);
			sendySubscriber.setUnsubscribed(1);
			sendySubscriber.setEmailStatus("USER_UNSUBSCRIBED");
			welcomeMailRepository.updateUserUnsubscribed(sendySubscriber);
			return new Response("Success", "unsubscribed successfully.");
		}
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
	public Response subscribetoMailAlerts(SubscriptionRequest subscriptionRequest) {
		if (1 == welcomeMailRepository.checkMailSubscribedStatus(subscriptionRequest.getMailID())) {
			LOGGER.debug("Already subscribed - mail : {} , name : {}", subscriptionRequest.getMailID(),
					subscriptionRequest.getName());
			return new Response("Success", "You've already subscribed.");
		} else {
			LOGGER.debug("subscription - mail : {} , name : {}", subscriptionRequest.getMailID(),
					subscriptionRequest.getName());
			WelcomeMailDetails sendySubscriber = new WelcomeMailDetails();
			sendySubscriber.setEmail(subscriptionRequest.getMailID());
			sendySubscriber.setPrintName(subscriptionRequest.getName());
			sendySubscriber.setSubscribed(1);
			welcomeMailRepository.updateUserSubscribedStatus(sendySubscriber);
			sendMail.sendConfirmSubscriptionMail(subscriptionRequest.getMailID(), subscriptionRequest.getName());
			return new Response("Success", "subscribed successfully.");
		}
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
	public String updateconfirmSubscribedStatus(String mailID) {
		if (1 == welcomeMailRepository.checkMailSubscribedStatus(mailID)) {
			if (1 == welcomeMailRepository.checkForconfirmStatusOfSubscription(mailID)) {
				LOGGER.debug("Already you have confirmed your mail ID. - mail : {}", mailID);
				return "Already you have confirmed your email address.";
			} else {
				welcomeMailRepository.updateconfirmSubscribedStatus(mailID);
				LOGGER.debug("You have successfully confirmed your mail ID. - mail : {}", mailID);
				return "Thank you for confirming your email address.";
			}
		} else {
			LOGGER.debug(
					"Please subscribe your emailID in Heartfulness Website to confirm your email address. Invalid mailID. - mailID : {}",
					mailID);
			return "Please subscribe your emailID in Heartfulness Website to confirm your email address.";
		}

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
