package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class holds the details of the abyasi to create abyasi profile in MySRCM.
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aspirant {

	private String name;

	@JsonProperty("first_name")
	private String firstName;

	private String email;

	@JsonProperty("srcm_group")
	private String srcmGroup;

	@JsonProperty("first_sitting_by")
	private String firstSittingBy;

	@JsonProperty("date_of_birth")
	private String dateOfBirth;

	@JsonProperty("date_of_joining")
	private String dateOfJoining;
	
	private String mobile;
	private String street;
	private String street2;
	
	@JsonProperty("postal_code")
	private String postalCode;
	
	private String city;
	private String state;
	private String country;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSrcmGroup() {
		return srcmGroup;
	}

	public void setSrcmGroup(String srcmGroup) {
		this.srcmGroup = srcmGroup;
	}

	public String getFirstSittingBy() {
		return firstSittingBy;
	}

	public void setFirstSittingBy(String firstSittingBy) {
		this.firstSittingBy = firstSittingBy;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(String dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "AspirantRequest [name=" + name + ", firstName=" + firstName + ", email=" + email + ", srcmGroup="
				+ srcmGroup + ", firstSittingBy=" + firstSittingBy + ", dateOfBirth=" + dateOfBirth + ", dateOfJoining="
				+ dateOfJoining + ", mobile=" + mobile + ", street=" + street + ", street2=" + street2 + ", postalCode="
				+ postalCode + ", city=" + city + ", state=" + state + ", country=" + country + "]";
	}


}
