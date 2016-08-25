/**
 * 
 */
package org.srcm.heartfulness.model;

/**
 * @author himasreev
 *
 */
public class APIAccessLogDetails {

	private int id;
	private String username;
	private String ipAddress;
	private String apiName;
	private String requestTime;
	private String responseTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	@Override
	public String toString() {
		return "APIAccessLogDetails [id=" + id + ", username=" + username + ", ipAddress=" + ipAddress + ", apiName="
				+ apiName + ", requestTime=" + requestTime + ", responseTime=" + responseTime + "]";
	}

}
