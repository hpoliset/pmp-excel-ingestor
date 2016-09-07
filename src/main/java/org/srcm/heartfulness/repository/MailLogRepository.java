/**
 * 
 */
package org.srcm.heartfulness.repository;

import org.srcm.heartfulness.model.PMPMailLog;

/**
 * @author Koustav Dutta
 *
 */
public interface MailLogRepository {

	public void createMailLog(PMPMailLog pmpMailLog);
	
}
