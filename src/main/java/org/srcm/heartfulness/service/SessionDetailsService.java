/**
 * 
 */
package org.srcm.heartfulness.service;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.response.PMPResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface SessionDetailsService {
	
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails);

	public PMPResponse deleteSessionDetail(SessionDetails sessionDetails);

	public List<SessionDetails> getSessionDetails(int programId,String eventId); 

}
