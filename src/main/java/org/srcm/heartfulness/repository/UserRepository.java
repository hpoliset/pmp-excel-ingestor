package org.srcm.heartfulness.repository;

import java.util.List;

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
	
	/**
	 * This method is used to get email Ids for a given
	 * MYSRCM Abhyasi Id.
	 * @param abyasiId, to get the list of email Ids.
	 * @return List<String> email Id's which are associated
	 * for a given MYSRCM Abhyasi Id.
	 */
	List<String> getEmailsWithAbhyasiId(String abyasiId);
	
	public User getUserMailWithId(int userId);

}
