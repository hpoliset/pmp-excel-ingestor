package org.srcm.heartfulness.repository;

import java.util.List;
import java.util.Map;

import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
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
	void updateParticipantByMailId(SendySubscriber subscriber);

	/**
	 * Update the participant subscribed status in the PMP.
	 * 
	 * @param sendySubscriber
	 */
	void updateUserSubscribedStatus(WelcomeMailDetails sendySubscriber);

	/**
	 * Update the participant subscription confirmed status for the given mailID
	 * in the PMP.
	 * 
	 * @param mailID
	 */
	void updateconfirmSubscribedStatus(String mailID);

	/**
	 * Method to check whether the given email ID has unsubscribed or not.
	 * 
	 * @param email
	 * @return
	 */
	int checkForMailSubcription(String email);

	/**
	 * Method to update the participant with Confirmation mail sent status.
	 * 
	 * @param participant
	 */
	void updateConfirmationMailStatus(Participant participant);

	/**
	 * Method to check whether the confirmation mail sent to the participant or
	 * not.
	 * 
	 * @param participant
	 * @return
	 */
	int CheckForConfirmationMailStatus(Participant participant);

	/**
	 * Method to check whether the email is subscribed or not.
	 * 
	 * @param mail
	 * @return
	 */
	int checkMailSubscribedStatus(String mail);

	/**
	 * Method to check whether the email subscription is confirmed or not.
	 * 
	 * @param mailID
	 * @return
	 */
	int checkForconfirmStatusOfSubscription(String mailID);

	/**
	 * Repository access method to get the coordinator emails with participant
	 * and event details.
	 * 
	 * @return Map<String,List<String>> Map of coordinator email with some
	 *         participant details and event details.
	 */

	public Map<String, List<String>> getCoordinatorWithEmailDetails();

	/**
	 * To get the count of participants for a particular program id.
	 * 
	 * @param programId
	 * @return count of participants for a given program id.
	 */
	int getPctptCountByPgrmId(String programId);

	/**
	 * Rteurns the count of participants who have already received welcome id
	 * for a given program id.
	 * 
	 * @param programId
	 * @return count of participants who have already received welcome email.
	 */
	int wlcmMailRcvdPctptCount(String programId);

	/**
	 * Method returns 1 or 0 depending on whether database value is updated or
	 * not.
	 * 
	 * @return 1 if database is updated else returns 0.
	 */
	public int updateCoordinatorInformedStatus(String programId);

	int checkForMailIdInWelcomeLog(String email);

	void updateVerificationStatus(String email, int status);

	/**
	 * Method to get the event and particpant details to whom PMP processed
	 * ewelcome Id generation.
	 * 
	 * @return
	 */
	Map<CoordinatorEmail, List<Participant>> getGeneratedEwelcomeIdDetails();

	/**
	 * Method to update the status after sending ewelcome id details to the
	 * event coordinator.
	 * 
	 * @param key
	 * @return
	 */
	int updateEwelcomeIDInformedStatus(String key);

	/**
	 * Method to get the count of events to which ewelcomeid generation
	 * processed and need to send mail to coordinators.
	 * 
	 * @return
	 */
	int getCountofIsWelcomeIdInformedcordinators();
}
