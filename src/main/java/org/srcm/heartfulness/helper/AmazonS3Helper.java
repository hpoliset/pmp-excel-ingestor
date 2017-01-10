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
	
	public String computeCanonicalRequest(String hashedPayload, String fileName) {
		String canonicalQueryString = "";
		String canonicalURI = AmazonS3Constants.PATH_SEPARATER.trim() + fileName;
		String CanonicalHeaders = AmazonS3Constants.HOST_HEADER + AmazonS3Constants.COLON_HEADER_SEPARATER + host
				+ AmazonS3Constants.NEXT_LINE + AmazonS3Constants.SHA256_CONTENT_HEADER
				+ AmazonS3Constants.COLON_HEADER_SEPARATER + hashedPayload + AmazonS3Constants.NEXT_LINE
				+ AmazonS3Constants.DATE_HEADER + AmazonS3Constants.COLON_HEADER_SEPARATER + getUTCDateAndTime()
				+ AmazonS3Constants.NEXT_LINE;
		String canonicalRequest = httpmethod.trim() + AmazonS3Constants.NEXT_LINE + canonicalURI.trim()
				+ AmazonS3Constants.NEXT_LINE + canonicalQueryString + AmazonS3Constants.NEXT_LINE + CanonicalHeaders
				+ AmazonS3Constants.NEXT_LINE + signedheaders.trim() + AmazonS3Constants.NEXT_LINE + hashedPayload;
		return canonicalRequest;
	}

	public String computeHashedCanonicalRequest(String hashedPayload, String fileName,String canonicalRequest) {
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

	public String getStringToSign(String hashedPayload, String fileName, String hashedCanonicalRequest) {
		String dateAndTime = getUTCDateAndTime();
		String credentialScope = dateAndTime.split("T")[0].trim() + AmazonS3Constants.PATH_SEPARATER + region
				+ AmazonS3Constants.PATH_SEPARATER + service + AmazonS3Constants.PATH_SEPARATER
				+ AmazonS3Constants.AWS4_REQUEST;
		String requestDate = dateAndTime;
		String stringToSign = algorithm + AmazonS3Constants.NEXT_LINE + requestDate + AmazonS3Constants.NEXT_LINE
				+ credentialScope + AmazonS3Constants.NEXT_LINE + hashedCanonicalRequest;
		return stringToSign;
	}

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

	byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName)
			throws NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, InvalidKeyException {
		byte[] kSecret = (AmazonS3Constants.AWS_SIGNATURE_VERSION + key).getBytes(AmazonS3Constants.UTF8_ENCODE);
		byte[] kDate = HmacSHA256(dateStamp, kSecret);
		byte[] kRegion = HmacSHA256(regionName, kDate);
		byte[] kService = HmacSHA256(serviceName, kRegion);
		byte[] kSigning = HmacSHA256(AmazonS3Constants.AWS4_REQUEST, kService);
		return kSigning;
	}

	public byte[] HmacSHA256(String data, byte[] key) throws NoSuchAlgorithmException, IllegalStateException,
			UnsupportedEncodingException, InvalidKeyException {
		String algorithm = AmazonS3Constants.ALGORITHM_HMACSHA256;
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data.getBytes(AmazonS3Constants.UTF8_ENCODE));
	}

	public String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

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
