/**
 * 
 */
package org.srcm.heartfulness.model.json.request;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Koustav Dutta
 *
 */

public class DashboardRequest {
	
	private String country;
	private String state;
	private String city;
	private String zone;
	private String center;
	private String fromDate;
	private String toDate;
	@JsonIgnore
	private Date sqlFromDate;
	@JsonIgnore
	private Date sqlTodate;
	
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
	
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	public Date getSqlFromDate() {
		return sqlFromDate;
	}
	public void setSqlFromDate(Date sqlFromDate) {
		this.sqlFromDate = sqlFromDate;
	}
	public Date getSqlTodate() {
		return sqlTodate;
	}
	public void setSqlTodate(Date sqlTodate) {
		this.sqlTodate = sqlTodate;
	}
	@Override
	public String toString() {
		return "DashboardRequest [country=" + country + ", state=" + state + ", city=" + city + ", zone=" + zone
				+ ", center=" + center + "]";
	}
	
}
