/**
 * 
 */
package org.srcm.heartfulness.constants;

/**
 * @author Koustav Dutta
 *
 */
public class DashboardConstants {
	
	public static final String PROCESSING_FAILED 										    = 	"Unable to serve your request";
	public static final String USER_UNAVAILABLE_IN_PMP 										= 	"User doesnot exists in PMP";
	
	/*Event related */
	public static final String INVALID_OR_EMPTY_EVENTID 									= 	"Event Id cannot be empty";
	public static final String INVALID_EVENTID 												= 	"Event Id doesnot exist";
	public static final String INVALID_SEARCH_FIELD											= 	"Invalid search field";
	public static final String INVALID_SEARCH_TEXT											= 	"Invalid search text";
	public static final String STATUS														= 	"status";
	public static final String INVALID_OR_EMPTY_STATUS										=	"Status cannot be empty";
	public static final String STATUS_SUCESS_UPDATE											=	"Status updated Successfully";
	public static final String STATUS_ENUM_UPDATE											= 	"Invalid program status";
	public static final String INVALID_USER													= 	"User doesn't have access to this event";
	
	/*Participant related*/
	public static final String PRINT_NAME_REQUIRED											= 	"Participant name is required";
	public static final String SEQ_ID_REQUIRED												= 	"SeqID is required";
	public static final String INVALID_SEQ_ID												= 	"Participant doesn't exist for the provided seqId";
	public static final String INVALID_GENDER												= 	"Gender is not valid";
	public static final String PARTICIPANT_City_REQ											= 	"City is required";
	public static final String PARTICIPANT_STATE_REQ										= 	"State is required";
	public static final String PARTICIPANT_COUNTRY_REQ										= 	"Country is required";
	public static final String PARTICIPANT_DISTRICT_REQ										= 	"District is required";
	public static final String INVALID_PARTICIPANT_EMAIL									= 	"Email is invalid";
	public static final String INVALID_INTRODUCED_DATE										= 	"Introduction date is invalid. Valid format DD-MM-YYYY";
	public static final String INVALID_DOB													= 	"DOB is invalid. Valid format DD-MM-YYYY";
	
	/*Channel related*/
	public static final String EMPTY_CHANNEL												= 	"Channel cannot be empty";
	
	/*Session related*/
	public static final String VALIDATION_RESPONSE											= 	"Validation successfull";
	public static final String EMPTY_SESSION_ID												= 	"Session Id cannot be empty";
	public static final String INVALID_SS_FROM_DATE											= 	"Invalid from date";
	public static final String INVALID_SS_TO_DATE											= 	"Invalid to date";
	
	public static final String G_CONNECT_CHANNEL											= 	"G-Connect";
	public static final String TESTIMONIAL_FOLDER											=	"Testimonial";
	
	/*Dashboard related*/
	public static final String COUNTRY_REQUIRED												=	"Country is required";
	public static final String ZONE_REQUIRED												=	"Zone is required";
	public static final String CENTER_REQUIRED												=	"Center is required";
	public static final String ALL_FIELD													=	"ALL";
	public static final String STATE_REQUIRED												=	"State is required";
	public static final String DISTRICT_REQUIRED											=	"District is required";
	public static final String CITY_REQUIRED												=	"City is required";
	
	
	public static final String ZONE_CENTER_COORDINATOR_NOTIFICATION_CRON					=	"ZONE_CENTER_COORDINATOR_NOTIFICATION_CRON";
	public static final String ZONE_GROUP_TYPE												=	"zone";
	public static final String CENTER_GROUP_TYPE											=	"center";
	
	public static final String ZONE_COORDINATOR_INFORMED_COLUMN								=	"is_zone_coordinator_informed";
	public static final String CENTER_COORDINATOR_INFORMED_COLUMN							=	"is_center_coordinator_informed";
	public static final String EVENT_STATUS_COMPLETED								    	=	"Event is already completed";
	
}
