package org.srcm.heartfulness.model;

import java.util.Date;

/**
 * Created by vsonnathi on 11/22/15.
 */
public class Participant {

	private int id;
	private String hashCode;
	private String firstName;
	private String lastName;
	private String middleName;
	private String email;
	private String mobilePhone;
	private String gender;
	private Date dateOfBirth;
	private Date dateOfRegistration;
	private String abhyasiId;
	private int status;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private int program_id;
	private String remarks;
	private String idCardNumber;
	private String syncStatus;
	private int introduced;
	private Date introductionDate;
	private String introducedBy;
	private String welcomeCardNumber;
	private Date welcomeCardDate;
	private String ageGroup;
	private String uploadStatus;
	private Date firstSittingDate;
	private Date secondSittingDate;
	private Date thirdSittingDate;
	private String batch;
	private int receiveUpdates;
	private String printName;
	private Integer firstSitting;
	private Integer secondSitting;
	private Integer thirdSitting;
	private String profession;
	private String department;
	private String language;
	private Program program;
	private int excelSheetSequenceNumber;
	private String seqId;
	private int welcomeMailSent;
	private String createdSource;
	private int isCoOrdinatorInformed;
	private Integer isEwelcomeIdInformed;
	private String ewelcomeIdState;
	private String ewelcomeIdRemarks;
	private Integer totalDays;
	private String phone;
	private String district;
	private String ewelcomeIdGenerationMsg;

	public String getCreatedSource() {
		return createdSource;
	}

	public void setCreatedSource(String createdSource) {
		this.createdSource = createdSource;
	}

	public int getWelcomeMailSent() {
		return welcomeMailSent;
	}

	public void setWelcomeMailSent(int welcomeMailSent) {
		this.welcomeMailSent = welcomeMailSent;
	}

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
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

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Date getDateOfRegistration() {
		return dateOfRegistration;
	}

	public void setDateOfRegistration(Date dateOfRegistration) {
		this.dateOfRegistration = dateOfRegistration;
	}

	public String getAbhyasiId() {
		return abhyasiId;
	}

	public void setAbhyasiId(String abhyasiId) {
		this.abhyasiId = abhyasiId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public int getProgramId() {
		return program_id;
	}

	public void setProgramId(int program_id) {
		this.program_id = program_id;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public String getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(String syncStatus) {
		this.syncStatus = syncStatus;
	}

	public int getIntroduced() {
		return introduced;
	}

	public void setIntroduced(int introduced) {
		this.introduced = introduced;
	}

	public Date getIntroductionDate() {
		return introductionDate;
	}

	public void setIntroductionDate(Date introductionDate) {
		this.introductionDate = introductionDate;
	}

	public String getIntroducedBy() {
		return introducedBy;
	}

	public void setIntroducedBy(String introducedBy) {
		this.introducedBy = introducedBy;
	}

	public String getWelcomeCardNumber() {
		return welcomeCardNumber;
	}

	public void setWelcomeCardNumber(String welcomeCardNumber) {
		this.welcomeCardNumber = welcomeCardNumber;
	}

	public Date getWelcomeCardDate() {
		return welcomeCardDate;
	}

	public void setWelcomeCardDate(Date welcomeCardDate) {
		this.welcomeCardDate = welcomeCardDate;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public Date getFirstSittingDate() {
		return firstSittingDate;
	}

	public void setFirstSittingDate(Date firstSittingDate) {
		this.firstSittingDate = firstSittingDate;
	}

	public Date getSecondSittingDate() {
		return secondSittingDate;
	}

	public void setSecondSittingDate(Date secondSittingDate) {
		this.secondSittingDate = secondSittingDate;
	}

	public Date getThirdSittingDate() {
		return thirdSittingDate;
	}

	public void setThirdSittingDate(Date thirdSittingDate) {
		this.thirdSittingDate = thirdSittingDate;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public int getReceiveUpdates() {
		return receiveUpdates;
	}

	public void setReceiveUpdates(int receiveUpdates) {
		this.receiveUpdates = receiveUpdates;
	}

	public String getPrintName() {
		return printName;
	}

	public String getProfession() {
		return profession;
	}

	public String getDepartment() {
		return department;
	}

	public String getLanguage() {
		return language;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
	}


	public void setProfession(String profession) {
		this.profession = profession;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
		this.setProgramId(program.getProgramId());
	}

	public void setExcelSheetSequenceNumber(int excelSheetSequenceNumber) {
		this.excelSheetSequenceNumber = excelSheetSequenceNumber;
	}

	public int getExcelSheetSequenceNumber() {
		return excelSheetSequenceNumber;
	}

	public int getIsCoOrdinatorInformed() {
		return isCoOrdinatorInformed;
	}

	public void setIsCoOrdinatorInformed(int isCoOrdinatorInformed) {
		this.isCoOrdinatorInformed = isCoOrdinatorInformed;
	}

	public Integer getFirstSitting() {
		return firstSitting;
	}

	public void setFirstSitting(Integer firstSitting) {
		this.firstSitting = firstSitting;
	}

	public Integer getSecondSitting() {
		return secondSitting;
	}

	public void setSecondSitting(Integer secondSitting) {
		this.secondSitting = secondSitting;
	}

	public Integer getThirdSitting() {
		return thirdSitting;
	}

	public void setThirdSitting(Integer thirdSitting) {
		this.thirdSitting = thirdSitting;
	}

	public Integer getIsEwelcomeIdInformed() {
		return isEwelcomeIdInformed;
	}

	public void setIsEwelcomeIdInformed(Integer isEwelcomeIdInformed) {
		this.isEwelcomeIdInformed = isEwelcomeIdInformed;
	}

	public String getEwelcomeIdState() {
		return ewelcomeIdState;
	}

	public void setEwelcomeIdState(String ewelcomeIdState) {
		this.ewelcomeIdState = ewelcomeIdState;
	}

	public String getEwelcomeIdRemarks() {
		return ewelcomeIdRemarks;
	}

	public void setEwelcomeIdRemarks(String ewelcomeIdRemarks) {
		this.ewelcomeIdRemarks = ewelcomeIdRemarks;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
	
	public String getEwelcomeIdGenerationMsg() {
		return ewelcomeIdGenerationMsg;
	}

	public void setEwelcomeIdGenerationMsg(String ewelcomeIdGenerationMsg) {
		this.ewelcomeIdGenerationMsg = ewelcomeIdGenerationMsg;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Participant other = (Participant) obj;
		
		if (printName == null) {
			if (other.printName != null)
				return false;
		}
		
		if (email == null) {
			if (other.email != null)
				return false;
		}
		
		if (mobilePhone == null) {
			if (other.mobilePhone != null)
				return false;
		}
		
		if(!((printName.equals(other.printName))&&(program_id==other.program_id)&&((email.equals(other.email)&&mobilePhone.equals(other.mobilePhone))
				|| excelSheetSequenceNumber == other.excelSheetSequenceNumber
				|| mobilePhone.equals(other.mobilePhone)
				|| email.equals(other.email)))){
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "Participant [firstName=" + firstName + ", lastName=" + lastName + ", middleName=" + middleName
				+ ", email=" + email + ", mobilePhone=" + mobilePhone + ", gender=" + gender + ", dateOfBirth="
				+ dateOfBirth + ", dateOfRegistration=" + dateOfRegistration + ", abhyasiId=" + abhyasiId + ", status="
				+ status + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", program_id=" + program_id + ", remarks=" + remarks
				+ ", idCardNumber=" + idCardNumber + ", syncStatus=" + syncStatus + ", introduced=" + introduced
				+ ", introductionDate=" + introductionDate + ", introducedBy=" + introducedBy + ", welcomeCardNumber="
				+ welcomeCardNumber + ", welcomeCardDate=" + welcomeCardDate + ", ageGroup=" + ageGroup
				+ ", uploadStatus=" + uploadStatus + ", firstSittingDate=" + firstSittingDate + ", secondSittingDate="
				+ secondSittingDate + ", thirdSittingDate=" + thirdSittingDate + ", batch=" + batch
				+ ", receiveUpdates=" + receiveUpdates + ", printName=" + printName + ", firstSitting=" + firstSitting
				+ ", secondSitting=" + secondSitting + ", thirdSitting=" + thirdSitting + ", profession=" + profession
				+ ", department=" + department + ", language=" + language + ", program=" + program
				+ ", excelSheetSequenceNumber=" + excelSheetSequenceNumber + ", seqId=" + seqId + ", welcomeMailSent="
				+ welcomeMailSent + ", createdSource=" + createdSource + ", isCoOrdinatorInformed="
				+ isCoOrdinatorInformed + ", isEwelcomeIdInformed=" + isEwelcomeIdInformed + ", ewelcomeIdState="
				+ ewelcomeIdState + ", ewelcomeIdRemarks=" + ewelcomeIdRemarks + ", totalDays=" + totalDays + ", phone="
				+ phone + ", district=" + district + "]";
	}

}
