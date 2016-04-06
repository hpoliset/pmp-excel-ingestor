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
@JsonIgnoreProperties(ignoreUnknown = true,allowGetters = false)
@JsonPropertyOrder({"autoGeneratedEventId","programChannel","programStartDate","programEndDate","coordinatorName","coordinatorEmail",
	"coordinatorMobile","eventPlace","eventCity","eventState","eventCountry","organizationDepartment","organizationName",
	"organizationWebSite","organizationContactName","organizationContactEmail","organizationContactMobile","preceptorName",
	"preceptorIdCardNumber","welcomeCardSignedByName","welcomeCardSignerIdCardNumber","remarks","status","errors"})
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
	private Map<String,String> errors;
	
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
        if (organizationDepartment != null) {
            elementsOfMessage.append(organizationDepartment);
        }
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
				+ ", coordinatorMobile=" + coordinatorMobile + ", eventPlace=" + eventPlace + ", eventCity="
				+ eventCity + ", eventState=" + eventState + ", eventCountry=" + eventCountry
				+ ", organizationDepartment=" + organizationDepartment + ", organizationName=" + organizationName
				+ ", organizationWebSite=" + organizationWebSite + ", organizationContactName="
				+ organizationContactName + ", organizationContactEmail=" + organizationContactEmail
				+ ", organizationContactMobile=" + organizationContactMobile + ", preceptorName=" + preceptorName
				+ ", preceptorIdCardNumber=" + preceptorIdCardNumber + ", welcomeCardSignedByName="
				+ welcomeCardSignedByName + ", welcomeCardSignerIdCardNumber=" + welcomeCardSignerIdCardNumber
				+ ", remarks=" + remarks + ", status=" + status + ", errors=" + errors + "]";
	}
    
}