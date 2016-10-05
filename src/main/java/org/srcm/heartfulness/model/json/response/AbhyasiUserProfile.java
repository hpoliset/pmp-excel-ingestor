package org.srcm.heartfulness.model.json.response;

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

	private boolean is_prefect;
	
	private String date_of_joining;
	
	private String date_of_birth;
	
	private String city;
	
	private AbhaysiCityID city_id;
	
	private AbhyasiState state;
	
	private AbhyasiCountry country;

	private Integer prefect_id;

	private String abhyasi_status;

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

	public boolean isIs_prefect() {
		return is_prefect;
	}

	public void setIs_prefect(boolean is_prefect) {
		this.is_prefect = is_prefect;
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

	public Integer getPrefect_id() {
		return prefect_id;
	}

	public void setPrefect_id(Integer prefect_id) {
		this.prefect_id = prefect_id;
	}

	public String getAbhyasi_status() {
		return abhyasi_status;
	}

	public void setAbhyasi_status(String abhyasi_status) {
		this.abhyasi_status = abhyasi_status;
	}

	@Override
	public String toString() {
		return "AbhyasiUserProfile [id=" + id + ", name=" + name + ", first_name=" + first_name + ", middle_name="
				+ middle_name + ", last_name=" + last_name + ", email=" + email + ", ref=" + ref + ", srcm_group="
				+ srcm_group + ", is_prefect=" + is_prefect + ", date_of_joining=" + date_of_joining
				+ ", date_of_birth=" + date_of_birth + ", city=" + city + ", city_id=" + city_id + ", state=" + state
				+ ", country=" + country + ", prefect_id=" + prefect_id + ", abhyasi_status=" + abhyasi_status + "]";
	}
	
}
