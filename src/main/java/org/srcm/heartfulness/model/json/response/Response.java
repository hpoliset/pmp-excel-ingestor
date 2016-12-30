package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class holds the success response for the web service endpoints.
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

	private String status;

	private String description;

	public Response() {
		super();
	}

	public Response(String status, String description) {
		super();
		this.status = status;
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "SuccessResponse [status=" + status + ", description=" + description + "]";
	}

}
