package org.srcm.heartfulness.rest.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
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
public class AmazonS3Interface {

	@Autowired
	ProxyHelper proxyHelper;

	private String accesskeyid;

	private String secretkey;

	private String bucketname;

	private long urlexpirytime;

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
		System.out.println(file.getAbsolutePath());
		System.out.println(file.getName());*/
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

}
