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
public class CoordinatorAccessControlSuccessResponse implements CoordinatorAccessControlResponse {

	@JsonProperty("success")
	private String success;

	@JsonProperty("success_description")
	private String success_description;

	public CoordinatorAccessControlSuccessResponse(String success, String success_description) {
		super();
		this.success = success;
		this.success_description = success_description;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getSuccess_description() {
		return success_description;
	}

	public void setSuccess_description(String success_description) {
		this.success_description = success_description;
	}

}
