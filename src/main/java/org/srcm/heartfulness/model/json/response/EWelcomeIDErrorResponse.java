package org.srcm.heartfulness.model.json.response;

import java.util.List;

public class EWelcomeIDErrorResponse {
	
	private List<String> email;

	public List<String> getEmail() {
		return email;
	}

	public void setEmail(List<String> email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "EWelcomeIDErrorResponse [email=" + email + "]";
	}
	
}
