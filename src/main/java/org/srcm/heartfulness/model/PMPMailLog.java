/**
 * 
 */
package org.srcm.heartfulness.model;

/**
 * @author Koustav Dutta
 *
 */
public class PMPMailLog {
	
	private int id;
	
	private String programId;
	
	private String coordinatorEmail;
	
	private String emailType;
	
	private String emailSentStatus;
	
	private String errorMessage;

	public PMPMailLog() {
		super();
	}

	public PMPMailLog(String programId, String coordinatorEmail, String emailType, String emailSentStatus,
			String errorMessage) {
		super();
		this.programId = programId;
		this.coordinatorEmail = coordinatorEmail;
		this.emailType = emailType;
		this.emailSentStatus = emailSentStatus;
		this.errorMessage = errorMessage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getCoordinatorEmail() {
		return coordinatorEmail;
	}

	public void setCoordinatorEmail(String coordinatorEmail) {
		this.coordinatorEmail = coordinatorEmail;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getEmailSentStatus() {
		return emailSentStatus;
	}

	public void setEmailSentStatus(String emailSentStatus) {
		this.emailSentStatus = emailSentStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "PMPMailLog [id=" + id + ", programId=" + programId + ", coordinatorEmail=" + coordinatorEmail
				+ ", emailType=" + emailType + ", emailSentStatus=" + emailSentStatus + ", errorMessage=" + errorMessage
				+ "]";
	}
	
}
