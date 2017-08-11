package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the endpoints of MySRCM and PMP. 
 * 
 * @author himasreev
 *
 */
public class EndpointConstants {
	
	public static final String  GET_USER_PROFILE 					= 	"https://profile.sahajmarg.org/api/me?format=json";
	public static final String  AUTHENTICATION_TOKEN_URL 			= 	"https://profile.sahajmarg.org/o/token/";
	public static final String  ABHYASI_INFO_URI 					= 	"https://profile.sahajmarg.org/api/v2/abhyasis/?format=json";
	public static final String  GEOSEARCH_URI 						= 	"https://profile.sahajmarg.org/api/v2/cities/geosearch/?format=json";
    public static final String  CREATE_ASPIRANT_URI 				= 	"https://profile.sahajmarg.org/api/v2/abhyasis/create_aspirant/?format=json";
	public static final String  CITIES_API 							= 	"https://profile.sahajmarg.org/api/v2/cities/";
	public static final String  MOBILE_AUTHENTICATION_ENDPOINT 		= 	"/pmp/api/mobile/authenticate";
	public static final String  MOBILE_CREATE_USER_ENDPOINT 		= 	"/pmp/api/v1/mobile/users";
	public static final String CREATE_USER_PROFILE 					= 	"https://profile.sahajmarg.org/api/users/?format=json";
	public static final String POSITIONS_API 						= 	"https://profile.sahajmarg.org/api/v2/positions?format=json";
	public static final String GET_POSITIONS_TYPE_API 				= 	"https://profile.sahajmarg.org/api/v2/positions-type?format=json";
	public static final String GROUPS_API_WITH_ZONE_PARAMETER 		= 	"https://profile.sahajmarg.org/api/v2/groups?group_type=zone&format=json&name__iexact=";
	public static final String GROUPS_API_WITH_CENTER_PARAMETER 	= 	"https://profile.sahajmarg.org/api/v2/groups?group_type=center&format=json&name__iexact=";
	
}
