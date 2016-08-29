package org.srcm.heartfulness.model.json.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is the response for the updating introduced status for the
 * participants
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateIntroductionResponse {

	private String seqId;
	
	private String participantName;

	private String status;

	private List<String> description;

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	
	public String getParticipantName() {
		return participantName;
	}

	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public UpdateIntroductionResponse(String seqId, String participantName, String status, List<String> description) {
		super();
		this.seqId = seqId;
		this.participantName = participantName;
		this.status = status;
		this.description = description;
	}

	public UpdateIntroductionResponse() {
		super();
	}

	@Override
	public String toString() {
		return "UpdateIntroductionResponse [seqId=" + seqId + ", participantName=" + participantName + ", status="
				+ status + ", description=" + description + "]";
	}

}
