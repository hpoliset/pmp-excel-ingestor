/**
 * 
 */
package org.srcm.heartfulness.service;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.model.json.response.SuccessResponse;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.repository.SessionDetailsRepository;

/**
 * @author Koustav Dutta
 *
 */
@Service
public class SessionDetailsServiceImpl implements SessionDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDetailsServiceImpl.class);

	@Autowired
	SessionDetailsRepository sessionDtlsRepo;

	@Autowired
	ProgramRepository programRepository;

	
	@Override
	public PMPResponse saveOrUpdateSessionDetails(SessionDetails sessionDetails) {

		PMPResponse response = null;
		try{
			response = sessionDtlsRepo.saveOrUpdateSessionDetails(sessionDetails);
		}catch(DataAccessException dae){
			dae.printStackTrace();
			LOGGER.error("DAE Failed to save session id details for event id " + sessionDetails.getEventId());
			response = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.SESSION_CREATION_FAILED);
		}catch(Exception ex){
			LOGGER.error("EX Failed to save sessiond idetails for event id " + sessionDetails.getEventId());
			response = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.SESSION_CREATION_FAILED);
		}
		return response;
	}


	@Override
	public PMPResponse deleteSessionDetail(SessionDetails sessionDetails) {
		int isUpdated = 0;
		try{
			isUpdated = sessionDtlsRepo.deleteSessionDetail(sessionDetails);
		}catch( DataAccessException dae){
			LOGGER.error("DAE Failed to delete session id details for event id " + sessionDetails.getEventId() +" and session id "+sessionDetails.getAutoGeneratedSessionId());
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,ErrorConstants.SESSION_DELETION_FAILED);
		}catch(Exception ex){
			LOGGER.error("EX Failed to delete session id details for event id " + sessionDetails.getEventId() +" and session id "+sessionDetails.getAutoGeneratedSessionId());
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,ErrorConstants.SESSION_DELETION_FAILED);
		}
		if(isUpdated > 0){
			return new SuccessResponse(ErrorConstants.STATUS_SUCCESS,ErrorConstants.SESSION_SUCCESSFULLY_DELETED);
		}else{
			return new ErrorResponse(ErrorConstants.STATUS_FAILED,ErrorConstants.SESSION_DELETION_FAILED);
		}

	}


	@Override
	public List<SessionDetails> getSessionDetails(int programId,String eventId) {
		List<SessionDetails> sessionDetailsList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try{
			sessionDetailsList = sessionDtlsRepo.getSessionDetails(programId);	
			for(SessionDetails session : sessionDetailsList){
				session.setEventId(eventId);
				session.setSessionStringDate(sdf.format(session.getSessionDate()));
			}
		}catch( DataAccessException dae){
			LOGGER.error("DAE Failed to retrieve session details for event Id :"+eventId);
		}catch(Exception ex){
			LOGGER.error("EX Failed to retrieve session details for event Id :"+eventId);
		}
		return sessionDetailsList;
	}

}
