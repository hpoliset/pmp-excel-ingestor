/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

	@Override
	public String toString() {
		return "DashboardResponse [eventCount=" + eventCount + ", participantCount=" + participantCount
				+ ", sessionCount=" + sessionCount + ", locationCount=" + locationCount + ", futureEventCount="
				+ futureEventCount + "]";
	}

}
