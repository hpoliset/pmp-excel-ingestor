package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.AmazonS3Constants;
import org.srcm.heartfulness.constants.ExpressionConstants;

/**
 * Helper Class - AWS S3 Integration.
 * 
 * @author himasreev
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.aws.s3.properties", ignoreUnknownFields = true, prefix = "aws.s3")
public class AmazonS3Helper {

	private String secretkey;
	private String httpmethod;
	private String algorithm;
	private String signedheaders;
	private String region;
	private String service;
	private String host;

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public String getHttpmethod() {
		return httpmethod;
	}

	public void setHttpmethod(String httpmethod) {
		this.httpmethod = httpmethod;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getSignedheaders() {
		return signedheaders;
	}

	public void setSignedheaders(String signedheaders) {
		this.signedheaders = signedheaders;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Method to generate the Hash for the given object and return that
	 * generated Hash of the payload content.
	 * 
	 * @param multipartFile
	 * @return HashedRequestPayload
	 */
	public String computeHashedRequestPayload(MultipartFile multipartFile) {
		MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance(AmazonS3Constants.ALGORITHM_SHA256);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			mDigest.update(multipartFile.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] hashBytes = mDigest.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hashBytes.length; i++) {
			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Method to compute the canonical request with the HTTP Method,Canonical
	 * URI, canonicalQueryString , CanonicalHeaders and the Payload Content
	 * Hash(Given Object Hash).
	 * 
	 * @param hashedPayload
	 * @param fileName
	 * @return
	 */
	public String computeCanonicalRequest(String hashedPayload, String fileName) {
		String canonicalQueryString = "";
		String canonicalURI = ExpressionConstants.PATH_SEPARATER.trim() + fileName;
		String CanonicalHeaders = AmazonS3Constants.HOST_HEADER + ExpressionConstants.COLON_HEADER_SEPARATER + host
				+ ExpressionConstants.NEXT_LINE + AmazonS3Constants.SHA256_CONTENT_HEADER
				+ ExpressionConstants.COLON_HEADER_SEPARATER + hashedPayload + ExpressionConstants.NEXT_LINE
				+ AmazonS3Constants.DATE_HEADER + ExpressionConstants.COLON_HEADER_SEPARATER + getUTCDateAndTime()
				+ ExpressionConstants.NEXT_LINE;
		String canonicalRequest = httpmethod.trim() + ExpressionConstants.NEXT_LINE + canonicalURI.trim()
		+ ExpressionConstants.NEXT_LINE + canonicalQueryString + ExpressionConstants.NEXT_LINE + CanonicalHeaders
		+ ExpressionConstants.NEXT_LINE + signedheaders.trim() + ExpressionConstants.NEXT_LINE + hashedPayload;
		return canonicalRequest;
	}

	/**
	 * Method to create the hash for the given canonical request.
	 * 
	 * @param canonicalRequest
	 * @return
	 */
	public String computeHashedCanonicalRequest(String canonicalRequest) {
		MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance(AmazonS3Constants.ALGORITHM_SHA256);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] result = mDigest.digest(canonicalRequest.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Method to create the StringToSign request for AWS S3 Upload request with
	 * AWS algorithm, UTC time, credential scope and Hashed canonical request.
	 * 
	 * @param hashedCanonicalRequest
	 * @return
	 */
	public String getStringToSign(String hashedCanonicalRequest) {
		String dateAndTime = getUTCDateAndTime();
		String credentialScope = dateAndTime.split("T")[0].trim() + ExpressionConstants.PATH_SEPARATER + region
				+ ExpressionConstants.PATH_SEPARATER + service + ExpressionConstants.PATH_SEPARATER
				+ AmazonS3Constants.AWS4_REQUEST;
		String requestDate = dateAndTime;
		String stringToSign = algorithm + ExpressionConstants.NEXT_LINE + requestDate + ExpressionConstants.NEXT_LINE
				+ credentialScope + ExpressionConstants.NEXT_LINE + hashedCanonicalRequest;
		return stringToSign;
	}

	/**
	 * Method to generate signing key with the secret key , region and service
	 * provided.
	 * 
	 * @return
	 */
	public byte[] computeSigningKey() {
		try {
			byte[] byteSingingKey = getSignatureKey(secretkey.trim(), getUTCDateAndTime().split("T")[0].trim(), region,
					service);
			return byteSingingKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to generate the signature, which is used to validate the object
	 * source and upload the object in AWS S3.
	 * 
	 * @param key
	 * @param dateStamp
	 * @param regionName
	 * @param serviceName
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 */
	byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName)
			throws NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, InvalidKeyException {
		byte[] kSecret = (AmazonS3Constants.AWS_SIGNATURE_VERSION + key).getBytes(AmazonS3Constants.UTF8_ENCODE);
		byte[] kDate = getHmacSHA256Content(dateStamp, kSecret);
		byte[] kRegion = getHmacSHA256Content(regionName, kDate);
		byte[] kService = getHmacSHA256Content(serviceName, kRegion);
		byte[] kSigning = getHmacSHA256Content(AmazonS3Constants.AWS4_REQUEST, kService);
		return kSigning;
	}

	/**
	 * Method to generate the <code> byte[] </code> for the given data using
	 * HmacSHA256 algorithm.
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 */
	public byte[] getHmacSHA256Content(String data, byte[] key) throws NoSuchAlgorithmException, IllegalStateException,
	UnsupportedEncodingException, InvalidKeyException {
		String algorithm = AmazonS3Constants.ALGORITHM_HMACSHA256;
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data.getBytes(AmazonS3Constants.UTF8_ENCODE));
	}

	/**
	 * Method to convert <code>byte[]</code> to Hex String.
	 * 
	 * @param bytes
	 * @return
	 */
	public String convertBytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Methodto get the UTC date and time in the s3 date request
	 * format(YYYYMMDDTHHMMSSZ).
	 * 
	 * @return date and time in the format of YYYYMMDDTHHMMSSZ.
	 */
	public String getUTCDateAndTime() {
		TimeZone timeZone = TimeZone.getTimeZone(AmazonS3Constants.UTC_TIME_FORMAT);
		Calendar calendar = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AmazonS3Constants.S3_DATE_FORMAT, Locale.US);
		simpleDateFormat.setTimeZone(timeZone);
		String date = simpleDateFormat.format(calendar.getTime());
		String[] str = date.split(" ");
		return str[0] + "T" + str[1] + "Z";
	}

}
