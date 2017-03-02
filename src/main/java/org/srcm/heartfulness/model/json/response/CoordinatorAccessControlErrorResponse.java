/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Koustav Dutta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoordinatorAccessControlErrorResponse implements CoordinatorAccessControlResponse{

	@JsonProperty("error")
	private String error;

	@JsonProperty("error_description")
	private String error_description;


	public CoordinatorAccessControlErrorResponse(String error, String error_description) {
		super();
		this.error = error;
		this.error_description = error_description;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError_description() {
		return error_description;
	}

	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

}
