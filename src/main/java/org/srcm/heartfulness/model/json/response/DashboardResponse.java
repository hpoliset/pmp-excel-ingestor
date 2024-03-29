/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Koustav Dutta
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
@JsonPropertyOrder({ "eventCount","participantCount", "sessionCount", "location_count", "future_event_count"})
public class DashboardResponse {

	@JsonProperty("event_count")
	private int eventCount;

	@JsonProperty("participant_count")
	private int participantCount;

	@JsonProperty("session_count")
	private int sessionCount;

	@JsonProperty("location_count")
	private int locationCount;

	@JsonProperty("future_event_count")
	private int futureEventCount;
	
	@JsonProperty("program_zone")
	@JsonInclude(value = Include.NON_NULL)
	private String programZone;
	
	@JsonProperty("program_center")
	@JsonInclude(value = Include.NON_NULL)
	private String programCenter;
	
	@JsonProperty("event_state")
	@JsonInclude(value = Include.NON_NULL)
	private String eventState;
	
	@JsonProperty("program_district")
	@JsonInclude(value = Include.NON_NULL)
	private String programDistrict;
	
	@JsonProperty("event_city")
	@JsonInclude(value = Include.NON_NULL)
	private String eventCity;
	

	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public int getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	public int getSessionCount() {
		return sessionCount;
	}

	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}

	public int getLocationCount() {
		return locationCount;
	}

	public void setLocationCount(int locationCount) {
		this.locationCount = locationCount;
	}

	public int getFutureEventCount() {
		return futureEventCount;
	}

	public void setFutureEventCount(int futureEventCount) {
		this.futureEventCount = futureEventCount;
	}
	
	public String getProgramZone() {
		return programZone;
	}

	public void setProgramZone(String programZone) {
		this.programZone = programZone;
	}

	public String getProgramCenter() {
		return programCenter;
	}

	public void setProgramCenter(String programCenter) {
		this.programCenter = programCenter;
	}
	
	public String getEventState() {
		return eventState;
	}

	public void setEventState(String eventState) {
		this.eventState = eventState;
	}

	public String getProgramDistrict() {
		return programDistrict;
	}

	public void setProgramDistrict(String programDistrict) {
		this.programDistrict = programDistrict;
	}

	public String getEventCity() {
		return eventCity;
	}

	public void setEventCity(String eventCity) {
		this.eventCity = eventCity;
	}

	@Override
	public String toString() {
		return "DashboardResponse [eventCount=" + eventCount + ", participantCount=" + participantCount
				+ ", sessionCount=" + sessionCount + ", locationCount=" + locationCount + ", futureEventCount="
				+ futureEventCount + ", programZone=" + programZone + ", programCenter=" + programCenter
				+ ", eventState=" + eventState + ", programDistrict=" + programDistrict + ", eventCity=" + eventCity
				+ "]";
	}
	
}
