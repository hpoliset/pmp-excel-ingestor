package org.srcm.heartfulness.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.helper.AmazonS3Helper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.SessionImageDetails;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.AmazonS3RestTemplate;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3ServiceImpl.class);

	@Autowired
	AmazonS3RestTemplate amazonS3Interface;

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	SessionDetailsService sessionDetailsService;

	@Autowired
	AmazonS3Helper amazonS3Helper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.AmazonS3Service#uploadObjectInAWSAndUpdateEvent
	 * (java.lang.String, org.springframework.web.multipart.MultipartFile,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog)
	 */
	@Override
	public ResponseEntity<Response> uploadObjectInAWSAndUpdateEvent(String eventId, MultipartFile multipartFile,
			PMPAPIAccessLog accessLog) {
		Response response = null;
		try {
			Program program = programRepository.findByAutoGeneratedEventId(eventId);
			String permissionLetterPath = program.getAutoGeneratedEventId() + "/" + multipartFile.getOriginalFilename();

			// Calculate payload sha1Hash
			String hashedPayload = amazonS3Helper.computeHashedRequestPayload(multipartFile);
			LOGGER.info("------------------------------------------------------------------");
			LOGGER.info("Hex Encode of Requestpayload : " + hashedPayload);

			// calculate the signature
			String signature = calculateAWSAuthorizationSignature(multipartFile, hashedPayload,permissionLetterPath);

			// upload file to aws
			System.out.println(amazonS3Interface.upload(multipartFile.getBytes(), signature, hashedPayload,
					permissionLetterPath));

			program.setCoordinatorPermissionLetterPath(permissionLetterPath);
			programRepository.saveProgram(program);
			response = new Response(ErrorConstants.STATUS_SUCCESS, "File Uploaded Successfully");
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(StackTraceUtils.convertPojoToJson(response)));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		} catch (AmazonServiceException ase) {
			LOGGER.error("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason. Exception : {}", ase);
			response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
					+ multipartFile.getOriginalFilename());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ase));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (AmazonClientException ace) {
			LOGGER.error("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.Exception : {}", ace);
			response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
					+ multipartFile.getOriginalFilename());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ace));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (IOException ioe) {
			LOGGER.error("IO Exception occured while uploading file. Exception : {}", ioe);
			response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
					+ multipartFile.getOriginalFilename());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error("Exception occured while uploading file. Exception : {}", e);
			response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
					+ multipartFile.getOriginalFilename());
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	private String calculateAWSAuthorizationSignature(MultipartFile multipartFile, String hashedPayload, String permissionLetterPath)
			throws Exception {

		// Create canonical request
		String hashedCanonicalRequest = amazonS3Helper.computeHashedCanonicalRequest(hashedPayload,
				permissionLetterPath);

		// Create string to sign
		String stringToSign = amazonS3Helper.getStringToSign(hashedPayload, multipartFile.getOriginalFilename(),
				hashedCanonicalRequest);
		LOGGER.info("------------------------------------------------------------------");
		LOGGER.info("stringToSign : " + stringToSign);

		// Create signing key
		byte[] singingKey = amazonS3Helper.computeSigningKey();
		LOGGER.info("------------------------------------------------------------------");
		LOGGER.info("singingKey : " + singingKey);

		// Create signature
		byte[] byeSignatureForm = amazonS3Helper.HmacSHA256(stringToSign, singingKey);
		String signature = amazonS3Helper.bytesToHexString(byeSignatureForm);
		LOGGER.info("------------------------------------------------------------------");
		LOGGER.info("signature : " + signature + "--------------" + signature.length());
		LOGGER.info("------------------------------------------------------------------");

		return signature;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.AmazonS3Service#createPresignedURL(java
	 * .lang.String, java.lang.String,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog)
	 */
	@Override
	public ResponseEntity<Response> createPresignedURL(String eventId, String fileName, PMPAPIAccessLog accessLog) {
		Response response = null;
		try {
			Program program = programRepository.findByAutoGeneratedEventId(eventId);
			String[] filePath = program.getCoordinatorPermissionLetterPath().split("/");
			if (filePath[1].equalsIgnoreCase(fileName)) {
				String presignedURL = amazonS3Interface.generatePresignedUrl(program
						.getCoordinatorPermissionLetterPath());
				response = new Response(ErrorConstants.STATUS_SUCCESS, presignedURL);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog
						.setResponseBody(StackTraceUtils.convertPojoToJson(StackTraceUtils.convertPojoToJson(response)));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<Response>(response, HttpStatus.OK);
			} else {
				response = new Response(ErrorConstants.STATUS_FAILED, "Invalid File Name");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson("File Name mismatch"));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return new ResponseEntity<Response>(response, HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (AmazonServiceException ase) {
			LOGGER.error("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason. Exception : {}", ase);
			response = new Response(ErrorConstants.STATUS_FAILED, "Failed to fetch image path for  " + fileName);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ase));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (AmazonClientException ace) {
			LOGGER.error("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.Exception : {}", ace);
			response = new Response(ErrorConstants.STATUS_FAILED, "Failed to fetch image path for  " + fileName);
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ace));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.AmazonS3Service#
	 * uploadListOfObjectsInAWSForSession(java.lang.String, java.lang.String,
	 * org.springframework.web.multipart.MultipartFile[],
	 * org.srcm.heartfulness.model.PMPAPIAccessLog)
	 */
	@Override
	public ResponseEntity<List<Response>> uploadListOfObjectsInAWSForSession(String eventId, String sessionId,
			MultipartFile[] multipartFiles, PMPAPIAccessLog accessLog) {
		List<Response> listOfResponse = new ArrayList<Response>();
		for (MultipartFile multipartFile : multipartFiles) {
			Response response = null;
			try {
				Program program = programRepository.findByAutoGeneratedEventId(eventId);
				int sessionDetailsId = sessionDetailsService.getSessionDetailsIdBySessionIdandProgramId(sessionId,
						program.getProgramId());
				String sessionFilesPath = program.getAutoGeneratedEventId() + "/" + sessionId + "/"
						+ multipartFile.getOriginalFilename();

				// Calculate payload sha1Hash
				String hashedPayload = amazonS3Helper.computeHashedRequestPayload(multipartFile);
				LOGGER.info("------------------------------------------------------------------");
				LOGGER.info("Hex Encode of Requestpayload : " + hashedPayload);

				// calculate the signature
				String signature = calculateAWSAuthorizationSignature(multipartFile, hashedPayload,sessionFilesPath);

				// upload file to aws
				System.out.println(amazonS3Interface.upload(multipartFile.getBytes(), signature, hashedPayload,
						sessionFilesPath));

				// amazonS3Interface.uploadObjectInAWS(multipartFile,
				// sessionFilesPath);

				SessionImageDetails sessionFiles = new SessionImageDetails(sessionDetailsId,
						multipartFile.getOriginalFilename(), sessionFilesPath, accessLog.getUsername());
				sessionDetailsService.saveSessionFiles(sessionFiles);
				response = new Response(ErrorConstants.STATUS_SUCCESS, multipartFile.getOriginalFilename()
						+ " uploaded successfully");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog
						.setResponseBody(StackTraceUtils.convertPojoToJson(StackTraceUtils.convertPojoToJson(response)));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (AmazonServiceException ase) {
				LOGGER.error("Caught an AmazonServiceException, which " + "means your request made it "
						+ "to Amazon S3, but was rejected with an error response" + " for some reason. Exception : {}",
						ase);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ase));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (AmazonClientException ace) {
				LOGGER.error("Caught an AmazonClientException, which " + "means the client encountered "
						+ "an internal error while trying to " + "communicate with S3, "
						+ "such as not being able to access the network.Exception : {}", ace);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ace));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (IOException ioe) {
				LOGGER.error("IO Exception occured while uploading file. Exception : {}", ioe);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (Exception e) {
				LOGGER.error("Exception occured while uploading file. Exception : {}", e);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			}
		}
		return new ResponseEntity<List<Response>>(listOfResponse, HttpStatus.OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.AmazonS3Service#
	 * createPresignedURLForSessionImages(java.lang.String, java.lang.String,
	 * org.srcm.heartfulness.model.PMPAPIAccessLog)
	 */
	@Override
	public ResponseEntity<?> createPresignedURLForSessionImages(String eventId, String sessionId,
			PMPAPIAccessLog accessLog) {
		Map<String, String> response = new HashMap<String, String>();
		Program program = programRepository.findByAutoGeneratedEventId(eventId);
		int sessionDetailsId = sessionDetailsService.getSessionDetailsIdBySessionIdandProgramId(sessionId,
				program.getProgramId());
		List<SessionImageDetails> listOfImages = sessionDetailsService.getListOfSessionImages(sessionDetailsId);
		for (SessionImageDetails sessionImage : listOfImages) {
			try {
				String presignedURL = amazonS3Interface.generatePresignedUrl(sessionImage.getImagePath());
				response.put(sessionImage.getImageName(), presignedURL);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog
						.setResponseBody(StackTraceUtils.convertPojoToJson(StackTraceUtils.convertPojoToJson(response)));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);

			} catch (AmazonServiceException ase) {
				LOGGER.error("Caught an AmazonServiceException, which " + "means your request made it "
						+ "to Amazon S3, but was rejected with an error response" + " for some reason. Exception : {}",
						ase);
				response.put(sessionImage.getImageName(), "Failed to fetch image path ");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ase));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			} catch (AmazonClientException ace) {
				LOGGER.error("Caught an AmazonClientException, which " + "means the client encountered "
						+ "an internal error while trying to " + "communicate with S3, "
						+ "such as not being able to access the network.Exception : {}", ace);
				response.put(sessionImage.getImageName(), "Failed to fetch image path ");
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ace));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			}
		}
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> uploadFileToAWS(MultipartFile multipartFile) {

		try {
			// Create payload sha1Hash
			String hashedPayload = amazonS3Helper.computeHashedRequestPayload(multipartFile);
			LOGGER.info("------------------------------------------------------------------");
			LOGGER.info("Hex Encode of Requestpayload : " + hashedPayload);

			// Create canonical request
			String hashedCanonicalRequest = amazonS3Helper.computeHashedCanonicalRequest(hashedPayload,
					multipartFile.getOriginalFilename());

			// Create string to sign
			String stringToSign = amazonS3Helper.getStringToSign(hashedPayload, multipartFile.getOriginalFilename(),
					hashedCanonicalRequest);
			LOGGER.info("------------------------------------------------------------------");
			LOGGER.info("stringToSign : " + stringToSign);

			// Create signing key
			byte[] singingKey = amazonS3Helper.computeSigningKey();
			LOGGER.info("------------------------------------------------------------------");
			LOGGER.info("singingKey : " + singingKey);

			// create signature
			byte[] byeSignatureForm = amazonS3Helper.HmacSHA256(stringToSign, singingKey);
			String signature = amazonS3Helper.bytesToHexString(byeSignatureForm);
			LOGGER.info("------------------------------------------------------------------");
			LOGGER.info("signature : " + signature + "--------------" + signature.length());
			LOGGER.info("------------------------------------------------------------------");

			// call rest template
			amazonS3Interface.upload(multipartFile.getBytes(), signature,hashedPayload, multipartFile.getOriginalFilename());

			return new ResponseEntity<String>("File Uploaded successfully", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("Exception : " + StackTraceUtils.convertStackTracetoString(e));
			return new ResponseEntity<String>("Failed to upload file.", HttpStatus.OK);
		}
	}

}
