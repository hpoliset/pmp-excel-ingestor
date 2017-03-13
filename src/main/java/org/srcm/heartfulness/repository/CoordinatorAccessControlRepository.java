package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.SecondaryCoordinatorRequest;
import org.srcm.heartfulness.model.User;

/**
 * Repository Class for Coordinator Access Control.
 * 
 * @author himasreev
 *
 */
public interface CoordinatorAccessControlRepository {

	/**
	 * This method is used to get the program details using auto generated event
	 * Id.
	 * 
	 * @param autoGeneratedEventId
	 *            used to get the program details.
	 * @return details about a particular program.
	 */
	Program getProgramIdByEventId(String autoGeneratedEventId);

	/**
	 * This method is used to get the user details using the email.
	 * 
	 * @param userEmail
	 *            , email to check whether record exists in PMP.
	 * @return User details if record exists in PMP
	 */
	User getUserbyUserEmail(String userEmail);

	/**
	 * This method is used to the Program coordinator details if data exists.
	 * 
	 * @param programId
	 *            , to get the coordinator name,email for a given programId.
	 * @return ProgramCoordinators details
	 */
	ProgramCoordinators getProgramCoordinatorByProgramId(int programId);

	/**
	 * This method is used to get the count of request raised for a particular
	 * event.
	 * 
	 * @param programId
	 *            , to find the request count for a given programId.
	 * @param userEmail
	 *            , to find the request count using this email.
	 * @return count of request by a particular user for a particular event.
	 */
	int checkRequestAlreadyRaised(int programId, String userEmail);

	/**
	 * This method is used to check whether a particular request for a
	 * particular event is approved or not.
	 * 
	 * @param programId
	 *            , to find the approval count for a given programId.
	 * @param userEmail
	 *            , to find the approval count for a given event.
	 * @return count of request for a particular user where status is approved.
	 */
	int checkRequestAlreadyApproved(int programId, String userEmail);

	/**
	 * This method is used to save the request raised by a secondary
	 * coordinator.
	 * 
	 * @param scReq
	 *            , SecondaryCoordinatorRequest data to create a request.
	 */
	void saveSecondaryCoordinatorRequest(SecondaryCoordinatorRequest scReq);

	/**
	 * This method is used to approve a request raised by the secondary
	 * coordinator.
	 * 
	 * @param programId
	 *            , id of an event.
	 * @param approvedBy
	 *            , email address of the primary coordinator or the preceptor of
	 *            a particular event.
	 * @param requestedBy
	 *            , email of secondary coordinator who has raised a request.
	 * @return 1 if approval is successfull else return 0.
	 * 
	 */
	int approveSecondaryCoordinatorRequest(int programId, String approvedBy, String requestedBy);

	/**
	 * This method is used to create a secondary coordinator record.
	 * 
	 * @param pgrmCoordinators
	 *            object is used to create a secondary coordinator record.
	 */
	void createProgramCoordinator(ProgramCoordinators pgrmCoordinators);
	
	/**
	 * This method is used to return the list of requests raised by secondary
	 * coordinators for a particular event.
	 * 
	 * @param programId
	 *            , to get the list for a given id.
	 * @return List<SecondaryCoordinatorRequest> if available else an empty
	 *         list.
	 */
	List<SecondaryCoordinatorRequest> getListOfRequests(StringBuilder programIdBuilder);

	/**
	 * This method is used to persist the coordinator details to the event with
	 * reference of program Id and user Id.
	 * 
	 * @param <code>programCoordinators</code>
	 */
	void saveCoordinatorDetails(ProgramCoordinators programCoordinators);

	/**
	 * This method is used to roll back request table if fails to
	 * update coordinator in program_coordinators table.
	 * @param programId, changes will be rolled back based on the 
	 * program Id.
	 * @param approvedBy,email of the approver who will approve 
	 * accesss for the given program Id.
	 * @param requestedBy, email of the requester who requested 
	 * accesss for the given program Id.
	 * @return if successfully updated returns 1 else 0.
	 */
	int rollbackApprovedSecondaryCoordinatorRequest(int programId, String approvedBy, String requestedBy);

}