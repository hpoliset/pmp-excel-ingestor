/**
 * 
 */
package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.response.PMPResponse;

/**
 * @author Koustav Dutta
 *
 */

public interface SessionDetailsRepository {
	
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails);
	
	public int getSessionId(String autoGnrtdSessionId);
	
	public int deleteSessionDetail(SessionDetails sessionDetails);

	public List<SessionDetails> getSessionDetails(int programId);

}
