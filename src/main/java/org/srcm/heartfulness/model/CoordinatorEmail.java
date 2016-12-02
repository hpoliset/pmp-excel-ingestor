/**
 * 
 */
package org.srcm.heartfulness.model;

/**
 * @author Koustav Dutta
 *
 */
public class CoordinatorEmail {

	private String coordinatorEmail;
	
	private String totalParticipantCount;
	
	private String pctptAlreadyRcvdWlcmMailCount;

	private String pctptRcvdWlcmMailYstrdayCount;
	
	private String eventName;
	
	private String coordinatorName;
	
	private String programId;
	
	private String programCreateDate;
	
	private String eventID;

	public String getCoordinatorEmail() {
		return coordinatorEmail;
	}

	public void setCoordinatorEmail(String coordinatorEmail) {
		this.coordinatorEmail = coordinatorEmail;
	}

	public String getTotalParticipantCount() {
		return totalParticipantCount;
	}

	public void setTotalParticipantCount(String totalParticipantCount) {
		this.totalParticipantCount = totalParticipantCount;
	}

	public String getPctptAlreadyRcvdWlcmMailCount() {
		return pctptAlreadyRcvdWlcmMailCount;
	}

	public void setPctptAlreadyRcvdWlcmMailCount(String pctptAlreadyRcvdWlcmMailCount) {
		this.pctptAlreadyRcvdWlcmMailCount = pctptAlreadyRcvdWlcmMailCount;
	}

	public String getPctptRcvdWlcmMailYstrdayCount() {
		return pctptRcvdWlcmMailYstrdayCount;
	}

	public void setPctptRcvdWlcmMailYstrdayCount(String pctptRcvdWlcmMailYstrdayCount) {
		this.pctptRcvdWlcmMailYstrdayCount = pctptRcvdWlcmMailYstrdayCount;
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

	@Override
	public String toString() {
		return "CoordinatorEmail [coordinatorEmail=" + coordinatorEmail + ", totalParticipantCount="
				+ totalParticipantCount + ", pctptAlreadyRcvdWlcmMailCount=" + pctptAlreadyRcvdWlcmMailCount
				+ ", pctptRcvdWlcmMailYstrdayCount=" + pctptRcvdWlcmMailYstrdayCount + ", eventName=" + eventName
				+ ", coordinatorName=" + coordinatorName + ", programId=" + programId + ", programCreateDate="
				+ programCreateDate + ", eventID=" + eventID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinatorEmail == null) ? 0 : coordinatorEmail.hashCode());
		result = prime * result + ((programId == null) ? 0 : programId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoordinatorEmail other = (CoordinatorEmail) obj;
		if (coordinatorEmail == null) {
			if (other.coordinatorEmail != null)
				return false;
		} else if (!coordinatorEmail.equals(other.coordinatorEmail))
			return false;
		if (programId == null) {
			if (other.programId != null)
				return false;
		} else if (!programId.equals(other.programId))
			return false;
		return true;
	}

}
