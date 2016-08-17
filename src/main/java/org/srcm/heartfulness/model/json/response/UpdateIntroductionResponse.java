package org.srcm.heartfulness.model.json.response;

import java.util.List;

/**
 * This class is the response for the updating introduced status for the
 * participants
 * 
 * @author himasreev
 *
 */
public class UpdateIntroductionResponse {

	private String seqId;

	private String status;

	private List<String> description;

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
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

	public UpdateIntroductionResponse(String seqId, String status, List<String> description) {
		super();
		this.seqId = seqId;
		this.status = status;
		this.description = description;
	}

	public UpdateIntroductionResponse() {
		super();
	}

	@Override
	public String toString() {
		return "UpdateIntroductionResponse [seqId=" + seqId + ", status=" + status + ", description=" + description
				+ "]";
	}

}
