package org.srcm.heartfulness.model.json.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is the request for changing the admin for the event
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventAdminChangeRequest {
	
	@JsonIgnore
	private int id;
	
	private String eventId;
	
	@JsonIgnore
	private String oldCoordinatorEmail;
	
	@JsonIgnore
	private int programId;
	
	private String newCoordinatorEmail;
	
	private String coordinatorName;
	
	private String coordinatorMobile;
	
	@JsonIgnore
	private String createdBy;
	
	public EventAdminChangeRequest() {
		super();
	}

	public EventAdminChangeRequest(int id, String eventId, String oldCoordinatorEmail, int programId,
			String newCoordinatorEmail, String coordinatorName, String coordinatorMobile, String createdBy) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.oldCoordinatorEmail = oldCoordinatorEmail;
		this.programId = programId;
		this.newCoordinatorEmail = newCoordinatorEmail;
		this.coordinatorName = coordinatorName;
		this.coordinatorMobile = coordinatorMobile;
		this.createdBy = createdBy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getOldCoordinatorEmail() {
		return oldCoordinatorEmail;
	}

	public void setOldCoordinatorEmail(String oldCoordinatorEmail) {
		this.oldCoordinatorEmail = oldCoordinatorEmail;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public String getNewCoordinatorEmail() {
		return newCoordinatorEmail;
	}

	public void setNewCoordinatorEmail(String newCoordinatorEmail) {
		this.newCoordinatorEmail = newCoordinatorEmail;
	}

	public String getCoordinatorName() {
		return coordinatorName;
	}

	public void setCoordinatorName(String coordinatorName) {
		this.coordinatorName = coordinatorName;
	}

	public String getCoordinatorMobile() {
		return coordinatorMobile;
	}

	public void setCoordinatorMobile(String coordinatorMobile) {
		this.coordinatorMobile = coordinatorMobile;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public String toString() {
		return "EventAdminChangeRequest [id=" + id + ", eventId=" + eventId + ", oldCoordinatorEmail="
				+ oldCoordinatorEmail + ", programId=" + programId + ", newCoordinatorEmail=" + newCoordinatorEmail
				+ ", coordinatorName=" + coordinatorName + ", coordinatorMobile=" + coordinatorMobile + ", createdBy="
				+ createdBy + "]";
	}
	
}
