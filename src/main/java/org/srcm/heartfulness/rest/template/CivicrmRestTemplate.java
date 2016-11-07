/**
 * 
 */
package org.srcm.heartfulness.rest.template;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Koustav Dutta
 *
 */
@Component
@PropertySource("classpath:application.properties")
@PropertySource("classpath:dev.civicrm.api.properties")
public class CivicrmRestTemplate extends RestTemplate {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CivicrmRestTemplate.class);
	
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
	
	@Value("${civicrm.subscribe.api}")
	private String civicrmAPI;
	
	public String subscribeParticipantToCivicrm(String name,String userEmail)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.FIRST_NAME, name);
		bodyParams.add(RestTemplateConstants.LAST_NAME, "");
		bodyParams.add(RestTemplateConstants.USER_EMAIL, userEmail);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		LOGGER.error("Request=={}",httpEntity);
		ResponseEntity<String> response = this.exchange(civicrmAPI, HttpMethod.POST, httpEntity, String.class);
		LOGGER.error("Response=={}",response);
		return String.valueOf(response.getBody());
	}
	
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
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
}
