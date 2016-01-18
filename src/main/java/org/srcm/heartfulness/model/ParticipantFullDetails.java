package org.srcm.heartfulness.model;

import java.util.Date;

/**
 * Created by vsonnathi on 11/22/15.
 */
public class ParticipantFullDetails {

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
    private int receiveUpdates;
    private String printName;
    private int firstSittingTaken;
    private int secondSittingTaken;
    private int thirdSittingTaken;
    private String profession;
    private String department;
    private String language;

    private String batch;
    private int excelSheetSequenceNumber;
    private Date batchProcessedTime;
    private Date aimsSyncTime; 
    private Date introductionRawDate;
    private Date createTime;
    private Date updateTime;

    // PROGRAM FIELDS
    private int programId;
    private String programHashCode;
    private int programChannelId;
    private String programChannel;
    private Date programStartDate;
    private Date programEndDate;

    private int coordinatorId;
    private String coordinatorName;
    private String coordinatorEmail;
    private String coordinatorMobile;

    private String eventPlace;
    private String eventCity;
    private String eventState;
    private String eventCountry;

    private int organizationId;
    private String organizationName;
    private String organizationDepartment;
    private String organizationWebSite;
    private String organizationContactName;
    private String organizationContactEmail;
    private String organizationContactMobile;

    private String preceptorName;
    private String preceptorIdCardNumber;

    private String welcomeCardSignedByName;
    private String welcomeCardSignerIdCardNumber;
    private String pgmRemarks;
    private Date pgmBatchProcessedTime;
    private Date pgmCreateTime;
    private Date pgmUpdateTime;
    private String pgmCreatedBy;
    private String pgmUpdatedBy;


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

    public int getFirstSittingTaken() {
        return firstSittingTaken;
    }

    public int getSecondSittingTaken() {
        return secondSittingTaken;
    }

    public int getThirdSittingTaken() {
        return thirdSittingTaken;
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

    public void setFirstSittingTaken(int firstSittingTaken) {
        this.firstSittingTaken = firstSittingTaken;
    }

    public void setSecondSittingTaken(int secondSittingTaken) {
        this.secondSittingTaken = secondSittingTaken;
    }

    public void setThirdSittingTaken(int thirdSittingTaken) {
        this.thirdSittingTaken = thirdSittingTaken;
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

    public void setExcelSheetSequenceNumber(int excelSheetSequenceNumber) {
        this.excelSheetSequenceNumber = excelSheetSequenceNumber;
    }

    public int getExcelSheetSequenceNumber() {
        return excelSheetSequenceNumber;
    }


	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}
    
	public String getProgramHashCode() {
		return programHashCode;
	}

	public void setProgramHashCode(String programHashCode) {
		this.programHashCode = programHashCode;
	}

	public int getProgramChannelId() {
		return programChannelId;
	}

	public void setProgramChannelId(int programChannelId) {
		this.programChannelId = programChannelId;
	}

	public String getProgramChannel() {
		return programChannel;
	}

	public void setProgramChannel(String programChannel) {
		this.programChannel = programChannel;
	}

	public Date getProgramStartDate() {
		return programStartDate;
	}

	public void setProgramStartDate(Date programStartDate) {
		this.programStartDate = programStartDate;
	}

	public Date getProgramEndDate() {
		return programEndDate;
	}

	public void setProgramEndDate(Date programEndDate) {
		this.programEndDate = programEndDate;
	}

	public String getCoordinatorName() {
		return coordinatorName;
	}

	public void setCoordinatorName(String coordinatorName) {
		this.coordinatorName = coordinatorName;
	}

	public String getCoordinatorEmail() {
		return coordinatorEmail;
	}

	public void setCoordinatorEmail(String coordinatorEmail) {
		this.coordinatorEmail = coordinatorEmail;
	}

	public String getCoordinatorMobile() {
		return coordinatorMobile;
	}

	public void setCoordinatorMobile(String coordinatorMobile) {
		this.coordinatorMobile = coordinatorMobile;
	}

	public String getEventPlace() {
		return eventPlace;
	}

	public void setEventPlace(String eventPlace) {
		this.eventPlace = eventPlace;
	}

	public String getEventCity() {
		return eventCity;
	}

	public void setEventCity(String eventCity) {
		this.eventCity = eventCity;
	}

	public String getEventState() {
		return eventState;
	}

	public void setEventState(String eventState) {
		this.eventState = eventState;
	}

	public String getEventCountry() {
		return eventCountry;
	}

	public void setEventCountry(String eventCountry) {
		this.eventCountry = eventCountry;
	}

	public String getOrganizationDepartment() {
		return organizationDepartment;
	}

	public void setOrganizationDepartment(String organizationDepartment) {
		this.organizationDepartment = organizationDepartment;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationWebSite() {
		return organizationWebSite;
	}

	public void setOrganizationWebSite(String organizationWebSite) {
		this.organizationWebSite = organizationWebSite;
	}

	public String getOrganizationContactName() {
		return organizationContactName;
	}

	public void setOrganizationContactName(String organizationContactName) {
		this.organizationContactName = organizationContactName;
	}

	public String getOrganizationContactEmail() {
		return organizationContactEmail;
	}

	public void setOrganizationContactEmail(String organizationContactEmail) {
		this.organizationContactEmail = organizationContactEmail;
	}

	public String getOrganizationContactMobile() {
		return organizationContactMobile;
	}

	public void setOrganizationContactMobile(String organizationContactMobile) {
		this.organizationContactMobile = organizationContactMobile;
	}

	public String getPreceptorName() {
		return preceptorName;
	}

	public void setPreceptorName(String preceptorName) {
		this.preceptorName = preceptorName;
	}

	public String getPreceptorIdCardNumber() {
		return preceptorIdCardNumber;
	}

	public void setPreceptorIdCardNumber(String preceptorIdCardNumber) {
		this.preceptorIdCardNumber = preceptorIdCardNumber;
	}

	public String getWelcomeCardSignedByName() {
		return welcomeCardSignedByName;
	}

	public void setWelcomeCardSignedByName(String welcomeCardSignedByName) {
		this.welcomeCardSignedByName = welcomeCardSignedByName;
	}

	public String getWelcomeCardSignerIdCardNumber() {
		return welcomeCardSignerIdCardNumber;
	}

	public void setWelcomeCardSignerIdCardNumber(String welcomeCardSignerIdCardNumber) {
		this.welcomeCardSignerIdCardNumber = welcomeCardSignerIdCardNumber;
	}

	public int getCoordinatorId() {
		return coordinatorId;
	}

	public void setCoordinatorId(int coordinatorId) {
		this.coordinatorId = coordinatorId;
	}

	public Date getBatchProcessedTime() {
		return batchProcessedTime;
	}

	public void setBatchProcessedTime(Date batchProcessedTime) {
		this.batchProcessedTime = batchProcessedTime;
	}

	public Date getAimsSyncTime() {
		return aimsSyncTime;
	}

	public void setAimsSyncTime(Date aimsSyncTime) {
		this.aimsSyncTime = aimsSyncTime;
	}

	public Date getIntroductionRawDate() {
		return introductionRawDate;
	}

	public void setIntroductionRawDate(Date introductionRawDate) {
		this.introductionRawDate = introductionRawDate;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

	public String getPgmRemarks() {
		return pgmRemarks;
	}

	public void setPgmRemarks(String pgmRemarks) {
		this.pgmRemarks = pgmRemarks;
	}

	public Date getPgmBatchProcessedTime() {
		return pgmBatchProcessedTime;
	}

	public void setPgmBatchProcessedTime(Date pgmBatchProcessedTime) {
		this.pgmBatchProcessedTime = pgmBatchProcessedTime;
	}

	public Date getPgmCreateTime() {
		return pgmCreateTime;
	}

	public void setPgmCreateTime(Date pgmCreateTime) {
		this.pgmCreateTime = pgmCreateTime;
	}

	public Date getPgmUpdateTime() {
		return pgmUpdateTime;
	}

	public void setPgmUpdateTime(Date pgmUpdateTime) {
		this.pgmUpdateTime = pgmUpdateTime;
	}

	public String getPgmCreatedBy() {
		return pgmCreatedBy;
	}

	public void setPgmCreatedBy(String pgmCreatedBy) {
		this.pgmCreatedBy = pgmCreatedBy;
	}

	public String getPgmUpdatedBy() {
		return pgmUpdatedBy;
	}

	public void setPgmUpdatedBy(String pgmUpdatedBy) {
		this.pgmUpdatedBy = pgmUpdatedBy;
	}

	public String toString(){
		return 
				"\""+id+"\"\t\""+printName+"\"\t\""+firstName+"\"\t\""+middleName+"\"\t\""+lastName+"\"\t\""+email+"\"\t\""+mobilePhone+"\"\t\""+
				gender+"\"\t\""+dateOfBirth+"\"\t\""+dateOfRegistration+"\"\t\""+language+"\"\t\""+profession+"\"\t\""+
				abhyasiId+"\"\t\""+idCardNumber+"\"\t\""+status+"\"\t\""+addressLine1+"\"\t\""+addressLine2+"\"\t\""+city+"\"\t\""+
				state+"\"\t\""+country+"\"\t\""+remarks+"\"\t\""+
				introduced+"\"\t\""+introducedBy+"\"\t\""+introductionDate+"\"\t\""+welcomeCardNumber+"\"\t\""+welcomeCardDate+"\"\t\""+ageGroup+"\"\t\""+
				firstSittingTaken+"\"\t\""+firstSittingDate+"\"\t\""+secondSittingTaken+"\"\t\""+secondSittingDate+"\"\t\""+thirdSittingTaken+"\"\t\""+
				thirdSittingDate+"\"\t\""+batch+"\"\t\""+receiveUpdates+"\"\t\""+syncStatus+"\"\t\""+aimsSyncTime+"\"\t\""+uploadStatus+"\"\t\""+
				programId+"\"\t\""+programChannel+"\"\t\""+programStartDate+"\"\t\""+programEndDate+"\"\t\""+
				eventPlace+"\"\t\""+eventState+"\"\t\""+eventCity+"\"\t\""+eventCountry+"\"\t\""+
				organizationId+"\"\t\""+organizationName+"\"\t\""+organizationDepartment+"\"\t\""+organizationWebSite+"\"\t\""+
				organizationContactName+"\"\t\""+organizationContactEmail+"\"\t\""+organizationContactMobile+"\"\t\""+
				preceptorName+"\"\t\""+preceptorIdCardNumber+"\"\t\""+welcomeCardSignedByName+"\"\t\""+welcomeCardSignerIdCardNumber+"\"" ;
	}

}
