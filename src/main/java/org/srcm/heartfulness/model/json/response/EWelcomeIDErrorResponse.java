package org.srcm.heartfulness.model.json.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EWelcomeIDErrorResponse {
	
	private List<String> email;
	
	private List<String> validation;
	
	private String error;

	public List<String> getEmail() {
		return email;
	}

	public void setEmail(List<String> email) {
		this.email = email;
	}
	
	public List<String> getValidation() {
		return validation;
	}

	public void setValidation(List<String> validation) {
		this.validation = validation;
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "EWelcomeIDErrorResponse [email=" + email + ", validation=" + validation + ", error=" + error + "]";
	}

}
