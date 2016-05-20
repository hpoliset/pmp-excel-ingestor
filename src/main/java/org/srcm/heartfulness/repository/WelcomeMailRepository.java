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
     * @param printName - print name to search for
     * @param email - emailID to search for
     * @return count of introduced participants
     */
	int getIntroducedParticipantCount(String printName, String email);

	/**
	 * Save an <code>WelcomeMailDetails</code> to the data store, either inserting or updating it.
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
	 * @param string 
	 */
	void updateParticipant(String mailID);

	/**
	 * Method to update the subscribed status in DB.
	 * @param name
	 * @param mailID
	 * @return
	 */
	String updateUserSubscribed(String name,String mailID);
	
	/**
	 * Method to update Participant mail sent status by ID.
	 * @param id
	 */
	public void updateParticipantMailSentById(int id);

	void updateUserUnsubscribed(WelcomeMailDetails sendySubscriber);

}
