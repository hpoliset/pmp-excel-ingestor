package org.srcm.heartfulness.model.json.sms.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author gouthamc
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSRequest {

	@JsonProperty(value="Messages")
	private Messages[] Messages;

	@JsonProperty(value="Account")
	private Account Account;

	public Messages[] getMessages() {
		return Messages;
	}

	public void setMessages(Messages[] Messages) {
		this.Messages = Messages;
	}

	public Account getAccount() {
		return Account;
	}

	public void setAccount(Account Account) {
		this.Account = Account;
	}

	@Override
	public String toString() {
		return "ClassPojo [Messages = " + Messages + ", Account = " + Account + "]";
	}

}
