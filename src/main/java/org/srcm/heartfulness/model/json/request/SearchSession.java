/**
 * 
 */
package org.srcm.heartfulness.model.json.request;

import java.util.List;

import org.srcm.heartfulness.model.SessionDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Koustav Dutta
 *
 */
public class SearchSession {
	
	@JsonProperty("search_field")
	private String searchField;

	@JsonProperty("search_text")
	private String searchText;

	@JsonProperty("date_from")
	private String dateFrom;

	@JsonProperty("date_to")
	private String dateTo;
	
	@JsonProperty("event_id")
	private String eventId;
	
	@JsonIgnore
	private String dbSearchField;
	
	@JsonProperty("sessionlist")
	private List<SessionDetails> sessionList;

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public List<SessionDetails> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<SessionDetails> sessionList) {
		this.sessionList = sessionList;
	}

	public String getDbSearchField() {
		return dbSearchField;
	}

	public void setDbSearchField(String dbSearchField) {
		this.dbSearchField = dbSearchField;
	}
}
