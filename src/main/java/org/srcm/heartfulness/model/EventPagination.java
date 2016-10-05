package org.srcm.heartfulness.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Koustav Dutta
 *
 */
@JsonPropertyOrder({ "totalcount", "pageindex", "pagesize", "eventlist" })
public class EventPagination {
	
	@JsonProperty("totalcount")
	private int  totalCount;
	
	@JsonProperty("pageindex")
	private int pageIndex;
	
	@JsonProperty("pagesize")
	private int pageSize;
	
	@JsonProperty("eventlist")
	private List<org.srcm.heartfulness.model.json.request.Event> eventList;

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

	public List<org.srcm.heartfulness.model.json.request.Event> getEventList() {
		return eventList;
	}

	public void setEventList(List<org.srcm.heartfulness.model.json.request.Event> eventList) {
		this.eventList = eventList;
	}
	
	
	

}
