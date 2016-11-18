/**
 * 
 */
package org.srcm.heartfulness.constants;

/**
 * @author Koustav Dutta
 *
 */
public class CoordinatorAccessControlConstants {
	
	public static final String  REQUEST_DEFAULT_STATUS  = "WAITING_FOR_APPROVAL";
	public static final String  REQUEST_APPROVAL_STATUS = "APPROVED";

	public static final String  EMPTY_EVENT_ID 		= "Event ID cannot be null or empty";
	public static final String  INVALID_EVENT_ID 	= "Request couldnot be completed.Invalid Event Id";
	public static final String  INVALID_USER_ID 	= "Request couldnot be completed.User email is not registered with MYSRCM";
	public static final String  INVALID_REQUEST 	= "Request could not be completed.Please try after sometime";

	public static final String  COORDINATOR_REQUEST_WAITING_FOR_APPROVAL 	 = "Your request is already waiting for approval";
	public static final String  COORDINATOR_REQUEST_ALREADY_APPROVED 		 = "Your request has been already approved ";
	public static final String  COORDINATOR_SUCCESSFULL_REQUEST 			 = "Your request has been successfully submitted";
	public static final String  COORDINATOR_FAILED_REQUEST 					 = "Failed to register your request.Please try after sometime";
	public static final String  COORDINATOR_EMAIL_INVALID 					 = "Coordinator email cannot be empty";
	public static final String  COORDINATOR_INVALID_SELF_REQUEST 			 = "You cannot raise a request for ýour own event";

	public static final String  PRECEPTOR_REQUEST_DOESNOT_EXIST 			= "Coordinator you are trying to add needs to raise a request first";
	public static final String  PRECEPTOR_REQUEST_ALREADY_APPROVED 			= "Coordinator you are trying to add hasbeen already provided access for this event";
	public static final String  PRECEPTOR_VALIDATION_SUCCESSFULL 			= "Preceptor validation successfull";
	public static final String  PRECEPTOR_SUCCESS_RESPONSE					= "You have successfully added ";
	public static final String  PRECEPTOR_SAME_APPROVER_REQUESTER			= "You cannot approve a request for yourself ";
	
	public static final String HEARTFULNESS_CREATE_PROFILE_URL = "https://profile.sahajmarg.org/accounts/signup/"; 
	
	
	
	
	
	
	
	
}
