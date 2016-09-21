/**
 * 
 */
package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class holds the api access time and other user information for log
 * purpose.
 * 
 * @author himasreev
 *
 */
@JsonPropertyOrder({ "serialNo", "id", "username", "ipAddress", "apiName", "totalRequestedTime", "totalResponseTime","timeDifference","status","errorMessage","requestBody","responseBody", "viewAccessLogDetailsData" })
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
	
	private String timeDifference;
	
	private String serialNo;
	
	private String viewAccessLogDetailsData;
	
	private String viewReqRespBody;

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
	
	public PMPAPIAccessLog(int id, String username, String ipAddress, String apiName, String totalRequestedTime,
			String totalResponseTime, String status ) {
		super();
		this.id = id;
		this.username = username;
		this.ipAddress = ipAddress;
		this.apiName = apiName;
		this.totalRequestedTime = totalRequestedTime;
		this.totalResponseTime = totalResponseTime;
		this.status = status;
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

	
	public String getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(String timeDifference) {
		this.timeDifference = timeDifference;
	}

	
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getViewAccessLogDetailsData() {
		return viewAccessLogDetailsData;
	}

	public void setViewAccessLogDetailsData(String viewAccessLogDetailsData) {
		this.viewAccessLogDetailsData = viewAccessLogDetailsData;
	}

	public String getViewReqRespBody() {
		return viewReqRespBody;
	}

	public void setViewReqRespBody(String viewReqRespBody) {
		this.viewReqRespBody = viewReqRespBody;
	}

	@Override
	public String toString() {
		return "PMPAPIAccessLog [id=" + id + ", username=" + username + ", ipAddress=" + ipAddress + ", apiName="
				+ apiName + ", totalRequestedTime=" + totalRequestedTime + ", totalResponseTime=" + totalResponseTime
				+ ", status=" + status + ", errorMessage=" + errorMessage + ", requestBody=" + requestBody
				+ ", responseBody=" + responseBody + ", timeDifference=" + timeDifference + ", serialNo=" + serialNo
				+ ", viewAccessLogDetailsData=" + viewAccessLogDetailsData + ", viewReqRespBody=" + viewReqRespBody
				+ "]";
	}
}