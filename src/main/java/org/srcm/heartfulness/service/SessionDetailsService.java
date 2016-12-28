package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.SessionFiles;
import org.srcm.heartfulness.model.json.response.PMPResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface SessionDetailsService {

	/**
	 * To create or update session details in pmp.
	 * 
	 * @param sessionDetails
	 *            to create a new session details record or update the existing
	 *            session details record.
	 * @return success response if successfully created or updated else error
	 *         response.
	 */
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails);

	/**
	 * To delete a particular session details for a particular event.
	 * 
	 * @param sessionDetails
	 *            to get the auto generated session id
	 * @return success response if session details is successfully deleted else
	 *         error response.
	 */
	public PMPResponse deleteSessionDetail(SessionDetails sessionDetails);

	/**
	 * Provides a list of session details for a particular event.
	 * 
	 * @param programId
	 *            to get the session list for a particular event.
	 * @param eventId
	 *            to set the event details for all session details.
	 * @return list of session details for a particular event.
	 */
	public List<SessionDetails> getSessionDetails(int programId, String eventId);

	public int getSessionDetailsIdBySessionIdandProgramId(String sessionId, int programId);

	public void saveSessionFiles(SessionFiles sessionFiles);

}
