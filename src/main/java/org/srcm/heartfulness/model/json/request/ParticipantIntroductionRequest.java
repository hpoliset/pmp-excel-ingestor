package org.srcm.heartfulness.model.json.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParticipantIntroductionRequest {
	
	@JsonProperty("introduced")
	private String introduced;
	
	@JsonProperty("eventId")
	private String eventId;
	
	@JsonProperty("participantIds")
	private List<ParticipantRequest> participantIds;

	public String getIntroduced() {
		return introduced;
	}

	public void setIntroduced(String introduced) {
		this.introduced = introduced;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public List<ParticipantRequest> getParticipantIds() {
		return participantIds;
	}

	public void setParticipantIds(List<ParticipantRequest> participantIds) {
		this.participantIds = participantIds;
	}

	@Override
	public String toString() {
		return "ParticipantIntroductionRequest [introduced=" + introduced + ", eventId=" + eventId
				+ ", participantIds=" + participantIds + "]";
	}
	
	
}
