/**
 * 
 */
package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.SessionImageDetails;
import org.srcm.heartfulness.model.json.request.SearchSession;
import org.srcm.heartfulness.model.json.response.PMPResponse;

/**
 * @author Koustav Dutta
 *
 */

public interface SessionDetailsRepository {

	/**
	 * To create or update session details in pmp.
	 * 
	 * @param sessionDetails
	 *            to create a new session details record or update the existing
	 *            session details record.
	 * @return success response if successfully created or updated else error
	 *         response.
	 * 
	 */
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails);

	/**
	 * To get the session id for a given auto generated session id.
	 * 
	 * @param autoGnrtdSessionId
	 *            is used to get the session id for a particular event.
	 * @return session id
	 */
	public int getSessionId(String autoGnrtdSessionId);

	/**
	 * Method is used to delete the session details.
	 * 
	 * @param sessionDetails
	 *            to get the program id and auto generated session id.
	 * @return 1if session details is deleted successfully else returns 0 is
	 *         failed to delete session details.
	 */
	public int deleteSessionDetail(SessionDetails sessionDetails);

	/**
	 * To get the list of session details for an particular event.
	 * 
	 * @param programId
	 *            to get the list of session details.
	 * @return list of session details for a particular id.
	 */
	public List<SessionDetails> getSessionDetails(int programId);

	/**
	 * To get the session details Id for given autogenerated session Id and
	 * program Id.
	 * 
	 * @param sessionId
	 * @param programId
	 * @return session details Id
	 */
	public int getSessionDetailsIdBySessionIdandProgramId(String sessionId, int programId);

	/**
	 * To save the session images w.r.t session in HFN Backed.
	 * 
	 * @param sessionFiles
	 */
	public void saveSessionFiles(SessionImageDetails sessionFiles);

	/**
	 * To get the count of available images for the given session Id.
	 * 
	 * @param sessionDetailsId
	 * @return
	 */
	public int getCountOfSessionImages(int sessionDetailsId);

	/**
	 * To get the List of available images for the given session Id.
	 * 
	 * @param sessionDetailsId
	 * @return
	 */
	public List<SessionImageDetails> getListOfSessionImages(int sessionDetailsId);

	/**
	 * This method is used to search session details for a 
	 * provided event Id.
	 * @param programId to specify the program for which session needs
	 *  to be searched.
	 * @param searchSession Object with search params.
	 * @return List<SessionDetails> list of session details is returned based
	 * on search criteria.
	 */
	public List<SessionDetails> searchSessionData(int programId, SearchSession searchSession);

	void saveSessionFilesWithType(SessionImageDetails sessionFiles);

}
