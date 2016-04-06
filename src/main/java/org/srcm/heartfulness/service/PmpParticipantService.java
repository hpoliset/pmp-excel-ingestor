package org.srcm.heartfulness.service;

import java.text.ParseException;

import org.srcm.heartfulness.model.json.request.ParticipantRequest;

public interface PmpParticipantService {
	
	public ParticipantRequest createParticipant(ParticipantRequest participant) throws ParseException;

	ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest);

}
