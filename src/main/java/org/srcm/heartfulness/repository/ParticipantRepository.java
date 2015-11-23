package org.srcm.heartfulness.repository;

import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;

import java.util.Collection;

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
     * Save an <code>Participant</code> to the data store, either inserting or updating it.
     */
    void save(Participant participant);
}
