package org.srcm.heartfulness.model.json.googleapi.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class hold the results of the google api
 * @author himasreev
 *
 */
public class GoogleResponse {
	 
	 @JsonProperty(value="results")
	 private List<Result> results ;
	 
	 private String status ;
	 
	 public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public String getStatus() {
	  return status;
	 }
	 public void setStatus(String status) {
	  this.status = status;
	 }
	 
	 

	}
