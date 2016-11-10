package org.srcm.heartfulness.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.SearchRequest;

/**
 * Created by vsonnathi on 11/23/15.
 */
public interface ParticipantRepository {
	/**
	 * Retrieve <code>Participant</code>s from the data store by hashCode.
	 *
	 * @param hashCode
	 *            Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an
	 *         empty <code>Collection</code> if none found)
	 */
	Collection<Participant> findByHashCode(String hashCode) throws DataAccessException;

	/**
	 * Retrieve a <code>Program</code> from the data store by id.
	 *
	 * @param id
	 *            the id to search for
	 * @return the <code>Participant</code> if found
	 * @throws org.springframework.dao.DataRetrievalFailureException
	 *             if not found
	 */
	Participant findById(int id) throws DataAccessException;

	/**
	 * Retrieve the List of Participants
	 * 
	 * @param programId
	 * @return
	 */
	List<Participant> findByProgramId(int programId);

	/**
	 * Save an <code>Participant</code> to the data store, either inserting or
	 * updating it.
	 * 
	 * @param participant
	 *            - the <code>Participant</code>
	 */
	void save(Participant participant);

	/**
	 * Retrieve a <code>Participant</code> from the data store by id.
	 * 
	 * @param introId
	 *            - Introduction ID to search for
	 * @param mobileNumber
	 *            - Mobile number to search for
	 * @return the <code>Participant</code> if found
	 */
	Participant getParticipantByIntroIdAndMobileNo(String introId, String seqNum);

	/**
	 * Retrieve a <code>Program</code> from the data store by id.
	 * 
	 * @param id
	 *            - Program IDto search for
	 * @return the <code>Program</code> if found
	 */
	Program findOnlyProgramById(int id);

	/**
	 * Retrieve <code>List<Participant></code> from the data store by values
	 * given in the SearchRequest.
	 * 
	 * @param searchRequest
	 * @return <code>List<Participant></code>
	 */
	List<Participant> getParticipantList(SearchRequest searchRequest);

	/* List<Participant> getParticipantListToGenerateEWelcomeID(); */

	/**
	 * Method to get the list of participants whose ewelcomeId generation got
	 * failed.
	 * 
	 * @param programId
	 * @return
	 */
	List<Participant> getEWelcomeIdGenerationFailedParticipants(String programId);

	/**
	 * Method to get the list of participants who got ewelcomeID's and not
	 * informed to coordinator.
	 * 
	 * @param programId
	 * @return
	 */
	List<Participant> getEWelcomeIdGeneratedParticipants(String programId);

	/**
	 * Method to get the list of program ID's of the events in order to generate
	 * ewelcomeID's for the participant's.
	 * 
	 * @return
	 */
	List<Integer> getProgramIDsToGenerateEwelcomeIds();

	/**
	 * Method to get the list of participants to whom PMP needs to generate
	 * ewelcomeID's with programId.
	 * 
	 * @param programId
	 * @return
	 */
	List<Participant> getParticipantwithProgramIdTogenerateEwelcomeId(Integer programId);

	/**
	 * Method to update the participant ewelcomeID details
	 * (ewelcome_id_state,..)
	 * 
	 * @param participant
	 */
	void UpdateParticipantEwelcomeIDDetails(Participant participant);

}
