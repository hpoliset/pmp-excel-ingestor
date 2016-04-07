package org.srcm.heartfulness.model;

/**
 * This class is the introduction details passed by the user
 * @author himasreev
 *
 */
public class IntroductionDetails {
	
	private int IntroductionId;
	
	private int id;
	
	private String requiredIntroduction;
	
	private String status;
	
	private String message;
	
	public IntroductionDetails() {
		super();
	}

	public IntroductionDetails(int introductionId, int id, String requiredIntroduction, String status, String message) {
		super();
		IntroductionId = introductionId;
		this.id = id;
		this.requiredIntroduction = requiredIntroduction;
		this.status = status;
		this.message = message;
	}

	public int getIntroductionId() {
		return IntroductionId;
	}

	public void setIntroductionId(int introductionId) {
		IntroductionId = introductionId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRequiredIntroduction() {
		return requiredIntroduction;
	}

	public void setRequiredIntroduction(String requiredIntroduction) {
		this.requiredIntroduction = requiredIntroduction;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "IntroductionDetails [IntroductionId=" + IntroductionId + ", id=" + id + ", requiredIntroduction="
				+ requiredIntroduction + ", status=" + status + ", message=" + message + "]";
	}

}
