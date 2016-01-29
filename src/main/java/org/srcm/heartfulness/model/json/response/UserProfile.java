package org.srcm.heartfulness.model.json.response;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

	private int id;
	private String name;
	private String first_name;
	private String last_name;
	private String ref;
	private String email;
	private String srcm_group;
	private String age;
	private String gender;
	private String first_sitting_by;
	private String indiv_sittings_by;
	private String resp_prefect;
	private boolean is_prefect;
	private Date date_of_birth;
	private Date date_of_joining;
	private String photo_url;
	private String street;
	private String street2;
	private String street3;
	private String street4;
	private String postal_code;
	private String state;
	private String country;
	private String mobile;
	private String mobile2;
	private String phone;
	private String phone2;
	private int prefect_id;
	private boolean new_address;
	private int abhyasi_id;
	private int aspirant_id;
	private String skills;

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

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSrcm_group() {
		return srcm_group;
	}

	public void setSrcm_group(String srcm_group) {
		this.srcm_group = srcm_group;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getFirst_sitting_by() {
		return first_sitting_by;
	}

	public void setFirst_sitting_by(String first_sitting_by) {
		this.first_sitting_by = first_sitting_by;
	}

	public String getIndiv_sittings_by() {
		return indiv_sittings_by;
	}

	public void setIndiv_sittings_by(String indiv_sittings_by) {
		this.indiv_sittings_by = indiv_sittings_by;
	}

	public String getResp_prefect() {
		return resp_prefect;
	}

	public void setResp_prefect(String resp_prefect) {
		this.resp_prefect = resp_prefect;
	}

	public boolean isIs_prefect() {
		return is_prefect;
	}

	public void setIs_prefect(boolean is_prefect) {
		this.is_prefect = is_prefect;
	}

	public Date getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public Date getDate_of_joining() {
		return date_of_joining;
	}

	public void setDate_of_joining(Date date_of_joining) {
		this.date_of_joining = date_of_joining;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
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

	public String getStreet3() {
		return street3;
	}

	public void setStreet3(String street3) {
		this.street3 = street3;
	}

	public String getStreet4() {
		return street4;
	}

	public void setStreet4(String street4) {
		this.street4 = street4;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public int getPrefect_id() {
		return prefect_id;
	}

	public void setPrefect_id(int prefect_id) {
		this.prefect_id = prefect_id;
	}

	public boolean isNew_address() {
		return new_address;
	}

	public void setNew_address(boolean new_address) {
		this.new_address = new_address;
	}

	public int getAbhyasi_id() {
		return abhyasi_id;
	}

	public void setAbhyasi_id(int abhyasi_id) {
		this.abhyasi_id = abhyasi_id;
	}

	public int getAspirant_id() {
		return aspirant_id;
	}

	public void setAspirant_id(int aspirant_id) {
		this.aspirant_id = aspirant_id;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", name=" + name + ", first_name=" + first_name + ", last_name=" + last_name
				+ ", ref=" + ref + ", email=" + email + ", srcm_group=" + srcm_group + ", age=" + age + ", gender="
				+ gender + ", first_sitting_by=" + first_sitting_by + ", indiv_sittings_by=" + indiv_sittings_by
				+ ", resp_prefect=" + resp_prefect + ", is_prefect=" + is_prefect + ", date_of_birth=" + date_of_birth
				+ ", date_of_joining=" + date_of_joining + ", photo_url=" + photo_url + ", street=" + street
				+ ", street2=" + street2 + ", street3=" + street3 + ", street4=" + street4 + ", postal_code="
				+ postal_code + ", state=" + state + ", country=" + country + ", mobile=" + mobile + ", mobile2="
				+ mobile2 + ", phone=" + phone + ", phone2=" + phone2 + ", prefect_id=" + prefect_id + ", new_address="
				+ new_address + ", abhyasi_id=" + abhyasi_id + ", aspirant_id=" + aspirant_id + ", skills=" + skills
				+ "]";
	}

}
