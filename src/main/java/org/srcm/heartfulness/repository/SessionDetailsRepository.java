/**
 * 
 */
package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.SessionImageDetails;
import org.srcm.heartfulness.model.json.response.PMPResponse;

/**
 * @author Koustav Dutta
 *
 */

public interface SessionDetailsRepository {
	
	/**
	 * To create or update session details in pmp.
	 * @param sessionDetails to create a new session details record 
	 * or update the existing session details record.
	 * @return success response if successfully created or updated
	 * else error response.
	 * 
	 */
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails);
	
	/**
	 * To get the session id for a given
	 * auto generated session id.
	 * 
	 * @param autoGnrtdSessionId is used to get the session id
	 * for a particular event.
	 * @return session id
	 */
	public int getSessionId(String autoGnrtdSessionId);
	
	/**
	 * Method is used to delete the session details.
	 * 
	 * @param sessionDetails to get the program id and auto
	 * generated session id.
	 * @return 1if session details is deleted successfully
	 * else returns 0 is failed to delete session details.
	 */
	public int deleteSessionDetail(SessionDetails sessionDetails);

	/**
	 * To get the list of session details for 
	 * an particular event.
	 * 
	 * @param programId to get the list of session details.
	 * @return list of session details for a particular id.
	 */
	public List<SessionDetails> getSessionDetails(int programId);

	public int getSessionDetailsIdBySessionIdandProgramId(String sessionId, int programId);

	public void saveSessionFiles(SessionImageDetails sessionFiles);

	public int getCountOfSessionImages(int sessionDetailsId);

	public List<SessionImageDetails> getListOfSessionImages(int sessionDetailsId);

}
