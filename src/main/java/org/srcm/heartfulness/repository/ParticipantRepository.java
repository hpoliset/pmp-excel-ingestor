package org.srcm.heartfulness.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

/**
 * Created by vsonnathi on 11/23/15.
 */
public interface ParticipantRepository {
    /**
     * Retrieve <code>Participant</code>s from the data store by hashCode.
     *
     * @param hashCode Value to search for
     * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none
     * found)
     */
    Collection<Participant> findByHashCode(String hashCode) throws DataAccessException;

    /**
     * Retrieve a <code>Program</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Participant</code> if found
     * @throws org.springframework.dao.DataRetrievalFailureException if not found
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
     * Save an <code>Participant</code> to the data store, either inserting or updating it.
     * 
     * @param participant - the <code>Participant</code>
     */
    void save(Participant participant);
    
    /**
     * Retrieve a <code>Participant</code> from the data store by id.
     * 
     * @param introId - Introduction ID to search for
     * @param mobileNumber - Mobile number to search for
     * @return the <code>Participant</code> if found
     */
    Participant getParticipantByIntroIdAndMobileNo(String introId,String mobileNumber);

    /**
     * Retrieve a <code>Program</code> from the data store by id.
     * 
     * @param id - Program IDto search for
     * @return the <code>Program</code> if found
     */
	Program findOnlyProgramById(int id);

}
