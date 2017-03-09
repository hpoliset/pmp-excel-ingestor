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
}