package org.srcm.heartfulness.rest.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.AmazonS3Constants;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.proxy.ProxyHelper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * 
 * @author himasreev
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.aws.s3.properties", ignoreUnknownFields = true, prefix = "aws.s3")
public class AmazonS3RestTemplate extends RestTemplate{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3RestTemplate.class);
	
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
	public void uploadObjectInAWS(MultipartFile multipartFile, String fileDestinationPath) throws IOException,
			AmazonServiceException, AmazonClientException {
		AWSCredentials credentials = new BasicAWSCredentials(accesskeyid, secretkey);
		ClientConfiguration config = proxyHelper.setProxyToAWSS3();
		AmazonS3 s3client = null;
		if (null != config) {
			s3client = new AmazonS3Client(credentials, config);
		} else {
			s3client = new AmazonS3Client(credentials);
		}

		// File file =
		// Files.write(Paths.get(multipartFile.getOriginalFilename()),
		// multipartFile.getBytes()).toFile();

	/*	File file = new File(multipartFile.getOriginalFilename());
		multipartFile.transferTo(file);
		LOGGER.info(file.getAbsolutePath());
		LOGGER.info(file.getName());*/
		// uploading file into S3 bucket
		InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
		s3client.putObject(new PutObjectRequest(bucketname, fileDestinationPath,inputStream,new ObjectMetadata())
				.withCannedAcl(CannedAccessControlList.Private));
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
	
	
	public void upload(MultipartFile multipartFile, String originalFilename, String signature, String hashedPayload) throws IOException {
		//setProxy();
		//String binaryFileContent = toBinary(multipartFile);
		String fullDateAndTime = getUTCDateAndTime();
		String URL=AmazonS3Constants.URI_PROTOCOL+host+AmazonS3Constants.PATH_SEPARATER+originalFilename;
		String authorization=AmazonS3Constants.ALGORITHM_TO_CALCULATE_SIGNATURE+" "+AmazonS3Constants.AWS_AUTHORIZATION_CREDENTIAL+"="
		+accesskeyid+AmazonS3Constants.PATH_SEPARATER+fullDateAndTime.split("T")[0]
				+AmazonS3Constants.PATH_SEPARATER+region+AmazonS3Constants.PATH_SEPARATER+service
				+AmazonS3Constants.PATH_SEPARATER+AmazonS3Constants.AWS4_REQUEST+","
				+AmazonS3Constants.AWS_AUTHORIZATION_SIGNEDHEADERS+"="+signedheaders+","+AmazonS3Constants.AWS_AUTHORIZATION_SIGNATURE+"="+signature;
		LOGGER.info("authorization : "+authorization);
		HttpHeaders httpHeaders = new HttpHeaders();
		//httpHeaders.add(RestTemplateConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(RestTemplateConstants.AUTHORIZATION, authorization);
		httpHeaders.set(AmazonS3Constants.DATE_HEADER, fullDateAndTime);
		httpHeaders.set(AmazonS3Constants.HOST_HEADER, host);
		httpHeaders.set(AmazonS3Constants.SHA256_CONTENT_HEADER, hashedPayload);
		InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
		HttpEntity<?> httpEntity = new HttpEntity<Object>(inputStream, httpHeaders);
		ResponseEntity<Object> response = this.exchange(URL, org.springframework.http.HttpMethod.PUT, httpEntity,Object.class);
		LOGGER.info("--------------------completed------------------------"+response);
	
		
	}
	
	
	/**
	 * Method to set the proxy (development use only)
	 */
	private void setProxy() {

	/*	CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials("koustavd", "123Welcome1"));
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.useSystemProperties();
		clientBuilder.setProxy(new HttpHost("10.1.28.12", 8080));
		clientBuilder.setDefaultCredentialsProvider(credsProvider);
		clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		CloseableHttpClient client = clientBuilder.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(client);
		this.setRequestFactory(factory);*/

	}
	
	String getUTCDateAndTime(){

		TimeZone timeZone = TimeZone.getTimeZone(AmazonS3Constants.UTC_TIME_FORMAT);
		Calendar calendar = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = 
				new SimpleDateFormat(AmazonS3Constants.S3_DATE_FORMAT, Locale.US);
		simpleDateFormat.setTimeZone(timeZone);
		String date=  simpleDateFormat.format(calendar.getTime());
		//date.replace('9' , 'T');
		String[] str=date.split(" ");
		String str2=str[0]+"T"+str[1]+"Z";
		return str2;
	}
	
	String toBinary( byte[] bytes )
	{
	    StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
	    for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
	        sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    return sb.toString();
	}

	
	
}
