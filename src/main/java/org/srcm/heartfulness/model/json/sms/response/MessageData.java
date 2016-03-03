package org.srcm.heartfulness.model.json.sms.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author gouthamc
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageData {

	@JsonProperty(value="MessageId")
	private String MessageId;

	@JsonProperty(value="Number")
	private String Number;

	public String getMessageId() {
		return MessageId;
	}

	public void setMessageId(String MessageId) {
		this.MessageId = MessageId;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String Number) {
		this.Number = Number;
	}

	@Override
	public String toString() {
		return "ClassPojo [MessageId = " + MessageId + ", Number = " + Number + "]";
	}
}
