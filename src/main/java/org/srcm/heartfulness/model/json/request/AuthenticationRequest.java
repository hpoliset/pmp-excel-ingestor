package org.srcm.heartfulness.model.json.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class is the request for the login through MYSRCM.
 * 
 * @author HimaSree
 *
 */
public class AuthenticationRequest {

	private String username;
	private String password;

	@JsonIgnore
	private String refreshToken;

	public AuthenticationRequest() {
		super();
	}

	public AuthenticationRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
