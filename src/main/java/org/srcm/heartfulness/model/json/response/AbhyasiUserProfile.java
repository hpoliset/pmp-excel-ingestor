package org.srcm.heartfulness.model.json.response;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author HimaSree
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbhyasiUserProfile {

	private String id;

	private String name;

	private String first_name;

	private String middle_name;

	private String last_name;

	private String email;

	private String ref;

	private AbhyasiSrcmGroup srcm_group;

	private String age;

	private String gender;

	private FirstSittingBy first_sitting_by;

	private IndivSittingsBy indiv_sittings_by;

	private RespPrefect resp_prefect;
	
	private boolean is_prefect;
	
	private String resp_prefect_active;
	
	private String date_of_joining;
	
	private String date_of_birth;
	
	private String photo_url;
	
	private String street;
	
	private String street2;
	
	private String street3;

	private String street4;
	
	private String postal_code;

	private String city;
	
	private AbhaysiCityID city_id;
	
	private AbhyasiState state;
	
	private AbhyasiCountry country;

	private String mobile;

	private String mobile2;
	
	private String phone;

	private String phone2;
	
	private Integer prefect_id;

	private String new_address;

	private String abhyasi_id;

	private String aspirant_id;

	private String[] skills;

	private String linkedin;
	
	private String volunteer_willing;
	
	private String volunteer_avl;
	
	private String latitude;
	
	private String longitude;
	
	private String abhyasi_status;

	private String last_update_user;

	private String last_update_app;
	
	private String email2;

	private String address_last_validated_on;

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

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public AbhyasiSrcmGroup getSrcm_group() {
		return srcm_group;
	}

	public void setSrcm_group(AbhyasiSrcmGroup srcm_group) {
		this.srcm_group = srcm_group;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public FirstSittingBy getFirst_sitting_by() {
		return first_sitting_by;
	}

	public void setFirst_sitting_by(FirstSittingBy first_sitting_by) {
		this.first_sitting_by = first_sitting_by;
	}

	public IndivSittingsBy getIndiv_sittings_by() {
		return indiv_sittings_by;
	}

	public void setIndiv_sittings_by(IndivSittingsBy indiv_sittings_by) {
		this.indiv_sittings_by = indiv_sittings_by;
	}

	public RespPrefect getResp_prefect() {
		return resp_prefect;
	}

	public void setResp_prefect(RespPrefect resp_prefect) {
		this.resp_prefect = resp_prefect;
	}

	public String getResp_prefect_active() {
		return resp_prefect_active;
	}

	public void setResp_prefect_active(String resp_prefect_active) {
		this.resp_prefect_active = resp_prefect_active;
	}

	public String getDate_of_joining() {
		return date_of_joining;
	}

	public void setDate_of_joining(String date_of_joining) {
		this.date_of_joining = date_of_joining;
	}

	public String getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(String date_of_birth) {
		this.date_of_birth = date_of_birth;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public AbhaysiCityID getCity_id() {
		return city_id;
	}

	public void setCity_id(AbhaysiCityID city_id) {
		this.city_id = city_id;
	}

	public AbhyasiState getState() {
		return state;
	}

	public void setState(AbhyasiState state) {
		this.state = state;
	}

	public AbhyasiCountry getCountry() {
		return country;
	}

	public void setCountry(AbhyasiCountry country) {
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

	public boolean isIs_prefect() {
		return is_prefect;
	}

	public void setIs_prefect(boolean is_prefect) {
		this.is_prefect = is_prefect;
	}

	public Integer getPrefect_id() {
		return prefect_id;
	}

	public void setPrefect_id(Integer prefect_id) {
		this.prefect_id = prefect_id;
	}

	public String getNew_address() {
		return new_address;
	}

	public void setNew_address(String new_address) {
		this.new_address = new_address;
	}

	public String getAbhyasi_id() {
		return abhyasi_id;
	}

	public void setAbhyasi_id(String abhyasi_id) {
		this.abhyasi_id = abhyasi_id;
	}

	public String getAspirant_id() {
		return aspirant_id;
	}

	public void setAspirant_id(String aspirant_id) {
		this.aspirant_id = aspirant_id;
	}

	public String[] getSkills() {
		return skills;
	}

	public void setSkills(String[] skills) {
		this.skills = skills;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getVolunteer_willing() {
		return volunteer_willing;
	}

	public void setVolunteer_willing(String volunteer_willing) {
		this.volunteer_willing = volunteer_willing;
	}

	public String getVolunteer_avl() {
		return volunteer_avl;
	}

	public void setVolunteer_avl(String volunteer_avl) {
		this.volunteer_avl = volunteer_avl;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAbhyasi_status() {
		return abhyasi_status;
	}

	public void setAbhyasi_status(String abhyasi_status) {
		this.abhyasi_status = abhyasi_status;
	}

	public String getLast_update_user() {
		return last_update_user;
	}

	public void setLast_update_user(String last_update_user) {
		this.last_update_user = last_update_user;
	}

	public String getLast_update_app() {
		return last_update_app;
	}

	public void setLast_update_app(String last_update_app) {
		this.last_update_app = last_update_app;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getAddress_last_validated_on() {
		return address_last_validated_on;
	}

	public void setAddress_last_validated_on(String address_last_validated_on) {
		this.address_last_validated_on = address_last_validated_on;
	}

	@Override
	public String toString() {
		return "AbhyasiUserProfile [id=" + id + ", name=" + name + ", first_name=" + first_name + ", middle_name="
				+ middle_name + ", last_name=" + last_name + ", email=" + email + ", ref=" + ref + ", srcm_group="
				+ srcm_group + ", age=" + age + ", gender=" + gender + ", first_sitting_by=" + first_sitting_by
				+ ", indiv_sittings_by=" + indiv_sittings_by + ", resp_prefect=" + resp_prefect + ", is_prefect="
				+ is_prefect + ", resp_prefect_active=" + resp_prefect_active + ", date_of_joining=" + date_of_joining
				+ ", date_of_birth=" + date_of_birth + ", photo_url=" + photo_url + ", street=" + street + ", street2="
				+ street2 + ", street3=" + street3 + ", street4=" + street4 + ", postal_code=" + postal_code
				+ ", city=" + city + ", city_id=" + city_id + ", state=" + state + ", country=" + country + ", mobile="
				+ mobile + ", mobile2=" + mobile2 + ", phone=" + phone + ", phone2=" + phone2 + ", prefect_id="
				+ prefect_id + ", new_address=" + new_address + ", abhyasi_id=" + abhyasi_id + ", aspirant_id="
				+ aspirant_id + ", skills=" + Arrays.toString(skills) + ", linkedin=" + linkedin
				+ ", volunteer_willing=" + volunteer_willing + ", volunteer_avl=" + volunteer_avl + ", latitude="
				+ latitude + ", longitude=" + longitude + ", abhyasi_status=" + abhyasi_status + ", last_update_user="
				+ last_update_user + ", last_update_app=" + last_update_app + ", email2=" + email2
				+ ", address_last_validated_on=" + address_last_validated_on + "]";
	}

}
