/**
 * 
 */
package org.srcm.heartfulness.model.json.response;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Koustav Dutta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PositionAPIResult {
	
	private int count;
	private String next;
	private String previous;

	@JsonProperty("results")
	private CoordinatorPositionResponse coordinatorPosition[];

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public CoordinatorPositionResponse[] getCoordinatorPosition() {
		return coordinatorPosition;
	}

	public void setCoordinatorPosition(CoordinatorPositionResponse[] coordinatorPosition) {
		this.coordinatorPosition = coordinatorPosition;
	}

	@Override
	public String toString() {
		return "PositionAPIResult [count=" + count + ", next=" + next + ", previous=" + previous
				+ ", coordinatorPosition=" + Arrays.toString(coordinatorPosition) + "]";
	}
	
}
