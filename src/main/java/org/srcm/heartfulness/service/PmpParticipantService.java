package org.srcm.heartfulness.service;

import java.text.ParseException;
import java.util.List;

import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;

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

}
