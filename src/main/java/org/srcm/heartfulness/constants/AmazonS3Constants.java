package org.srcm.heartfulness.constants;

/**
 * Constant class to hold the constants used in S3 Integration.
 * 
 * @author himasreev
 *
 */

public class AmazonS3Constants {

	public static String SHA256_CONTENT_HEADER 					= 	"x-amz-content-sha256";
	public static String DATE_HEADER 							= 	"x-amz-date";
	public static String HOST_HEADER 							= 	"host";
	public static String AWS4_REQUEST 							= 	"aws4_request";
	public static String ALGORITHM_SHA256						= 	"SHA-256";
	public static String ALGORITHM_HMACSHA256 					= 	"HmacSHA256";
	public static String AWS_SIGNATURE_VERSION 					= 	"AWS4";
	public static String UTF8_ENCODE 							= 	"UTF8";
	public static String UTC_TIME_FORMAT 						= 	"UTC";
	public static String S3_DATE_FORMAT 						= 	"yyyyMMdd HHmmss";
	public static String ALGORITHM_TO_CALCULATE_SIGNATURE 		= 	"AWS4-HMAC-SHA256";
	public static String AWS_AUTHORIZATION_CREDENTIAL 			= 	"Credential=";
	public static String AWS_AUTHORIZATION_SIGNEDHEADERS 		= 	"SignedHeaders=";
	public static String AWS_AUTHORIZATION_SIGNATURE 			= 	"Signature=";
	public static String URI_PROTOCOL 							= 	"https://";

}
