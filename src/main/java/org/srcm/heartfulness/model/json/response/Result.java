package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class to store the result of srcm getprofile api
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

	private int count;

	private String next;

	private String previous;

	@JsonProperty("results")
	private UserProfile userProfile[];

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

	public UserProfile[] getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile[] userProfile) {
		this.userProfile = userProfile;
	}

	@Override
	public String toString() {
		return "Result [count=" + count + ", next=" + next + ", previous=" + previous + ", userProfile=" + userProfile
				+ "]";
	}

}
