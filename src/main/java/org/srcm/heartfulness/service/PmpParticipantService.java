package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.UpdateIntroductionResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is service provider for the participant related actions.
 * 
 * @author himasreev
 *
 */
public interface PmpParticipantService {

	/**
	 * Service to create new participant or to update the existing participant
	 * of the event.
	 * 
	 * @param participant
	 *            has the participant related details to create or update the
	 *            participant
	 * @return participant details
	 * @throws ParseException
	 */
	public ParticipantRequest createParticipant(ParticipantRequest participant) throws ParseException;

	/**
	 * Service to get the participant details depending on the seqId and event
	 * Id.
	 * 
	 * @param participantRequest
	 *            contains seqId and event Id
	 * @return participant details
	 */
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest,String mail);

	/**
	 * Service to get the participant details depending on the values given in
	 * the search request.
	 * 
	 * @param searchRequest
	 * @return
	 */
	public List<ParticipantRequest> searchParticipants(SearchRequest searchRequest);

	/**
	 * Retrieve <code>Participant</code> from the data store by SeqID.
	 * 
	 * @param participantRequest
	 * @return <code>Participant</code>
	 */
	public Participant findBySeqId(ParticipantRequest participantRequest);
	
	/**
	 * Retrieve <code>Participant</code> from the data store by SeqID.
	 * 
	 * @param participantRequest
	 * @param mail
	 * @return <code>Participant</code>
	 */
	public Participant findBySeqIdAndRole(ParticipantRequest participantRequest, String mail);

	/**
	 * Service to update the participants and generate eWelcomeID by calling MySRCM API.
	 * @param participantRequest
	 * @param id 
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public List<UpdateIntroductionResponse> introduceParticipants(ParticipantIntroductionRequest participantRequest,String userEmailID, int id) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException, ParseException ;

	/**
	 * Service to delete the participants from the Heartfulness backend.
	 * @param participantRequest
	 * @param userEmailID
	 * @return List <code>UpdateIntroductionResponse</code>
	 */
	public List<UpdateIntroductionResponse> deleteParticipantsBySeqID(ParticipantIntroductionRequest participantRequest,String userEmailID);

	/**
	 * Service to update the participant ewelcome Id status and remarks.
	 * @param programId
	 * @param eWelcomeIDStatus
	 * @param remarks
	 */
	public void updateParticipantEWelcomeIDStatuswithProgramID(int programId, String eWelcomeIDStatus, String remarks);
	
    /**
     * 
     * @param originalFilename Name of the excel file
     * @param bytes byte content of the excel file
     * @param accessLog reference to update access log details.
     * @param details List containing event and participant details
     * 
     * @return SuccessResponse or FailureResponse depending on the validations.
     */
    public ResponseEntity<?> validateExcelAndPersistParticipantData(String originalFilename, byte[] bytes,PMPAPIAccessLog accessLog,List<String> details);


}
