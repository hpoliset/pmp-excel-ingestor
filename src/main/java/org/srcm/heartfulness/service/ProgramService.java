package org.srcm.heartfulness.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;

public interface ProgramService {
	
	/**
	 * To create a program and persist
	 * 
	 * @param program - <code>Program</code>
	 * @return the <code>Program</code> 
	 */
	public Program createProgram(Program program);
	
	/**
	 * Check whether program exists or not
	 * 
	 * @param program - <code>Program</code>
	 * @return true or false
	 */
	public boolean isProgramExist(Program program);
	
	/**
	 * Retrieve <code>Program</code> from the data store by Auto generated event id.
	 * 
	 * @param autoGeneratedEventid - Auto generated Event ID to search for
	 * @return the <code>Program</code> if found
	 * @throws DataAccessException
	 */
	Program findByAutoGeneratedEventId(String autoGeneratedEventid) throws DataAccessException;
	
	public List<Program> getProgramByEmail(String email,boolean isAdmin);

	public Program getProgramById(int id);

	public List<Participant> getParticipantByProgramId(int decryptedProgramId);
	
	
	/**
	 * Get the list of events depending on the coordinator email
	 * @param email
	 * @param isAdmin
	 * @return List<Event>
	 */
	public List<Event> getEventListByEmail(String email,boolean isAdmin);

	
	/**
	 * Returns the list of Participant details for a given auto
	 * GeneratedEventId.
	 * @param eventId
	 * @return List<ParticipantRequest>
	 */
	public List<ParticipantRequest> getParticipantByEventId(String eventId);
	
	/**
	 * This service method is used to create a new record or update an existing record.
	 * 
	 * @param events List<Event> is sent to this service method as an argument against 
	 * which mandatory,duplicate eventId and other  validations are performed. 
	 * @return List<Event>
	 * @throws InvalidDateException if the program_start_date is in invalid format.
	 */
	public List<Event> createOrUpdateEvent(List<Event> events);
	
	public int getEventCountByEmail(String username, boolean isAdmin);

	public int getNonCategorizedEventsByEmail(String coOrdinator, boolean isAdmin);

	public Participant findParticipantBySeqId(String seqId, int programId);
	
	public int getProgramIdByEventId(String eventId);
	
	public void UpdateParticipantsStatus(String participantIds, String eventId , String introduced);
	
	public List<String> getAllEventCategories();
	
	public int getEventCountByCategory(String email, boolean isAdmin, String eventCategory);
	
	public int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories);
	
	public void updateEventAdmin(EventAdminChangeRequest eventAdminChangeRequest);
	
	public void updateCoOrdinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest);

}
