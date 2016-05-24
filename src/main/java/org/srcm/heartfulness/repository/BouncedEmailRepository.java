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
	public int updateBouncedEmails(String email);
}
