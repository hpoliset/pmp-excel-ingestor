package org.srcm.heartfulness.model.json.request;

import java.nio.charset.Charset;
import java.util.Map;

import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Koustav Dutta
 */
@JsonInclude(value = Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
@JsonPropertyOrder({ "autoGeneratedEventId","programName", "programChannel", "programStartDate", "programEndDate", "programZone",
	"programCenter", "coordinatorName", "coordinatorEmail", "coordinatorMobile", "coordinatorAbhyasiId",
	"coordinatorPermissionLetterPath", "eventPlace", "eventCity", "eventState", "eventCountry",
	"organizationDepartment", "organizationName", "organizationWebSite", "organizationBatchNo", "organizationCity",
	"organizationLocation", "organizationFullAddress", "organizationContactName", "organizationContactEmail",
	"organizationContactMobile", "organizationDecisionMakerName", "organizationDecisionMakerPhoneNo",
	"organizationDecisionMakerEmail", "preceptorName", "preceptorIdCardNumber", "welcomeCardSignedByName",
	"welcomeCardSignerIdCardNumber", "remarks", "isEwelcomeIdGenerationDisabled","status","isReadOnly", "jiraIssueNumber", 
	"secondaryCoordinatorNotes","batchDescription","programAddress", "programDistrict","organizationContactDesignation","programChannelType","errors" })
public class Event {

	@JsonProperty(value = "eventId")
	private String autoGeneratedEventId;

	@JsonIgnore
	private String autoGeneratedIntroId;

	@JsonIgnore
	private int programId;
	private String programName;

	@JsonIgnore
	private String programHashCode;

	private String programChannel;
	private String programStartDate;
	private String programEndDate;
	private String coordinatorName;
	private String coordinatorEmail;
	private String coordinatorMobile;
	private String eventPlace;
	private String eventCity;
	private String eventState;
	private String eventCountry;
	private String organizationDepartment;
	private String organizationName;
	private String organizationWebSite;
	private String organizationContactName;
	private String organizationContactEmail;
	private String organizationContactMobile;
	private String preceptorName;
	private String preceptorIdCardNumber;
	private String welcomeCardSignedByName;
	private String welcomeCardSignerIdCardNumber;
	private String remarks;
	private String status;
	private String abhyasiID;
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
	private Map<String, String> errors;
	private String isEwelcomeIdGenerationDisabled;
	private String isReadOnly;
	private String secondaryCoordinatorNotes;
	//@JsonInclude(value = Include.ALWAYS)
	private String jiraIssueNumber;
	private String batchDescription;
	private String programAddress;
	private String programDistrict;
	private String organizationContactDesignation;
	@JsonInclude(value = Include.ALWAYS)
	private int programChannelType;
	@JsonInclude(value = Include.ALWAYS)
	private String programStatus;
	@JsonIgnore
	private String createdSource;
	private String statusDescription;

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

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramHashCode() {
		return programHashCode;
	}

	public void setProgramHashCode(String programHashCode) {
		this.programHashCode = programHashCode;
	}

	public String getProgramChannel() {
		return programChannel;
	}

	public void setProgramChannel(String programChannel) {
		this.programChannel = programChannel;
		this.programHashCode = computeHashCode();
	}

	public String getProgramStartDate() {
		return programStartDate;
	}

	public void setProgramStartDate(String programStartDate) {
		this.programStartDate = programStartDate;
		this.programHashCode = computeHashCode();
	}

	public String getProgramEndDate() {
		return programEndDate;
	}

	public void setProgramEndDate(String programEndDate) {
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

	public void setOrganizationDepartment(String organizationDepartment) {
		this.organizationDepartment = organizationDepartment;
		this.programHashCode = computeHashCode();
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public String getAbhyasiID() {
		return abhyasiID;
	}

	public void setAbhyasiID(String abhyasiID) {
		this.abhyasiID = abhyasiID;
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
	
	public String getSecondaryCoordinatorNotes() {
		return secondaryCoordinatorNotes;
	}

	public void setSecondaryCoordinatorNotes(String secondaryCoordinatorNotes) {
		this.secondaryCoordinatorNotes = secondaryCoordinatorNotes;
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

	public String getProgramStatus() {
		return programStatus;
	}

	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}
	
	public String getCreatedSource() {
		return createdSource;
	}

	public void setCreatedSource(String createdSource) {
		this.createdSource = createdSource;
	}
	
	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	/**
	 * Generates a MD5 hash code based on program channel, Organisation Name,
	 * Organisation Dept, Event Date, Event Place
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
		return "Event [autoGeneratedEventId=" + autoGeneratedEventId + ", autoGeneratedIntroId=" + autoGeneratedIntroId
				+ ", programId=" + programId + ", programName=" + programName + ", programHashCode=" + programHashCode
				+ ", programChannel=" + programChannel + ", programStartDate=" + programStartDate + ", programEndDate="
				+ programEndDate + ", coordinatorName=" + coordinatorName + ", coordinatorEmail=" + coordinatorEmail
				+ ", coordinatorMobile=" + coordinatorMobile + ", eventPlace=" + eventPlace + ", eventCity=" + eventCity
				+ ", eventState=" + eventState + ", eventCountry=" + eventCountry + ", organizationDepartment="
				+ organizationDepartment + ", organizationName=" + organizationName + ", organizationWebSite="
				+ organizationWebSite + ", organizationContactName=" + organizationContactName
				+ ", organizationContactEmail=" + organizationContactEmail + ", organizationContactMobile="
				+ organizationContactMobile + ", preceptorName=" + preceptorName + ", preceptorIdCardNumber="
				+ preceptorIdCardNumber + ", welcomeCardSignedByName=" + welcomeCardSignedByName
				+ ", welcomeCardSignerIdCardNumber=" + welcomeCardSignerIdCardNumber + ", remarks=" + remarks
				+ ", status=" + status + ", abhyasiID=" + abhyasiID + ", coordinatorAbhyasiId=" + coordinatorAbhyasiId
				+ ", coordinatorPermissionLetterPath=" + coordinatorPermissionLetterPath + ", programZone="
				+ programZone + ", programCenter=" + programCenter + ", organizationBatchNo=" + organizationBatchNo
				+ ", organizationCity=" + organizationCity + ", organizationLocation=" + organizationLocation
				+ ", organizationFullAddress=" + organizationFullAddress + ", organizationDecisionMakerName="
				+ organizationDecisionMakerName + ", organizationDecisionMakerEmail=" + organizationDecisionMakerEmail
				+ ", organizationDecisionMakerPhoneNo=" + organizationDecisionMakerPhoneNo + ", errors=" + errors
				+ ", isEwelcomeIdGenerationDisabled=" + isEwelcomeIdGenerationDisabled + ", isReadOnly=" + isReadOnly
				+ ", secondaryCoordinatorNotes=" + secondaryCoordinatorNotes + ", jiraIssueNumber=" + jiraIssueNumber
				+ ", batchDescription=" + batchDescription + ", programAddress=" + programAddress + ", programDistrict="
				+ programDistrict + ", organizationContactDesignation=" + organizationContactDesignation
				+ ", programChannelType=" + programChannelType + "]";
	}

}
