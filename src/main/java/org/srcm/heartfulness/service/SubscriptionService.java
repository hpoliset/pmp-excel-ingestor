package org.srcm.heartfulness.service;

import org.srcm.heartfulness.model.json.request.SubscriptionRequest;
import org.srcm.heartfulness.model.json.response.Response;

/**
 * This class is the service provider for the subscription based services.
 * 
 * @author himasreev
 *
 */
public interface SubscriptionService {

	/**
	 * Method unsubscribe the user to stop receiving mails.
	 * 
	 * @param mailID
	 * @param name
	 * @return 
	 */
	public Response unsubscribe(String emailID,String name);

	/**
	 * Method to unsubscribe the user to stop receiving emails.
	 * 
	 * @param mail
	 * @return
	 */
	public int checkForMailSubcription(String mail);

	/**
	 * Method to subscribe the given mail ID and receive emails.
	 * 
	 * @param mail
	 * @param name
	 * @return 
	 */
	public Response subscribetoMailAlerts(SubscriptionRequest subscriptionRequest);

	/**
	 * Method to check whether the given mail ID is subscribed or not.
	 * 
	 * @param mail
	 * @return
	 */
	public int checkMailSubscribedStatus(String mail);

	/**
	 * Method to update confirmed status for the given mail ID.
	 * 
	 * @param mailID
	 * @return 
	 */
	public String updateconfirmSubscribedStatus(String mailID);

	/**
	 * Method to check whether the given mail ID is confirmed their subscription
	 * or not.
	 * 
	 * @param mailID
	 * @return
	 */
	public int checkForconfirmStatusOfSubscription(String mailID);
	
	/**
	 * Method to validate the email Id and update the status in PMP.
	 * @param mailID
	 * @return
	 */
	public String updateValidationStatus(String mailID);

}
