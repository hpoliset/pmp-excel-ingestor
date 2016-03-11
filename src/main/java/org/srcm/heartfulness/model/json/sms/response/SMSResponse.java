package org.srcm.heartfulness.model.json.sms.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author gouthamc
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SMSResponse {

	@JsonProperty(value="ErrorMessage")
	private String ErrorMessage;

	@JsonProperty(value="JobId")
	private String JobId;

	@JsonProperty(value="MessageData")
	private MessageData[] MessageData;

	@JsonProperty(value="ErrorCode")
	private String ErrorCode;

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String ErrorMessage) {
		this.ErrorMessage = ErrorMessage;
	}

	public String getJobId() {
		return JobId;
	}

	public void setJobId(String JobId) {
		this.JobId = JobId;
	}

	public MessageData[] getMessageData() {
		return MessageData;
	}

	public void setMessageData(MessageData[] MessageData) {
		this.MessageData = MessageData;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String ErrorCode) {
		this.ErrorCode = ErrorCode;
	}

	@Override
	public String toString() {
		return "ClassPojo [ErrorMessage = " + ErrorMessage + ", JobId = " + JobId + ", MessageData = " + MessageData
				+ ", ErrorCode = " + ErrorCode + "]";
	}
}
