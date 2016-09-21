package org.srcm.heartfulness.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author rramesh
 * 
 *         SMS Integration utilities
 */
public class SmsUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(SmsUtil.class);

	/**
	 * To Parse the sms content and get the values
	 * 
	 * @param smsContent - sms content
	 * @return the response
	 */
	public static String[] parseSmsContent(String smsContent) {
		String[] response = smsContent.split("\\s+");
		return response;
	}

	/**
	 * To generate a random number based on the digits given
	 * 
	 * @param digit - number of digits
	 * @return the generated random number
	 */
	public static String generateRandomNumber(int digit) {
		String generatedNumber = new String();
		SecureRandom secureRandomGenerator;
		try {
			secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
			byte[] randomBytes = new byte[128];
			secureRandomGenerator.nextBytes(randomBytes);
			int generatedInt = secureRandomGenerator.nextInt();
			generatedNumber = Integer.valueOf(Math.abs(generatedInt)).toString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.debug("Exception while generating random number {}", e.getMessage());
		}
		return generatedNumber.substring(0, digit);
	}

	/**
	 * To generate four digit sequence number
	 * 
	 * @return the 4 digit sequence number
	 */
	public static String generateFourDigitPIN() {
		SecureRandom secureRandomGenerator;
		int generatedInt = 0;
		try {
			secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
			byte[] randomBytes = new byte[128];
			secureRandomGenerator.nextBytes(randomBytes);
			generatedInt = secureRandomGenerator.nextInt(9999);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.debug("Exception while generating sequence number {}", e.getMessage());
		}
		return String.format("%04d", generatedInt);
	}

}
