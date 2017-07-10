package org.srcm.heartfulness.model;

public class CoordinatorAccessControlEmail {

	private String coordinatorEmail;
	private String eventName;
	private String coordinatorName;
	private String programId;
	private String programCreateDate;
	private String eventID;
	private String preceptorEmailId;
	private String preceptorName;
	private String eventPlace;
	private String uploaderMail;
	private String jiraNumber;
	private String pgrmCreatedSource;

	public String getCoordinatorEmail() {
		return coordinatorEmail;
	}

	public void setCoordinatorEmail(String coordinatorEmail) {
		this.coordinatorEmail = coordinatorEmail;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getCoordinatorName() {
		return coordinatorName;
	}

	public void setCoordinatorName(String coordinatorName) {
		this.coordinatorName = coordinatorName;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramCreateDate() {
		return programCreateDate;
	}

	public void setProgramCreateDate(String programCreateDate) {
		this.programCreateDate = programCreateDate;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getPreceptorEmailId() {
		return preceptorEmailId;
	}

	public void setPreceptorEmailId(String preceptorEmailId) {
		this.preceptorEmailId = preceptorEmailId;
	}

	public String getPreceptorName() {
		return preceptorName;
	}

	public void setPreceptorName(String preceptorName) {
		this.preceptorName = preceptorName;
	}

	public String getEventPlace() {
		return eventPlace;
	}

	public void setEventPlace(String eventPlace) {
		this.eventPlace = eventPlace;
	}
	
	public String getUploaderMail() {
		return uploaderMail;
	}

	public void setUploaderMail(String uploaderMail) {
		this.uploaderMail = uploaderMail;
	}

	public String getJiraNumber() {
		return jiraNumber;
	}

	public void setJiraNumber(String jiraNumber) {
		this.jiraNumber = jiraNumber;
	}
	
	public String getPgrmCreatedSource() {
		return pgrmCreatedSource;
	}

	public void setPgrmCreatedSource(String pgrmCreatedSource) {
		this.pgrmCreatedSource = pgrmCreatedSource;
	}

	@Override
	public String toString() {
		return "CoordinatorAccessControlEmail [coordinatorEmail=" + coordinatorEmail + ", eventName=" + eventName
				+ ", coordinatorName=" + coordinatorName + ", programId=" + programId + ", programCreateDate="
				+ programCreateDate + ", eventID=" + eventID + ", preceptorEmailId=" + preceptorEmailId
				+ ", preceptorName=" + preceptorName + ", eventPlace=" + eventPlace + "]";
	}

}
