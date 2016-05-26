package org.srcm.heartfulness.service;

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
	 */
	public void unsubscribe(String mailID, String name);

	/**
	 * Method to unsubscribe the user to stop receiving emails.
	 * 
	 * @param mail
	 * @return
	 */
	public int checkForMailSubcription(String mail);

	/**
	 * Method to subscribe the given mail ID and recieve emails.
	 * 
	 * @param mail
	 * @param name
	 */
	public void subscribetoMailAlerts(String mail, String name);

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
	 */
	public void updateconfirmSubscribedStatus(String mailID);

	/**
	 * Method to check whether the given mail ID is confirmed their subscription
	 * or not.
	 * 
	 * @param mailID
	 * @return
	 */
	public int checkForconfirmStatusOfSubscription(String mailID);

}
