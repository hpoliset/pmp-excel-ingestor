package org.srcm.heartfulness.model.json.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Koustav Dutta
 *
 */

public class SearchRequest {

	@JsonProperty("searchfield")
	private String searchField;

	@JsonProperty("searchtext")
	private String searchText;

	@JsonProperty("datefrom")
	private String dateFrom;

	@JsonProperty("dateto")
	private String dateTo;

	@JsonProperty("sortby")
	private String sortBy;

	@JsonProperty("sortdirection")
	private String sortDirection;

	@JsonProperty("totalcount")
	private int totalCount;

	@JsonProperty("pageindex")
	private int pageIndex;

	@JsonProperty("pagesize")
	private int pageSize;

	@JsonProperty("eventlist")
	private List<Event> eventList;

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

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<Event> getEventList() {
		return eventList;
	}

	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}

}
