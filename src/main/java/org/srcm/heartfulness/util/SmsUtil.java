package org.srcm.heartfulness.util;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
	/**	long timeSeed = System.nanoTime(); // to get the current date time value
		double randSeed = Math.random() * 1000; // random number generation
		long midSeed = (long) (timeSeed * randSeed); // mixing up the time and rand number.
		String s = midSeed + "";
		String generatedNumber = s.substring(0, digit);
		//int finalSeed = Integer.parseInt(subStr); // integer value
		return generatedNumber;*/
		String generatedNumber = new String();
		SecureRandom secureRandomGenerator;
		try {
			secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
			byte[] randomBytes = new byte[128];
			secureRandomGenerator.nextBytes(randomBytes);
			//long randSeed = (long) Math.random() * 1000;
			//secureRandomGenerator.setSeed(seed);
			//System.out.println("secure secureRandomGenerator : " + secureRandomGenerator.nextInt());
			//System.out.println("secure secureRandomGenerator long : " + secureRandomGenerator.nextLong());
			int generatedInt = secureRandomGenerator.nextInt();
			System.out.println("secure secureRandomGenerator : " + generatedInt);
			System.out.println("Converted from negative to positive :"+Math.abs(generatedInt));
			generatedNumber = Integer.valueOf(Math.abs(generatedInt)).toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return generatedNumber.substring(0, digit);
	}
	
}
