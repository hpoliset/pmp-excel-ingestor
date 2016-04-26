package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMail {
	
	@JsonProperty("apiKey")
	private String apiKey;
	
	@JsonProperty("fromName")
	private String fromName;
	
	@JsonProperty("fromMailID")
	private String fromMailID;
	
	@JsonProperty("replyTo")
	private String replyTo;
	
	@JsonProperty("subject")
	private String subject;
	
	@JsonIgnore
	private String htmlText;
	
	@JsonIgnore
	private String plainText;
	
	@JsonProperty("subscribersList")
	private String subscribersList;
	
	@JsonProperty("sendCampaign")
	private String sendCampaign;
	
	
	public SendMail() {
		super();
	}
	public SendMail(String apiKey, String fromName, String fromMailID, String replyTo, String subject, String htmlText,
			String plainText, String subscribersList, String sendCampaign) {
		super();
		this.apiKey = apiKey;
		this.fromName = fromName;
		this.fromMailID = fromMailID;
		this.replyTo = replyTo;
		this.subject = subject;
		this.htmlText = htmlText;
		this.plainText = plainText;
		this.subscribersList = subscribersList;
		this.sendCampaign = sendCampaign;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
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
	public String getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getHtmlText() {
		return htmlText;
	}
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}
	public String getPlainText() {
		return plainText;
	}
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}
	public String getSubscribersList() {
		return subscribersList;
	}
	public void setSubscribersList(String subscribersList) {
		this.subscribersList = subscribersList;
	}
	public String getSendCampaign() {
		return sendCampaign;
	}
	public void setSendCampaign(String sendCampaign) {
		this.sendCampaign = sendCampaign;
	}
	@Override
	public String toString() {
		return "SendMail [apiKey=" + apiKey + ", fromName=" + fromName + ", fromMailID=" + fromMailID + ", replyTo="
				+ replyTo + ", subject=" + subject + ", htmlText=" + htmlText + ", plainText=" + plainText
				+ ", subscribersList=" + subscribersList + ", sendCampaign=" + sendCampaign + "]";
	}
	
}
