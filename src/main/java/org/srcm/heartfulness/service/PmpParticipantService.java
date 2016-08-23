package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
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
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest);

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
	 * Service to update the participants in
	 * @param participantRequest
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<UpdateIntroductionResponse> introduceParticipants(ParticipantIntroductionRequest participantRequest) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException ;

	public List<UpdateIntroductionResponse> deleteparticipantsBySeqID(ParticipantIntroductionRequest participantRequest,String userEmailID);

}
