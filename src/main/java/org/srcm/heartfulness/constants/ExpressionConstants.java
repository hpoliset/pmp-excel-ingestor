package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the regular expressions used for data validations in PMP. 
 * 
 * @author himasreev
 *
 */
public class ExpressionConstants {

	public static final String  DATE_REGEX 						= 	"^\\d{2}-\\d{2}-\\d{4}$";
	public static final String EMAIL_REGEX 						= 	"^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; //"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String MOBILE_REGEX 					= 	"^[7-9]\\d{9}$";
	public static final String EVENT_ID_PREFIX					=	"E";
	public static final String INTRO_ID_PREFIX					=	"I";
	public static final String EWELCOME_ID_REGEX				=	"^[A-Z]\\d{8}$";
	public static final String ZIPCODE_REGEX					=	"^[1-9][0-9]{5}$";
	public static final String PARTICIPANT_EWELCOME_ID_REGEX	=	"^[HB][0-9]{8}$";
	public static final String EVENT_ID_REGEX 					= 	"^E[0-9]{6}$";
	public static final String COLON_HEADER_SEPARATER 			= 	":";
	public static final String PATH_SEPARATER 					= 	"/";
	public static final  String SPACE_SEPARATER 				= 	" ";
	public static final String DOT_SEPARATER 					=	 ".";
	public static final String COMMA_SEPARATER 					= 	",";
	public static final String NEXT_LINE 						= 	"\n";
	public static final String DATE_FORMAT 						= 	"dd-MM-yyyy";
	public static final String SQL_DATE_FORMAT 					= 	"yyyy-MM-dd";
	public static final String MAIL_DATE_FORMAT 				= 	"dd-MMM-yyyy";
	//public static final String MOBILE_V1_0_REGEX 				= 	"^([0|\\+[0-9]{1,5})?([7-9][0-9]{9})$";
	
}
