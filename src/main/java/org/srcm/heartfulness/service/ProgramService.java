package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface ProgramService {

	/**
	 * To create a program and persist
	 * 
	 * @param program
	 *            - <code>Program</code>
	 * @return the <code>Program</code>
	 */
	public Program createProgram(Program program);

	/**
	 * Check whether program exists or not
	 * 
	 * @param program
	 *            - <code>Program</code>
	 * @return true or false
	 */
	public boolean isProgramExist(Program program);

	/**
	 * Retrieve <code>Program</code> from the data store by Auto generated event
	 * id.
	 * 
	 * @param autoGeneratedEventid
	 *            - Auto generated Event ID to search for
	 * @return the <code>Program</code> if found
	 * @throws DataAccessException
	 */
	Program findByAutoGeneratedEventId(String autoGeneratedEventid) throws DataAccessException;

	/**
	 * Retrieve <code>Program</code> from the data store by Id.
	 * 
	 * @param id
	 * @return
	 */
	public Program getProgramById(int id);

	/**
	 * Retrieve <code>List<Program></code> from the data store by email.
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	public List<Program> getProgramByEmail(String email, boolean isAdmin);

	/**
	 * Retrieve <code>List<Participant></code> from the data store by ProgramId.
	 * 
	 * @param decryptedProgramId
	 * @return
	 */
	List<Participant> getParticipantByProgramId(int decryptedProgramId);

	/**
	 * Get the list of events depending on the coordinator email
	 * 
	 * @param email
	 * @param isAdmin
	 * @return List<Event>
	 */
	public List<Event> getEventListByEmail(String email, boolean isAdmin);

	/**
	 * Returns the list of Participant details for a given auto
	 * GeneratedEventId.
	 * 
	 * @param eventId
	 * @return List<ParticipantRequest>
	 */
	public List<ParticipantRequest> getParticipantByEventId(String eventId);

	/**
	 * This service method is used to create a new record or update an existing
	 * record.
	 * 
	 * @param events
	 *            List<Event> is sent to this service method as an argument
	 *            against which mandatory,duplicate eventId and other
	 *            validations are performed.
	 * @return List<Event>
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws HttpClientErrorException
	 * @throws InvalidDateException
	 *             if the program_start_date is in invalid format.
	 */
	public List<Event> createOrUpdateEvent(List<Event> events, int id) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException, ParseException;

	/**
	 * Service to get the total number of available events count based on the
	 * user email and the user role
	 * 
	 * @param username
	 * @param isAdmin
	 * @return count(int)
	 */
	public int getEventCountByEmail(String username, boolean isAdmin);

	/**
	 * Service to get the non categorized events count based on the user email
	 * and the user role
	 * 
	 * @param coOrdinator
	 * @param isAdmin
	 * @return count(int)
	 */
	public int getNonCategorizedEventsByEmail(String coOrdinator, boolean isAdmin);

	/**
	 * Service to get the participant for the given programID and seq ID
	 * 
	 * @param seqId
	 * @param programId
	 * @return Participant
	 */
	public Participant findParticipantBySeqId(String seqId, int programId);

	/**
	 * Service to the programId for the given auto generated eventID
	 * 
	 * @param eventId
	 * @return programId
	 */
	public int getProgramIdByEventId(String eventId);

	/**
	 * Service to update the participant introduced status for the given
	 * participant Ids of an given eventID
	 * 
	 * @param participantIds
	 * @param eventId
	 * @param introduced
	 */
	public void UpdateParticipantsStatus(String participantIds, String eventId, String introduced, String userEmailID);

	/**
	 * Service to get the all available event categories from the database
	 * 
	 * @return
	 */
	public List<String> getAllEventCategories();

	/**
	 * Service to get the event count based on the user email and the user role
	 * and event category
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventCategory
	 * @return count of events
	 */
	public int getEventCountByCategory(String email, boolean isAdmin, String eventCategory);

	/**
	 * Service to get the miscillaneous event count based on the user email and
	 * the user role and event eventcategories
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventcategories
	 * @return
	 */
	public int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories);

	/**
	 * Service to update the admin for the event
	 * 
	 * @param eventAdminChangeRequest
	 */
	public void updateEventAdmin(EventAdminChangeRequest eventAdminChangeRequest);

	/**
	 * Service to update the co-ordinator details in the database after changing
	 * admin for the event
	 * 
	 * @param eventAdminChangeRequest
	 */
	public void updateCoOrdinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest);

	/**
	 * Service to get all the available co-ordinators list from the database.
	 * 
	 * @return List<Coordinator>
	 */
	public List<Coordinator> getAllCoOrdinatorsList();

	/**
	 * Service to get the uncategorized event list for the given email and user
	 * role.
	 * 
	 * @param email
	 * @param isAdmin
	 * @return List<String>
	 */
	public List<String> getUncategorizedEvents(String email, boolean isAdmin);

	/**
	 * Service to delete the participant for the given eventId and seqId.
	 * 
	 * @param seqId
	 * @param eventId
	 * @return
	 */
	public Participant deleteParticipant(String seqId, String eventId);

	/**
	 * Service to update the deleted participant details to the database.
	 * 
	 * @param deletedParticipant
	 * @param deletedBy
	 */
	public void updateDeletedParticipant(Participant deletedParticipant, String deletedBy);

	/**
	 * Service to get the event details for the given eventID.
	 * 
	 * @param EventId
	 * @return Event
	 */
	public Event getEventDetails(String EventId);

	/**
	 * Retrieve <code>List<Event></code> from the data store by values given in
	 * the search request.
	 * 
	 * @param searchRequest
	 * @return
	 */
	public List<Event> searchEvents(SearchRequest searchRequest);

	/**
	 * Retrieve <code>Auto generated eventId</code> from the data store by
	 * programId.
	 * 
	 * @param programId
	 * @return
	 */
	public String getEventIdByProgramID(int programId);

	/**
	 * Retrieve <code>e-Welcome ID</code> generated in MySRCM and persist in
	 * data store for the given eventID and seqID.
	 * 
	 * @param id
	 * @param seqID
	 * @param eventId
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public String generateeWelcomeID(Participant participant, int id) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException, ParseException;

	public String validatePreceptorIDCardNumber(Program program, int id);

}
