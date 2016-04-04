package org.srcm.heartfulness.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class SendySubscriber {
	
	@JsonProperty("name")
	private String userName;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("list")
	private String listID;
	
	@JsonProperty("fields")
	private Map<String, String> fields = new HashMap<String, String>();
	
	@JsonProperty("sendFlag")
	private String sendFlag;
	
	public SendySubscriber(String userName, String email, String listID, String event, String eventDate,
			String sendFlag) {
		super();
		this.userName = userName;
		this.email = email;
		this.listID = listID;
		this.sendFlag = sendFlag;
	}

	public SendySubscriber() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getListID() {
		return listID;
	}

	public void setListID(String listID) {
		this.listID = listID;
	}

	public String getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
	
	public Map<String, String> getfields() {
		return fields;
	}

	public void setfields(Map<String, String> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "SendySubscriberDetails [userName=" + userName + ", email=" + email + ", listID=" + listID + ", Event="
				+", sendFlag=" + sendFlag + "]";
	}
	
	
	
	
}
