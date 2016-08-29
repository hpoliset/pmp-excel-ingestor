package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is the response for the geosearch API from MySRCM.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoSearchResponse {

	@JsonProperty("city_id")
	private int cityId;

	@JsonProperty("state_id")
	private int stateId;

	@JsonProperty("country_id")
	private int countryId;

	@JsonProperty("sub_state_id")
	private boolean subStateId;

	@JsonProperty("nearest_center")
	private int nearestCenter;

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getStateId() {
		return stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public boolean isSubStateId() {
		return subStateId;
	}

	public void setSubStateId(boolean subStateId) {
		this.subStateId = subStateId;
	}

	public int getNearestCenter() {
		return nearestCenter;
	}

	public void setNearestCenter(int nearestCenter) {
		this.nearestCenter = nearestCenter;
	}

	@Override
	public String toString() {
		return "GeoSearchResponse [cityId=" + cityId + ", stateId=" + stateId + ", countryId=" + countryId
				+ ", subStateId=" + subStateId + ", nearestCenter=" + nearestCenter + "]";
	}

}
