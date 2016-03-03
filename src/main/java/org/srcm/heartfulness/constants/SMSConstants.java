package org.srcm.heartfulness.constants;

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
	public static final String SMS_CREATE_EVENT_SUB_KEYWORD="CE";
	
	/** Holds the sub keyword for create event	 */
	public static final String SMS_UPDATE_EVENT_SUB_KEYWORD="UE";
	
	/** Holds the sub keyword to get total registered users	 */
	public static final String SMS_GET_TOTAL_REGISTERED_USERS_SUB_KEYWORD = "CR";
	
	/** Holds the sub keyword to get total introduced users	 */
	public static final String SMS_GET_TOTAL_REGISTERED_USERS_BY_INTRO_ID_SUB_KEYWORD = "CI";
	
	/** Holds the length of event ID	 */
	public static final int SMS_EVENT_ID_LENGTH = 7;
	
	/** Holds the length of introduction ID	 */
	public static final int SMS_INTRO_ID_LENGTH = 8;
	
	/** Holds the Heartfulness home page URL	 */
	public static final String SMS_HEARTFULNESS_HOMEPAGE_URL="https://pmp.heartfulness.org";
	
	/** Holds the Heartfulness home page URL	 */
	public static final String SMS_HEARTFULNESS_UPDATEEVENT_URL="https://pmp.heartfulness.org/updateevent";
	
}
