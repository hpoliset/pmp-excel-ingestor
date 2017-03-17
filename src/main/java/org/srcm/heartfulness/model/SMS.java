package org.srcm.heartfulness.model;

public class SMS {

	private String senderMobile;
	private String messageConetent;
	private String mobileOperator;
	private String carrier;
	private String dateTime;

	public SMS() {
		super();
	}

	public SMS(String senderMobile, String messageConetent, String mobileOperator, String carrier, String dateTime) {
		super();
		this.senderMobile = senderMobile;
		this.messageConetent = messageConetent;
		this.mobileOperator = mobileOperator;
		this.carrier = carrier;
		this.dateTime = dateTime;
	}

	public String getSenderMobile() {
		return senderMobile;
	}

	public void setSenderMobile(String senderMobile) {
		this.senderMobile = senderMobile;
	}

	public String getMessageConetent() {
		return messageConetent;
	}

	public void setMessageConetent(String messageConetent) {
		this.messageConetent = messageConetent;
	}

	public String getMobileOperator() {
		return mobileOperator;
	}

	public void setMobileOperator(String mobileOperator) {
		this.mobileOperator = mobileOperator;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return "SMS [senderMobile=" + senderMobile + ", messageConetent=" + messageConetent + ", mobileOperator="
				+ mobileOperator + ", carrier=" + carrier + ", dateTime=" + dateTime + "]";
	}

}
