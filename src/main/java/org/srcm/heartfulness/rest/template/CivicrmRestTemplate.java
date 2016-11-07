/**
 * 
 */
package org.srcm.heartfulness.rest.template;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

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

	@Autowired
	APIAccessLogService apiAccessLogService;

	public void subscribeParticipantToCivicrm(String name,String userEmail)
			throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		PMPAPIAccessLog accessLog = null;
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.FIRST_NAME, name);
		bodyParams.add(RestTemplateConstants.LAST_NAME, "");
		bodyParams.add(RestTemplateConstants.USER_EMAIL, userEmail);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		try{
			accessLog = new PMPAPIAccessLog(userEmail, null,EmailLogConstants.SUBSCRIBE_VIA_CIVICRM,DateUtils.getCurrentTimeInMilliSec(), 
					null, ErrorConstants.STATUS_FAILED, null,String.valueOf(httpEntity));
			int id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		}catch(Exception ex){
			LOGGER.error("Failed to insert record in pmp access log table for user{}",userEmail);
		}
		ResponseEntity<String> response = null;
		try{
			 response = this.exchange(civicrmAPI, HttpMethod.POST, httpEntity, String.class);
		}catch(Exception ex){
			LOGGER.error("Stack Trace=={}",ex);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
			LOGGER.error("Failed to call civicrm api to subscribe participant{}",userEmail);
		}
		try{
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(String.valueOf(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		}catch(Exception ex){
			LOGGER.error("Failed to update record in pmp access log table");
		}
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
