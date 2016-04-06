package org.srcm.heartfulness.rest.template;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
 * This class is template class and communicates with srcm by calling srcm api
 * @author HimaSree
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.srcm.api.properties", ignoreUnknownFields = false, prefix = "srcm.oauth2")
public class SrcmRestTemplate extends RestTemplate {

	private String clientId;
	private String clientSecret;
	private String accessTokenUri;
	private String tokenName;
	private String refreshTokenName;
	private String userInfoUri;
	private String createUserUri;
	private boolean proxy = true;
	private String proxyHost = "10.1.28.10";
	private int proxyPort = 8080;
	private String proxyUser = "gvivek";
	private String proxyPassword = "123Welcome1";
	private String clientIdToCreateProfile;
	private String clientSecretToCreateProfile;
	private String tokenNameToCreateProfile;

	private HttpHeaders httpHeaders;
	private HttpEntity<?> httpEntity;
	private MultiValueMap<String, String> body;
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * method to authenticate the user with srcm and return the response with token details
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
	 * method to get the refresh token details from srcm by calling srcm api
	 * @param refreshtoken
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public SrcmAuthenticationResponse getRefreshTokenDetails(String refreshtoken) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		body = new LinkedMultiValueMap<String, String>();
		body.add("refresh_token", refreshtoken.toString());
		body.add("grant_type", refreshTokenName);
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set("Authorization", "Basic " + getBase64Credentials(clientId, clientSecret));
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), SrcmAuthenticationResponse.class);
	}

	/**
	 * method to create the user profile in srcm and pmp by calling srcm api
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
		SrcmAuthenticationResponse tokenResponse = mapper.readValue(response.getBody(),
				SrcmAuthenticationResponse.class);

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
	 * method to get the user profile with token details by calling srcm api
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
		body = new LinkedMultiValueMap<String, String>();
		httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add("Authorization", "Bearer " + accessToken);
		body.clear();
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(userInfoUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), Result.class);
	}

	/**
	 * method to set the proxy (development use only)
	 */
	private void setProxy() {
		  CredentialsProvider credsProvider = new BasicCredentialsProvider();
		  credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,AuthScope.ANY_PORT), 
				  new UsernamePasswordCredentials(proxyUser, proxyPassword)); 
		  HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		  clientBuilder.useSystemProperties();
		  clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort));
		  clientBuilder.setDefaultCredentialsProvider(credsProvider);
		  clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		  CloseableHttpClient client =  clientBuilder.build();
		  HttpComponentsClientHttpRequestFactory factory
		  = new HttpComponentsClientHttpRequestFactory();
		  factory.setHttpClient(client); this.setRequestFactory(factory);
	}

	/**
	 * method to get the client id and client secret in the form of base64 credentials
	 * @return base64CredsBytes
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

	public void setRefreshTokenName(String refreshTokenName) {
		this.refreshTokenName = refreshTokenName;
	}

	public void setUserInfoUri(String userInfoUri) {
		this.userInfoUri = userInfoUri;
	}

	public void setCreateUserUri(String createUserUri) {
		this.createUserUri = createUserUri;
	}

	public void setClientIdToCreateProfile(String clientIdToCreateProfile) {
		this.clientIdToCreateProfile = clientIdToCreateProfile;
	}

	public void setClientSecretToCreateProfile(String clientSecretToCreateProfile) {
		this.clientSecretToCreateProfile = clientSecretToCreateProfile;
	}

	public void setTokenNameToCreateProfile(String tokenNameToCreateProfile) {
		this.tokenNameToCreateProfile = tokenNameToCreateProfile;
	}

}
