/**
 * 
 */
package org.srcm.heartfulness.model;

/**
 * This class holds the MYSRCM api access time for log purpose.
 * 
 * @author himasreev
 *
 */
public class PMPAPIAccessLogDetails {

	private int id;
	
	private int pmpAccessLogId;
	
	private String endpoint;
	
	private String requestedTime;
	
	private String responseTime;
	
	private String status;
	
	private String errorMessage;

	public PMPAPIAccessLogDetails() {
		super();
	}

	public PMPAPIAccessLogDetails(int pmpAccessLogId, String endpoint, String requestedTime, String responseTime,
			String status, String errorMessage) {
		super();
		this.pmpAccessLogId = pmpAccessLogId;
		this.endpoint = endpoint;
		this.requestedTime = requestedTime;
		this.responseTime = responseTime;
		this.status = status;
		this.errorMessage = errorMessage;
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

	@Override
	public String toString() {
		return "PMPAPIAccessLogDetails [id=" + id + ", pmpAccessLogId=" + pmpAccessLogId + ", endpoint=" + endpoint
				+ ", requestedTime=" + requestedTime + ", responseTime=" + responseTime + ", status=" + status
				+ ", errorMessage=" + errorMessage + "]";
	}

}
