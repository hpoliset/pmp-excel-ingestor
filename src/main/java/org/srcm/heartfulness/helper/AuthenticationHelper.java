package org.srcm.heartfulness.helper;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
@ConfigurationProperties(locations = "classpath:dev.srcm.api.properties", ignoreUnknownFields = true, prefix = "srcm.oauth2")
public class AuthenticationHelper {

	private String clientId;
	
	private String clientSecret;

	@NotNull
	private MobileCredentials mobile;

	/**
	 * Class to hold the abyasi related information by reading from properties
	 * file.
	 */
	public static class MobileCredentials {

		private String clientId;

		private String clientSecret;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public MobileCredentials getMobile() {
		return mobile;
	}

	public void setMobile(MobileCredentials mobile) {
		this.mobile = mobile;
	}
	
	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	public SrcmAuthenticationResponse getClientCredentialsandAuthenticate(AuthenticationRequest authenticationRequest, String userAgent) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if(userAgent.indexOf("Mobile")== -1){
			System.out.println(userAgent.toString());
			return srcmRestTemplate.authenticate(authenticationRequest,mobile.clientId,mobile.clientSecret);
		}else{
			System.out.println(userAgent.toString());
			return srcmRestTemplate.authenticate(authenticationRequest,clientId,clientSecret);
		}
		
	}
	
	
	public SrcmAuthenticationResponse getClientCredentialsandAuthenticateUser(AuthenticationRequest authenticationRequest, String requestURL) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if("/pmp/api/mobile/authenticate".equalsIgnoreCase(requestURL.toString())){
			System.out.println(requestURL.toString());
			return srcmRestTemplate.authenticate(authenticationRequest,mobile.clientId,mobile.clientSecret);
		}else{
			System.out.println(requestURL.toString());
			return srcmRestTemplate.authenticate(authenticationRequest,clientId,clientSecret);
		}
		
	}
	
	

}
