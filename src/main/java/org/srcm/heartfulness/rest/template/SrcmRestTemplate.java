package org.srcm.heartfulness.rest.template;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author HimaSree
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:srcm.api.properties", ignoreUnknownFields = false, prefix = "srcm.oauth2")
public class SrcmRestTemplate extends RestTemplate {

	private String clientId;
	private String clientSecret;
	private String accessTokenUri;
	private String tokenName;
	private String userInfoUri;
	private String createUserUri;
	private boolean proxy=false;
	private String proxyHost="172.16.6.61";
	private int proxyPort=8080;
	private String proxyUser="gvivek";
	private String proxyPassword="123Welcome";
	private String clientIdToCreateProfile;
	private String clientSecretToCreateProfile;
	private String tokenNameToCreateProfile;

	private HttpHeaders httpHeaders;
	private HttpEntity<?> httpEntity;
	private MultiValueMap<String, String> body;
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * method to authenticate the user and return authentication response
	 * @param authenticationRequest
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public SrcmAuthenticationResponse authenticate(AuthenticationRequest authenticationRequest)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		body = new LinkedMultiValueMap<String, String>();
		body.add("username", authenticationRequest.getUsername());
		body.add("password", authenticationRequest.getPassword());
		body.add("grant_type", tokenName);
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set("Authorization", "Basic " + getBase64Credentials(clientId, clientSecret));
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), SrcmAuthenticationResponse.class);
	}

	/**
	 * method to create the user profile
	 * @param user
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public User createUserProfile(User user) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException {
		if (proxy)
			setProxy();
		body = new LinkedMultiValueMap<String, String>();
		body.add("grant_type", tokenNameToCreateProfile);
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set("Authorization",
				"Basic " + getBase64Credentials(clientIdToCreateProfile, clientSecretToCreateProfile));
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		SrcmAuthenticationResponse tokenResponse = mapper.readValue(response.getBody(), SrcmAuthenticationResponse.class);

		httpHeaders.clear();
		body.clear();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.add("Authorization", "Bearer " + tokenResponse.getAccess_token());
		httpEntity = new HttpEntity<Object>(mapper.writeValueAsString(user), httpHeaders);
		ResponseEntity<String> createUserResponse = this.exchange(createUserUri, HttpMethod.POST, httpEntity,
				String.class);
		user = mapper.readValue(createUserResponse.getBody(), User.class);
		return user;
	}

	/**
	 * method to get the user profile
	 * @param accessToken
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Result getUserProfile(String accessToken) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		if (proxy)
			setProxy();
		httpHeaders = new HttpHeaders();
		body = new LinkedMultiValueMap<String, String>();
		httpHeaders.clear();
		httpHeaders.add("Authorization", "Bearer " + accessToken);
		body.clear();
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(userInfoUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), Result.class);
	}

	/**
	 * 
	 */
	private void setProxy() {
		/*CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(proxyUser, proxyPassword));
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.useSystemProperties();
		clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort));
		clientBuilder.setDefaultCredentialsProvider(credsProvider);
		clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		CloseableHttpClient client = clientBuilder.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		this.setRequestFactory(factory);*/
	}

	/**
	 * Method to set the authorization with client Id and Client secret
	 * @return
	 */
	private String getBase64Credentials(String clientId, String clientSecret) {
		String plainCreds = clientId + ":" + clientSecret;
		byte[] plainCredsBytes = plainCreds.getBytes(Charset.forName("US-ASCII"));
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setAccessTokenUri(String accessTokenUri) {
		this.accessTokenUri = accessTokenUri;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public void setUserInfoUri(String userInfoUri) {
		this.userInfoUri = userInfoUri;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public void setCreateUserUri(String createUserUri) {
		this.createUserUri = createUserUri;
	}

	public void setClientIdToCreateProfile(String clientIdToCrateProfile) {
		this.clientIdToCreateProfile = clientIdToCrateProfile;
	}

	public void setClientSecretToCreateProfile(String clientSecretToCrateProfile) {
		this.clientSecretToCreateProfile = clientSecretToCrateProfile;
	}

	public void setTokenNameToCreateProfile(String tokenNameToCreateProfile) {
		this.tokenNameToCreateProfile = tokenNameToCreateProfile;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getAccessTokenUri() {
		return accessTokenUri;
	}

	public String getTokenName() {
		return tokenName;
	}

	public String getUserInfoUri() {
		return userInfoUri;
	}

	public String getCreateUserUri() {
		return createUserUri;
	}

	public boolean isProxy() {
		return proxy;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public String getClientIdToCreateProfile() {
		return clientIdToCreateProfile;
	}

	public String getClientSecretToCreateProfile() {
		return clientSecretToCreateProfile;
	}

	public String getTokenNameToCreateProfile() {
		return tokenNameToCreateProfile;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public HttpEntity<?> getHttpEntity() {
		return httpEntity;
	}

	public MultiValueMap<String, String> getBody() {
		return body;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
	

}
