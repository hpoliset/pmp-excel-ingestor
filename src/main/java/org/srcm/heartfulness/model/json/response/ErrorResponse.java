package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is the response for the unauthorized login attempt from MYSRCM
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse implements PMPResponse {

	@JsonProperty("error_description")
	private String error_description;

	@JsonProperty("error")
	private String error;

	public ErrorResponse() {
		super();
	}

	public ErrorResponse(String error,String error_description) {
		super();
		this.error = error;
		this.error_description = error_description;
	}

	public String getError_description() {
		return error_description;
	}

	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
