package org.srcm.heartfulness.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;

/**
 * Repository class for <code>Program</code> domain class.
 *
 * @author Venkat Sonnathi
 */
public interface ProgramRepository {

    /**
     * Retrieve <code>Program</code>s from the data store by hashCode.
     *
     * @param hashCode Value to search for
     * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none
     * found)
     */
    Collection<Program> findByHashCode(String hashCode) throws DataAccessException;

    /**
     * Retrieve a <code>Program</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Program</code> if found
     * @throws org.springframework.dao.DataRetrievalFailureException if not found
     */
    Program findById(int id) throws DataAccessException;

    /**
     * Retrieve <code>Program</code> objects that are modified after a given date.
     *
     * @param lastBatchRun the date to compare modified time for the program objects.
     * @return the list of program ids (not the full object)
     */
    List<Integer> findUpdatedProgramIdsSince(Date lastBatchRun);

    /**
     * Save an <code>Program</code> to the data store, either inserting or updating it.
     */
    void save(Program program);
    
    /**
     * To check whether the program exists or not
     * 
     * @param program - the <code>Program</code>
     * @return the boolean(true or false)
     */
    boolean isProgramExist(Program program);
    
    /**
     * Retrieve a <code>Program</code> from the data store by Auto generated Event ID
     * 
     * @param autoGeneratedEventId - Auto generated Event ID to search for
     * @return the <code>Program</code> if found
     */
	Program findByAutoGeneratedEventId(String autoGeneratedEventId);
	
	/**
     * Retrieve a <code>Program</code> from the data by Auto generated Event ID
     * 
     * @param autoGeneratedIntroId - Auto generated Introduction ID to search for
     * @return the <code>Program</code> if found
     */
	Program findByAutoGeneratedIntroId(String autoGeneratedIntroId);
	
	/**
     * Retrieve a <code>Program</code> from the data by Auto generated Event ID
     * 
     * @param eventName - Event Name to search for
     * @return the <code>Program</code> if found
     */
	Program findByEventName(String eventName);

	/**
	 * checking the existance of the program for the given name
	 * 
	 * @param program
	 * @return
	 */
	boolean isProgramExistByProgramName(Program program);

	/**
	 * persisting program with the given program name
	 * 
	 * @param program
	 */
	void saveWithProgramName(Program program);

	/**
	 * get the event details for the given id
	 * 
	 * @param id
	 * @return
	 */
	Program getEventById(int id);

	/**
	 * gets the list of participants for the given program ID
	 * 
	 * @param decryptedProgramId
	 * @return List<Participant>
	 */
	List<Participant> getParticipantList(int decryptedProgramId);

	/**
	 * Get the list of programs depending on the coordinator email and whether
	 * he is admin or not.
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	List<Program> getEventsByEmail(String email, boolean isAdmin,int offset,int pageSize);

	/**
	 * Repository method to create a new record or update an existing event
	 * record.
	 * 
	 * @param event
	 *            to persist into the database.
	 * @return Program
	 */
	Program saveProgram(Program program);

	/**
	 * gets the program ID for the given event ID
	 * 
	 * @param eventID
	 * @return
	 */
	int getProgramIdByEventId(String eventID);

	/**
	 * gets the count of the events available for the given mail and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	int getEventCountByEmail(String email, boolean isAdmin);

	/**
	 * gets the count of un-categorized events for the given email and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	int getNonCategorizedEventsByEmail(String email, boolean isAdmin);

	/**
	 * gets the participant for the given seqId and program ID
	 * 
	 * @param seqId
	 * @param programId
	 * @return
	 */
	Participant findParticipantBySeqId(String seqId, int programId);

	/**
	 * updates the participant introduced status for the given participant Ids
	 * of the goven eventId
	 * 
	 * @param participantIds
	 * @param eventId
	 * @param introduced
	 */
	void UpdateParticipantsStatus(String participantIds, String eventId, String introduced,String userEmailID);

	/**
	 * gets the all the available event categories from the database
	 * 
	 * @return List<String>
	 */
	List<String> getAllEventCategories();

	/**
	 * gets the count of events for the given email and based on user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventCategory
	 * @return
	 */
	int getEventCountByCategory(String email, boolean isAdmin, String eventCategory);

	/**
	 * gets the count of miscellaneous events for the given email and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventcategories
	 * @return
	 */
	int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories);

	/**
	 * update the co-ordinator details in the datbase after changing the admin
	 * for the event
	 * 
	 * @param eventAdminChangeRequest
	 */
	void updateCoOrdinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest);

	/**
	 * gets the count of un-catgorized events for the given email and based on
	 * user role
	 * 
	 * @param username
	 * @param isAdmin
	 * @return List<String>
	 */
	List<String> getNonCategorizedEventListByEmail(String username, boolean isAdmin);

	/**
	 * gets the all available co-ordinators from the database
	 * 
	 * @return List<Coordinator>
	 */
	List<Coordinator> getAllCoOrdinatorsList();

	/**
	 * deletes participant for the given seq Id of the given Event Id
	 * 
	 * @param seqId
	 * @param eventId
	 * @return Participant
	 */
	Participant deleteParticipant(String seqId, String eventId);

	/**
	 * updates the deleted participant details in the database
	 * 
	 * @param deletedParticipant
	 * @param deletedBy
	 */
	void updateDeletedParticipant(Participant deletedParticipant, String deletedBy);

	List<Program> searchEvents(SearchRequest searchRequest,String userEmail,boolean isAdmin,int offset);

	String getEventIdByProgramID(int programId);
	
	int getProgramCount(String userEmail,boolean isAdmin);
	
	int getPgrmCountBySrchParams(SearchRequest searchRequest, String userEmail, boolean isAdmin);
	
	void updatePreceptorDetails(Program program);

}
