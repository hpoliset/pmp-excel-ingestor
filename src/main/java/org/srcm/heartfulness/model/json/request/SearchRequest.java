package org.srcm.heartfulness.model.json.request;

public class SearchRequest {
	
	private String searchField;
	
	private String searchText;
	
	private String dateFrom;
	
	private String dateTo;
	
	private String sortBy;
	
	private String sortDirection;

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

	@Override
	public String toString() {
		return "SearchRequest [searchField=" + searchField + ", searchText=" + searchText + ", dateFrom=" + dateFrom
				+ ", dateTo=" + dateTo + ", sortBy=" + sortBy + ", sortDirection=" + sortDirection + "]";
	}

}
