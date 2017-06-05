package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.SessionImageDetails;
import org.srcm.heartfulness.model.json.request.SearchSession;
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
	 * @param emailList to get the list of emails associated with an user.           
	 *  
	 * @param userRole Role of user in PMP           
	 * @return list of session details for a particular event.
	 */
	public List<SessionDetails> getSessionDetails(int programId, String eventId, List<String> emailList, String userRole);

	public int getSessionDetailsIdBySessionIdandProgramId(String sessionId, int programId);

	public void saveSessionFiles(SessionImageDetails sessionFiles);

	public int getCountOfSessionImages(int sessionDetailsId);

	public List<SessionImageDetails> getListOfSessionImages(int sessionDetailsId);

	/**
	 * Returns back the session id for a given 
	 * auto generated session id
	 * @param autoGeneratedSessionId is used to get the session id for
	 * a particular session.
	 * @return session id  
	 */
	public int getSessionId(String autoGeneratedSessionId);

	/**
	 * This method is used to search session details for a 
	 * provided event Id.
	 * @param emailList user emails associated with single abhyasi Id.
	 * @param userRole role of the log in user.
	 * @param searchSession Object with search params.
	 * @return List<SessionDetails> list of session details is returned based
	 * on search criteria. 
	 */
	public List<SessionDetails> getSearchSessionData(List<String> emailList, String userRole, SearchSession searchSession); 

}
