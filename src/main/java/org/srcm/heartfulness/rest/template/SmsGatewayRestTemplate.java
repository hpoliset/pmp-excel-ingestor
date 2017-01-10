package org.srcm.heartfulness.rest.template;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

	public SMSResponse sendSMS(String mobileNumber, String textMessage) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {

		proxyHelper.setProxy();

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
		httpHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		System.out.println("request params" + mapper.writeValueAsString(smsRequestParams));
		httpEntity = new HttpEntity<Object>(mapper.writeValueAsString(smsRequestParams), httpHeaders);
		ResponseEntity<String> response = this.exchange(sendSmsUri, HttpMethod.POST, httpEntity, String.class);
		return mapper.readValue(response.getBody(), SMSResponse.class);
		// return new SMSResponse();
	}

	public GoogleResponse getLocationdetails(String address, String pincode) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		proxyHelper.setProxy();
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		ResponseEntity<String> response = this.exchange("http://maps.google.com/maps/api/geocode/json?address="
				+ address + "" + "&components=postal_code:" + pincode + "&sensor=false", HttpMethod.GET, httpEntity,
				String.class);
		return mapper.readValue(response.getBody(), GoogleResponse.class);
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
