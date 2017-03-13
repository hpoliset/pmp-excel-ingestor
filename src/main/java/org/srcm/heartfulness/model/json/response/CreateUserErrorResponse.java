package org.srcm.heartfulness.model.json.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Parser class to hold the error messages from MYSRCM create user API.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserErrorResponse {

	private String detail;
	private List<String> non_field_errors;
	private List<String> email;

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public List<String> getNon_field_errors() {
		return non_field_errors;
	}

	public void setNon_field_errors(List<String> non_field_errors) {
		this.non_field_errors = non_field_errors;
	}

	public List<String> getEmail() {
		return email;
	}

	public void setEmail(List<String> email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "CreateUserErrorResponse [detail=" + detail + ", non_field_errors=" + non_field_errors + ", email="
				+ email + "]";
	}

}
