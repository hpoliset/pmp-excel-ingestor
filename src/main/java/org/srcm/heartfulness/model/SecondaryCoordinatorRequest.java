/**
 * 
 */
package org.srcm.heartfulness.model;

import java.util.Date;

/**
 * @author Koustav Dutta
 *
 */
public class SecondaryCoordinatorRequest {
	
	private int requestId;
	
	private int programId;
	
	private int userId;
	
	private String status;
	
	private String approvedBy;
	
	private Date requestTime;
	
	private String requestedBy;
	
	private Date approvalTime;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public Date getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
	}
	
}
