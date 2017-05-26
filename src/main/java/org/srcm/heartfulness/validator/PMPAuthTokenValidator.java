/**
 * 
 */
package org.srcm.heartfulness.validator;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.PMPResponse;

/**
 * @author Koustav Dutta
 *
 */
public interface PMPAuthTokenValidator {
	
	/**
	 * This method is used to validate the authentication token against
	 * MYSRCM.If the token is successfully authenticated then a success response
	 * is returned else an error response is returned.
	 * 
	 * @param authToken
	 *            authToken Token to authenticate with mysrcm.
	 * @param accessLog
	 *            to persist the api log details.
	 * @return PMPResponse success or failure response
	 */
	public PMPResponse validateAuthToken(String authToken, PMPAPIAccessLog accessLog);

}
