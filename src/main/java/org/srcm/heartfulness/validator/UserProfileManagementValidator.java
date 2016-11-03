package org.srcm.heartfulness.validator;

import org.srcm.heartfulness.model.json.request.CreateUserRequest;

/**
 * Validator to streamline all User Profile Management validation
 * implementation.
 * 
 */
public interface UserProfileManagementValidator {

	/**
	 * Method to validate mandatory fields in the user request before
	 * creating user in MySRCM & PMP.
	 * 
	 * @param user
	 * @return
	 */
	public String checkCreateUserManadatoryFields(CreateUserRequest user);

}
