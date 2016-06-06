package org.srcm.heartfulness.model.json.response;

import org.srcm.heartfulness.model.json.sms.response.MessageData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailverificationResponse {
	
	@JsonProperty(value="result")
	private String result;

	@JsonProperty(value="reason")
	private String reason;

	@JsonProperty(value="email")
	private String email;

	@JsonProperty(value="success")
	private String success;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return "EmailverificationResponse [result=" + result + ", reason=" + reason + ", email=" + email + ", success="
				+ success + "]";
	}
	
}
