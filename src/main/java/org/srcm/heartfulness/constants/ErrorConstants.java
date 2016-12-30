package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the status and error messages. 
 * 
 * @author himasreev
 *
 */
public class ErrorConstants {

	public static final String STATUS_SUCCESS = "Success";

	public static final String STATUS_FAILED = "Failed";
	
	public static final String STATUS_ERROR = "ERROR";

	public static final String PARSE_ERROR = "parse-error : error while parsing json data";

	public static final String JSON_MAPPING_ERROR = "json mapping-error : json data is not mapped properly";

	public static final String INPUT_OUTPUT_ERROR = "input/output-error ; Please try after sometime";

	public static final String PLEASE_TRY_AFTER_SOMETIME = "Please try after sometime.";

	public static final String EMAILID_MISMATCH = "Email ID doesnot match with logged in e-mail";

	public static final String USER_MISMATCH = "username mismatch.";

	public static final String INVALID_REQUEST = "Invalid Request.";

	public static final String EMAIL_NOT_SENT = "Email Not Sent";
	
	public static final String INVALID_AUTH_TOKEN = "Invalid auth token";
	
	public static final String INVALID_CREDENTIALS= "Invalid credentials";
	
	public static final String EWELCOMEID_DUPLICATE_RECORD_RESPONSE_FROM_MYSRCM="Duplicate record with name and mobile";
	
	public static final String EWELCOMEID_DUPLICATE_RECORD_CUSTOMIZED_RESPONSE="Member already exists with eWelcomeId - ";
	
	public static final String EMPTY_EVENT_ID 						=	"Event Id cannot be empty";
	public static final String INVALID_EVENT_ID 					=	"Please provide a valid event Id";
	public static final String EMPTY_SESSION_NUMBER 				=	"Session Number cannot be empty";
	public static final String EMPTY_SESSION_DATE 					= 	"Session Date cannot be empty";
	public static final String INVALID_DATE_FORMAT 					= 	"Please enter session date in dd-MM-yyyy format";
	public static final String INVALID_PCTPT_COUNT 					= 	"There should be atleast a single participant for an event";
	public static final String INVALID_NEW_PCTPT_COUNT 				= 	"Please enter a valid count for new participants";
	public static final String EMPTY_PRECEPTOR_ID_CARD_NO 			= 	"Preceptor Id card number cannot be empty";
	public static final String INVALID_PRECEPTOR_ID_CARD_NO     	= 	"Please enter a valid Preceptor Id card number";
	public static final String SESSION_SUCCESSFULLY_CREATED     	= 	"Session Details has been successfully created";
	public static final String SESSION_SUCCESSFULLY_UPDATED     	= 	"Session Details has been successfully updated";
	public static final String SESSION_CREATION_FAILED     			= 	"Failed to save session details";
	public static final String EMPTY_SESSION_ID 					=	"Session Id cannot be empty";
	public static final String INVALID_SESSION_ID 					=	"Please provide a valid session Id";
	public static final String SESSION_DELETION_FAILED     			= 	"Failed to remove session details";
	public static final String SESSION_SUCCESSFULLY_DELETED     	= 	"Successfully removed session details";
	public static final String SESSION_DETAILS_RETRIEVAL_FAILED 	= 	"Failed to retrieve session details for ";
}
