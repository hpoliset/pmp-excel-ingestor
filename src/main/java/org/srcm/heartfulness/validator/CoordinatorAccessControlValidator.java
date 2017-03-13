package org.srcm.heartfulness.validator;

import java.util.LinkedHashMap;
import java.util.List;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlErrorResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlResponse;

/**
 * 
 * @author Koustav Dutta
 *
 */

public interface CoordinatorAccessControlValidator {

	/**
	 * This method is used to validate the mandatory event params.
	 * @param autoGeneratedEventId user should not pass this value null
	 * or empty
	 * @return CoordinatorAccessControlErrorResponse is the validation 
	 * fails else null is returned.
	 */
	CoordinatorAccessControlErrorResponse checkMandatoryFields(String autoGeneratedEventId);

	/**
	 * This method is used to validate the mandatory params 
	 * before approving request for a secondary coordinator.
	 * @param approvedBy email of the primary coordinator
	 * or the preceptor for that event.
	 * @param pgrmCoordinators to get the email of the secondary coordinator 
	 * and the eventId for which secondary coordinator has requested for access.
	 * @return CoordinatorAccessControlResponse depending on the response is 
	 * success or failure.
	 */
	CoordinatorAccessControlResponse validateCoordinatorRequest(String approvedBy,ProgramCoordinators pgrmCoordinators);

	/**
	 * This method is used to get all the program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 * @param emailList List of emails associated witha abhyasi id for the 
	 * logged in user.
	 * @param userRole, role of the logged in user.
	 * @return LinkedHashMap<Integer,String> containing program Id and auto generated
	 * event Id's for the logged in user have conducted.
	 */
	LinkedHashMap<Integer,String> getProgramAndagEventIds(List<String> emailList,String userRole);
	

}