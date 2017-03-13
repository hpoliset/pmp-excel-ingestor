package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is the response for the api/v2/cities/ of MySRCM to fetch the city
 * name.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CitiesAPIResponse {

	private String id;
	private String name;
	private State state;
	private String longitude;
	private String latitude;

	public CitiesAPIResponse() {
		super();
	}

	public CitiesAPIResponse(String id, String name, State state, String longitude, String latitude) {
		super();
		this.id = id;
		this.name = name;
		this.state = state;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "CitiesAPIResponse [id=" + id + ", name=" + name + ", state=" + state + ", longitude=" + longitude
				+ ", latitude=" + latitude + "]";
	}

}
