package org.srcm.heartfulness.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This class is to hold the user details and response user Profile by srcm
 * @author HimaSree
 *
 */
//@JsonInclude(value = Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "first_name", "last_name", "email", "user_type" })
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class User {


	private int id;

	@JsonProperty("name")
	private String name;

	@NotEmpty(message = "Please enter your first name.")
	@JsonProperty("first_name")
	private String first_name;

	@NotEmpty(message = "Please enter your last name.")
	@JsonProperty("last_name")
	private String last_name;

	private String gender;

	@NotEmpty(message = "Please enter your email addresss.")
	@Email
	@JsonProperty("email")
	private String email;

	@JsonProperty("mobile")
	private String mobile;

	@JsonProperty("user_type")
	private String user_type;

	@NotEmpty(message = "Please enter the password.")
	@JsonIgnore
	private String password;

	@JsonIgnore
	private String confirmPassword;

	private String address;

	@JsonProperty("country")
	private String country;

	@JsonProperty("state")
	private String state;

	@JsonProperty("city")
	private String city;

	@JsonIgnore
	private String access_token;

	@JsonIgnore
	private String role;

	
	@JsonProperty("message")
	private String message;
	
	@JsonIgnore
	private String isSahajmargAllowed;
	
	@JsonIgnore
	private String isPmpAllowed;
	
	@JsonIgnore
	private int abyasiId;
	
	@JsonProperty("abyasi_id")
	private String membershipId;
	
	public User() {
		super();
	}

	
	public User(int id, String name, String first_name, String last_name, String gender, String email, String mobile,
			String user_type, String password, String confirmPassword, String address, String country, String state,
			String city, String access_token, String role, String message, String isSahajmargAllowed,
			String isPmpAllowed, int abyasiId, String membershipId) {
		super();
		this.id = id;
		this.name = name;
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
		this.email = email;
		this.mobile = mobile;
		this.user_type = user_type;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.address = address;
		this.country = country;
		this.state = state;
		this.city = city;
		this.access_token = access_token;
		this.role = role;
		this.message = message;
		this.isSahajmargAllowed = isSahajmargAllowed;
		this.isPmpAllowed = isPmpAllowed;
		this.abyasiId = abyasiId;
		this.membershipId = membershipId;
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

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
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

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
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

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIsSahajmargAllowed() {
		return isSahajmargAllowed;
	}

	public void setIsSahajmargAllowed(String isSahajmargAllowed) {
		this.isSahajmargAllowed = isSahajmargAllowed;
	}

	public String getIsPmpAllowed() {
		return isPmpAllowed;
	}


	public void setIsPmpAllowed(String isPmpAllowed) {
		this.isPmpAllowed = isPmpAllowed;
	}


	public int getAbyasiId() {
		return abyasiId;
	}

	public void setAbyasiId(int abyasiId) {
		this.abyasiId = abyasiId;
	}

	public String getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(String membershipId) {
		this.membershipId = membershipId;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", first_name=" + first_name + ", last_name=" + last_name
				+ ", gender=" + gender + ", email=" + email + ", mobile=" + mobile + ", user_type=" + user_type
				+ ", password=" + password + ", confirmPassword=" + confirmPassword + ", address=" + address
				+ ", country=" + country + ", state=" + state + ", city=" + city + ", access_token=" + access_token
				+ ", role=" + role + ", message=" + message + ", isSahajmargAllowed=" + isSahajmargAllowed
				+ ", isPmpAllowed=" + isPmpAllowed + ", abyasiId=" + abyasiId + ", membershipId=" + membershipId + "]";
	}

}
