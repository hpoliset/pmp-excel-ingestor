package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class holds the details of the event coordinators.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramCoordinators {

	private int id;

	private int programId;

	private int userId;

	private String coordinatorName;

	private String coordinatorEmail;

	private int isPrimaryCoordinator;
	
	public ProgramCoordinators() {
		super();
	}
	
	public ProgramCoordinators(int programId, int userId, String coordinatorName, String coordinatorEmail,
			int isPrimaryCoordinator) {
		super();
		this.programId = programId;
		this.userId = userId;
		this.coordinatorName = coordinatorName;
		this.coordinatorEmail = coordinatorEmail;
		this.isPrimaryCoordinator = isPrimaryCoordinator;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public int getIsPrimaryCoordinator() {
		return isPrimaryCoordinator;
	}

	public void setIsPrimaryCoordinator(int isPrimaryCoordinator) {
		this.isPrimaryCoordinator = isPrimaryCoordinator;
	}

	@Override
	public String toString() {
		return "ProgramCoordinators [id=" + id + ", programId=" + programId + ", userId=" + userId
				+ ", coordinatorName=" + coordinatorName + ", coordinatorEmail=" + coordinatorEmail
				+ ", isPrimaryCoordinator=" + isPrimaryCoordinator + "]";
	}

}
