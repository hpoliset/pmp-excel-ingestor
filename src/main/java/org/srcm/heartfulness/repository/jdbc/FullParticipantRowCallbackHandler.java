package org.srcm.heartfulness.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.srcm.heartfulness.model.ParticipantFullDetails;

class FullParticipantRowCallbackHandler implements RowCallbackHandler {
	
	static Logger LOGGER = LoggerFactory.getLogger(FullParticipantRowCallbackHandler.class);
	
	private Collection<ParticipantFullDetails>  participantDetails = new ArrayList<ParticipantFullDetails>();
	
    public void processRow(ResultSet rs) {

       ParticipantFullDetails participant = new ParticipantFullDetails();

       try {	

       int programId = rs.getInt("pg.program_id");
       String programChannel = rs.getString("pg.program_channel");
   	   Date programStartDate = rs.getDate("pg.program_start_date");   
       Date programEndDate = rs.getDate("pg.program_end_date");

       String eventPlace = rs.getString("pg.event_place");
       String eventCity = rs.getString("pg.event_city");
       String eventState = rs.getString("pg.event_state");
       String eventCountry = rs.getString("pg.event_country");

       int coordinatorId = rs.getInt("pg.coordinator_id");
       String coordinatorName = rs.getString("pg.coordinator_name");
       String coordinatorEmail = rs.getString("pg.coordinator_email");
       String coordinatorMobile = rs.getString("pg.coordinator_mobile");

       int organizationId = rs.getInt("pg.organization_id");
       String organizationName = rs.getString("pg.organization_name");
       String organizationDepartment = rs.getString("pg.organization_department");
       String organizationWebSite = rs.getString("pg.organization_web_site");
       String organizationContactName = rs.getString("pg.organization_contact_name");
       String organizationContactEmail = rs.getString("pg.organization_contact_email");
       String organizationContactMobile = rs.getString("pg.organization_contact_mobile");

       String preceptorName = rs.getString("pg.preceptor_name");
       String preceptorIdCardNumber = rs.getString("pg.preceptor_id_card_number");

       String welcomeCardSignedByName = rs.getString("pg.welcome_card_signed_by_name");
       String welcomeCardSignerIdCardNumber = rs.getString("pg.welcome_card_signer_id_card_number");

       String pgmRemarks = rs.getString("pg.remarks");

       Date pgmCreateTime  = rs.getDate("pg.create_time");
       Date pgmUpdateTime = rs.getDate("pg.update_time");
       String pgmCreatedBy = rs.getString("pg.created_by");
       String pgmUpdatedBy = rs.getString("pg.updated_by");

       int id = rs.getInt("pr.id");
       String printName = rs.getString("pr.print_name");
       String firstName = rs.getString("pr.first_name");
       String lastName = rs.getString("pr.last_name");
       String middleName = rs.getString("pr.middle_name");
       String email  = rs.getString("pr.email");
       String mobilePhone = rs.getString("pr.mobile_phone");
       String gender = rs.getString("pr.gender");
       
       Date dateOfBirth = rs.getDate("pr.date_of_birth");
       Date dateOfRegistration = rs.getDate("pr.date_of_registration");
       
       String abhyasiId = rs.getString("pr.abhyasi_id");
       int status = rs.getInt("pr.status");
       String addressLine1 = rs.getString("pr.address_line1");
       String addressLine2 = rs.getString("pr.address_line2");
       String city = rs.getString("pr.city");
       String state = rs.getString("pr.state");
       String country = rs.getString("pr.country");
       String remarks = rs.getString("pr.remarks");
       String idCardNumber = rs.getString("pr.id_card_number");
       String syncStatus = rs.getString("pr.sync_status");
       Date aimsSyncTime = rs.getDate("pr.aims_sync_time");
       
       int introduced = rs.getInt("pr.introduced");
       Date introductionDate = rs.getDate("pr.introduction_date");
       String introducedBy = rs.getString("pr.introduced_by");
       String welcomeCardNumber = rs.getString("pr.welcome_card_number");
       Date welcomeCardDate = rs.getDate("pr.welcome_card_date");
       String ageGroup = rs.getString("pr.age_group");
       String uploadStatus = rs.getString("pr.upload_status");
       int firstSittingTaken = rs.getInt("pr.first_sitting");
       int secondSittingTaken = rs.getInt("pr.second_sitting");
       int thirdSittingTaken = rs.getInt("pr.third_sitting");
       Date firstSittingDate = rs.getDate("pr.date_of_birth");
       Date secondSittingDate = rs.getDate("pr.date_of_birth");
       Date thirdSittingDate = rs.getDate("pr.date_of_birth");
       
       String batch = rs.getString("pr.batch");
       int receiveUpdates = rs.getInt("pr.receive_updates");
       String profession = rs.getString("pr.profession");
       //String department = rs.getString("pr.gender");
       String language = rs.getString("pr.language");
       
       participant.setProgramId(programId);
       //participant.setProgramChannelId(programChannelId);
       participant.setProgramChannel(programChannel);
       participant.setProgramStartDate(programStartDate);
       participant.setProgramEndDate(programEndDate);
       
       participant.setEventPlace(eventPlace);
       participant.setEventCity(eventCity);
       participant.setEventState(eventState);
       participant.setEventCountry(eventCountry);
       
       participant.setCoordinatorName(coordinatorName);
       participant.setCoordinatorId(coordinatorId);
       participant.setCoordinatorEmail(coordinatorEmail);
       participant.setCoordinatorMobile(coordinatorMobile);
       
       participant.setOrganizationId(organizationId);
       participant.setOrganizationName(organizationName);
       participant.setOrganizationWebSite(organizationWebSite);
       participant.setOrganizationDepartment(organizationDepartment);
       participant.setOrganizationContactName(organizationContactName);
       participant.setOrganizationContactEmail(organizationContactEmail);
       participant.setOrganizationContactMobile(organizationContactMobile);

       participant.setPreceptorName(preceptorName);
       participant.setPreceptorIdCardNumber(preceptorIdCardNumber);
       participant.setWelcomeCardSignedByName(welcomeCardSignedByName);
       participant.setWelcomeCardSignerIdCardNumber(welcomeCardSignerIdCardNumber);
       
       participant.setPgmRemarks(pgmRemarks);
       participant.setPgmCreateTime(pgmCreateTime);
       participant.setPgmUpdateTime(pgmUpdateTime);
       participant.setPgmCreatedBy(pgmCreatedBy);
       participant.setPgmUpdatedBy(pgmUpdatedBy);
       
       participant.setId(id);
       participant.setPrintName(printName);
       participant.setFirstName(firstName);
       participant.setMiddleName(middleName);
       participant.setLastName(lastName);
       participant.setEmail(email);
       participant.setMobilePhone(mobilePhone);
       participant.setGender(gender);
       participant.setDateOfBirth(dateOfBirth);
       participant.setDateOfRegistration(dateOfRegistration);
       participant.setLanguage(language);
       participant.setProfession(profession);
       
       participant.setAbhyasiId(abhyasiId);
       participant.setIdCardNumber(idCardNumber);
       participant.setStatus(status);
       participant.setAddressLine1(addressLine1);
       participant.setAddressLine2(addressLine2);
       participant.setCity(city);
       participant.setState(state);
       participant.setCountry(country);
       participant.setRemarks(remarks);
       participant.setBatch(batch);
       participant.setReceiveUpdates(receiveUpdates);
       participant.setSyncStatus(syncStatus);
       participant.setAimsSyncTime(aimsSyncTime);

       participant.setIntroduced(introduced);
       participant.setIntroducedBy(introducedBy);
       participant.setIntroductionDate(introductionDate);
       participant.setWelcomeCardNumber(welcomeCardNumber);
       participant.setWelcomeCardDate(welcomeCardDate);
       participant.setAgeGroup(ageGroup);
       participant.setUploadStatus(uploadStatus);
       
       participant.setFirstSittingTaken(firstSittingTaken);
       participant.setFirstSittingDate(firstSittingDate);
       participant.setSecondSittingTaken(secondSittingTaken);
       participant.setSecondSittingDate(secondSittingDate);
       participant.setThirdSittingTaken(thirdSittingTaken);
       participant.setThirdSittingDate(thirdSittingDate);       

       participantDetails.add(participant);
      }
      catch (SQLException se) {
    	  System.out.println("ERROR WHILE READING PARTICIPANT DETAILS ROW in RowCallBackHandler: " + se.getMessage());
    	  LOGGER.error("ERROR WHILE READING PARTICIPANT DETAILS ROW in RowCallBackHandler: ", se);
      }
    }

    public Collection<ParticipantFullDetails> getParticipantDetails() {
       return participantDetails;
    }

 }

