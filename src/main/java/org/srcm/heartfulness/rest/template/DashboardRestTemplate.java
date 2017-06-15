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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Koustav Dutta
 *
 */
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(locations = "classpath:dev.srcm.api.properties", ignoreUnknownFields = true, prefix = "srcm.oauth2")
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
	private ObjectMapper mapper = new ObjectMapper();


	public String getGetCoordinatorPositionUri() {
		return getCoordinatorPositionUri;
	}


	public void setGetCoordinatorPositionUri(String getCoordinatorPositionUri) {
		this.getCoordinatorPositionUri = getCoordinatorPositionUri;
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
