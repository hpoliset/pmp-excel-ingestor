package org.srcm.heartfulness.model.json.googleapi.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class to hold the address components in the response of GOOGLE MAPS API.
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressComponents {

	private String long_name;

	private String short_name;

	private List<String> types;

	public AddressComponents() {
		super();
	}

	public String getLong_name() {
		return long_name;
	}

	public void setLong_name(String long_name) {
		this.long_name = long_name;
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	@Override
	public String toString() {
		return "AddressComponents [long_name=" + long_name + ", short_name=" + short_name + ", types=" + types + "]";
	}

}
