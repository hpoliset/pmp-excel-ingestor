package org.srcm.heartfulness.validator.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.model.json.request.SubscriptionRequest;
import org.srcm.heartfulness.validator.SubscriptionValidator;

/**
 * Validator implementation to streamline all Event dashboard validation
 * implementation.
 * 
 * @author himasreev
 */
@Component
public class SubscriptionValidatorImpl implements SubscriptionValidator {

	/**
	 * Method to validate the mandatory fields in the request for subscription
	 * and unsubscription.
	 * 
	 * @param subscriptionRequest
	 * @return
	 */
	@Override
	public Map<String, String> checkMandatoryFieldsinSubscriptionRequest(SubscriptionRequest subscriptionRequest) {
		Map<String, String> errors = new HashMap<>();
		if (null == subscriptionRequest.getMailID() || subscriptionRequest.getMailID().isEmpty()) {
			errors.put("emailID", "Email ID cannot be empty.");
		} else if (!subscriptionRequest.getMailID().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("emailID", "Email ID is not valid.");
		}
		if (null == subscriptionRequest.getName() || subscriptionRequest.getName().isEmpty()) {
			errors.put("name", "First Name cannot be empty.");
		}
		return errors;
	}

}
