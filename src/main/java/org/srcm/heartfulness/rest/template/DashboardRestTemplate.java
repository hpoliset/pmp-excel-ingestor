/**
 * 
 */
package org.srcm.heartfulness.rest.template;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.MysrcmGroup;
import org.srcm.heartfulness.model.json.response.MysrcmPositionType;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Koustav Dutta
 *
 */
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(locations = "classpath:prod.srcm.api.properties", ignoreUnknownFields = true, prefix = "srcm.oauth2")
public class DashboardRestTemplate extends RestTemplate{

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardRestTemplate.class);

	@Value("${proxy}")
	private boolean proxy;

	@Value("${proxyHost}")
	private String proxyHost;

	@Value("${proxyPort}")
	private int proxyPort;

	@Value("${proxyUser}")
	private String proxyUser;

	@Value("${proxyPassword}")
	private String proxyPassword;

	private String getCoordinatorPositionUri;
	private String getPositionTypeUri;
	private String getGroupTypeUri;
	
	private String clientIdToCreateProfile;
	private String clientSecretToCreateProfile;
	private String tokenNameToCreateProfile;
	private String accessTokenUri;
	private String getAbhyasiProfileUri;
	
	
	private ObjectMapper mapper = new ObjectMapper();


	public boolean isProxy() {
		return proxy;
	}


	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}


	public String getProxyHost() {
		return proxyHost;
	}


	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}


	public int getProxyPort() {
		return proxyPort;
	}


	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}


	public String getProxyUser() {
		return proxyUser;
	}


	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}


	public String getProxyPassword() {
		return proxyPassword;
	}


	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}


	public String getGetPositionTypeUri() {
		return getPositionTypeUri;
	}


	public void setGetPositionTypeUri(String getPositionTypeUri) {
		this.getPositionTypeUri = getPositionTypeUri;
	}


	public String getGetCoordinatorPositionUri() {
		return getCoordinatorPositionUri;
	}


	public void setGetCoordinatorPositionUri(String getCoordinatorPositionUri) {
		this.getCoordinatorPositionUri = getCoordinatorPositionUri;
	}

	public String getGetGroupTypeUri() {
		return getGroupTypeUri;
	}


	public void setGetGroupTypeUri(String getGroupTypeUri) {
		this.getGroupTypeUri = getGroupTypeUri;
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


	public String getTokenNameToCreateProfile() {
		return tokenNameToCreateProfile;
	}


	public void setTokenNameToCreateProfile(String tokenNameToCreateProfile) {
		this.tokenNameToCreateProfile = tokenNameToCreateProfile;
	}
	

	public String getAccessTokenUri() {
		return accessTokenUri;
	}


	public void setAccessTokenUri(String accessTokenUri) {
		this.accessTokenUri = accessTokenUri;
	}

	public String getGetAbhyasiProfileUri() {
		return getAbhyasiProfileUri;
	}


	public void setGetAbhyasiProfileUri(String getAbhyasiProfileUri) {
		this.getAbhyasiProfileUri = getAbhyasiProfileUri;
	}


	public PositionAPIResult findCoordinatorPosition(String authToken) throws JsonParseException, JsonMappingException, IOException{

		setProxy();

		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BEARER_TOKEN + authToken);
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(getCoordinatorPositionUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), PositionAPIResult.class);

	}

	public PositionAPIResult findCoordinatorPosition(String authToken,String nextUrl) throws JsonParseException, JsonMappingException, IOException{

		setProxy();

		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BEARER_TOKEN + authToken);
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(nextUrl, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), PositionAPIResult.class);

	}
	
	
	public MysrcmPositionType getPositionType() throws JsonParseException, JsonMappingException, IOException {

		setProxy();

		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(getPositionTypeUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), MysrcmPositionType.class);

	}
	
	public MysrcmPositionType getPositionType(String nextUrl) throws JsonParseException, JsonMappingException, IOException {

		setProxy();

		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(nextUrl, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), MysrcmPositionType.class);

	}
	
	public MysrcmGroup getMysrcmGroupType(String type, String zoneOrCenterValue ) throws JsonParseException, JsonMappingException, IOException{
		
		setProxy();
		String groupUri = getGroupTypeUri + type + "&name__iexact="+zoneOrCenterValue;
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(groupUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), MysrcmGroup.class);

	}
	
	
	public SrcmAuthenticationResponse getAccessToken() throws JsonParseException, JsonMappingException, IOException{

		setProxy();
		
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.clear();
		bodyParams.add(RestTemplateConstants.GRANT_TYPE, tokenNameToCreateProfile);
		bodyParams.add(RestTemplateConstants.CLIENT_ID, clientIdToCreateProfile);
		bodyParams.add(RestTemplateConstants.CLIENT_SECRET, clientSecretToCreateProfile);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), SrcmAuthenticationResponse.class);
	}
	
	public PositionAPIResult findCoordinatorPosition(String authToken,int srcmGroupId,int positionId) throws JsonParseException, JsonMappingException, IOException{

		setProxy();
		
		String positionUri = getCoordinatorPositionUri + "&active=True&srcm_group="+srcmGroupId + "&position_type="+positionId;
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BEARER_TOKEN + authToken);
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(positionUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), PositionAPIResult.class);

	}
	
	public AbhyasiUserProfile getAbyasiProfile(String authToken,int assignedPartnerId) throws JsonParseException, JsonMappingException, IOException{
		
		setProxy();
		String abhyasiProfileUri = getAbhyasiProfileUri+assignedPartnerId + "?format=json";
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BEARER_TOKEN + authToken);
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(abhyasiProfileUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), AbhyasiUserProfile.class);
		
	}

	public void setProxy() {

		/*if (proxy) {

			CredentialsProvider credsProvider = new BasicCredentialsProvider();
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
			this.setRequestFactory(factory);

		}*/
	}

}
