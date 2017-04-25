package org.srcm.heartfulness.repository;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramPermissionLetterdetails;
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
	 * @param hashCode
	 *            Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an
	 *         empty <code>Collection</code> if none found)
	 */
	Collection<Program> findByHashCode(String hashCode) throws DataAccessException;

	/**
	 * Retrieve a <code>Program</code> from the data store by id.
	 *
	 * @param id
	 *            the id to search for
	 * @return the <code>Program</code> if found
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             if not found
	 */
	Program findById(int id) throws DataAccessException;

	/**
	 * Retrieve <code>Program</code> objects that are modified after a given
	 * date.
	 *
	 * @param lastBatchRun
	 *            the date to compare modified time for the program objects.
	 * @return the list of program ids (not the full object)
	 */
	List<Integer> findUpdatedProgramIdsSince(Date lastBatchRun);

	/**
	 * Save an <code>Program</code> to the data store, either inserting or
	 * updating it.
	 */
	void save(Program program);

	/**
	 * To check whether the program exists or not
	 * 
	 * @param program
	 *            - the <code>Program</code>
	 * @return the boolean(true or false)
	 */
	boolean isProgramExist(Program program);

	/**
	 * Retrieve a <code>Program</code> from the data store by Auto generated
	 * Event ID
	 * 
	 * @param autoGeneratedEventId
	 *            - Auto generated Event ID to search for
	 * @return the <code>Program</code> if found
	 */
	Program findByAutoGeneratedEventId(String autoGeneratedEventId);

	/**
	 * Retrieve a <code>Program</code> from the data by Auto generated Event ID
	 * 
	 * @param autoGeneratedIntroId
	 *            - Auto generated Introduction ID to search for
	 * @return the <code>Program</code> if found
	 */
	Program findByAutoGeneratedIntroId(String autoGeneratedIntroId);

	/**
	 * Retrieve a <code>Program</code> from the data by Auto generated Event ID
	 * 
	 * @param eventName
	 *            - Event Name to search for
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
	 * Fetch the event details for the given id
	 * 
	 * @param id
	 * @return
	 */
	Program getEventById(int id);

	/**
	 * Fetch the list of participants for the given program ID
	 * 
	 * @param decryptedProgramId
	 * @return List<Participant>
	 */
	List<Participant> getParticipantList(int decryptedProgramId,List<String> mail,String role);

	/**
	 * Get the list of programs depending on the coordinator email and whether
	 * he is admin or not.
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	List<Program> getEventsByEmail(String email, boolean isAdmin, int offset, int pageSize);

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
	 * Fetch the program ID for the given event ID
	 * 
	 * @param eventID
	 * @return
	 */
	int getProgramIdByEventId(String eventID);

	/**
	 * Fetch the count of the events available for the given mail and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	int getEventCountByEmail(String email, boolean isAdmin);

	/**
	 * Fetch the count of un-categorized events for the given email and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	int getNonCategorizedEventsByEmail(String email, boolean isAdmin);

	/**
	 * Fetch the participant for the given seqId and program ID
	 * 
	 * @param seqId
	 * @param programId
	 * @return
	 */
	Participant findParticipantBySeqId(String seqId, int programId);
	
	/**
	 * Fetch the participant for the given seqId and program ID
	 * 
	 * @param seqId
	 * @param programId
	 * @param mail
	 * @return
	 */
	Participant findParticipantBySeqIdAndRole(String seqId, int programId,List<String> mail,String role);

	/**
	 * Updates the participant introduced status for the given participant Ids
	 * of the given eventId
	 * 
	 * @param participantIds
	 * @param eventId
	 * @param introduced
	 */
	void updateParticipantsStatus(String participantIds, String eventId, String introduced, String userEmailID);

	/**
	 * Fetch the all the available event categories from the database
	 * 
	 * @return List<String>
	 */
	List<String> getAllEventCategories();

	/**
	 * Fetch the count of events for the given email and based on user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventCategory
	 * @return
	 */
	int getEventCountByCategory(String email, boolean isAdmin, String eventCategory);

	/**
	 * Fetch the count of miscellaneous events for the given email and based on
	 * user role
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventcategories
	 * @return
	 */
	int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories);

	/**
	 * Update the coordinator details in the database after changing the admin
	 * for the event
	 * 
	 * @param eventAdminChangeRequest
	 */
	void updateCoordinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest);

	/**
	 * Fetch the count of un-catgorized events for the given email and based on
	 * user role
	 * 
	 * @param username
	 * @param isAdmin
	 * @return List<String>
	 */
	List<String> getNonCategorizedEventListByEmail(String username, boolean isAdmin);

	/**
	 * Fetch all the available co-ordinators from the database
	 * 
	 * @return List<Coordinator>
	 */
	List<Coordinator> getAllCoOrdinatorsList();

	/**
	 * Deletes participant for the given seq Id of the given Event Id
	 * 
	 * @param seqId
	 * @param eventId
	 * @return Participant
	 */
	Participant deleteParticipant(String seqId, String eventId);

	/**
	 * Updates the deleted participant details in the database
	 * 
	 * @param deletedParticipant
	 * @param deletedBy
	 */
	void updateDeletedParticipant(Participant deletedParticipant, String deletedBy);

	/**
	 * Method to get event ID by using program Id.
	 * 
	 * @param programId
	 * @return
	 */
	String getEventIdByProgramID(int programId);

	/**
	 * Method to get the count of programs for the given user mail ID.
	 * 
	 * @param userEmail
	 * @param isAdmin
	 * @return
	 */
	int getProgramCount(String userEmail, boolean isAdmin);

	/**
	 * Method to get the required program details inorder to generate ewelcome
	 * Id's for participants.
	 * 
	 * @param programId
	 * @return
	 */
	Program getProgramDetailsToGenerateEwelcomeIDById(Integer programId);

	/**
	 * Method to update the preceptor details after validating the preceptor ID
	 * against MYSRCM.
	 * 
	 * @param program
	 */
	void updatePreceptorDetails(Program program);

	/**
	 * Method to find whether there is any events conducted with the logged in
	 * user.
	 * 
	 * @param email
	 * @return true,if event exists for the given email Id
	 * @return false,if event doesn't exists with the given email Id.
	 */
	boolean isEventCoordinatorExistsWithUserEmailId(String email);

	/**
	 * Method to get the count of programs for the given user mail ID based on
	 * role.
	 * 
	 * @param email
	 * @param role
	 * @return
	 */
	int getProgramCountWithUserRoleAndEmailId(/*String email*/List<String> emailList, String role);

	/**
	 * Get the list of programs depending on the coordinator email and role.
	 * 
	 * @param email
	 * @param role
	 * @param offset
	 * @param pageSize
	 * @return
	 */
	List<Program> getEventsByEmailAndRole(/*String email*/List<String> emailList, String role, int offset, int pageSize);

	/**
	 * Method to get the program count w.r.t the search params provided and with
	 * user mail ID and role.
	 * 
	 * @param searchRequest
	 * @param email
	 * @param role
	 * @return
	 */
	int getPgrmCountBySrchParamsWithUserRoleAndEmailId(SearchRequest searchRequest, List<String> emailList/*String email*/, String role);

	/**
	 * Method to search the events from the HFN Backend using few params.
	 * 
	 * @param searchRequest
	 * @param email
	 * @param role
	 * @param offset
	 * @return
	 */
	List<Program> searchEventsWithUserRoleAndEmailId(SearchRequest searchRequest, List<String> emailList/*String email*/, String role, int offset);

	/**
	 * Method to check whether the auto generated event id already exists or
	 * not. If exists, generates new one.
	 * 
	 * @param generatedEventId
	 * @return
	 */
	String checkExistanceOfAutoGeneratedEventId(String generatedEventId);

	/**
	 * Method to check whether the auto generated intro id already exists or
	 * not. If exists, generates new one.
	 * 
	 * @param generatedIntroId
	 * @return
	 */
	String checkExistanceOfAutoGeneratedIntroId(String generatedIntroId);

	/**
	 * @param autoGeneratedEventId
	 *            , Id for a particular HFN event
	 * @return List<String> containing event id,is welcome id disabled and max
	 *         count of excel sheet sequence number.
	 * 
	 */
	List<String> getProgramAndParticipantDetails(String autoGeneratedEventId);

	/**
	 * Method to save the coordinator permission letter name and path details in
	 * the PMP with reference to the program.
	 * 
	 * @param programPermissionLetterdetails
	 */
	void saveProgramPermissionLetterDetails(ProgramPermissionLetterdetails programPermissionLetterdetails);

	/**
	 * Method to get the list of coordinator permission letters available for
	 * the program with the given program Id.
	 * 
	 * @param programId
	 * @return <code>List<ProgramPermissionLetterdetails></code>
	 */
	List<ProgramPermissionLetterdetails> getListOfPermissionLetters(int programId);
	
	/**
	 * This method is used to get the program details 
	 * using email,role of logged in user and auto generated event Id.
	 * @param emailList emails associated with Abhyasi Id for the
	 * logged in person.
	 * @param userRole, role of the logged in user.
	 * @param agEventId, auto generated event Id for a particular event.
	 * @return program details based on emails,role of log in user and 
	 * auto generated event Id.
	 */
	Program getProgramByEmailAndRole(List<String> emailList,String userRole,String agEventId);

	/**
	 * This method is used to get all the program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 * @param emailList List of emails associated witha abhyasi id for the 
	 * logged in user.
	 * @param userRole, role of the logged in user.
	 * @return LinkedHashMap<Integer,String> containing program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 */
	LinkedHashMap<Integer,String> getListOfProgramIdsByEmail(List<String> emailList,String userRole);

}
