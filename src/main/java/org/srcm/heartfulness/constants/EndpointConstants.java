package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the endpoints of MySRCM and PMP. 
 * 
 * @author himasreev
 *
 */
public class EndpointConstants {
	
	public static final String  GET_USER_PROFILE = "http://profile.srcm.net/api/me?format=json";
	
	public static final String  AUTHENTICATION_TOKEN_URL = "http://profile.srcm.net/o/token/";
	
	public static final String  ABHYASI_INFO_URI = "http://profile.srcm.net:80/api/v2/abhyasis/?format=json";
	
	public static final String  GEOSEARCH_URI = "http://profile.srcm.net:80/api/v2/cities/geosearch/?format=json";
	
    public static final String  CREATE_ASPIRANT_URI = "http://profile.srcm.net:80/api/v2/abhyasis/create_aspirant/?format=json";
	
	public static final String  CITIES_API = "http://profile.srcm.net/api/v2/cities/";
	
	public static final String  MOBILE_AUTHENTICATION_ENDPOINT = "/pmp/api/mobile/authenticate";
	
	public static final String  MOBILE_CREATE_USER_ENDPOINT = "/pmp/api/v1/mobile/users";

	public static final String CREATE_USER_PROFILE = "http://profile.srcm.net/api/users/?format=json";
	
}
