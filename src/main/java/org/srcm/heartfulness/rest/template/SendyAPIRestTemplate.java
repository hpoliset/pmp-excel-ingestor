package org.srcm.heartfulness.rest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.model.WelcomeMailDetails;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sun.mail.smtp.SMTPMessage;

/**
 * This class is template class and communicates with srcm by calling srcm api
 * 
 * @author HimaSree
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.sendy.api.properties", ignoreUnknownFields = false, prefix = "sendy")
public class SendyAPIRestTemplate extends RestTemplate {
	
	private VelocityEngine velocityEngine=new VelocityEngine();

	private String subscribeUri;
	private String sendMailUri;
	private String apiKey;
	private String subscriberListID;
	private String fromName;
	private String fromMailID;
	private String replyToMailID;
	private String subject;
	private String sendCampaign;
	private String sendFlag;

	private boolean proxy = false;
	private String proxyHost = "10.1.28.10";
	private int proxyPort = 8080;
	private String proxyUser = "gvivek";
	private String proxyPassword = "123Welcome";

	private HttpHeaders httpHeaders;
	private HttpEntity<?> httpEntity;
	private MultiValueMap<String, String> body;
	private String unsubscribeUri;
	private String scheduledCronJobUri;

	private String username;
	private String password;
	private String errorAlertMailSubject;
	private String errorMailTemplate;
	private String host;
	private String toMailIds;
	private String ccMailIds;
	
	private VelocityContext context;

	/**
	 * 
	 * @param sendySubscriberDetails
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String addNewSubscriber(SendySubscriber sendySubscriberDetails) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		body = new LinkedMultiValueMap<String, String>();

		body.add("name", sendySubscriberDetails.getUserName());
		body.add("email", sendySubscriberDetails.getEmail());
		body.add("boolean", sendFlag);
		body.add("list", subscriberListID);

		for (Entry<String, String> entry : sendySubscriberDetails.getfields().entrySet()) {
			body.add(entry.getKey(), entry.getValue());
		}
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		//System.out.println("HTTP response " + httpEntity.toString());
		ResponseEntity<String> response = this.exchange(subscribeUri,HttpMethod.POST, httpEntity, String.class);
		return response.getBody();
		//return "ok";
	}

	public String sendMail() throws HttpClientErrorException, JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		StringBuffer content = new StringBuffer("");
		URL url = this.getClass().getResource("./src/main/resources/templates/SendyWelcomeMail.html");
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(url.getFile()));
			while ((sCurrentLine = br.readLine()) != null) {
				content.append(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		body = new LinkedMultiValueMap<String, String>();
		body.add("api_key", apiKey);
		body.add("from_name", fromName);
		body.add("from_email", fromMailID);
		body.add("reply_to", replyToMailID);
		body.add("subject", subject);
		body.add("plain_text", "");
		body.add("html_text", content.toString());

		/*
		 * List<SubcriberListID> listIDs = new ArrayList<SubcriberListID>();
		 * listIDs = sendMail.getSubscribersList(); StringBuffer
		 * subscriberListID = new StringBuffer(""); for (SubcriberListID listID
		 * : listIDs) {
		 * subscriberListID.append(subscriberListID.length()>1?","+listID
		 * :listID); }
		 */
		body.add("list_ids", subscriberListID);
		body.add("send_campaign", sendCampaign);

		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		//System.out.println("http " + content.toString());
		ResponseEntity<String> response = this.exchange(sendMailUri,HttpMethod.POST, httpEntity, String.class);
		return response.getBody();
		//return "ok";
	}

	public String executeCronJob() throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		body = new LinkedMultiValueMap<String, String>();
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		//System.out.println("HTTP response " + httpEntity.toString());
		ResponseEntity<String> response = this.exchange(scheduledCronJobUri, HttpMethod.GET, httpEntity, String.class);
		
		return response.getBody();
		//return "";
	}
	
	public String unsubscribeUser(WelcomeMailDetails welcomeMailDetails) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException {
		if (proxy)
			setProxy();
		body = new LinkedMultiValueMap<String, String>();

		body.add("email", welcomeMailDetails.getEmail());
		body.add("boolean", sendFlag);
		body.add("list", subscriberListID);
		httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpEntity = new HttpEntity<Object>(body, httpHeaders);
		System.out.println("HTTP response " + httpEntity.toString());
		ResponseEntity<String> response = this.exchange(unsubscribeUri, HttpMethod.POST, httpEntity, String.class);
		return response.getBody();
	}
	
	public void sendErrorAlertMail() throws MessagingException {
		List<String> toMailIdList = new ArrayList<String>();
		List<String> ccMailIdList = new ArrayList<String>();
		
		String[] toMail = toMailIds.split(",");
		String[] ccMail = ccMailIds.split(",");
		for(String toMailID : toMail){
			toMailIdList.add(toMailID);
		}
		for(String ccMailID : ccMail){
			ccMailIdList.add(ccMailID);
		}
		
		if(toMailIdList.size()>0){
			Properties props = System.getProperties();
			setProperties(props);
			Session session =Session.getDefaultInstance(props,new javax.mail.Authenticator(){
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{				
					return new PasswordAuthentication(username,password);
				}
			});
				SMTPMessage message = new SMTPMessage(session);
				message.setFrom(new InternetAddress(username));
				for (String toMailId : toMailIdList) {
					message.addRecipients(Message.RecipientType.TO,InternetAddress.parse(toMailId));
				}
				for (String ccMailId : ccMailIdList) {
					message.addRecipients(Message.RecipientType.CC,InternetAddress.parse(ccMailId));
				}
				message.setSubject(errorAlertMailSubject);
				/*URL url = this.getClass().getResource("/org/srcm/heartfulness/helper");
				File file=new File(url.getFile());
				velocityEngine = new VelocityEngine();
				velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
				velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, file.getAbsolutePath());
				velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
				velocityEngine.init();*/
				message.setContent(getWelcomeMailContent(errorMailTemplate),"text/html");
				message.setAllow8bitMIME(true);
				message.setSentDate(new Date());
				message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
				message.getReturnOption();
				//int returnOption = message.getReturnOption();
				Transport.send(message);
		}
	}

	private String getWelcomeMailContent(String errormail){
		Template template = velocityEngine.getTemplate("./src/main/resources/templates/"+errormail+".vm");
		StringWriter stringWriter = new StringWriter();
		template.merge(getParameter(), stringWriter);
		return stringWriter.toString();
	}
	public VelocityContext getParameter() {
		return this.context;
	}

	public SendyAPIRestTemplate() {
		context = new VelocityContext();
	}

	public SendyAPIRestTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}

	public SendyAPIRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	private void setProperties(Properties props) {
		props.put("mail.debug", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");
	}
	
	public String getSubscribeUri() {
		return subscribeUri;
	}

	public String getSendMailUri() {
		return sendMailUri;
	}

	public void setSendMailUri(String sendMailUri) {
		this.sendMailUri = sendMailUri;
	}

	public void setSubscribeUri(String subscribeUri) {
		this.subscribeUri = subscribeUri;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSubscriberListID() {
		return subscriberListID;
	}

	public void setSubscriberListID(String subscriberListID) {
		this.subscriberListID = subscriberListID;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromMailID() {
		return fromMailID;
	}

	public void setFromMailID(String fromMailID) {
		this.fromMailID = fromMailID;
	}

	public String getReplyToMailID() {
		return replyToMailID;
	}

	public void setReplyToMailID(String replyToMailID) {
		this.replyToMailID = replyToMailID;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSendCampaign() {
		return sendCampaign;
	}

	public void setSendCampaign(String sendCampaign) {
		this.sendCampaign = sendCampaign;
	}

	public String getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
	public String getUnsubscribeUri() {
		return unsubscribeUri;
	}

	public void setUnsubscribeUri(String unsubscribeUri) {
		this.unsubscribeUri = unsubscribeUri;
	}
	public String getScheduledCronJobUri() {
		return scheduledCronJobUri;
	}

	public void setScheduledCronJobUri(String scheduledCronJobUri) {
		this.scheduledCronJobUri = scheduledCronJobUri;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getErrorAlertMailSubject() {
		return errorAlertMailSubject;
	}

	public void setErrorAlertMailSubject(String errorAlertMailSubject) {
		this.errorAlertMailSubject = errorAlertMailSubject;
	}

	public String getErrorMailTemplate() {
		return errorMailTemplate;
	}

	public void setErrorMailTemplate(String errorMailTemplate) {
		this.errorMailTemplate = errorMailTemplate;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getToMailIds() {
		return toMailIds;
	}

	public void setToMailIds(String toMailIds) {
		this.toMailIds = toMailIds;
	}

	public String getCcMailIds() {
		return ccMailIds;
	}

	public void setCcMailIds(String ccMailIds) {
		this.ccMailIds = ccMailIds;
	}

	public void setProxy() {
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

	

}

