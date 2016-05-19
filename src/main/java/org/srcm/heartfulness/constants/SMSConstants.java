package org.srcm.heartfulness.constants;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * Constant class to hold the messages, keywords,sub-keywords for the SMS integration. 
 * 
 * @author rramesh
 *
 */
public class SMSConstants {
	
	/** Holds the keyword	 */
	public static final String SMS_KEYWORD="LOVHFN";
	
	/** Holds the sub keyword for create event  */
	public static final String SMS_CREATE_EVENT_SUB_KEYWORD="CES";
	
	/** Holds the sub keyword for create event	 */
	public static final String SMS_UPDATE_EVENT_SUB_KEYWORD="UES";
	
	/** Holds the sub keyword to get total registered users	 */
	public static final String SMS_GET_TOTAL_REGISTERED_USERS_SUB_KEYWORD = "CRS";
	
	/** Holds the sub keyword to get total introduced users	 */
	public static final String SMS_GET_TOTAL_REGISTERED_USERS_BY_INTRO_ID_SUB_KEYWORD = "CIS";
	
	/** Holds the length of event ID	 */
	public static final int SMS_EVENT_ID_LENGTH = 7;
	
	/** Holds the length of introduction ID	 */
	public static final int SMS_INTRO_ID_LENGTH = 8;
	
	/** Holds the Heartfulness home page URL	 */
	public static final String SMS_HEARTFULNESS_HOMEPAGE_URL="http://en.heartfulness.org/";
	
	/** Holds the Heartfulness home page URL	 */
	public static final String SMS_HEARTFULNESS_UPDATEEVENT_URL="https://pmpbeta.heartfulness.org/pmp/updateevent";
	
	
	public static final String SMS_CREATE_EVENT_RESPONSE_DUPLICATE_EVENT1 ="Specified Event with the name( ";
	
	public static final String SMS_CREATE_EVENT_RESPONSE_DUPLICATE_EVENT2 = " ) is already available. Please check";
	
	
	public static final String SMS_CREATE_EVENT_RESPONSE_SUCCESS_1 ="Event creation success: Event ID - ";
	
	public static final String SMS_CREATE_EVENT_RESPONSE_SUCCESS_2 =", Intro ID - ";
	
	public static final String SMS_CREATE_EVENT_RESPONSE_SUCCESS_3 =" Your Seq ID - ";
	
	public static final String SMS_PLEASE_CLICK=". Please Click ";
	
	public static final String SMS_CREATE_EVENT_RESPONSE_SUCCESS_4 =". Please click on ";
	
	public static final String SMS_CREATE_EVENT_RESPONSE_SUCCESS_5 =" to update the event details.";
	
	public static final String SMS_EWELCOME_RESPONSE_INVALID_SEQ_FORMAT_1 =  "Specified Seq ID ( ";

    public static final String SMS_EWELCOME_RESPONSE_INVALID_SEQ_FORMAT_2 =  " ) is not available. Please enter a valid Seq ID.";

	public static final String SMS_RESPONSE_INVALID_FORMAT_1 = "Invalid request format, please use: ";
	
	public static final String SMS_CREATE_EVENT_RESPONSE_INVALID_FORMAT_1 =" <EVENT_NAME> <ABHYASI_ID> <EVENT_ZIPCODE>";
	
	public static final String SMS_EMPTY_SPACE =" ";
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_NOT_AVAILABLE_1 = "Specified Event ID(";
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_NOT_AVAILABLE_2 = ") is not available. Please enter a valid event ID.";
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_ALREADY_EXISTS_1 = "Specified new Event name(";
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_ALREADY_EXISTS_2 = ") is already exist. Please use different name.";
	
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_SUCCESS_1 = "Event updated successfully";
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_SUCCESS_2 = " to update the event details.";
	
	public static final String SMS_UPDATE_EVENT_RESPONSE_INVALID_FORMAT_1 = " <EVENT_ID> <NEW_EVENT_NAME>";
	
	public static final String SMS_CREATE_PARTICIPANT_RESPONSE_SUCCESS_1 = "Welcome to Heartfulness. ";
	
	public static final String SMS_CREATE_PARTICIPANT_RESPONSE_SUCCESS_2 = " to visit Heartfulness website.";
	
	public static final String SMS_CREATE_EVENT_PARTICIANT_INVALID_RESPONSE_1 = " <EVENT_ID> <PARTICIPANT_NAME> <EMAIL_ID>";
	
	public static final String SMS_EWELCOME_RESPONSE_SUCCESS_1 = "Your eWelcome ID : ";
	
	public static final String SMS_EWELCOME_RESPONSE_SUCCESS_2 =  ", this can be used as reference for further communications.";
	
	public static final String SMS_EWELCOME_RESPONSE_INVALID_FORMAT_1 =  "Specified Intro ID ( ";
	
	public static final String SMS_EWELCOME_RESPONSE_INVALID_FORMAT_2 =  " ) is not available. Please enter a valid event ID.";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_1 =  "Invalid request format, please use, To create participant: ";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_2 = " <event-ID> <participant-name> <email ID>. ";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_3 = "To Create eWelcome ID: ";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_4 = " <Intro-ID> <seq ID>";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_5 = "Specified event ID or Intro ID( ";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_6 = " ) is not available. Please enter a valid event ID.";
	
	public static final String SMS_CREATE_PARTICIPANT_INVALID_FORMAT_7 =  "Specified event ID (";
	
	public static final String SMS_REGISTER_PARTICIPANT_COUNT_SUCCESS_1 = "Number of participants registered for the Event ID( ";
	
	public static final String SMS_REGISTER_PARTICIPANT_COUNT_SUCCESS_2 = " ) : ";
	
	public static final String SMS_REGISTER_PARTICIPANT_INVALID_FORMAT_1 = "Specified event ID ( ";
	
	public static final String SMS_REGISTER_PARTICIPANT_INVALID_FORMAT_2 = " ) is not available. Please enter a valid event ID.";
	
	public static final String SMS_INTRODUCED_PARTICIPANT_COUNT_SUCCESS_1 = "Number of participants introduced for the Intro ID( ";
	
	public static final String SMS_EVENT_ID_PREFIX="E";
	
	public static final String SMS_INTRO_ID_PREFIX="I";
	
	public static final String SMS_HELP_KEYWORD="HELP";
	
	public static final String SMS_HELP_CREATE_EVENT="CREATE EVENT - ";
	
	public static final String SMS_HELP_UPDATE_EVENT="UPDATE EVENT - ";
	
	public static final String SMS_HELP_REGISTER_PARTICIPANT="REGISTER PARTICIPANT - ";
	
	public static final String SMS_HELP_INTRODUCE_PARTICIPANT="INTRODUCTION SITTING CONFIRMATION - ";
	
	public static final String SMS_HELP_NO_OF_REGISTERED_PARTICIPANTS="NO OF REGISTERED PARTICIPANTS - ";
	
	public static final String SMS_HELP_NO_OF_INTRODUCED_PARTICIPANTS="NO OF INTRODUCED PARTICIPANTS - ";
	
	public static final String SMS_HELP_FORMAT=SMS_KEYWORD+SMS_EMPTY_SPACE+SMS_HELP_KEYWORD+" to get SMS formats";
	
	public static final String SMS_MISSING_EVENT_ID="EVENT ID is Missing. ";
	
	public static final String SMS_MISSING_INTRO_ID="INTRO ID is Missing. ";
	
	public static final String SMS_SEQUENCE_NUMBER_RESPONSE_INVALID_FORMAT_1 =  "Specified Seq ID ( ";
	
	public static final String SMS_SEQUENCE_NUMBER_RESPONSE_INVALID_FORMAT_2 =  " ) is not available. Please provide a valid Seq ID.";
	
	public static final String SMS_REGISTER_PARTICIPANT_SUB_KEYWORD = "RP";
	
	public static final String SMS_INTRODUCE_PARTICIPANT_SUB_KEYWORD = "IN";
	
	public static final String SMS_NO_OF_REGISTERED_PARTICIPANT_INVALID_FORMAT_4 = " <EVENT_ID>";
	
	public static final String SMS_NO_OF_INTRODUCED_PARTICIPANT_INVALID_FORMAT_4 = " <INTRO_ID>";
	
	public static final String SMS_INTRODUCE_PARTICIPANT_RESPONSE_INVALID_FORMAT_2 =  " ) is not available. Please check.";
	
	public static final String SMS_INTRODUCE_PARTICIPANT_INVALID_FORMAT_1 =  "Specified Intro ID/Seq ID ( ";
	
	public static final String SMS_INTRODUCE_PARTICIPANT_RESPONSE_INVALID_FORMAT_3 =  " ) is not available. Please enter a valid Intro ID.";
	
	public static final String SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_1="Specified zipcode( ";
	
	public static final String SMS_CREATE_EVENT_INVALID_ZIPCODE_RESPONSE_2=" ) is not valid. Please enter a valid zipcode.";

	public static final String SMS_CREATE_EVENT_INVALID_MOBILE_RESPONSE_1 = "Specified Mobile number ( ";

	public static final String SMS_CREATE_EVENT_INVALID_MOBILE_RESPONSE_2 = " ) is not valid. Please provide a valid mobile number.";
	
	public static final String SMS_CREATE_EVENT_INVALID_EMAIL_RESPONSE_1 = "Specified emailID ( ";

	public static final String SMS_CREATE_EVENT_INVALID_EMAIL_RESPONSE_2 = " ) is not valid. Please provide a valid emailID.";
}
		
