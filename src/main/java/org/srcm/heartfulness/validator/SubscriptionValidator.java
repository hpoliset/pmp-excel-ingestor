package org.srcm.heartfulness.validator;

import java.util.Map;

import org.srcm.heartfulness.model.json.request.SubscriptionRequest;

/**
 * Validator to streamline all Event dashboard validation implementation.
 * 
 * @author himasreev
 */
public interface SubscriptionValidator {

	/**
	 * Method to validate the mandatory fields in the request for subscription
	 * and unsubscription.
	 * 
	 * @param subscriptionRequest
	 * @return
	 */
	Map<String, String> checkMandatoryFieldsinSubscriptionRequest(SubscriptionRequest subscriptionRequest);

}
