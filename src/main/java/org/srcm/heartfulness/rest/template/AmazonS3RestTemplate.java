package org.srcm.heartfulness.rest.template;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.constants.AmazonS3Constants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.helper.AmazonS3Helper;
import org.srcm.heartfulness.proxy.ProxyHelper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

/**
 * Template class to communicate with AWS S3 and upload and download object
 * to/from AWS S3.
 * 
 * @author himasreev
 *
 */
@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties(locations = "classpath:prod.aws.s3.properties", ignoreUnknownFields = true, prefix = "aws.s3")
public class AmazonS3RestTemplate extends RestTemplate {

	@Autowired
	AmazonS3Helper amazonS3Helper;

	@Autowired
	ProxyHelper proxyHelper;

	private String accesskeyid;

	private String secretkey;

	private String bucketname;

	private long urlexpirytime;

	private String signedheaders;

	private String region;

	private String service;

	private String host;
	
	@Value("${proxy}")
	private boolean proxy;

	@Value("${proxyHost}")
	private String proxyHost;

	@Value("${proxyPort}")
	private int proxyPort;

	@Value("${proxyUser}")
	private String proxyUser;

	@Value("${proxyPassword}")
	private String proxyPassword;

	public String getAccesskeyid() {
		return accesskeyid;
	}

	public void setAccesskeyid(String accesskeyid) {
		this.accesskeyid = accesskeyid;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public String getBucketname() {
		return bucketname;
	}

	public void setBucketname(String bucketname) {
		this.bucketname = bucketname;
	}

	public long getUrlexpirytime() {
		return urlexpirytime;
	}

	public void setUrlexpirytime(long urlexpirytime) {
		this.urlexpirytime = urlexpirytime;
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
	 * Method to upload the provided image to AWS S3 server.
	 * 
	 * @param multipartFile
	 * @param fileDestinationPath
	 * @throws IOException
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 */
	public ResponseEntity<Object> uploadObjectToAWS(byte[] objectBinaryContent, String signature, String hashedPayload,
			String objectPath) throws HttpClientErrorException {
		setProxy();
		String fullDateAndTime = amazonS3Helper.getUTCDateAndTime();
		String URL = AmazonS3Constants.URI_PROTOCOL + host + ExpressionConstants.PATH_SEPARATER + objectPath;
		String authorization = AmazonS3Constants.ALGORITHM_TO_CALCULATE_SIGNATURE + ExpressionConstants.SPACE_SEPARATER
				+ AmazonS3Constants.AWS_AUTHORIZATION_CREDENTIAL + accesskeyid + ExpressionConstants.PATH_SEPARATER
				+ fullDateAndTime.split("T")[0] + ExpressionConstants.PATH_SEPARATER + region
				+ ExpressionConstants.PATH_SEPARATER + service + ExpressionConstants.PATH_SEPARATER
				+ AmazonS3Constants.AWS4_REQUEST + ExpressionConstants.COMMA_SEPARATER
				+ AmazonS3Constants.AWS_AUTHORIZATION_SIGNEDHEADERS + signedheaders + ExpressionConstants.COMMA_SEPARATER
				+ AmazonS3Constants.AWS_AUTHORIZATION_SIGNATURE + signature;
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, authorization);
		httpHeaders.set(AmazonS3Constants.DATE_HEADER, fullDateAndTime);
		httpHeaders.set(AmazonS3Constants.HOST_HEADER, host);
		httpHeaders.set(AmazonS3Constants.SHA256_CONTENT_HEADER, hashedPayload);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(objectBinaryContent, httpHeaders);
		ResponseEntity<Object> response = this.exchange(URL, org.springframework.http.HttpMethod.PUT, httpEntity,
				Object.class);
		return response;
	}

	/**
	 * Method to generate the presigned URL to the given image.
	 * 
	 * @param fileName
	 * @return
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 */
	public String generatePresignedUrl(String fileName) throws AmazonServiceException, AmazonClientException {
		AWSCredentials credentials = new BasicAWSCredentials(accesskeyid, secretkey);
		ClientConfiguration config = proxyHelper.setProxyToAWSS3();
		AmazonS3 s3client = null;
		if (null != config) {
			s3client = new AmazonS3Client(credentials, config);
		} else {
			s3client = new AmazonS3Client(credentials);
		}
		java.util.Date expiration = new java.util.Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += urlexpirytime;
		expiration.setTime(milliSeconds);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketname, fileName);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		generatePresignedUrlRequest.setExpiration(expiration);
		return s3client.generatePresignedUrl(generatePresignedUrlRequest).toString();
	}
	
	
	/**
	 * Method to set the proxy (development use only)
	 */
	public void setProxy() {
		if (proxy) {
		/*	CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					new UsernamePasswordCredentials(proxyUser, proxyPassword));
			HttpClientBuilder clientBuilder = HttpClientBuilder.create();
			clientBuilder.useSystemProperties();
			clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort));
			clientBuilder.setDefaultCredentialsProvider(credsProvider);
			clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
			CloseableHttpClient client = clientBuilder.build();
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setHttpClient(client);
			this.setRequestFactory(factory);*/

		}

	}

}
