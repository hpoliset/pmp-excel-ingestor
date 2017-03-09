package org.srcm.heartfulness.model;

import java.util.Date;

public class WelcomeMailDetails {

	private int id;
	private String printName;
	private Date createTime;
	private String email;
	private int unsubscribed;
	private int subscribed;
	private int confirmed;
	private String emailStatus;

	public WelcomeMailDetails() {
		super();
	}

	public WelcomeMailDetails(int id, String printName, Date createTime, String email, int unsubscribed,
			int subscribed, int confirmed, String emailStatus) {
		super();
		this.id = id;
		this.printName = printName;
		this.createTime = createTime;
		this.email = email;
		this.unsubscribed = unsubscribed;
		this.subscribed = subscribed;
		this.confirmed = confirmed;
		this.emailStatus = emailStatus;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrintName() {
		return printName;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date date) {
		this.createTime = date;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUnsubscribed() {
		return unsubscribed;
	}

	public void setUnsubscribed(int unsubscribed) {
		this.unsubscribed = unsubscribed;
	}

	public int getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(int subscribed) {
		this.subscribed = subscribed;
	}

	public int getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}

	public String getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(String emailStatus) {
		this.emailStatus = emailStatus;
	}

	@Override
	public String toString() {
		return "WelcomeMailDetails [id=" + id + ", printName=" + printName + ", createTime=" + createTime + ", email="
				+ email + ", unsubscribed=" + unsubscribed + ", subscribed=" + subscribed + ", confirmed=" + confirmed
				+ ", emailStatus=" + emailStatus + "]";
	}

}
