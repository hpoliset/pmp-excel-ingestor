package org.srcm.heartfulness.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.model.Program;

import java.util.Collection;
import java.util.List;

/**
 * Created by vsonnathi on 11/23/15.
 */
public interface ParticipantFullDetailsRepository {
    /**
     * Retrieve <code>Participant</code>s from the data store by hashCode.
     *
     * @param hashCode Value to search for
     * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none
     * found)
     */
//    Collection<ParticipantFullDetails> findByHashCode(String hashCode) throws DataAccessException;

    /**
     * Retrieve a <code>Program</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Participant</code> if found
     * @throws org.springframework.dao.DataRetrievalFailureException if not found
     */
//    ParticipantFullDetails findById(int id) throws DataAccessException;

    Collection<ParticipantFullDetails> findByChannel(String programChannel);

    /**
     * Save an <code>Participant</code> to the data store, either inserting or updating it.
     */
    //void save(Participant participant);
}
