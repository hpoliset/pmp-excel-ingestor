package org.srcm.heartfulness.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * @author HimaSree
 *
 */
@JsonPropertyOrder({"id","name","first_name","last_name","email","user_type"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	private int id;

	@JsonProperty("name")
	private String name;

	@NotEmpty(message = "Please enter your first name.")
	@JsonProperty("first_name")
	private String firstName;

	@NotEmpty(message = "Please enter your last name.")
	@JsonProperty("last_name")
	private String lastName;

	private String gender;

	@NotEmpty(message = "Please enter your email addresss.")
	@Email
	@JsonProperty("email")
	private String email;

	private String mobile;

	@JsonProperty("user_type")
	private String userType;

	@NotEmpty(message = "Please enter the password.")
	private String password;

	@JsonIgnore
	private String confirmPassword;

	private String address;

	private String country;

	private String state;

	private String city;

	public User() {
		super();
	}

	public User(int id, String name, String firstName, String lastName, String gender, String email, String mobile,
			String userType, String password, String confirmPassword, String address, String country, String state,
			String city) {
		super();
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.email = email;
		this.mobile = mobile;
		this.userType = userType;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.address = address;
		this.country = country;
		this.state = state;
		this.city = city;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", gender=" + gender + ", email=" + email + ", mobile=" + mobile + ", userType=" + userType
				+ ", password=" + password + ", confirmPassword=" + confirmPassword + ", address=" + address
				+ ", country=" + country + ", state=" + state + ", city=" + city + "]";
	}

}
