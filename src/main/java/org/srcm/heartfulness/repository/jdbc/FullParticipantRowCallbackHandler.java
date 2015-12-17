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
       String programChannel = rs.getString("pg.program_channel");
   	   Date programStartDate = rs.getDate("pg.program_start_date");
       String eventState = rs.getString("pg.event_state");
       String eventCity = rs.getString("pg.event_city");
       String organizationName  = rs.getString("pg.organization_name");
       int id  = rs.getInt("pr.id");
       String firstName = rs.getString("pr.first_name");
       String lastName = rs.getString("pr.last_name");
       String email  = rs.getString("pr.email");
       
       participant.setId(id);
       participant.setFirstName(firstName);
       participant.setLastName(lastName);
       participant.setEmail(email);
       participant.setOrganizationName(organizationName);
       participant.setEventCity(eventCity);
       participant.setEventState(eventState);
       participant.setProgramStartDate(programStartDate);
       participant.setProgramChannel(programChannel);

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

