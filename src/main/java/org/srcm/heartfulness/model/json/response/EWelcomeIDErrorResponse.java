package org.srcm.heartfulness.model.json.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EWelcomeIDErrorResponse {
	
	private List<String> email;
	
	private List<String> validation;
	
	private String error;
	
	private List<String> mobile;

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
	
	public List<String> getMobile() {
		return mobile;
	}

	public void setMobile(List<String> mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "EWelcomeIDErrorResponse [email=" + email + ", validation=" + validation + ", error=" + error
				+ ", mobile=" + mobile + "]";
	}

}
