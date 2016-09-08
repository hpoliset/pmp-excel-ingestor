package org.srcm.heartfulness.helper;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.request.CreateUserRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
@ConfigurationProperties(locations = "classpath:dev.srcm.api.properties", ignoreUnknownFields = true, prefix = "srcm.oauth2")
public class MySRCMIntegrationHelper {

	private String clientId;
	
	private String clientSecret;
	
	private String clientIdToCreateProfile;
	
	private String clientSecretToCreateProfile;

	@NotNull
	private MobileCredentials mobile;

	/**
	 * Class to hold the abyasi related information by reading from properties
	 * file.
	 */
	public static class MobileCredentials {

		private String clientId;

		private String clientSecret;
		
		private String clientIdToCreateProfile;
		
		private String clientSecretToCreateProfile;
		
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

		public String getClientIdToCreateProfile() {
			return clientIdToCreateProfile;
		}

		public void setClientIdToCreateProfile(String clientIdToCreateProfile) {
			this.clientIdToCreateProfile = clientIdToCreateProfile;
		}

		public String getClientSecretToCreateProfile() {
			return clientSecretToCreateProfile;
		}

		public void setClientSecretToCreateProfile(String clientSecretToCreateProfile) {
			this.clientSecretToCreateProfile = clientSecretToCreateProfile;
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
	
	public String getClientIdToCreateProfile() {
		return clientIdToCreateProfile;
	}

	public void setClientIdToCreateProfile(String clientIdToCreateProfile) {
		this.clientIdToCreateProfile = clientIdToCreateProfile;
	}

	public String getClientSecretToCreateProfile() {
		return clientSecretToCreateProfile;
	}

	public void setClientSecretToCreateProfile(String clientSecretToCreateProfile) {
		this.clientSecretToCreateProfile = clientSecretToCreateProfile;
	}

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	public SrcmAuthenticationResponse getClientCredentialsandAuthenticate(AuthenticationRequest authenticationRequest, String userAgent) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if(userAgent.indexOf("Mobile")== -1){
			return srcmRestTemplate.authenticate(authenticationRequest,mobile.clientId,mobile.clientSecret);
		}else{
			System.out.println(userAgent.toString());
			return srcmRestTemplate.authenticate(authenticationRequest,clientId,clientSecret);
		}
		
	}
	
	public SrcmAuthenticationResponse getClientCredentialsandAuthenticateUser(AuthenticationRequest authenticationRequest, String requestURL) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if(EndpointConstants.MOBILE_AUTHENTICATION_ENDPOINT.equalsIgnoreCase(requestURL.toString())){
			return srcmRestTemplate.authenticate(authenticationRequest,mobile.clientId,mobile.clientSecret);
		}else{
			System.out.println(requestURL.toString());
			return srcmRestTemplate.authenticate(authenticationRequest,clientId,clientSecret);
		}
		
	}

	public User getClientCredentialsandCreateUser(CreateUserRequest user, String requestURL) throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if(EndpointConstants.MOBILE_CREATE_USER_ENDPOINT.equalsIgnoreCase(requestURL.toString())){
			return srcmRestTemplate.createUserProfile(user,mobile.clientIdToCreateProfile,mobile.clientSecretToCreateProfile);
		}else{
			System.out.println(requestURL.toString());
			return srcmRestTemplate.createUserProfile(user,clientIdToCreateProfile,clientSecretToCreateProfile);
		}
		
	}
	
	

}
