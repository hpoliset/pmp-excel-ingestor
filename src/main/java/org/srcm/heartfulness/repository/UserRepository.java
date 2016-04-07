package org.srcm.heartfulness.repository;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.srcm.heartfulness.model.IntroductionDetails;
import org.srcm.heartfulness.model.User;

/**
 * 
 * @author HimaSree
 *
 */
public interface UserRepository {
    
	/**
	 * Retrieve a <code>User</code> from the data store by email.
	 * 
	 * @param email
	 * @return
	 * @throws DataAccessException
	 */
    User findByEmail(String email) throws DataAccessException;

    /**
     * This is used to save & update the user
     * 
     * Save an <code>User</code> to the data store, either inserting or updating it.
     * 
     * @param user
     */
    void save(User user);
    
    /**
     * method to get the user ID
     * @param user
     * @throws SQLException
     */
    void getUserID(User user) throws SQLException;
    
    /**
     * method to update the introduction details of the seeker
     * @param user
     * @return
     */
	IntroductionDetails updateIntroductionDetails(User user);
    
}
