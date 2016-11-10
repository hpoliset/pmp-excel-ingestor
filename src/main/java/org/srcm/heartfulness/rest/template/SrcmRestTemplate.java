package org.srcm.heartfulness.rest.template;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;

import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.model.Aspirant;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.AbhyasiResult;
import org.srcm.heartfulness.model.json.response.CitiesAPIResponse;
import org.srcm.heartfulness.model.json.response.GeoSearchResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is template class and communicates with MySRCM by calling MySRCM
 * API.
 * 
 * @author HimaSree
 *
 */
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(locations = "classpath:prod.srcm.api.properties", ignoreUnknownFields = true, prefix = "srcm.oauth2")
public class SrcmRestTemplate extends RestTemplate {

	private String clientId;
	private String clientSecret;
	private String accessTokenUri;
	private String tokenName;
	private String refreshTokenName;
	private String userInfoUri;
	private String createUserUri;
	
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
	
	private String clientIdToCreateProfile;
	private String clientSecretToCreateProfile;
	private String tokenNameToCreateProfile;

	private ObjectMapper mapper = new ObjectMapper();

	@NotNull
	private AbyasiInfo abyasi;

	/**
	 * Class to hold the abyasi related information by reading from properties
	 * file.
	 */
	public static class AbyasiInfo {

		private String abyasiInfoUri;

		private String geosearch;

		private String createAspirant;

		private String citiesapi;

		public String getAbyasiInfoUri() {
			return abyasiInfoUri;
		}

		public void setAbyasiInfoUri(String abyasiInfoUri) {
			this.abyasiInfoUri = abyasiInfoUri;
		}

		public String getGeosearch() {
			return geosearch;
		}

		public void setGeosearch(String geosearch) {
			this.geosearch = geosearch;
		}

		public String getCreateAspirant() {
			return createAspirant;
		}

		public void setCreateAspirant(String createAspirant) {
			this.createAspirant = createAspirant;
		}

		public String getCitiesapi() {
			return citiesapi;
		}

		public void setCitiesapi(String citiesapi) {
			this.citiesapi = citiesapi;
		}

	}

	public AbyasiInfo getAbyasi() {
		return abyasi;
	}

	public void setAbyasi(AbyasiInfo abyasi) {
		this.abyasi = abyasi;
	}

	/**
	 * Method to authenticate the user with MySRCM and return the response with
	 * token details.
	 * 
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
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.PARAMS_USERNAME, authenticationRequest.getUsername());
		bodyParams.add(RestTemplateConstants.PARAMS_PASSWORD, authenticationRequest.getPassword());
		bodyParams.add(RestTemplateConstants.GRANT_TYPE, tokenName);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BASIC_AUTHORIZATION
				+ getBase64Credentials(clientId, clientSecret));
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), SrcmAuthenticationResponse.class);
	}

	/**
	 * Method to get the user profile with token details by calling MySRCM API.
	 * 
	 * @param accessToken
	 * @param id
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Result getUserProfile(String accessToken) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException, ParseException {
		if (proxy)
			setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.clear();
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BEARER_TOKEN + accessToken);
		bodyParams.clear();
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(userInfoUri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), Result.class);
	}

	/**
	 * Method to create the user profile in MySRCM and PMP by calling MySRCM
	 * API.
	 * 
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
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.GRANT_TYPE, tokenNameToCreateProfile);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BASIC_AUTHORIZATION
				+ getBase64Credentials(clientIdToCreateProfile, clientSecretToCreateProfile));
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		SrcmAuthenticationResponse tokenResponse = mapper.readValue(response.getBody(),
				SrcmAuthenticationResponse.class);

		httpHeaders.clear();
		bodyParams.clear();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION,
				RestTemplateConstants.BEARER_TOKEN + tokenResponse.getAccess_token());
		httpEntity = new HttpEntity<Object>(mapper.writeValueAsString(user), httpHeaders);
		ResponseEntity<String> createUserResponse = this.exchange(createUserUri, HttpMethod.POST, httpEntity,
				String.class);
		return mapper.readValue(createUserResponse.getBody(), User.class);
	}

	/**
	 * Method to get the abhyasi user profile with token details by calling
	 * MySRCM API.
	 * 
	 * @param refNo
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public AbhyasiResult getAbyasiProfile(String refNo) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		if (proxy)
			setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.GRANT_TYPE, tokenNameToCreateProfile);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BASIC_AUTHORIZATION
				+ getBase64Credentials(clientIdToCreateProfile, clientSecretToCreateProfile));
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> tokenResponse = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		SrcmAuthenticationResponse authResponse = mapper.readValue(tokenResponse.getBody(),
				SrcmAuthenticationResponse.class);

		httpHeaders.clear();
		bodyParams.clear();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION,
				RestTemplateConstants.BEARER_TOKEN + authResponse.getAccess_token());
		httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(abyasi.abyasiInfoUri + "&ref=" + refNo, HttpMethod.GET,
				httpEntity, String.class);
		return mapper.readValue(response.getBody(), AbhyasiResult.class);
	}

	/**
	 * Method to get the already generated e-welcomeIDs for the participant by
	 * calling MySRCM API.
	 * 
	 * @param refNo
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public AbhyasiResult fetchparticipanteWelcomeID(String email) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		if (proxy)
			setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.GRANT_TYPE, tokenNameToCreateProfile);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BASIC_AUTHORIZATION
				+ getBase64Credentials(clientIdToCreateProfile, clientSecretToCreateProfile));
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> tokenResponse = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		SrcmAuthenticationResponse authResponse = mapper.readValue(tokenResponse.getBody(),
				SrcmAuthenticationResponse.class);

		httpHeaders.clear();
		bodyParams.clear();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION,
				RestTemplateConstants.BEARER_TOKEN + authResponse.getAccess_token());
		httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(abyasi.abyasiInfoUri + "&email__iexact=" + email,
				HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), AbhyasiResult.class);
	}

	/**
	 * Method to get the city, state and country codes for the given address by
	 * calling MySRCM API.
	 * 
	 * @param address
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public GeoSearchResponse geoSearch(String address) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		if (proxy)
			setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.PARAMS_FORMATTED_ADDRESS, address);
		System.out.println("address : " + address);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(abyasi.geosearch, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), GeoSearchResponse.class);
	}

	/**
	 * Method to Create Aspirant and generate e-WelcomeID by calling MySRCM API.
	 * 
	 * @param aspirant
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public UserProfile createAspirant(Aspirant aspirant) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {
		if (proxy)
			setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.GRANT_TYPE, tokenNameToCreateProfile);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, RestTemplateConstants.BASIC_AUTHORIZATION
				+ getBase64Credentials(clientIdToCreateProfile, clientSecretToCreateProfile));
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		ResponseEntity<String> response = this.exchange(accessTokenUri, HttpMethod.POST, httpEntity, String.class);
		SrcmAuthenticationResponse tokenResponse = mapper.readValue(response.getBody(),
				SrcmAuthenticationResponse.class);

		httpHeaders.clear();
		bodyParams.clear();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.add(RestTemplateConstants.AUTHORIZATION,
				RestTemplateConstants.BEARER_TOKEN + tokenResponse.getAccess_token());
		httpEntity = new HttpEntity<Object>(mapper.writeValueAsString(aspirant), httpHeaders);
		ResponseEntity<String> Response = this.exchange(abyasi.createAspirant, HttpMethod.POST, httpEntity,
				String.class);
		return mapper.readValue(Response.getBody(), UserProfile.class);
	}

	public CitiesAPIResponse getCityName(int cityId) throws JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(null, httpHeaders);
		ResponseEntity<String> response = this.exchange(abyasi.citiesapi + cityId + "/?format=json", HttpMethod.GET,
				httpEntity, String.class);
		return mapper.readValue(response.getBody(), CitiesAPIResponse.class);
	}

	/**
	 * Method to set the proxy (development use only)
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
	 * Method to get the client id and client secret in the form of base64
	 * credentials.
	 * 
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
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
