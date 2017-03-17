package org.srcm.heartfulness.model.json.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class to hold the details of end user to subscribe/unsubscribe to Heartfulness.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class SubscriptionRequest {

	private String name;
	private String mailID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMailID() {
		return mailID;
	}

	public void setMailID(String mailID) {
		this.mailID = mailID;
	}

}
