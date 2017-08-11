/**
 * 
 */
package org.srcm.heartfulness.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author Koustav Dutta
 *
 */
public class CoordinatorHistory {

	private int id;
	private int programId;
	private String coordinatorName;
	private String coordinatorEmail;
	private String abhyasiId;
	private Timestamp assignedTime;
	private Timestamp removalTime;
	
	public CoordinatorHistory() {
		super();
	}
	
	public CoordinatorHistory(int programId, String coordinatorName, String coordinatorEmail, String abhyasiId) {
		super();
		this.programId = programId;
		this.coordinatorName = coordinatorName;
		this.coordinatorEmail = coordinatorEmail;
		this.abhyasiId = abhyasiId;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProgramId() {
		return programId;
	}
	public void setProgramId(int programId) {
		this.programId = programId;
	}
	public String getCoordinatorName() {
		return coordinatorName;
	}
	public void setCoordinatorName(String coordinatorName) {
		this.coordinatorName = coordinatorName;
	}
	public String getCoordinatorEmail() {
		return coordinatorEmail;
	}
	public void setCoordinatorEmail(String coordinatorEmail) {
		this.coordinatorEmail = coordinatorEmail;
	}

	public String getAbhyasiId() {
		return abhyasiId;
	}

	public void setAbhyasiId(String abhyasiId) {
		this.abhyasiId = abhyasiId;
	}

	public Timestamp getAssignedTime() {
		return assignedTime;
	}

	public void setAssignedTime(Timestamp assignedTime) {
		this.assignedTime = assignedTime;
	}

	public Timestamp getRemovalTime() {
		return removalTime;
	}

	public void setRemovalTime(Timestamp removalTime) {
		this.removalTime = removalTime;
	}

	@Override
	public String toString() {
		return "CoordinatorHistory [id=" + id + ", programId=" + programId + ", coordinatorName=" + coordinatorName
				+ ", coordinatorEmail=" + coordinatorEmail + ", abhyasiId=" + abhyasiId + ", assignedTime="
				+ assignedTime + ", removalTime=" + removalTime + "]";
	}
}
