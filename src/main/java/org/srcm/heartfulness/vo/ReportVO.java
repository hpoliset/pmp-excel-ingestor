package org.srcm.heartfulness.vo;

/**
 * 
 * This class is used to hold the values from the reports screen which will be
 * used in the service and DAO.
 *
 */
public class ReportVO {

	private String channel;

	private String fromDate;

	private String tillDate;

	private String country;

	private String state;

	private String city;

	private String username;

	private String userRole;

	public ReportVO() {
		super();
	}

	public ReportVO(String channel, String fromDate, String tillDate, String country, String state, String city) {
		super();
		this.channel = channel;
		this.fromDate = fromDate;
		this.tillDate = tillDate;
		this.country = country;
		this.state = state;
		this.city = city;
	}

	public ReportVO(String channel, String fromDate, String tillDate, String country, String state, String city,
			String username, String userRole) {
		super();
		this.channel = channel;
		this.fromDate = fromDate;
		this.tillDate = tillDate;
		this.country = country;
		this.state = state;
		this.city = city;
		this.username = username;
		this.userRole = userRole;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getTillDate() {
		return tillDate;
	}

	public void setTillDate(String tillDate) {
		this.tillDate = tillDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "ReportVO [channel=" + channel + ", fromDate=" + fromDate + ", tillDate=" + tillDate + ", country="
				+ country + ", state=" + state + ", city=" + city + ", username=" + username + ", userRole=" + userRole
				+ "]";
	}

}
