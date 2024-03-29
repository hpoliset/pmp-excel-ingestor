package org.srcm.heartfulness.model;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Domain class representing Program.
 *
 * @author Venkat Sonnathi
 */
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true,allowGetters = false)
public class Program {
	
	@JsonIgnore
	private int programId;
	private String encryptedId; 
	private String programHashCode;
	private int programChannelId;
	private String programChannel;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@NotNull(message = "Program start date is required")
	private Date programStartDate;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date programEndDate;
	@NotEmpty(message = "Coordinator name is required")
	private String coordinatorName;
	@NotEmpty(message = "Coordinator email is required")
	private String coordinatorEmail;
	@NotEmpty(message = "Coordinator mobile is required")
	private String coordinatorMobile;
	@NotEmpty(message = "Event place is required")
	private String eventPlace;
	@NotEmpty(message = "Event city is required")
	private String eventCity;
	@NotEmpty(message = "Event state is required")
	private String eventState;
	@NotEmpty(message = "Event country is required")
	private String eventCountry;
	private String organizationDepartment;
	@NotEmpty(message = "Organization name is required")
	private String organizationName;
	private String organizationWebSite;
	@NotEmpty(message = "Organization contact name is required")
	private String organizationContactName;
	@NotEmpty(message = "Organization contact email is required")
	@Email(message="Organization contact email is required")
	private String organizationContactEmail;
	@NotEmpty(message = "Organization contact mobile is required")
	private String organizationContactMobile;
	@NotEmpty(message = "Preceptor name is required")
	private String preceptorName;
	@NotEmpty(message = "Preceptor id card number is required")
	private String preceptorIdCardNumber;
	private String welcomeCardSignedByName;
	private String welcomeCardSignerIdCardNumber;
	private String remarks;
	private Date createTime;
	private Date updateTime;
	private String createdBy;
	private String updatedBy;
	private String autoGeneratedEventId;
	private String autoGeneratedIntroId;
	private String programName;
	private List<Participant> participantList = Collections.emptyList();
	private String createdSource;
	private String srcmGroup;
	private int firstSittingBy;
	private String abyasiRefNo;
	private String coordinatorAbhyasiId;
	private String coordinatorPermissionLetterPath;
	private String programZone;
	private String programCenter;
	private String organizationBatchNo;
	private String organizationCity;
	private String organizationLocation;
	private String organizationFullAddress;
	private String organizationDecisionMakerName;
	private String organizationDecisionMakerEmail;
	private String organizationDecisionMakerPhoneNo;
	private String isEwelcomeIdGenerationDisabled;
	private String isReadOnly;
	private String jiraIssueNumber;
	private String sendersEmailAddress;
	
	private String batchDescription;
	private String programAddress;
	private String programDistrict;
	private String organizationContactDesignation;
	private int programChannelType;
	private int userId;
	private int uploadedFileId;
	@JsonIgnore
	private String uploaderMail;
	private String programStatus;
	private String statusDescription;

	public String getSrcmGroup() {
		return srcmGroup;
	}

	public void setSrcmGroup(String srcmGroup) {
		this.srcmGroup = srcmGroup;
	}

	public int getFirstSittingBy() {
		return firstSittingBy;
	}

	public void setFirstSittingBy(int firstSittingBy) {
		this.firstSittingBy = firstSittingBy;
	}

	public String getAbyasiRefNo() {
		return abyasiRefNo;
	}

	public void setAbyasiRefNo(String abyasiRefNo) {
		this.abyasiRefNo = abyasiRefNo;
	}


	public String getCreatedSource() {
		return createdSource;
	}

	public void setCreatedSource(String createdSource) {
		this.createdSource = createdSource;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public String getEncryptedId() {
		return encryptedId;
	}

	public void setEncryptedId(String encryptedId) {
		this.encryptedId = encryptedId;
	}

	public int getProgramChannelId() {
		return programChannelId;
	}

	public void setProgramChannelId(int programChannelId)   {
		this.programChannelId = programChannelId;
		this.programHashCode = computeHashCode();
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

	public void setProgramStartDate(Date programStartDate)   {
		if (programStartDate != null) {
			this.programStartDate = new java.sql.Date(programStartDate.getTime()); // to just focus on date and time.
			this.programHashCode = computeHashCode();
		}
	}

	public Date getProgramEndDate() {
		return programEndDate;
	}

	public void setProgramEndDate(Date programEndDate) {
		if (programEndDate != null) {
			this.programEndDate = new java.sql.Date(programEndDate.getTime());
		}
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

	public void setEventPlace(String eventPlace)   {
		this.eventPlace = eventPlace;
		this.programHashCode = computeHashCode();
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

	public void setOrganizationDepartment(String organizationDepartment)   {
		this.organizationDepartment = organizationDepartment;
		this.programHashCode = computeHashCode();
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName)   {
		this.organizationName = organizationName;
		this.programHashCode = computeHashCode();
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getProgramHashCode() {
		return programHashCode;
	}

	public void setProgramHashCode(String programHashCode) {
		this.programHashCode = programHashCode;
	}

	public String getAutoGeneratedEventId() {
		return autoGeneratedEventId;
	}

	public void setAutoGeneratedEventId(String autoGeneratedEventId) {
		this.autoGeneratedEventId = autoGeneratedEventId;
	}

	public String getAutoGeneratedIntroId() {
		return autoGeneratedIntroId;
	}

	public void setAutoGeneratedIntroId(String autoGeneratedIntroId) {
		this.autoGeneratedIntroId = autoGeneratedIntroId;
	}

	public void setParticipantList(List<Participant> participantList) {
		this.participantList = participantList;
	}

	public List<Participant> getParticipantList() {
		return participantList;
	}

	public String getCoordinatorAbhyasiId() {
		return coordinatorAbhyasiId;
	}

	public void setCoordinatorAbhyasiId(String coordinatorAbhyasiId) {
		this.coordinatorAbhyasiId = coordinatorAbhyasiId;
	}

	public String getCoordinatorPermissionLetterPath() {
		return coordinatorPermissionLetterPath;
	}

	public void setCoordinatorPermissionLetterPath(String coordinatorPermissionLetterPath) {
		this.coordinatorPermissionLetterPath = coordinatorPermissionLetterPath;
	}

	public String getProgramZone() {
		return programZone;
	}

	public void setProgramZone(String programZone) {
		this.programZone = programZone;
	}

	public String getProgramCenter() {
		return programCenter;
	}

	public void setProgramCenter(String programCenter) {
		this.programCenter = programCenter;
	}

	public String getOrganizationBatchNo() {
		return organizationBatchNo;
	}

	public void setOrganizationBatchNo(String organizationBatchNo) {
		this.organizationBatchNo = organizationBatchNo;
	}

	public String getOrganizationCity() {
		return organizationCity;
	}

	public void setOrganizationCity(String organizationCity) {
		this.organizationCity = organizationCity;
	}

	public String getOrganizationLocation() {
		return organizationLocation;
	}

	public void setOrganizationLocation(String organizationLocation) {
		this.organizationLocation = organizationLocation;
	}

	public String getOrganizationFullAddress() {
		return organizationFullAddress;
	}

	public void setOrganizationFullAddress(String organizationFullAddress) {
		this.organizationFullAddress = organizationFullAddress;
	}

	public String getOrganizationDecisionMakerName() {
		return organizationDecisionMakerName;
	}

	public void setOrganizationDecisionMakerName(String organizationDecisionMakerName) {
		this.organizationDecisionMakerName = organizationDecisionMakerName;
	}

	public String getOrganizationDecisionMakerEmail() {
		return organizationDecisionMakerEmail;
	}

	public void setOrganizationDecisionMakerEmail(String organizationDecisionMakerEmail) {
		this.organizationDecisionMakerEmail = organizationDecisionMakerEmail;
	}

	public String getOrganizationDecisionMakerPhoneNo() {
		return organizationDecisionMakerPhoneNo;
	}

	public void setOrganizationDecisionMakerPhoneNo(String organizationDecisionMakerPhoneNo) {
		this.organizationDecisionMakerPhoneNo = organizationDecisionMakerPhoneNo;
	}

	public String getIsEwelcomeIdGenerationDisabled() {
		return isEwelcomeIdGenerationDisabled;
	}

	public void setIsEwelcomeIdGenerationDisabled(String isEwelcomeIdGenerationDisabled) {
		this.isEwelcomeIdGenerationDisabled = isEwelcomeIdGenerationDisabled;
	}

	public String getIsReadOnly() {
		return isReadOnly;
	}

	public void setIsReadOnly(String isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public String getJiraIssueNumber() {
		return jiraIssueNumber;
	}

	public void setJiraIssueNumber(String jiraIssueNumber) {
		this.jiraIssueNumber = jiraIssueNumber;
	}
	
	public String getSendersEmailAddress() {
		return sendersEmailAddress;
	}

	public void setSendersEmailAddress(String sendersEmailAddress) {
		this.sendersEmailAddress = sendersEmailAddress;
	}
	
	public String getBatchDescription() {
		return batchDescription;
	}

	public void setBatchDescription(String batchDescription) {
		this.batchDescription = batchDescription;
	}

	public String getProgramAddress() {
		return programAddress;
	}

	public void setProgramAddress(String programAddress) {
		this.programAddress = programAddress;
	}

	public String getProgramDistrict() {
		return programDistrict;
	}

	public void setProgramDistrict(String programDistrict) {
		this.programDistrict = programDistrict;
	}

	public String getOrganizationContactDesignation() {
		return organizationContactDesignation;
	}

	public void setOrganizationContactDesignation(String organizationContactDesignation) {
		this.organizationContactDesignation = organizationContactDesignation;
	}
	
	public int getProgramChannelType() {
		return programChannelType;
	}

	public void setProgramChannelType(int programChannelType) {
		this.programChannelType = programChannelType;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUploadedFileId() {
		return uploadedFileId;
	}

	public void setUploadedFileId(int uploadedFileId) {
		this.uploadedFileId = uploadedFileId;
	}

	public String getUploaderMail() {
		return uploaderMail;
	}

	public void setUploaderMail(String uploaderMail) {
		this.uploaderMail = uploaderMail;
	}
	
	public String getProgramStatus() {
		return programStatus;
	}

	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}
	
	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	/**
	 * Generates a MD5 hash code based on program channel, Organisation Name, Organisation Dept, Event Date, Event Place
	 *
	 * @return computed MD5 hash code.
	 */
	public String computeHashCode() {
		// Concatenate all the fields.
		StringBuilder elementsOfMessage = new StringBuilder();
		if (programChannel != null) {
			elementsOfMessage.append(programChannel);
		}
		if (organizationName != null) {
			elementsOfMessage.append(organizationName);
		}
		/*	if (organizationDepartment != null) {
			elementsOfMessage.append(organizationDepartment);
		}*/
		if (programStartDate != null) {
			elementsOfMessage.append(programStartDate);
		}
		if (eventPlace != null) {
			elementsOfMessage.append(eventPlace);
		}

		byte[] bytesOfMessage = elementsOfMessage.toString().getBytes(Charset.forName("UTF-8"));
		return DigestUtils.md5DigestAsHex(bytesOfMessage);
	}

	@Override
	public String toString() {
		return "Program [encryptedId=" + encryptedId + ", programChannelId=" + programChannelId + ", programChannel="
				+ programChannel + ", programStartDate=" + programStartDate + ", programEndDate=" + programEndDate
				+ ", coordinatorName=" + coordinatorName + ", coordinatorEmail=" + coordinatorEmail
				+ ", coordinatorMobile=" + coordinatorMobile + ", eventPlace=" + eventPlace + ", eventCity=" + eventCity
				+ ", eventState=" + eventState + ", eventCountry=" + eventCountry + ", organizationDepartment="
				+ organizationDepartment + ", organizationName=" + organizationName + ", organizationWebSite="
				+ organizationWebSite + ", organizationContactName=" + organizationContactName
				+ ", organizationContactEmail=" + organizationContactEmail + ", organizationContactMobile="
				+ organizationContactMobile + ", preceptorName=" + preceptorName + ", preceptorIdCardNumber="
				+ preceptorIdCardNumber + ", welcomeCardSignedByName=" + welcomeCardSignedByName
				+ ", welcomeCardSignerIdCardNumber=" + welcomeCardSignerIdCardNumber + ", remarks=" + remarks
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", createdBy=" + createdBy
				+ ", updatedBy=" + updatedBy + ", autoGeneratedEventId=" + autoGeneratedEventId
				+ ", autoGeneratedIntroId=" + autoGeneratedIntroId + ", programName=" + programName
				+ ", participantList=" + participantList + ", createdSource=" + createdSource + ", srcmGroup="
				+ srcmGroup + ", firstSittingBy=" + firstSittingBy + ", abyasiRefNo=" + abyasiRefNo
				+ ", coordinatorAbhyasiId=" + coordinatorAbhyasiId + ", coordinatorPermissionLetterPath="
				+ coordinatorPermissionLetterPath + ", programZone=" + programZone + ", programCenter=" + programCenter
				+ ", organizationBatchNo=" + organizationBatchNo + ", organizationCity=" + organizationCity
				+ ", organizationLocation=" + organizationLocation + ", organizationFullAddress="
				+ organizationFullAddress + ", organizationDecisionMakerName=" + organizationDecisionMakerName
				+ ", organizationDecisionMakerEmail=" + organizationDecisionMakerEmail
				+ ", organizationDecisionMakerPhoneNo=" + organizationDecisionMakerPhoneNo
				+ ", isEwelcomeIdGenerationDisabled=" + isEwelcomeIdGenerationDisabled + ", isReadOnly=" + isReadOnly
				+ ", jiraIssueNumber=" + jiraIssueNumber + ", sendersEmailAddress=" + sendersEmailAddress
				+ ", batchDescription=" + batchDescription + ", programAddress=" + programAddress + ", programDistrict="
				+ programDistrict + ", organizationContactDesignation=" + organizationContactDesignation
				+ ", programChannelType=" + programChannelType + ", userId=" + userId + ", uploadedFileId="
				+ uploadedFileId + ", uploaderMail=" + uploaderMail + "]";
	}

}
