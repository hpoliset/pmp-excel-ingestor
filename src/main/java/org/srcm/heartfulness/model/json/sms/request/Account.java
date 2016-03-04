package org.srcm.heartfulness.model.json.sms.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * @author gouthamc
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonPropertyOrder({ "User", "Password", "SenderId", "Channel", "DCS", "SchedTime", "GroupId"})
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class Account {
	
	@JsonProperty("User")
	private String User;

	@JsonProperty("DCS")
	private String DCS;

	@JsonProperty("Channel")
	private String Channel;

	@JsonProperty("Password")
	private String Password;

	@JsonProperty("SenderId")
	private String SenderId;

	@JsonProperty("GroupId")
	private String GroupId;

	@JsonProperty("SchedTime")
	private String SchedTime;

	public String getUser() {
		return User;
	}

	public void setUser(String User) {
		this.User = User;
	}

	public String getDCS() {
		return DCS;
	}

	public void setDCS(String DCS) {
		this.DCS = DCS;
	}

	public String getChannel() {
		return Channel;
	}

	public void setChannel(String Channel) {
		this.Channel = Channel;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String Password) {
		this.Password = Password;
	}

	public String getSenderId() {
		return SenderId;
	}

	public void setSenderId(String SenderId) {
		this.SenderId = SenderId;
	}

	public String getGroupId() {
		return GroupId;
	}

	public void setGroupId(String GroupId) {
		this.GroupId = GroupId;
	}

	public String getSchedTime() {
		return SchedTime;
	}

	public void setSchedTime(String SchedTime) {
		this.SchedTime = SchedTime;
	}

	@Override
	public String toString() {
		return "ClassPojo [User = " + User + ", DCS = " + DCS + ", Channel = " + Channel + ", Password = " + Password
				+ ", SenderId = " + SenderId + ", GroupId = " + GroupId + ", SchedTime = " + SchedTime + "]";
	}
}
