/**
 * 
 */
package org.srcm.heartfulness.model;

import java.util.Date;

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
	
	private Date programCreateDate;
	
	private String eventID;
	
	private String eventCity;
	
	private String eventPlace;
	
	private Date programCreationDate;
	

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

	public Date getProgramCreateDate() {
		return programCreateDate;
	}

	public void setProgramCreateDate(Date programCreateDate) {
		this.programCreateDate = programCreateDate;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getEventCity() {
		return eventCity;
	}

	public void setEventCity(String eventCity) {
		this.eventCity = eventCity;
	}

	public String getEventPlace() {
		return eventPlace;
	}

	public void setEventPlace(String eventPlace) {
		this.eventPlace = eventPlace;
	}
	
	public Date getProgramCreationDate() {
		return programCreationDate;
	}

	public void setProgramCreationDate(Date programCreationDate) {
		this.programCreationDate = programCreationDate;
	}

	@Override
	public String toString() {
		return "CoordinatorEmail [coordinatorEmail=" + coordinatorEmail + ", totalParticipantCount="
				+ totalParticipantCount + ", pctptAlreadyRcvdWlcmMailCount=" + pctptAlreadyRcvdWlcmMailCount
				+ ", pctptRcvdWlcmMailYstrdayCount=" + pctptRcvdWlcmMailYstrdayCount + ", eventName=" + eventName
				+ ", coordinatorName=" + coordinatorName + ", programId=" + programId + ", programCreateDate="
				+ programCreateDate + ", eventID=" + eventID + ", eventCity=" + eventCity + ", eventPlace="
				+ eventPlace + ", programCreationDate=" + programCreationDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinatorEmail == null) ? 0 : coordinatorEmail.hashCode());
		result = prime * result + ((coordinatorName == null) ? 0 : coordinatorName.hashCode());
		result = prime * result + ((eventCity == null) ? 0 : eventCity.hashCode());
		result = prime * result + ((eventID == null) ? 0 : eventID.hashCode());
		result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		result = prime * result + ((eventPlace == null) ? 0 : eventPlace.hashCode());
		result = prime * result
				+ ((pctptAlreadyRcvdWlcmMailCount == null) ? 0 : pctptAlreadyRcvdWlcmMailCount.hashCode());
		result = prime * result
				+ ((pctptRcvdWlcmMailYstrdayCount == null) ? 0 : pctptRcvdWlcmMailYstrdayCount.hashCode());
		result = prime * result + ((programCreateDate == null) ? 0 : programCreateDate.hashCode());
		result = prime * result + ((programId == null) ? 0 : programId.hashCode());
		result = prime * result + ((totalParticipantCount == null) ? 0 : totalParticipantCount.hashCode());
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
		if (coordinatorName == null) {
			if (other.coordinatorName != null)
				return false;
		} else if (!coordinatorName.equals(other.coordinatorName))
			return false;
		if (eventCity == null) {
			if (other.eventCity != null)
				return false;
		} else if (!eventCity.equals(other.eventCity))
			return false;
		if (eventID == null) {
			if (other.eventID != null)
				return false;
		} else if (!eventID.equals(other.eventID))
			return false;
		if (eventName == null) {
			if (other.eventName != null)
				return false;
		} else if (!eventName.equals(other.eventName))
			return false;
		if (eventPlace == null) {
			if (other.eventPlace != null)
				return false;
		} else if (!eventPlace.equals(other.eventPlace))
			return false;
		if (pctptAlreadyRcvdWlcmMailCount == null) {
			if (other.pctptAlreadyRcvdWlcmMailCount != null)
				return false;
		} else if (!pctptAlreadyRcvdWlcmMailCount.equals(other.pctptAlreadyRcvdWlcmMailCount))
			return false;
		if (pctptRcvdWlcmMailYstrdayCount == null) {
			if (other.pctptRcvdWlcmMailYstrdayCount != null)
				return false;
		} else if (!pctptRcvdWlcmMailYstrdayCount.equals(other.pctptRcvdWlcmMailYstrdayCount))
			return false;
		if (programCreateDate == null) {
			if (other.programCreateDate != null)
				return false;
		} else if (!programCreateDate.equals(other.programCreateDate))
			return false;
		if (programId == null) {
			if (other.programId != null)
				return false;
		} else if (!programId.equals(other.programId))
			return false;
		if (totalParticipantCount == null) {
			if (other.totalParticipantCount != null)
				return false;
		} else if (!totalParticipantCount.equals(other.totalParticipantCount))
			return false;
		return true;
	}

	
}
