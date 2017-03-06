package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.mail.CoordinatorAccessControlMail;
import org.srcm.heartfulness.model.CoordinatorAccessControlEmail;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.model.SecondaryCoordinatorRequest;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.AbhyasiResult;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlErrorResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlResponse;
import org.srcm.heartfulness.model.json.response.CoordinatorAccessControlSuccessResponse;
import org.srcm.heartfulness.repository.CoordinatorAccessControlRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.repository.UserRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class CoordinatorAccessControlServiceImpl implements CoordinatorAccessControlService {

	private static Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessControlServiceImpl.class);

	@Autowired
	CoordinatorAccessControlRepository coordntrAccssCntrlRepo;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	private CoordinatorAccessControlMail coordinatorAccessControlMail;

	/**
	 * This method is used to insert Secondary coordinator details in database.
	 * 
	 * @param autoGeneratedEventId
	 *            to get the program Id for a particular program.
	 * @param userEmail
	 *            email of the requested person i.e who is raising this request
	 *            to access other than his own event.
	 * @param accessLog
	 *            to register the log details into our database.
	 * @return CoordinatorAccessControlResponse If the request is successfull,
	 *         CoordinatorAccessControlSuccessResponse is returned else
	 *         CoordinatorAccessControlErrorResponse is returned.
	 * 
	 */
	@Override
	public CoordinatorAccessControlResponse addSecondaryCoordinatorRequest(String autoGeneratedEventId,
			String userEmail, PMPAPIAccessLog accessLog) {

		Program program;
		try {
			program = coordntrAccssCntrlRepo.getProgramIdByEventId(autoGeneratedEventId);
		} catch (Exception daex) {

			LOGGER.error("DataAccess problem while fetching program details {}", daex);
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_EVENT_ID);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(daex));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

		User user;
		try {
			user = coordntrAccssCntrlRepo.getUserbyUserEmail(userEmail);
		} catch (Exception ex) {
			LOGGER.error("Exception while fetching user details {}", ex);
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_USER_ID);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

		// Coordinator cannot raise request for his own event
		if (program.getCoordinatorEmail().equalsIgnoreCase(userEmail)) {
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.REQUESTER_INVALID_SELF_REQUEST);
			accessLog.setErrorMessage(CoordinatorAccessControlConstants.REQUESTER_INVALID_SELF_REQUEST);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

		// preceptor cannot raise request for his own event
		ProgramCoordinators pgrmCoodDetails = null;
		try {
			pgrmCoodDetails = coordntrAccssCntrlRepo.getProgramCoordinatorByProgramId(program.getProgramId());
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch preceptor for event " + autoGeneratedEventId);
		}

		if (null != pgrmCoodDetails && null != pgrmCoodDetails.getEmail()) {
			if (pgrmCoodDetails.getEmail().equalsIgnoreCase(userEmail)) {
				CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
						ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.REQUESTER_INVALID_SELF_REQUEST);
				accessLog.setErrorMessage(CoordinatorAccessControlConstants.REQUESTER_INVALID_SELF_REQUEST);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return cacEResponse;
			}
		} else {
			LOGGER.info("No preceptor email available for event " + autoGeneratedEventId);
		}

		// Check if the request is already raised by the same user
		int reqAlreadyRaised = -1;
		try {
			reqAlreadyRaised = coordntrAccssCntrlRepo.checkRequestAlreadyRaised(program.getProgramId(), userEmail);
		} catch (DataAccessException daex) {
			LOGGER.error("DataAccess problem while checking request already raised {}", daex);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(daex));
		} catch (Exception ex) {
			LOGGER.error("Exception while checking request already raised {}", ex);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		// Check if the request is already approved for the same user
		if (reqAlreadyRaised > 0) {
			LOGGER.error("User " + userEmail + " request is pending for approval for the event id "
					+ autoGeneratedEventId);
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED,
					CoordinatorAccessControlConstants.REQUESTER_REQUEST_WAITING_FOR_APPROVAL);
			accessLog.setErrorMessage("request waiting for approval for user " + userEmail + " for Event id "
					+ autoGeneratedEventId);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

		int reqAlreadyApproved = -1;
		try {
			reqAlreadyApproved = coordntrAccssCntrlRepo.checkRequestAlreadyApproved(program.getProgramId(), userEmail);
		} catch (DataAccessException daex) {
			LOGGER.error("DataAccess problem while checking request already approved {}", daex);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(daex));
		} catch (Exception ex) {
			LOGGER.error("Exception while checking request already approved {}", ex);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		if (reqAlreadyApproved > 0) {
			LOGGER.error("Request already approved for user " + userEmail + " for event " + autoGeneratedEventId);
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.REQUESTER_REQUEST_ALREADY_APPROVED);
			accessLog.setErrorMessage("Request already approved for user " + userEmail + " for event "
					+ autoGeneratedEventId);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

		if (reqAlreadyRaised == 0) {

			SecondaryCoordinatorRequest scReq = new SecondaryCoordinatorRequest();
			scReq.setProgramId(program.getProgramId());
			scReq.setUserId(user.getId());
			scReq.setRequestedBy(userEmail);
			scReq.setStatus(CoordinatorAccessControlConstants.REQUEST_DEFAULT_STATUS);
			try {
				coordntrAccssCntrlRepo.saveSecondaryCoordinatorRequest(scReq);

				CoordinatorAccessControlSuccessResponse cacSResponse = new CoordinatorAccessControlSuccessResponse(
						ErrorConstants.STATUS_SUCCESS, CoordinatorAccessControlConstants.REQUESTER_SUCCESSFULL_REQUEST);
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage("");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacSResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return cacSResponse;

			} catch (DataAccessException daex) {
				LOGGER.error("Failed to save Secondary Coordinator Request", daex);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(daex));
			} catch (Exception ex) {
				LOGGER.error("Failed to save Secondary Coordinator Request", ex);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
			}

			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.REQUESTER_FAILED_REQUEST);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;

		} else {
			LOGGER.error("Error while checking request already raised for user " + userEmail + " for event "
					+ autoGeneratedEventId);
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}
	}

	/**
	 * This method is used to approve the request raised by secondary
	 * coordinators to access other's event.
	 * 
	 * @param approvedBy
	 *            email of the primary coordinator or the preceptor of that
	 *            event.
	 * @param pgrmCoordinators
	 *            Details of the secondary coordinators who wants to raise a
	 *            request to access other events
	 * @param accessLog
	 *            to update the PMP API access log data.
	 * @return CoordinatorAccessControlResponse, if the request is successfull,
	 *         CoordinatorAccessControlSuccessResponse is returned else
	 *         CoordinatorAccessControlErrorResponse is returned.
	 */
	@Override
	@Transactional
	public CoordinatorAccessControlResponse approveSecondaryCoordinatorRequest(String approvedBy,
			ProgramCoordinators pgrmCoordinators, PMPAPIAccessLog accessLog) {

		// Check if the requested user is available in PMP
		User user = null;
		try {
			user = coordntrAccssCntrlRepo.getUserbyUserEmail(pgrmCoordinators.getEmail());
		} catch (Exception ex) {
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.REQUESTER_INVALID_EMAIL_ADDRESS);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

		int approvedStatus = 0;
		try {
			approvedStatus = coordntrAccssCntrlRepo.approveSecondaryCoordinatorRequest(
					/* program.getProgramId() */pgrmCoordinators.getProgramId(), approvedBy,
					pgrmCoordinators.getEmail());
		} catch (DataAccessException daex) {
			LOGGER.error("Error while approving request {}", daex);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(daex));
		} catch (Exception ex) {
			LOGGER.error("Error while approving request {}", ex);
			accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
		}

		if (approvedStatus > 0) {

			ProgramCoordinators newPgrmCoordinator = new ProgramCoordinators();
			newPgrmCoordinator.setProgramId(pgrmCoordinators.getProgramId());
			newPgrmCoordinator.setUserId(user.getId());
			newPgrmCoordinator.setName(user.getName());
			newPgrmCoordinator.setEmail(pgrmCoordinators.getEmail());
			newPgrmCoordinator.setIsPrimaryCoordinator(0);
			newPgrmCoordinator.setIsPreceptor(0);
			try {

				coordntrAccssCntrlRepo.createProgramCoordinator(newPgrmCoordinator);

				CoordinatorAccessControlSuccessResponse cacSResponse = new CoordinatorAccessControlSuccessResponse(
						ErrorConstants.STATUS_SUCCESS, CoordinatorAccessControlConstants.APPROVER_SUCCESS_RESPONSE
								+ user.getName() // pgrmCoordinators.getName()
								+ " as a coordinator for your event " + pgrmCoordinators.getEventId());
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setErrorMessage("");
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacSResponse));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				return cacSResponse;

			} catch (Exception ex) {
				LOGGER.error("Failed to create program  coordinator EX ", ex);
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				if(coordntrAccssCntrlRepo.rollbackApprovedSecondaryCoordinatorRequest(pgrmCoordinators.getProgramId(),approvedBy,pgrmCoordinators.getEmail()) > 0){
					LOGGER.info("Rolled back request successfully for requester :"+pgrmCoordinators.getEmail() 
						+ " and approver :"+approvedBy);
				}else{
					LOGGER.info("Failed to roll back request for requester :"+pgrmCoordinators.getEmail() +" and approver :"+approvedBy);
				}
			} 

			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;

		} else {
			LOGGER.error("Failed to update request table for event Id " + pgrmCoordinators.getEventId());
			CoordinatorAccessControlErrorResponse cacEResponse = new CoordinatorAccessControlErrorResponse(
					ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(cacEResponse));
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return cacEResponse;
		}

	}

	/**
	 * This method is used to get the list of secondary coordinators who have
	 * raised a request to access other event details.
	 * 
	 * @param programIds, LinkedHashMap with programId and auto generated event 
	 * id's for the logged in user.
	 *            
	 * @param accessLog
	 *            to update the PMP API access log data.
	 * @return ResponseEntity<List<SecondaryCoordinatorRequest>> or an empty
	 *         array list if no request is raised for the event.
	 */
	@Override
	public ResponseEntity<?> getListOfSecondaryCoordinatorRequests(LinkedHashMap<Integer,String> programIds,
			PMPAPIAccessLog accessLog) {

		Object[] arrayOfProgramIds = programIds.keySet().toArray();
		StringBuilder programIdBuilder = new StringBuilder("");
		for(int i = 0; i < arrayOfProgramIds.length; i++){
			programIdBuilder.append( i != (programIds.size() -1 ) ? arrayOfProgramIds[i] + ",": arrayOfProgramIds[i]);
		}
		
		List<SecondaryCoordinatorRequest> listOfSecondaryCoordinators = coordntrAccssCntrlRepo.getListOfRequests(programIdBuilder);
		if(!listOfSecondaryCoordinators.isEmpty()){
			for(SecondaryCoordinatorRequest requests : listOfSecondaryCoordinators){
				requests.setAutoGeneratedEventId(null != programIds.get(requests.getProgramId()) ? programIds.get(requests.getProgramId()) : "");
				requests.setProgramId(0);
			}
		}
		accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLog.setErrorMessage("");
		accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(listOfSecondaryCoordinators));
		apiAccessLogService.updatePmpAPIAccessLog(accessLog);
		return new ResponseEntity<List<SecondaryCoordinatorRequest>>(listOfSecondaryCoordinators, HttpStatus.OK);
	}

	/**
	 * Method to validate the preceptor ID card number against MYSRCM and
	 * 
	 * If preceptor ID is valid, create user in HFN Backend and assign with role
	 * 'PRECEPTOR'. If invalid, returns appropriate error message.
	 * 
	 * @param program
	 * @param id
	 * @return
	 */
	@Override
	public String validatePreceptorIDCardNumberandCreateUser(Program program, int id, String source) {
		PMPAPIAccessLogDetails accessLogDetails = null;
		// if (program.getFirstSittingBy() == 0) {

		if (null == program.getPreceptorIdCardNumber() || program.getPreceptorIdCardNumber().isEmpty()) {
			return "Preceptor ID is required for the Event";
		} else {
			try {
				if (0 != id) {
					try {
						accessLogDetails = new PMPAPIAccessLogDetails(id, EndpointConstants.ABHYASI_INFO_URI,
								DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
								StackTraceUtils.convertPojoToJson("Request : " + program.getPreceptorIdCardNumber()
										+ " ,Event ID : " + program.getAutoGeneratedEventId()));
						int accessdetailsID = apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
						accessLogDetails.setId(accessdetailsID);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
				}
				AbhyasiResult result;
				result = srcmRestTemplate.getAbyasiProfile(program.getPreceptorIdCardNumber());
				if (result.getUserProfile().length > 0) {
					AbhyasiUserProfile userProfile = result.getUserProfile()[0];
					if (null != userProfile) {
						if (0 != userProfile.getId()) {

							// update program
							program.setAbyasiRefNo(program.getPreceptorIdCardNumber());
							program.setFirstSittingBy(userProfile.getId());
							program.setPreceptorName(userProfile.getName());
							// programRepository.updatePreceptorDetails(program);
							programRepository.save(program);

							if (null != userProfile.getEmail()) {
								// create user and assign role
								User user = new User();
								user.setAbyasiId(userProfile.getRef());
								user.setEmail(userProfile.getEmail());
								user.setFirst_name(userProfile.getFirst_name());
								user.setLast_name(userProfile.getLast_name());
								user.setName(userProfile.getName());
								user.setCity(userProfile.getCity());
								user.setGender(userProfile.getGender());
								user.setState((null != userProfile.getState()) ? userProfile.getState().getName()
										: null);
								user.setCountry((null != userProfile.getCountry()) ? userProfile.getCountry().getName()
										: null);
								user.setRole(PMPConstants.LOGIN_ROLE_PRECEPTOR);
								userRepository.save(user);

								// persist coordinator details
								ProgramCoordinators programCoordinators = new ProgramCoordinators(
										program.getProgramId(), user.getId(), user.getName(), user.getEmail(), 0);
								programCoordinators.setIsPreceptor(1);
								//programCoordinators.setIsPrimaryCoordinator((user.getEmail().equalsIgnoreCase(program.getCoordinatorEmail()) ? 1 : 0));
								coordntrAccssCntrlRepo.saveCoordinatorDetails(programCoordinators);

								if (PMPConstants.CREATED_SOURCE_EXCEL.equalsIgnoreCase(source)) {
									CoordinatorAccessControlEmail coordinator = new CoordinatorAccessControlEmail();
									coordinator.setPreceptorName(program.getPreceptorName());
									coordinator.setPreceptorEmailId(user.getEmail());
									coordinator.setCoordinatorEmail(program.getCoordinatorEmail());
									coordinator.setCoordinatorName(program.getCoordinatorName());
									coordinator.setEventID(program.getAutoGeneratedEventId());
									coordinator.setEventName(program.getProgramChannel());
									coordinator.setProgramId(String.valueOf(program.getProgramId()));
									coordinator.setEventPlace(program.getEventPlace());
									SimpleDateFormat inputsdf = new SimpleDateFormat(ExpressionConstants.SQL_DATE_FORMAT);
									coordinator.setProgramCreateDate(inputsdf.format(program.getProgramStartDate()));
									if (null == program.getCoordinatorEmail()
											|| program.getCoordinatorEmail().isEmpty()) {
										Runnable task = new Runnable() {
											@Override
											public void run() {
												try {
													coordinatorAccessControlMail.sendMailToPreceptorToUpdateCoordinatorEmailID(coordinator);
												} catch (Exception ex) {
													LOGGER.error("Error while sending email to the coordinator - {} ",program.getCoordinatorEmail());
												}
											}
										};
										new Thread(task, "ServiceThread").start();
									} else {
										// persist coordinator details
										ProgramCoordinators programpreceptor = new ProgramCoordinators(
												program.getProgramId(), 0, program.getCoordinatorName(),
												program.getCoordinatorEmail(), 1);
										saveCoordinatorDetails(programpreceptor);
										Runnable task = new Runnable() {
											@Override
											public void run() {
												try {
													coordinatorAccessControlMail.sendMailToCoordinatorWithLinktoCreateProfile(coordinator);
												} catch (Exception ex) {
													LOGGER.error("Error while sending email to the coordinator - {} ",program.getCoordinatorEmail());
												}
											}
										};
										new Thread(task, "ServiceThread").start();
									}
								}

							} else {
								return "Preceptor Email Id is not available for the provided preceptor Id.";
							}

							try {
								if (null != accessLogDetails) {
									accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(userProfile));
									accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
									accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
									apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
								}
							} catch (Exception e) {
								LOGGER.error("Exception while inserting PMP API log details in table : {} ",
										StackTraceUtils.convertPojoToJson(e));
							}
						} else {
							return "Invalid preceptor ID";
						}
					} else {
						try {
							if (null != accessLogDetails) {
								accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
								accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(result));
								accessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
								accessLogDetails.setErrorMessage("Invalid preceptor ID");
								apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
							}
						} catch (Exception e) {
							LOGGER.error("Exception while inserting PMP API log details in table : {} ",
									StackTraceUtils.convertPojoToJson(e));
						}
						return "Invalid preceptor ID";
					}
				} else {
					try {
						if (null != accessLogDetails) {
							accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
							accessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
							accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(result));
							accessLogDetails.setErrorMessage("Invalid preceptor ID");
							apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
						}
					} catch (Exception e) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
					return "Invalid preceptor ID";
				}
			} catch (HttpClientErrorException e) {
				LOGGER.error("Exception while fecthing abhyasi profile: HttpClientErrorException : {} ", e.getMessage());
				if (null != accessLogDetails) {
					try {
						accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
						accessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
						accessLogDetails.setResponseBody(StackTraceUtils
								.convertPojoToJson("Error while fetching abhyasi profile from MySRCM : "
										+ e.getMessage()));
						accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
						apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
					} catch (Exception ex) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(ex));
					}
				}
				return "Error while fetching abhyasi profile from MySRCM ";
			} catch (JsonParseException | JsonMappingException e) {
				LOGGER.error("Exception while fecthing abhyasi profile : JsonParseException : {} ", e.getMessage());
				if (null != accessLogDetails) {
					try {
						accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
						accessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
						accessLogDetails
								.setResponseBody(StackTraceUtils
										.convertPojoToJson("Error while fetching abhyasi profile from MySRCM : parsing exception "));
						accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
						apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
					} catch (Exception ex) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(ex));
					}
				}
				return "Error while fetching abhyasi profile from MySRCM : parsing exception ";
			} catch (IOException e) {
				LOGGER.error("Exception while fecthing abhyasi profile : {} ", e.getMessage());
				if (null != accessLogDetails) {
					try {
						accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
						accessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
						accessLogDetails
								.setResponseBody(StackTraceUtils
										.convertPojoToJson("Error while fetching abhyasi profile from MySRCM : parsing exception "));
						accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
						apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
					} catch (Exception ex) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(ex));
					}
				}
				e.printStackTrace();
				return "Error while fetching abhyasi profile from MySRCM : IO exception ";
			} catch (Exception e) {
				LOGGER.error("Exception while fecthing abhyasi profile : Exception : {} ", e.getMessage());
				e.printStackTrace();
				if (null != accessLogDetails) {
					try {
						accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
						accessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
						accessLogDetails
								.setResponseBody(StackTraceUtils
										.convertPojoToJson("Error while fetching abhyasi profile from MySRCM : parsing exception "));
						accessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
						apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);
					} catch (Exception ex) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(ex));
					}
				}
				return "Error while fetching abhyasi profile from MySRCM : Internal server Error ";
			}
		}
		// }
		return null;
	}

	/**
	 * Method to validate coordinator email Id against MYSRCM.
	 * 
	 * @param program
	 * @param id
	 * @return
	 */
	@Override
	public String validateCoordinatorEmailID(Program program, int id) {
		PMPAPIAccessLogDetails fetchEwelcomeIDAPIAccessLogDetails = null;
		if (null != program.getCoordinatorEmail() && !program.getCoordinatorEmail().isEmpty()) {
			try {
				LOGGER.debug("Validating email of coordinator :  NAME: {}, EMAIL: {}", program.getCoordinatorName(),
						program.getCoordinatorEmail());
				try {
					fetchEwelcomeIDAPIAccessLogDetails = new PMPAPIAccessLogDetails(id,
							EndpointConstants.ABHYASI_INFO_URI + "?email_exact=" + program.getCoordinatorEmail(),
							DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
							StackTraceUtils.convertPojoToJson("Request: Coordinator Email:"
									+ program.getCoordinatorEmail() + ", Coordinator Name:"
									+ program.getCoordinatorName()));
					apiAccessLogService.createPmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
				} catch (Exception e) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(e));
				}
				AbhyasiResult abhyasiResult = srcmRestTemplate
						.fetchParticipantEWelcomeID(program.getCoordinatorEmail());
				if (abhyasiResult.getUserProfile().length > 0) {
					AbhyasiUserProfile userProfile = abhyasiResult.getUserProfile()[0];
					if (null == userProfile) {
						try {
							if (null != fetchEwelcomeIDAPIAccessLogDetails) {
								fetchEwelcomeIDAPIAccessLogDetails
										.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
								fetchEwelcomeIDAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
								fetchEwelcomeIDAPIAccessLogDetails.setResponseBody(StackTraceUtils
										.convertPojoToJson(abhyasiResult));
								fetchEwelcomeIDAPIAccessLogDetails.setErrorMessage("Invalid coordinator email Id");
								apiAccessLogService.updatePmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
							}
						} catch (Exception e) {
							LOGGER.error("Exception while inserting PMP API log details in table : {} ",
									StackTraceUtils.convertPojoToJson(e));
						}
						return "Invalid coordinator email Id";
					} else {

						// create user and assign role
						User user = new User();
						user.setAbyasiId(userProfile.getRef());
						user.setEmail(userProfile.getEmail());
						user.setFirst_name(userProfile.getFirst_name());
						user.setLast_name(userProfile.getLast_name());
						user.setName((userProfile.getFirst_name() + " " + userProfile.getLast_name()).trim());
						user.setCity(userProfile.getCity());
						user.setGender(userProfile.getGender());
						user.setState((null != userProfile.getState()) ? userProfile.getState().getName() : null);
						user.setCountry((null != userProfile.getCountry()) ? userProfile.getCountry().getName() : null);
						user.setRole(PMPConstants.LOGIN_ROLE_COORDINATOR);
						userRepository.save(user);

						try {
							fetchEwelcomeIDAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
							fetchEwelcomeIDAPIAccessLogDetails.setResponseBody(StackTraceUtils
									.convertPojoToJson(abhyasiResult));
							fetchEwelcomeIDAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
							apiAccessLogService.updatePmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
						} catch (Exception e) {
							LOGGER.error("Exception while inserting PMP API log details in table : {} ",
									StackTraceUtils.convertPojoToJson(e));
						}

					}
				} else {
					try {
						if (null != fetchEwelcomeIDAPIAccessLogDetails) {
							fetchEwelcomeIDAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
							fetchEwelcomeIDAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
							fetchEwelcomeIDAPIAccessLogDetails.setResponseBody(StackTraceUtils
									.convertPojoToJson(abhyasiResult));
							fetchEwelcomeIDAPIAccessLogDetails.setErrorMessage("Invalid coordinator email Id");
							apiAccessLogService.updatePmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
						}
					} catch (Exception e) {
						LOGGER.error("Exception while inserting PMP API log details in table : {} ",
								StackTraceUtils.convertPojoToJson(e));
					}
					return "Invalid coordinator email Id";
				}
			} catch (HttpClientErrorException e) {
				LOGGER.error(
						"HTTP CLIENT ERROR : Error While Validating email of coordinator. NAME: {}, EMAIL: {}, EXCEPTION: {} ",
						program.getCoordinatorName(), program.getCoordinatorEmail(),
						StackTraceUtils.convertStackTracetoString(e));
				try {
					fetchEwelcomeIDAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
					fetchEwelcomeIDAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
					fetchEwelcomeIDAPIAccessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
					apiAccessLogService.updatePmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
				} catch (Exception ex) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(ex));
				}
				return e.getResponseBodyAsString();
			} catch (JsonParseException | JsonMappingException e) {
				LOGGER.error(
						"JSONPARSE/JSONMAPPING ERROR : Error While Validating email of coordinator. NAME: {}, EMAIL: {}, EXCEPTION: {} ",
						program.getCoordinatorName(), program.getCoordinatorEmail(),
						StackTraceUtils.convertStackTracetoString(e));
				try {
					fetchEwelcomeIDAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
					fetchEwelcomeIDAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
					fetchEwelcomeIDAPIAccessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
					apiAccessLogService.updatePmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
				} catch (Exception ex) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(ex));
				}
				return "Parsing error while fetching already generated ewelcomeId of the participant";
			} catch (Exception e) {
				LOGGER.error(
						"Exception : Error While Validating email of coordinator. NAME: {}, EMAIL: {}, EXCEPTION: {} ",
						program.getCoordinatorName(), program.getCoordinatorEmail(),
						StackTraceUtils.convertStackTracetoString(e));
				try {
					fetchEwelcomeIDAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
					fetchEwelcomeIDAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_FAILED);
					fetchEwelcomeIDAPIAccessLogDetails.setErrorMessage(StackTraceUtils.convertStackTracetoString(e));
					apiAccessLogService.updatePmpAPIAccesslogDetails(fetchEwelcomeIDAPIAccessLogDetails);
				} catch (Exception ex) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(ex));
				}
				return "Error while fetching already generated ewelcomeId of the participant";
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.CoordinatorAccessControlService#saveCoordinatorDetails(org.srcm.heartfulness.model.ProgramCoordinators)
	 */
	@Override
	public void saveCoordinatorDetails(ProgramCoordinators programCoordinators) {
		coordntrAccssCntrlRepo.saveCoordinatorDetails(programCoordinators);
	}
}
