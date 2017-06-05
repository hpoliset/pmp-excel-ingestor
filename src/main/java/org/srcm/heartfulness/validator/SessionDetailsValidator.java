package org.srcm.heartfulness.validator;

import java.util.List;

import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.SessionDetails;
import org.srcm.heartfulness.model.json.request.SearchSession;
import org.srcm.heartfulness.model.json.response.PMPResponse;

public interface SessionDetailsValidator {

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
	//PMPResponse validateAuthToken(String authToken, PMPAPIAccessLog accessLog);

	/**
	 * Method is used to validate the mandatory session details parameters. If
	 * all the parameters are correct a success response is returned else an
	 * error response is returned.
	 * 
	 * @param sessionDetails
	 *            To validate mandatory session details parameters.
	 * @param accessLog
	 *            to create the pmp api access log details in db.
	 * @return success response is returned else an error response is returned
	 */
	PMPResponse validateSessionDetailsParams(SessionDetails sessionDetails, PMPAPIAccessLog accessLog);

	/**
	 * Method is used to validate the delete session details api call
	 * parameters.For a particular event and a particular session this method
	 * will validate the parameters provided are correct or wrong.
	 * 
	 * @param sessionDetails
	 *            to get the auto generated event and session id.
	 * @param accessLog
	 *            to persist the access log details in db.
	 * @return success or error response depending on the parameters validated.
	 */
	PMPResponse validateDeleteSessionDetailParams(SessionDetails sessionDetails, PMPAPIAccessLog accessLog);

	/**
	 * Method is used to validate the get session details list parametrs.
	 * 
	 * @param sessionDetails
	 *            object is used to get the auto generated event id.
	 * @param accessLog
	 *            is used to create log details in pmp.
	 * @return success or error response depending on the validation.
	 */
	PMPResponse validateGetSessionDetailsParams(SessionDetails sessionDetails, PMPAPIAccessLog accessLog);

	/**
	 * Method is used to validate parameters which are
	 * send to search sessions for a particular event.
	 * @param searchSession Object is used to search session for 
	 * a particular event Id.
	 * @param accessLog to log details in PMP system.
	 * @return Success or Error response is returned depending on
	 * validations.
	 */
	PMPResponse validateSearchSessionParams(SearchSession searchSession, PMPAPIAccessLog accessLog);

}
