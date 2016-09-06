/**
 * 
 */
package org.srcm.heartfulness.model;

/**
 * This class holds the api access time and other user information for log
 * purpose.
 * 
 * @author himasreev
 *
 */
public class PMPAPIAccessLog {

	private int id;

	private String username;

	private String ipAddress;

	private String apiName;

	private String totalRequestedTime;

	private String totalResponseTime;

	private String status;

	private String errorMessage;
	
	private String requestBody;
	
	private String responseBody;

	public PMPAPIAccessLog() {
		super();
	}
	
	public PMPAPIAccessLog(String username, String ipAddress, String apiName, String totalRequestedTime,
			String totalResponseTime, String status, String errorMessage, String requestBody, String responseBody) {
		super();
		this.username = username;
		this.ipAddress = ipAddress;
		this.apiName = apiName;
		this.totalRequestedTime = totalRequestedTime;
		this.totalResponseTime = totalResponseTime;
		this.status = status;
		this.errorMessage = errorMessage;
		this.requestBody = requestBody;
		this.responseBody = responseBody;
	}
	
	public PMPAPIAccessLog(String username, String ipAddress, String apiName, String totalRequestedTime,
			String totalResponseTime, String status, String errorMessage, String requestBody) {
		super();
		this.username = username;
		this.ipAddress = ipAddress;
		this.apiName = apiName;
		this.totalRequestedTime = totalRequestedTime;
		this.totalResponseTime = totalResponseTime;
		this.status = status;
		this.errorMessage = errorMessage;
		this.requestBody = requestBody;
	}

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

	public String getTotalRequestedTime() {
		return totalRequestedTime;
	}

	public void setTotalRequestedTime(String totalRequestedTime) {
		this.totalRequestedTime = totalRequestedTime;
	}

	public String getTotalResponseTime() {
		return totalResponseTime;
	}

	public void setTotalResponseTime(String totalResponseTime) {
		this.totalResponseTime = totalResponseTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public String toString() {
		return "PMPAPIAccessLog [id=" + id + ", username=" + username + ", ipAddress=" + ipAddress + ", apiName="
				+ apiName + ", totalRequestedTime=" + totalRequestedTime + ", totalResponseTime=" + totalResponseTime
				+ ", status=" + status + ", errorMessage=" + errorMessage + ", requestBody=" + requestBody
				+ ", responseBody=" + responseBody + "]";
	}

}
