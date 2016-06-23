package org.srcm.heartfulness.model.json.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is the request to create or update the paricipant for the event
 * 
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class ParticipantRequest {

	@JsonIgnore
	private int id;

	@JsonIgnore
	private int programId;

	private String seqId;

	private String eventId;

	private String printName;

	private String gender;

	private String dateOfBirth;

	private String addressLine1;

	private String addressLine2;

	private String email;

	private String mobilePhone;

	private String city;

	private String state;

	private String country;

	private String introducedStatus;

	private String introductionDate;

	@JsonIgnore
	private int introduced;

	private String introducedBy;

	private String abhyasiId;

	@JsonIgnore
	private int excelSheetSequenceNumber;

	public int getExcelSheetSequenceNumber() {
		return excelSheetSequenceNumber;
	}

	public void setExcelSheetSequenceNumber(int excelSheetSequenceNumber) {
		this.excelSheetSequenceNumber = excelSheetSequenceNumber;
	}

	public String getIntroductionDate() {
		return introductionDate;
	}

	public void setIntroductionDate(String introductionDate) {
		this.introductionDate = introductionDate;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getPrintName() {
		return printName;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
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

	public int getIntroduced() {
		return introduced;
	}

	public void setIntroduced(int introduced) {
		this.introduced = introduced;
	}

	public String getIntroducedBy() {
		return introducedBy;
	}

	public void setIntroducedBy(String introducedBy) {
		this.introducedBy = introducedBy;
	}

	public String getAbhyasiId() {
		return abhyasiId;
	}

	public void setAbhyasiId(String abhyasiId) {
		this.abhyasiId = abhyasiId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getIntroducedStatus() {
		return introducedStatus;
	}

	public void setIntroducedStatus(String introducedStatus) {
		this.introducedStatus = introducedStatus;
	}

	public ParticipantRequest() {
		super();
	}

	public ParticipantRequest(int id, int programId, String seqId, String eventId, String printName, String gender,
			String dateOfBirth, String addressLine1, String addressLine2, String email, String mobilePhone,
			String city, String state, String country, String introducedStatus, String introductionDate,
			int introduced, String introducedBy, String abhyasiId, int excelSheetSequenceNumber) {
		super();
		this.id = id;
		this.programId = programId;
		this.seqId = seqId;
		this.eventId = eventId;
		this.printName = printName;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.email = email;
		this.mobilePhone = mobilePhone;
		this.city = city;
		this.state = state;
		this.country = country;
		this.introducedStatus = introducedStatus;
		this.introductionDate = introductionDate;
		this.introduced = introduced;
		this.introducedBy = introducedBy;
		this.abhyasiId = abhyasiId;
		this.excelSheetSequenceNumber = excelSheetSequenceNumber;
	}

	@Override
	public String toString() {
		return "ParticipantRequest [id=" + id + ", programId=" + programId + ", seqId=" + seqId + ", eventId="
				+ eventId + ", printName=" + printName + ", gender=" + gender + ", dateOfBirth=" + dateOfBirth
				+ ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", email=" + email
				+ ", mobile=" + mobilePhone + ", city=" + city + ", state=" + state + ", country=" + country
				+ ", introducedStatus=" + introducedStatus + ", introductionDate=" + introductionDate + ", introduced="
				+ introduced + ", introducedBy=" + introducedBy + ", abhyasiId=" + abhyasiId
				+ ", excelSheetSequenceNumber=" + excelSheetSequenceNumber + "]";
	}

}
