package org.srcm.heartfulness.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.srcm.heartfulness.model.Program;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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

}
