package org.srcm.heartfulness.validator.impl;

import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.model.json.request.CreateUserRequest;
import org.srcm.heartfulness.validator.UserProfileManagementValidator;

/**
 * Validator to streamline all User Profile Management validation
 * implementation.
 * 
 */
@Component
public class UserProfileManagementValidatorImpl implements UserProfileManagementValidator {

	/**
	 * Method to validate mandatory fields in the user request before creating
	 * user in MySRCM & PMP.
	 * 
	 * @param user
	 * @return
	 */
	public String checkCreateUserManadatoryFields(CreateUserRequest user) {
		StringBuilder description = new StringBuilder();
		if (null == user.getEmail() || user.getEmail().isEmpty()) {
			description.append(!(description.length() > 0) ? "emailID is required" : ", " + "emailID is required");
		} else if (!user.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			description.append(!(description.length() > 0) ? "Invalid emailID" : ", " + "Invalid emailID");
		}
		if (null == user.getPassword() || user.getPassword().isEmpty()) {
			description.append(!(description.length() > 0) ? "password is required" : ", " + "password is required");
		}
		if (null == user.getFirstName() || user.getFirstName().isEmpty()) {
			description.append(!(description.length() > 0) ? "FirstName is required" : ", " + "FirstName is required");
		}
		if (null == user.getLastName() || user.getLastName().isEmpty()) {
			description.append(!(description.length() > 0) ? "LastName is required" : ", " + "LastName is required");
		}
		if (null != user.getZipcode() && !user.getZipcode().isEmpty()
				&& !user.getZipcode().matches(ExpressionConstants.ZIPCODE_REGEX)) {
			description.append(!(description.length() > 0) ? "Invalid zipcode" : ", " + "Invalid zipcode");
		}
		if (description.length() > 0) {
			return description.toString();
		} else {
			return null;
		}
	}
}
