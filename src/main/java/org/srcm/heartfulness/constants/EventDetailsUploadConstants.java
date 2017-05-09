package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the messages, sheet names and other related information for the Excel Upload program. 
 * 
 * @author Koustav Dutta
 *
 */
public class EventDetailsUploadConstants {
	
	/** Holds the name of the sheet that contains Event details for Excel version 2.1. */
	public static final String EVENT_SHEET_NAME 					= "Event Details";
	
	/** Holds the name of the sheet that contains Participant details for Excel version 2.1. */
	public static final String PARTICIPANT_SHEET_NAME 				= "Participants Details";
	
	//for altered v1.0
	/** Holds the name of the sheet for Excel version 1.0. For Version 1.0, there is only one sheet available.  */
	public static final String V1_SHEET_NAME 						= "Sheet1";
	
	/** Holds the error message to be displayed when the user tries to upload an invalid template.  */
	public static final String INVALID_TEMPLATE_MSG 				= "Template version could not be identified.";
	
	/** Holds the response status for successful upload.  */
	public static final String SUCCESS_STATUS 						= "Success";
	
	/** Holds the response status when the upload fails. */
	public static final String FAILURE_STATUS 						= "Failure";
	
	/*** Holds the disabled eWelcome Id state */
	public static final String EWELCOME_ID_DISABLED_STATE 			= "D";
	
	/*** Holds the enabled eWelcome Id state **/
	public static final String EWELCOME_ID_ENABLED_STATE  			= "E";
	
	/** Holds the name of the sheet that contains Event details for Excel version 1.0. **/
	public static final String V1_EVENT_SHEET_NAME 					= "Sheet1";
	
	/** Holds the name of the sheet that contains Participant details for Excel version 1.0.*/
	public static final String V1_PARTICIPANT_SHEET_NAME 			= "Sheet2";
	
	/** Holds the country value for Event and Participant details of Excel version m1.0.*/
	public static final String M1_0_EVENT_COUNTRY 					= "India";
	
	/**Holds the failure response which will be sent to the client while validating authorization token */
	public static final String INVALID_UPLOAD_REQUEST 			    = "Unable to complete your request. Please try after sometime";
	
	/**Holds the failure response which will be sent to the client while validating count of file and jira issue number mismatch */
	public static final String COUNT_MISMATCH 			    		= "Count of files selected doesn't match count of Jira Issue numbers provided";
	
	/**Holds the default jira issue number */
	public static final String DEFAULT_JIRA_NUMBER					= "";
	
	/**Holds the error message to be returned to the client in case of no excel file is uploaded*/
	public static final String MINIMUM_FILE_UPLOAD_COUNT			= "Please upload atleast a single excel file";
	
}