package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.mail.CoordinatorAccessControlMail;
import org.srcm.heartfulness.model.CoordinatorAccessControlEmail;
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
	
	@Override
	public CoordinatorAccessControlResponse addSecondaryCoordinatorRequest(String autoGeneratedEventId,String userEmail) {

		Program program = coordntrAccssCntrlRepo.getProgramIdByEventId(autoGeneratedEventId);
		User user = coordntrAccssCntrlRepo.getUserbyUserEmail(userEmail);

		if(program.getCoordinatorEmail().equalsIgnoreCase(userEmail)){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED,CoordinatorAccessControlConstants.COORDINATOR_INVALID_SELF_REQUEST);
		}
		if(program.getProgramId() == 0){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED,CoordinatorAccessControlConstants.INVALID_EVENT_ID);
		}else if(user.getId() == 0){
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED,CoordinatorAccessControlConstants.INVALID_USER_ID);
		}else{
			int reqAlreadyRaised = coordntrAccssCntrlRepo.checkRequestAlreadyRaised(program.getProgramId(),userEmail);

			if(reqAlreadyRaised > 0){
				LOGGER.error("User " + userEmail + " request is pending for approval for the event id "+autoGeneratedEventId);
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED,CoordinatorAccessControlConstants.COORDINATOR_REQUEST_WAITING_FOR_APPROVAL);
			}

			int reqAlreadyApproved = coordntrAccssCntrlRepo.checkRequestAlreadyApproved(program.getProgramId(),userEmail);
			if(reqAlreadyApproved > 0){
				LOGGER.error("Request already approved for user "+userEmail+" for event "+autoGeneratedEventId);
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED,CoordinatorAccessControlConstants.COORDINATOR_REQUEST_ALREADY_APPROVED);
			}else if(reqAlreadyRaised == 0){
				SecondaryCoordinatorRequest scReq = new SecondaryCoordinatorRequest();
				scReq.setProgramId(program.getProgramId());
				scReq.setUserId(user.getId());
				scReq.setRequestedBy(userEmail);
				scReq.setStatus(CoordinatorAccessControlConstants.REQUEST_DEFAULT_STATUS);
				try{
					coordntrAccssCntrlRepo.saveSecondaryCoordinatorRequest(scReq);
					//send mail to preceptor and coordinator
					

					return new CoordinatorAccessControlSuccessResponse(ErrorConstants.STATUS_SUCCESS, CoordinatorAccessControlConstants.COORDINATOR_SUCCESSFULL_REQUEST);
				}catch(DataAccessException daex){
					LOGGER.error("Failed to save Secondary Coordinator Request",daex);
				}catch(Exception ex){
					LOGGER.error("Failed to save Secondary Coordinator Request",ex);
				}
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.COORDINATOR_FAILED_REQUEST);
			}else{
				LOGGER.error("Exception while checking request already raised for user "+userEmail);
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED,CoordinatorAccessControlConstants.INVALID_REQUEST);
			}


		}
	}

	@Override
	@Transactional
	public CoordinatorAccessControlResponse approveSecondaryCoordinatorRequest(String approvedBy, ProgramCoordinators pgrmCoordinators) {

		Program program = coordntrAccssCntrlRepo.getProgramIdByEventId(pgrmCoordinators.getEventId());
		int approvedStatus = coordntrAccssCntrlRepo.approveSecondaryCoordinatorRequest(program.getProgramId(),approvedBy,pgrmCoordinators.getCoordinatorEmail());
		if(approvedStatus > 0){
			User user = coordntrAccssCntrlRepo.getUserbyUserEmail(pgrmCoordinators.getCoordinatorEmail());
			ProgramCoordinators newPgrmCoordinator = new ProgramCoordinators();
			newPgrmCoordinator.setProgramId(program.getProgramId());
			newPgrmCoordinator.setUserId(user.getId());
			newPgrmCoordinator.setCoordinatorName((null == user.getName() || user.getName().isEmpty() == true) ? 
					(null == pgrmCoordinators.getCoordinatorName() || pgrmCoordinators.getCoordinatorName().isEmpty() == true) ? "" : pgrmCoordinators.getCoordinatorName() :user.getName() );
			newPgrmCoordinator.setCoordinatorEmail(pgrmCoordinators.getCoordinatorEmail());
			newPgrmCoordinator.setIsPrimaryCoordinator(0);
			try{
				coordntrAccssCntrlRepo.createProgramCoordinator(newPgrmCoordinator);
				//send mail to new coordinator
				

				return new CoordinatorAccessControlSuccessResponse(ErrorConstants.STATUS_SUCCESS, CoordinatorAccessControlConstants.PRECEPTOR_SUCCESS_RESPONSE 
						+   pgrmCoordinators.getCoordinatorName()
								+ " as a coordinator for your event " + pgrmCoordinators.getEventId());
			}catch(DataAccessException daex){
				LOGGER.error("Failed to create program  coordinator DAEX ",daex);
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
			}catch(Exception ex){
				LOGGER.error("Failed to create program  coordinator EX ",ex);
				return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
			}

		}else{
			LOGGER.error("Failed to update request table for event Id " + pgrmCoordinators.getEventId());
			return new CoordinatorAccessControlErrorResponse(ErrorConstants.STATUS_FAILED, CoordinatorAccessControlConstants.INVALID_REQUEST);
		}

	}


	/**
	 * Method to validate the preceptor ID card number against MYSRCM and
	 * 
	 * If preceptor ID is valid, create user in HFN Backend and assign with role
	 * 'PRECEPTOR'.
	 * If invalid, returns appropriate error message.
	 * 
	 * @param program
	 * @param id
	 * @return
	 */
	@Override
	public String validatePreceptorIDCardNumberandCreateUser(Program program, int id, String source) {
		PMPAPIAccessLogDetails accessLogDetails = null;
		//if (program.getFirstSittingBy() == 0) {
		
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
							System.out.println(accessLogDetails.toString());
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
									user.setName((userProfile.getFirst_name() + " " + userProfile.getLast_name())
											.trim());
									user.setCity(userProfile.getCity());
									user.setGender(userProfile.getGender());
									user.setState((null != userProfile.getState()) ? userProfile.getState().getName()
											: null);
									user.setCountry((null != userProfile.getCountry()) ? userProfile.getCountry()
											.getName() : null);
									user.setRole(PMPConstants.LOGIN_ROLE_PRECEPTOR);
									userRepository.save(user);

									// persist coordinator details
									ProgramCoordinators programCoordinators = new ProgramCoordinators(
											program.getProgramId(), user.getId(), user.getName(), user.getEmail(), 0);
									coordntrAccssCntrlRepo.savecoordinatorDetails(programCoordinators);
									
									if(PMPConstants.CREATED_SOURCE_EXCEL.equalsIgnoreCase(source)){
										CoordinatorAccessControlEmail coordinator=new CoordinatorAccessControlEmail();
										coordinator.setPreceptorName(program.getPreceptorName());
										coordinator.setPreceptorEmailId(user.getEmail());
										coordinator.setCoordinatorEmail(program.getCoordinatorEmail());
										coordinator.setCoordinatorName(program.getCoordinatorName());
										coordinator.setEventID(program.getAutoGeneratedEventId());
										coordinator.setEventName(program.getProgramChannel());
										SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
										coordinator.setProgramCreateDate(inputsdf.format(program.getProgramStartDate()));
										if(null == program.getCoordinatorEmail() || program.getCoordinatorEmail().isEmpty() ){
											coordinatorAccessControlMail.sendMailToPreceptorToUpdateCoordinatorEmailID(coordinator);
											System.out.println("Need to send mail to preceptor to update the coordinator email in dashboard.");
										}else{
											// persist coordinator details
											ProgramCoordinators programpreceptor = new ProgramCoordinators(program.getProgramId(), 0,
													program.getCoordinatorName(), program.getCoordinatorEmail(), 1);
											savecoordinatorDetails(programpreceptor);
											coordinatorAccessControlMail.sendMailToPreceptorandCoordinatorToCreateProfileAndAccessDashboard(coordinator);
											System.out.println("Need to send mail to preceptor and coordinator regd. profile creation and access link to dashboard.");
										}
									}

								} else {
									return "Preceptor Email Id is not available for the provided preceptor Id.";
								}

								try {
									if (null != accessLogDetails) {
										accessLogDetails
												.setResponseBody(StackTraceUtils.convertPojoToJson(userProfile));
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
					LOGGER.error("Exception while fecthing abhyasi profile: HttpClientErrorException : {} ",
							e.getMessage());
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
		//}
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
						.fetchparticipanteWelcomeID(program.getCoordinatorEmail());
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
						user.setName((userProfile.getFirst_name() + " " + userProfile.getLast_name())
								.trim());
						user.setCity(userProfile.getCity());
						user.setGender(userProfile.getGender());
						user.setState((null != userProfile.getState()) ? userProfile.getState().getName()
								: null);
						user.setCountry((null != userProfile.getCountry()) ? userProfile.getCountry()
								.getName() : null);
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


	@Override
	public void savecoordinatorDetails(ProgramCoordinators programCoordinators) {
		coordntrAccssCntrlRepo.savecoordinatorDetails(programCoordinators);
	}
}
