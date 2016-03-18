package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is the response for the successful login from MYSRCM
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SrcmAuthenticationResponse {

	@JsonProperty("access_token")
	private String access_token;

	@JsonProperty("token_type")
	private String token_type;

	@JsonProperty("expires_in")
	private String expires_in;

	@JsonProperty("refresh_token")
	private String refresh_token;

	@JsonProperty("scope")
	private String scope;
	
	@JsonProperty("is_pmp_allowed")
	private String ispmpAllowed;
	
	@JsonProperty("is_sahajmarg_allowed")
	private String isSahajmargAllowed;

	
	public SrcmAuthenticationResponse() {
	}

	public SrcmAuthenticationResponse(String access_token, String token_type, String expires_in, String refresh_token,
			String scope, String ispmpAllowed, String isSahajmargAllowed) {
		super();
		this.access_token = access_token;
		this.token_type = token_type;
		this.expires_in = expires_in;
		this.refresh_token = refresh_token;
		this.scope = scope;
		this.ispmpAllowed = ispmpAllowed;
		this.isSahajmargAllowed = isSahajmargAllowed;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getIspmpAllowed() {
		return ispmpAllowed;
	}

	public void setIspmpAllowed(String ispmpAllowed) {
		this.ispmpAllowed = ispmpAllowed;
	}

	public String getIsSahajmargAllowed() {
		return isSahajmargAllowed;
	}

	public void setIsSahajmargAllowed(String isSahajmargAllowed) {
		this.isSahajmargAllowed = isSahajmargAllowed;
	}

	@Override
	public String toString() {
		return "SrcmAuthenticationResponse [access_token=" + access_token + ", token_type=" + token_type
				+ ", expires_in=" + expires_in + ", refresh_token=" + refresh_token + ", scope=" + scope
				+ ", ispmpAllowed=" + ispmpAllowed + ", isSahajmargAllowed=" + isSahajmargAllowed + "]";
	}

}
