package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the regular expressions used for data validations in PMP. 
 * 
 * @author himasreev
 *
 */
public class ExpressionConstants {

	public static final String  DATE_REGEX = "^\\d{2}-\\d{2}-\\d{4}$";

	public static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
	 //"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String MOBILE_REGEX = "^[7-9]\\d{9}$";

	public static final String EVENT_ID_PREFIX="E";

	public static final String INTRO_ID_PREFIX="I";
	
	public static final String EWELCOME_ID_REGEX="^[A-Z]\\d{8}$";
	
	public static final String ZIPCODE_REGEX="^[1-9][0-9]{5}$";
	
	public static final String PARTICIPANT_EWELCOME_ID_REGEX="^[HB][0-9]{8}$";
	
	public static final String EVENT_ID_REGEX = "^E[0-9]{6}$";
	
	public static final String COLON_HEADER_SEPARATER = ":";
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	public static final String PATH_SEPARATER = "/";

	public static final  String SPACE_SEPARATER = " ";

	public static final String DOT_SEPARATER = ".";

	public static final String COMMA_SEPARATER = ",";

	public static final String NEXT_LINE = "\n";
	
	public static final String GENERATE_EWELCOME_ID_REGEX  = "^[GENERATEEWELCOMEID_ ]*$";
	
	public static final String PLEASE_GENERATE_EWELCOME_ID_REGEX  = "^[PLEASEGENERATEEWELCOMEID_ ]*$";
	
	public static final String ISSUE_EWELCOME_ID_REGEX  = "^[ISSUEEWELCOMEID_ ]*$";
	
	public static final String PLEASE_ISSUE_EWELCOME_ID_REGEX  = "^[PLEASEISSUEEWELCOMEID_ ]*$";
	
}
