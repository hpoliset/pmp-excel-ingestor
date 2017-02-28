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

	public static final String  REQUESTER_REQUEST_WAITING_FOR_APPROVAL 	 	 = "Your request is already pending for approval";
	public static final String  REQUESTER_REQUEST_ALREADY_APPROVED 		 	 = "Your request has been already approved ";
	public static final String  REQUESTER_SUCCESSFULL_REQUEST 			 	 = "Your request has been successfully submitted";
	public static final String  REQUESTER_FAILED_REQUEST 					 = "Failed to complete your request.Please try after sometime";
	public static final String  REQUESTER_EMAIL_INVALID 					 = "Coordinator email cannot be empty";
	public static final String  REQUESTER_INVALID_SELF_REQUEST 				 = "You cannot raise a request for ýour own event";
	public static final String  REQUESTER_INVALID_PRECEPTOR_DETAILS			 = "Please update the preceptor details first";
	public static final String  REQUESTER_INVALID_EMAIL_ADDRESS			 	 = "Coordinator you are trying to approve is not registerd with MYSRCM";

	public static final String  APPROVER_REQUEST_DOESNOT_EXIST 				= "Coordinator you are trying to approve needs to raise a request first";
	public static final String  APPROVER_REQUEST_ALREADY_APPROVED 			= "Coordinator you are trying to approve is already having access for this event";
	public static final String  APPROVER_VALIDATION_SUCCESSFULL 			= "Approver validation successfull";
	public static final String  APPROVER_SUCCESS_RESPONSE					= "You have successfully added ";
	public static final String  APPROVER_SAME_APPROVER_REQUESTER			= "You cannot approve a request which was requested by you";
	public static final String  APPROVER_NO_AUTHORITY						= "It seems you are not the primary coordinator or preceptor for this event";
	
	public static final String  REQUESTERLIST_EMPTY								= "You are not having any request for your events";
	
	public static final String  IS_READ_ONLY_TRUE							= "Y";
	public static final String  IS_READ_ONLY_FALSE							= "N";
	
	public static final String HEARTFULNESS_CREATE_PROFILE_URL = "https://profile.sahajmarg.org/accounts/signup/"; 
	
	
	
	
	
	
	
	
}
