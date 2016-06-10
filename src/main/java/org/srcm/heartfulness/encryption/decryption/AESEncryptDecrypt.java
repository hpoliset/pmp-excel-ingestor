package org.srcm.heartfulness.encryption.decryption;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author HimaSree
 *
 */
@Component
public class AESEncryptDecrypt {

	private static final Logger LOGGER = LoggerFactory.getLogger(AESEncryptDecrypt.class);

	@Autowired
	Environment env;

	/**
	 * method to encrypt plain data to encrypted one
	 * 
	 * @param Data
	 * @return encrypted data
	 */
	public String encrypt(String Data, String key) {
		Cipher c;
		SecretKey skey = convertStringToSecretKey(key);
		try {
			c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, skey);
			byte[] encVal = c.doFinal(Data.getBytes());
			String encryptedValue = new BigInteger(encVal).toString(16);
			LOGGER.debug("Token encrypted successfully. ");
			return encryptedValue;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.debug("Exception while encrypting the token {} ", e.getMessage());
		} catch (NoSuchPaddingException e) {
			LOGGER.debug("Exception while encrypting the token {} ", e.getMessage());
		} catch (InvalidKeyException e) {
			LOGGER.debug("Exception while encrypting the token {} ", e.getMessage());
		} catch (IllegalBlockSizeException e) {
			LOGGER.debug("Exception while encrypting the token {} ", e.getMessage());
		} catch (BadPaddingException e) {
			LOGGER.debug("Exception while encrypting the token {} ", e.getMessage());
		}
		return null;
	}

	/**
	 * method to decrypt encrypted data to plain text
	 * 
	 * @param Data
	 * @return plain data
	 */
	public String decrypt(String encryptedData, String key) {
		Cipher c;
		SecretKey skey = convertStringToSecretKey(key);
		try {
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, skey);
			byte[] decordedValue = new BigInteger(encryptedData, 16).toByteArray();
			byte[] decValue = c.doFinal(decordedValue);
			String decryptedValue = new String(decValue);
			return decryptedValue;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
		} catch (NoSuchPaddingException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
		} catch (InvalidKeyException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
		} catch (IllegalBlockSizeException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
		} catch (BadPaddingException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
		}
		return null;

	}

	/**
	 * method to convert key value to Secret key
	 * 
	 * @param skey
	 * @return
	 */
	public SecretKey convertStringToSecretKey(String key) {
		byte[] decodedKey = Base64.getDecoder().decode(key);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	}

}
