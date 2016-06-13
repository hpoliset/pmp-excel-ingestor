package org.srcm.heartfulness.rest.template;

import java.io.IOException;
import java.util.List;

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
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.model.json.response.EmailverificationResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a template class, communicates with quickEmailVerfication by calling
 * quick Email Verfication api
 * 
 * @author rramesh
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.quickmailverification.api.properties", ignoreUnknownFields = false, prefix = "quickmailverification")
public class QuickEmailVerificationRestTemplate extends RestTemplate {

	private String emailVerificationUri;
	private String mailapikey;

	private boolean proxy = true;
	private String proxyHost = "10.1.28.10";
	private int proxyPort = 8080;
	private String proxyUser = "rramesh";
	private String proxyPassword = "123Welcome";

	private HttpHeaders httpHeaders;
	private HttpEntity<?> httpEntity;
	private MultiValueMap<String, String> body;
	private ObjectMapper mapper = new ObjectMapper();

	public EmailverificationResponse verifyEmailAddress(String mailID) throws JsonParseException, JsonMappingException,
			IOException {
		if (proxy)
			setProxy();
		String uri = null;
		uri = emailVerificationUri + mailID + "&apikey=" + mailapikey;
		System.out.println("uri- " + uri);
		httpHeaders = new HttpHeaders();
		// httpHeaders.add("Content-Type",MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(uri, HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), EmailverificationResponse.class);
	}

	public QuickEmailVerificationRestTemplate() {
	}

	public QuickEmailVerificationRestTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}

	public QuickEmailVerificationRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	public String getEmailVerificationUri() {
		return emailVerificationUri;
	}

	public void setEmailVerificationUri(String emailVerificationUri) {
		this.emailVerificationUri = emailVerificationUri;
	}

	public String getMailapikey() {
		return mailapikey;
	}

	public void setMailapikey(String mailapikey) {
		this.mailapikey = mailapikey;
	}

	public void setProxy() {
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
	}

}
