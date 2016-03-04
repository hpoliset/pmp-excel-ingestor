package org.srcm.heartfulness.util;


/**
 * 
 * @author rramesh
 * 
 * SMS Integration utilities
 */
public class SmsUtil {
	
	/**
	 * To Parse the sms content and get the values
	 * 
	 * @param smsContent - sms content
	 * @return the response
	 */
	public static String[] parseSmsContent(String smsContent){
		String[] response = smsContent.split("\\s+");
		return response;
	}

	/**
	 * To generate a random number based on the digits given
	 * 
	 * @param digit - number of digits
	 * @return the generated random number
	 */
	public static String generateRandomNumber(int digit){
		long timeSeed = System.nanoTime(); // to get the current date time value
		double randSeed = Math.random() * 1000; // random number generation
		long midSeed = (long) (timeSeed * randSeed); // mixing up the time and rand number.
		String s = midSeed + "";
		String generatedNumber = s.substring(0, digit);
		//int finalSeed = Integer.parseInt(subStr); // integer value
		return generatedNumber;
	}
	
}
