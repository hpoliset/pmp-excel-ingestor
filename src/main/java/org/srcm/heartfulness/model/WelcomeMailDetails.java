package org.srcm.heartfulness.model;

import java.util.Date;

public class WelcomeMailDetails {
	
	private int id;
	private String printName;
    private Date createTime;
    private String email;
    private String unsubscribed;
    
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
	public String getUnsubscribed() {
		return unsubscribed;
	}
	public void setUnsubscribed(String unsubscribed) {
		this.unsubscribed = unsubscribed;
	}
	@Override
	public String toString() {
		return "WelcomeMailDetails [id=" + id + ", printName=" + printName + ", createTime=" + createTime + ", email="
				+ email + ", unsubscribed=" + unsubscribed + "]";
	}
    
}
