/**
 * 
 */
package org.srcm.heartfulness.service;

/**
 * The BouncedEmailService interface defines all public business
 * behaviors for operations on fetching and handling bounced emails 
 * from hfnbounce@srcm.org.
 * 
 * @author Koustav Dutta
 *
 */

public interface BouncedEmailService {
	 
	/**
	 * This mehod fetches all the mails from 
	 * hfnbounce@srcm.org handles them calls dao 
	 * classes to update participant email as bounced 
	 * if required and marks them as read in the mail server.
	 */
	
	public void fetchBouncedEmails();

}
