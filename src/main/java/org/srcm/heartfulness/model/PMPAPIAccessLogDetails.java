package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class holds the MYSRCM api access time for log purpose.
 * 
 * @author himasreev
 *
 */
@JsonPropertyOrder({ "srNo", "id", "pmpAccessLogId", "endpoint", "requestedTime", "responseTime", "timeDifference",
	"status", "errorMessage", "requestBody", "responseBody", "viewReqRespData" })
public class PMPAPIAccessLogDetails {

	private int id;
	private int pmpAccessLogId;
	private String endpoint;
	private String requestedTime;
	private String responseTime;
	private String status;
	private String errorMessage;
	private String requestBody;
	private String responseBody;
	private String timeDifference;
	private String srNo;
	private String viewReqRespData;

	public PMPAPIAccessLogDetails() {
		super();
	}

	public PMPAPIAccessLogDetails(int pmpAccessLogId, String endpoint, String requestedTime, String responseTime,
			String status, String errorMessage, String requestBody, String responseBody) {
		super();
		this.pmpAccessLogId = pmpAccessLogId;
		this.endpoint = endpoint;
		this.requestedTime = requestedTime;
		this.responseTime = responseTime;
		this.status = status;
		this.errorMessage = errorMessage;
		this.requestBody = requestBody;
		this.responseBody = responseBody;
	}

	public PMPAPIAccessLogDetails(int pmpAccessLogId, String endpoint, String requestedTime, String responseTime,
			String status, String errorMessage, String requestBody) {
		super();
		this.pmpAccessLogId = pmpAccessLogId;
		this.endpoint = endpoint;
		this.requestedTime = requestedTime;
		this.responseTime = responseTime;
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

	public int getPmpAccessLogId() {
		return pmpAccessLogId;
	}

	public void setPmpAccessLogId(int pmpAccessLogId) {
		this.pmpAccessLogId = pmpAccessLogId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(String requestedTime) {
		this.requestedTime = requestedTime;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
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

	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public String getViewReqRespData() {
		return viewReqRespData;
	}

	public void setViewReqRespData(String viewReqRespData) {
		this.viewReqRespData = viewReqRespData;
	}

	@Override
	public String toString() {
		return "PMPAPIAccessLogDetails [id=" + id + ", pmpAccessLogId=" + pmpAccessLogId + ", endpoint=" + endpoint
				+ ", requestedTime=" + requestedTime + ", responseTime=" + responseTime + ", status=" + status
				+ ", errorMessage=" + errorMessage + ", requestBody=" + requestBody + ", responseBody=" + responseBody
				+ ", timeDifference=" + timeDifference + ", srNo=" + srNo + ", viewReqRespData=" + viewReqRespData
				+ "]";
	}

}
