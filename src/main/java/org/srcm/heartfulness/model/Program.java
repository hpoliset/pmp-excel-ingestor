package org.srcm.heartfulness.model;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Domain class representing Program.
 *
 * @author Venkat Sonnathi
 */
public class Program {
    private int programId;
    private String programHashCode;
    private int programChannelId;
    private String programChannel;
    private Date programStartDate;
    private Date programEndDate;

    private String coordinatorId;
    private String coordinatorName;
    private String coordinatorEmail;
    private String coordinatorMobile;

    private String eventId;
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

    private Date createTime;
    private Date updateTime;
    private String createdBy;
    private String updatedBy;
    private List<Participant> participantList = Collections.emptyList();

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
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

    public String getCoordinatorId() {
		return coordinatorId;
	}

	public void setCoordinatorId(String coordinatorId) {
		this.coordinatorId = coordinatorId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
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
        return "Program{" +
                "programId=" + programId +
                ", programHashCode='" + programHashCode + '\'' +
                ", programChannelId=" + programChannelId +
                ", programChannel='" + programChannel + '\'' +
                ", programStartDate=" + programStartDate +
                ", programEndDate=" + programEndDate +
                ", coordinatorId='" + coordinatorId + '\'' +
                ", coordinatorName='" + coordinatorName + '\'' +
                ", coordinatorEmail='" + coordinatorEmail + '\'' +
                ", coordinatorMobile='" + coordinatorMobile + '\'' +
                ", eventId='" + eventId + '\'' +
                ", eventPlace='" + eventPlace + '\'' +
                ", eventCity='" + eventCity + '\'' +
                ", eventState='" + eventState + '\'' +
                ", eventCountry='" + eventCountry + '\'' +
                ", organizationDepartment='" + organizationDepartment + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", organizationWebSite='" + organizationWebSite + '\'' +
                ", organizationContactName='" + organizationContactName + '\'' +
                ", organizationContactEmail='" + organizationContactEmail + '\'' +
                ", organizationContactMobile='" + organizationContactMobile + '\'' +
                ", preceptorName='" + preceptorName + '\'' +
                ", preceptorIdCardNumber='" + preceptorIdCardNumber + '\'' +
                ", welcomeCardSignedByName='" + welcomeCardSignedByName + '\'' +
                ", welcomeCardSignerIdCardNumber='" + welcomeCardSignerIdCardNumber + '\'' +
                ", remarks='" + remarks + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }

    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }
}
