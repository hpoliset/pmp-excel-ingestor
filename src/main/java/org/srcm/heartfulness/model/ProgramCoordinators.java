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
	private String name;
	private String email;
	private int isPrimaryCoordinator;
	private String eventId;
	private int isPreceptor;

	public ProgramCoordinators() {
		super();
	}

	public ProgramCoordinators(int programId, int userId, String name, String email, int isPrimaryCoordinator) {
		super();
		this.programId = programId;
		this.userId = userId;
		this.name = name;
		this.email = email;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIsPrimaryCoordinator() {
		return isPrimaryCoordinator;
	}

	public void setIsPrimaryCoordinator(int isPrimaryCoordinator) {
		this.isPrimaryCoordinator = isPrimaryCoordinator;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public int getIsPreceptor() {
		return isPreceptor;
	}

	public void setIsPreceptor(int isPreceptor) {
		this.isPreceptor = isPreceptor;
	}

	@Override
	public String toString() {
		return "ProgramCoordinators [id=" + id + ", programId=" + programId + ", userId=" + userId + ", name=" + name
				+ ", email=" + email + ", isPrimaryCoordinator=" + isPrimaryCoordinator + ", eventId=" + eventId
				+ ", isPreceptor=" + isPreceptor + "]";
	}

}
