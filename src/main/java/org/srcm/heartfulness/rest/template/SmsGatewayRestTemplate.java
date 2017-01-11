package org.srcm.heartfulness.rest.template;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.model.json.googleapi.response.GoogleResponse;
import org.srcm.heartfulness.model.json.sms.request.Account;
import org.srcm.heartfulness.model.json.sms.request.Messages;
import org.srcm.heartfulness.model.json.sms.request.SMSRequest;
import org.srcm.heartfulness.model.json.sms.response.SMSResponse;
import org.srcm.heartfulness.proxy.ProxyHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author HimaSree
 *
 */
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(locations = "classpath:dev.sms.gateway.properties", ignoreUnknownFields = false, prefix = "gateway")
public class SmsGatewayRestTemplate extends RestTemplate {

	@Autowired
	ProxyHelper proxyHelper;

	private String username;
	private String password;
	private String apiKey;
	private String senderid;
	private String channel;
	private String route;
	private String sendSmsUri;
	private String DCS;

	private ObjectMapper mapper = new ObjectMapper();
	private HttpHeaders httpHeaders = new HttpHeaders();
	private MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
	private HttpEntity<?> httpEntity;

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

	public SMSResponse sendSMS(String mobileNumber, String textMessage) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {

		setProxy();

		Account account = new Account();
		account.setUser(username);
		account.setPassword(password);
		account.setSenderId(senderid);
		account.setDCS(DCS);
		account.setGroupId(null);
		account.setSchedTime(null);
		account.setChannel(channel);

		Messages messages = new Messages();
		messages.setNumber(mobileNumber);
		messages.setText(textMessage);

		SMSRequest smsRequestParams = new SMSRequest();
		smsRequestParams.setAccount(account);
		smsRequestParams.setMessages(new Messages[] { messages });

		httpHeaders = new HttpHeaders();
		httpHeaders.set(RestTemplateConstants.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		httpHeaders.set(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		System.out.println(RestTemplateConstants.REQUEST_PARAMS + mapper.writeValueAsString(smsRequestParams));
		httpEntity = new HttpEntity<Object>(mapper.writeValueAsString(smsRequestParams), httpHeaders);
		ResponseEntity<String> response = this.exchange(sendSmsUri, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), SMSResponse.class);
		// return new SMSResponse();
	}

	public GoogleResponse getLocationdetails(String address, String pincode) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		setProxy();
		httpHeaders = new HttpHeaders();
		httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange(SMSConstants.SMS_GOOGLE_MAPS_API_PART_1 + address
				+ SMSConstants.SMS_GOOGLE_MAPS_API_PART_2 + pincode + SMSConstants.SMS_GOOGLE_MAPS_API_PART_3,
				HttpMethod.GET, httpEntity, String.class);
		return mapper.readValue(response.getBody(), GoogleResponse.class);
	}

	/**
	 * Method to set the proxy (development use only)
	 */
	public void setProxy() {
		if (proxy) {
		/*	CredentialsProvider credsProvider = new BasicCredentialsProvider();
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
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setSendSmsUri(String sendSmsUri) {
		this.sendSmsUri = sendSmsUri;
	}

	public void setDCS(String dCS) {
		DCS = dCS;
	}

	public void setBody(MultiValueMap<String, String> body) {
		this.body = body;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
