/**
 * 
 */
package org.srcm.heartfulness.constants;

/**
 * @author KOustav Dutta
 *
 */
public class EventConstants {

	public static final String  DATE_REGEX = "^\\d{2}-\\d{2}-\\d{4}$";

	public static final String EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static final String MOBILE_REGEX = "^[7-9]\\d{9}$";

	public static final String EVENT_ID_PREFIX="E";

	public static final String INTRO_ID_PREFIX="I";
	
	public static final String EWELCOME_ID_REGEX="^[A-Z]\\d{8}$";
	
	public static final String ZIPCODE_REGEX="^[1-9][0-9]{5}$";
}
