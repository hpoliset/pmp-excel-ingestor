package org.srcm.heartfulness.model.json.googleapi.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is to response of the google api to fetch the address details for the given pincode
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

	private String formatted_address;

	@JsonIgnore
	private Object geometry;

	@JsonProperty(value="address_components")
	private List<AddressComponents> address_components;

	@JsonIgnore
	private List<Object> types;

	@JsonIgnore
	private String place_id;

	public String getPlace_id() {
		return place_id;
	}

	public void setPlace_id(String place_id) {
		this.place_id = place_id;
	}

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public Object getGeometry() {
		return geometry;
	}

	public void setGeometry(Object geometry) {
		this.geometry = geometry;
	}

	public List<Object> getTypes() {
		return types;
	}

	public void setTypes(List<Object> types) {
		this.types = types;
	}

	public List<AddressComponents> getAddress_components() {
		return address_components;
	}

	public void setAddress_components(List<AddressComponents> address_components) {
		this.address_components = address_components;
	}

	@Override
	public String toString() {
		return "Result [formatted_address=" + formatted_address  +  ", geometry="
				+ geometry + ", address_components=" + address_components + ", types=" + types + ", place_id="
				+ place_id + "]";
	}

}
