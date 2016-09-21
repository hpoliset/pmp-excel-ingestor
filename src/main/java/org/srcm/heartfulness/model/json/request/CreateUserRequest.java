package org.srcm.heartfulness.model.json.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class CreateUserRequest {
	
	private String name;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	private String gender;

	private String email;
	
	private String mobile;

	private String password;

	@JsonProperty("age_group")
	private String ageGroup;

	private String zipcode;

	private String languagePreference;

	private String city;

	private String state;

	private String country;
	
	@JsonProperty("user_type")
	private String userType;
	
	@JsonProperty("abyasi_id")
	private String abyasiId;
	
	public CreateUserRequest() {
		super();
	}

	public CreateUserRequest(String name, String firstName, String lastName, String gender, String email,
			String mobile, String password, String ageGroup, String zipcode, String languagePreference, String city,
			String state, String country, String userType, String abyasiId) {
		super();
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.email = email;
		this.mobile = mobile;
		this.password = password;
		this.ageGroup = ageGroup;
		this.zipcode = zipcode;
		this.languagePreference = languagePreference;
		this.city = city;
		this.state = state;
		this.country = country;
		this.userType = userType;
		this.abyasiId = abyasiId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getLanguagePreference() {
		return languagePreference;
	}

	public void setLanguagePreference(String languagePreference) {
		this.languagePreference = languagePreference;
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
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getAbyasiId() {
		return abyasiId;
	}

	public void setAbyasiId(String abyasiId) {
		this.abyasiId = abyasiId;
	}

	@Override
	public String toString() {
		return "CreateUserRequest [name=" + name + ", firstName=" + firstName + ", lastName=" + lastName + ", gender="
				+ gender + ", email=" + email + ", mobile=" + mobile + ", password=" + password + ", ageGroup="
				+ ageGroup + ", zipcode=" + zipcode + ", languagePreference=" + languagePreference + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", userType=" + userType + ", abyasiId=" + abyasiId
				+ "]";
	}

}
