package org.srcm.heartfulness.service;

import java.text.ParseException;

import org.srcm.heartfulness.model.json.request.ParticipantRequest;

/**
 * This class contains participant related services
 * @author himasreev
 *
 */
public interface PmpParticipantService {
	
	/**
	 * service to create new participant or to update the existing participant of the event
	 * @param participant has the participant related details to create or update the participant
	 * @return participant details
	 * @throws ParseException
	 */
	public ParticipantRequest createParticipant(ParticipantRequest participant) throws ParseException;

	
	/**
	 * service to get the participant details depending on the seqId and event Id
	 * @param participantRequest contains seqId and event Id
	 * @return  participant details
	 */
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest);

}
