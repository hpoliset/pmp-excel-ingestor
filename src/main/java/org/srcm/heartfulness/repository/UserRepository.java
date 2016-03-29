package org.srcm.heartfulness.repository;

import org.springframework.dao.DataAccessException;
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
	 * Save an <code>User</code> to the data store, either inserting or updating
	 * it.
	 * 
	 * @param user
	 */
	void save(User user);

}
