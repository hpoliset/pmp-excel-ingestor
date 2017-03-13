package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class to store the result of MYSRCM get Abhyasi profile API.
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbhyasiResult {

	private int count;
	private String next;
	private String previous;

	@JsonProperty("results")
	private AbhyasiUserProfile userProfile[];

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

	public AbhyasiUserProfile[] getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(AbhyasiUserProfile[] userProfile) {
		this.userProfile = userProfile;
	}

	@Override
	public String toString() {
		return "Result [count=" + count + ", next=" + next + ", previous=" + previous + ", userProfile=" + userProfile
				+ "]";
	}

}
