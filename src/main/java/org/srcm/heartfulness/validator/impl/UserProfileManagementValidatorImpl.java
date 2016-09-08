package org.srcm.heartfulness.validator.impl;

import java.util.HashMap;
import java.util.Map;

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
	 * Method to validate mandatory fields in the user request before
	 * creating user in MySRCM & PMP.
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, String> checkCreateUserManadatoryFields(CreateUserRequest user){
		Map<String, String> errors = new HashMap<>();
		if(null == user.getEmail() || user.getEmail().isEmpty()){
			errors.put("email", "emailID is required");
		}else if(!user.getEmail().matches(ExpressionConstants.EMAIL_REGEX)){
			errors.put("email", "Invalid emailID.");
		}
		if(null == user.getPassword() || user.getPassword().isEmpty()){
			errors.put("password", "password is required");
		}
		if(null == user.getFirstName() || user.getFirstName().isEmpty()){
			errors.put("first_name", "FirstName is required");
		}
		if(null != user.getZipcode() && !user.getZipcode().isEmpty() && !user.getZipcode().matches(ExpressionConstants.ZIPCODE_REGEX) ){
			errors.put("zipcode", "Invalid zipcode");
		}
		return errors;
	}
}
