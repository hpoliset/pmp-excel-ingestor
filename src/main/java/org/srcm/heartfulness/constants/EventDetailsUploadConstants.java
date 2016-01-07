package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the messages, sheet names and other related information for the Excel Upload program. 
 * 
 * @author Koustav Dutta
 *
 */
public class EventDetailsUploadConstants {
	
	//for v2.1
	
	/** Holds the name of the sheet that contains Event details for Excel version 2.1. */
	public static final String V2_EVENT_SHEET_NAME = "Event Details";
	
	/** Holds the name of the sheet that contains Participant details for Excel version 2.1. */
	public static final String V2_PARTICIPANT_SHEET_NAME = "Participants Details";
	
	
	//for altered v1.0
	/** Holds the name of the sheet for Excel version 1.0. For Version 1.0, there is only one sheet available.  */
	public static final String V1_SHEET_NAME = "Sheet1";
	
	//for altered v3.0
	/** Holds the name of the sheet for Excel version 3.0. For Version 3.0, there is only one sheet available.  */
	public static final String V3_SHEET_NAME = "Heartfulness Data";
	
	//TODO move this constant out, incase of internationalization requirements  
	/** Holds the error message to be displayed when the user tries to upload an invalid template.  */
	public static final String INVALID_TEMPLATE_MSG = "Template version could not be identified.";
	
	
	// Response Statuses
	
	/** Holds the response status for successful upload.  */
	public static final String SUCCESS_STATUS = "Success";
	
	/** Holds the response status when the upload fails. */
	public static final String FAILURE_STATUS = "Failure";
}