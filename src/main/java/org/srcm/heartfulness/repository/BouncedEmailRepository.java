/**
 * 
 */
package org.srcm.heartfulness.repository;

/**
 * 
 * The BouncedEmailRepository interface is a data repository for
 * marking the participant email as  bounced.
 * @author Koustav Dutta
 *
 */
public interface BouncedEmailRepository {
	
	/**
	 * If the email matches with any participant email then
	 * it is marked as bounced email.
	 * @param email is used to update participant email as bounced or not.
	 * @return if participant email is successfully
	 * updated as bounced it will return 1 else it will return 0.
	 */
	public int updateEmailAsBounced(String email);
	
	/**
	 * If the subcriber email is present it will update status 
	 * and unsubscribed flag as 1 for that particular email id.
	 * @param bouncedEmail to update subscriber email as bounced or not.
	 * @return 1 if updated successfully else 0.
	 */
	public int updateEmailStatusAsBounced(String bouncedEmail);
	
	/**
	 * Creates a new record if the email is not found.
	 * @param bouncedEmail to create a new subscriber record.
	 */
	public void createEmailAsBounced(String bouncedEmail);
	
}
