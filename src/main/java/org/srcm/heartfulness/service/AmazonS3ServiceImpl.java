package org.srcm.heartfulness.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.helper.AmazonS3Helper;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramPermissionLetterdetails;
import org.srcm.heartfulness.model.SessionImageDetails;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.AmazonS3RestTemplate;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

/**
 * Service Implementation class for upload and download images from AWS S3.
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
	public ResponseEntity<List<Response>> uploadObjectInAWSAndUpdateEvent(String eventId,
			MultipartFile[] multipartFiles, PMPAPIAccessLog accessLog) {
		Program program = programRepository.findByAutoGeneratedEventId(eventId);
		List<Response> listOfResponse = new ArrayList<Response>();
		for (MultipartFile multipartFile : multipartFiles) {
			Response response = null;
			try {
				String permissionLetterPath = program.getAutoGeneratedEventId() + ExpressionConstants.PATH_SEPARATER
						+ multipartFile.getOriginalFilename();

				calculateSignatureAndUploadObjectToAWS(multipartFile, permissionLetterPath);

				ProgramPermissionLetterdetails programPermissionLetterdetails = new ProgramPermissionLetterdetails(
						program.getProgramId(), multipartFile.getOriginalFilename(), permissionLetterPath,
						accessLog.getUsername());
				programRepository.saveProgramPermissionLetterDetails(programPermissionLetterdetails);

				response = new Response(ErrorConstants.STATUS_SUCCESS, "Successfully uploaded "
						+ multipartFile.getOriginalFilename());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog
						.setResponseBody(StackTraceUtils.convertPojoToJson(StackTraceUtils.convertPojoToJson(response)));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (HttpClientErrorException cee) {
				LOGGER.error("HttpClientErrorException occured while uploading file. Exception : {}", cee);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(cee));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException ex) {
				LOGGER.error(
						"Exception occured while uploading file. Problem while generating signature.  Exception : {}",
						ex);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (IOException ioe) {
				LOGGER.error("IO Exception occured while uploading file. Exception : {}", ioe);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (Exception ex) {
				LOGGER.error("Exception occured while uploading file. Exception : {}", ex);
				response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server Error : Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			}
		}
		return new ResponseEntity<List<Response>>(listOfResponse, HttpStatus.OK);

	}

	/**
	 * Method to calculate the signature for the given object and upload the
	 * given object to AWS with the calculated signature.
	 * 
	 * @param multipartFile
	 * @param permissionLetterPath
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalStateException
	 * @throws HttpClientErrorException
	 */
	private void calculateSignatureAndUploadObjectToAWS(MultipartFile multipartFile, String permissionLetterPath)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
			HttpClientErrorException {
		// Calculate payload sha1Hash
		String hashedPayload = amazonS3Helper.computeHashedRequestPayload(multipartFile);
		// calculate the signature
		String signature = calculateAWSAuthorizationSignature(multipartFile, hashedPayload, permissionLetterPath);
		// upload file to aws
		ResponseEntity<Object> response = amazonS3Interface.uploadObjectToAWS(multipartFile.getBytes(), signature,
				hashedPayload, permissionLetterPath);
		LOGGER.info("Response------------" + response);
	}

	/**
	 * Method to calculate the signature to validate the object in AWS.
	 * 
	 * @param multipartFile
	 * @param hashedPayload
	 * @param permissionLetterPath
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 */
	private String calculateAWSAuthorizationSignature(MultipartFile multipartFile, String hashedPayload,
			String permissionLetterPath) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
			UnsupportedEncodingException {

		// Create canonical request
		String canonicalRequest = amazonS3Helper.computeCanonicalRequest(hashedPayload, permissionLetterPath);

		// Create Hash of canonical request
		String hashedCanonicalRequest = amazonS3Helper.computeHashedCanonicalRequest(canonicalRequest);

		// Create string to sign
		String stringToSign = amazonS3Helper.getStringToSign(hashedCanonicalRequest);

		// Create signing key
		byte[] singingKey = amazonS3Helper.computeSigningKey();

		// Create signature
		byte[] byeSignatureForm = amazonS3Helper.getHmacSHA256Content(stringToSign, singingKey);
		return amazonS3Helper.convertBytesToHexString(byeSignatureForm);
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
	public ResponseEntity<Map<String, String>> createPresignedURL(String eventId,PMPAPIAccessLog accessLog) {
		Map<String, String> response = new HashMap<String, String>();
		Program program = programRepository.findByAutoGeneratedEventId(eventId);
		List<ProgramPermissionLetterdetails> listOfPermissionLetters = programRepository
				.getListOfPermissionLetters(program.getProgramId());
		for (ProgramPermissionLetterdetails programPermissionLetter : listOfPermissionLetters) {
			try {
				String presignedURL = amazonS3Interface.generatePresignedUrl(program.getAutoGeneratedEventId()+ExpressionConstants.PATH_SEPARATER+programPermissionLetter.getPermissionLetterName());
				response.put(programPermissionLetter.getPermissionLetterName(), presignedURL);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog
						.setResponseBody(StackTraceUtils.convertPojoToJson(StackTraceUtils.convertPojoToJson(response)));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			} catch (AmazonServiceException ase) {
				LOGGER.error("Caught an AmazonServiceException, which " + "means your request made it "
						+ "to Amazon S3, but was rejected with an error response" + " for some reason. Exception : {}",
						ase);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ase));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				response.put(programPermissionLetter.getPermissionLetterName(),
						"Failed to fetch image path for  " + programPermissionLetter.getPermissionLetterName());
			} catch (AmazonClientException ace) {
				LOGGER.error("Caught an AmazonClientException, which " + "means the client encountered "
						+ "an internal error while trying to " + "communicate with S3, "
						+ "such as not being able to access the network.Exception : {}", ace);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ace));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				response.put(programPermissionLetter.getPermissionLetterName(),
						"Failed to fetch image path for  " + programPermissionLetter.getPermissionLetterName());
			}
		}
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
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
		Program program = programRepository.findByAutoGeneratedEventId(eventId);
		int sessionDetailsId = sessionDetailsService.getSessionDetailsIdBySessionIdandProgramId(sessionId,
				program.getProgramId());
		List<Response> listOfResponse = new ArrayList<Response>();
		for (MultipartFile multipartFile : multipartFiles) {
			Response response = null;
			try {
				String sessionFilesPath = program.getAutoGeneratedEventId() + ExpressionConstants.PATH_SEPARATER + sessionId + ExpressionConstants.PATH_SEPARATER
						+ multipartFile.getOriginalFilename();

				// Calculate payload sha1Hash
				String hashedPayload = amazonS3Helper.computeHashedRequestPayload(multipartFile);

				// calculate the signature
				String signature = calculateAWSAuthorizationSignature(multipartFile, hashedPayload, sessionFilesPath);

				// upload file to aws
				System.out.println(amazonS3Interface.uploadObjectToAWS(multipartFile.getBytes(), signature,
						hashedPayload, sessionFilesPath));

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
			} catch (HttpClientErrorException cee) {
				LOGGER.error("HttpClientErrorException occured while uploading file. Exception : {}", cee);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(cee));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException ex) {
				LOGGER.error(
						"Exception occured while uploading file. Problem while generating signature.  Exception : {}",
						ex);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (IOException ioe) {
				LOGGER.error("IO Exception occured while uploading file. Exception : {}", ioe);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ioe));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				listOfResponse.add(response);
			} catch (Exception e) {
				LOGGER.error("Exception occured while uploading file. Exception : {}", e);
				response = new Response(ErrorConstants.STATUS_FAILED, "Failed to upload "
						+ multipartFile.getOriginalFilename());
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
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ase));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			} catch (AmazonClientException ace) {
				LOGGER.error("Caught an AmazonClientException, which " + "means the client encountered "
						+ "an internal error while trying to " + "communicate with S3, "
						+ "such as not being able to access the network.Exception : {}", ace);
				response.put(sessionImage.getImageName(), "Failed to fetch image path ");
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ace));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			}
		}
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}

}
