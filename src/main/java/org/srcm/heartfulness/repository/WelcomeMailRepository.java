package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.WelcomeMailDetails;

public interface WelcomeMailRepository {

	/**
	 * Retrieve the list of <code>Participant</code> to send welcome mail
	 * 
	 * @return the <code>Participant</code> if found
	 */
	List<Participant> getParticipantsToSendWelcomeMail();

	/**
	 * To check whether the print name and email exists or not
	 * 
	 * @param printName
	 *            - print name to search for
	 * @param email
	 *            - emailID to search for
	 * @return count of introduced participants
	 */
	int getIntroducedParticipantCount(String printName, String email);

	/**
	 * Save an <code>WelcomeMailDetails</code> to the data store, either
	 * inserting or updating it.
	 * 
	 * @param welcomeMailDetails
	 */
	void save(WelcomeMailDetails welcomeMailDetails);

	/**
	 * To retrieve the list of users to unsubscribe
	 * 
	 * @return the <code>WelcomeMailDetails</code> if found
	 */
	List<WelcomeMailDetails> getSubscribersToUnsubscribe();

	/**
	 * Update the welcome mail sent in participant table
	 * 
	 * @param string
	 */
	void updateParticipant(String mailID);

	/**
	 * Update the user as unsubscribed from all mail
	 * 
	 * @param mailID
	 *            - maildID to be unsubscribe
	 */
	void updateUserUnsubscribed(String mailID);

	/**
	 * To subscribe the user for receiving the emails
	 * 
	 * @param name
	 *            - participant name to be subscribe
	 * @param mailID
	 *            - mailID to be subscribed
	 * @return subscription status
	 */
	String updateUserSubscribed(String name, String mailID);

	/**
	 * Update the participant that welcome mail sent
	 * 
	 * @param id
	 *            - participant ID to be mark as welcome mail sent
	 */
	public void updateParticipantMailSentById(int id);

	/**
	 * Update participant as unsubscribed from all the mails
	 * 
	 * @param welcomeMailDetails
	 *            - <code>WelcomeMailDetails</code>
	 */
	void updateUserUnsubscribed(WelcomeMailDetails welcomeMailDetails);

	/**
	 * Retrieve the list of <code>Participant</code> to send welcome mail to
	 * whom confirmation mail sent
	 * 
	 * @return - the <code>Participant</code> if found
	 */
	List<Participant> getParticipantsToSendWelcomeEmails();

	/**
	 * To unsubscribe an user for receiving the emails
	 * 
	 * @param userName
	 *            - participant name
	 * @param email
	 *            - participant maildID
	 */
	void updateWelcomeMailLog(String userName, String email);

	/**
	 * Update the participant that welcome mail sent to whom confirmation mail
	 * sent
	 * 
	 * @param email
	 *            -email ID to be update welcome mail sent
	 */
	void updateParticipantByMailId(String email);

}
