package org.srcm.heartfulness.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.mail.CoordinatorAccessControlMail;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.CoordinatorAccessControlEmail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.repository.OrganisationRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.util.VersionIdentifier;
import org.srcm.heartfulness.validator.EventDetailsExcelValidatorFactory;

/**
 * Created by vsonnathi on 11/19/15.
 */
@Service
public class PmpIngestionServiceImpl implements PmpIngestionService {

	private static Logger LOGGER = LoggerFactory.getLogger(PmpIngestionServiceImpl.class);

	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Autowired
	private VersionIdentifier versionIdentifier;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	SendMail sendMail;

	@Autowired
	private ProgramService programService;

	@Autowired
	private PmpParticipantService participantService;

	@Autowired
	private MailLogRepository mailLogRepository;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	private CoordinatorAccessControlMail coordinatorAccessControlMail;

	@Autowired
	private CoordinatorAccessControlService coordinatorAccessControlService;

	/**
	 * This method is used to parse the excel file and populate the data into
	 * database.
	 * 
	 * @param fileName
	 * @param fileContent
	 * @return the status and error messages.
	 */
	@Override
	@Transactional
	@SuppressWarnings("static-access")
	public ExcelUploadResponse parseAndPersistExcelFile(String fileName, byte[] fileContent, String eWelcomeIdCheckbox) {

		LOGGER.info("Started parsing and persisting Excel file.");
		List<String> errorResponse = new ArrayList<String>();
		ExcelUploadResponse response = new ExcelUploadResponse(fileName,ExcelType.INVALID,EventDetailsUploadConstants.FAILURE_STATUS, errorResponse);
		Workbook workBook = null;
		try {

			workBook = ExcelParserUtils.getWorkbook(fileName, fileContent); //get the workbook type

		} catch (InvalidExcelFileException iefex) {
			errorResponse.add(iefex.getMessage());
		} catch (Exception ex) {
			errorResponse.add("Excel sheet workbook content is invalid or empty");
		}

		if(null != workBook){
			ExcelType version = versionIdentifier.findVersion(workBook); // Find the excel version
			response.setExcelVersion(version);

			if (version != version.INVALID) {

				try{
					errorResponse = EventDetailsExcelValidatorFactory.validateExcel(workBook, version); //Sheet structure and mandatory fields validation
				} catch(NullPointerException npex){
					errorResponse.add("Excel file you are trying to upload seems to be corrupted. "
							+ "Please copy the content into a valid excel file and re-try");
				} catch(Exception ex){
					errorResponse.add("Error while validating excel file " + fileName + ".Please contact Administrator ");
				}

				if(errorResponse.isEmpty()){
					try {
						Program program = ExcelDataExtractorFactory.extractProgramDetails(workBook, version,eWelcomeIdCheckbox);
						program.setCreatedSource(PMPConstants.CREATED_SOURCE_EXCEL);
						programRepository.save(program);
						validatePreceptorIdandCoordinatorEmailIdAndPersistProgram(program, response, errorResponse); // preceptor ID card number validation
					} catch (InvalidExcelFileException ex) {
						errorResponse.add("Invalid excel file version.Available versions are v1 and v2.1");
					} catch(TypeMismatchDataAccessException tmdae){
						errorResponse.add("There is a mismatch between Java type and data type, such as trying to insert a String into a numeric column");
					} catch(DataIntegrityViolationException divex) {
						errorResponse.add(divex.getCause().getMessage().replaceAll("at row 1",""));
					} catch (InvalidDataAccessResourceUsageException idarue) {
						errorResponse.add("A data access resource is used incorrectly, such as using bad SQL grammar to access a relational database");
					} catch(DataAccessException daex){
						errorResponse.add("Something went wrong!! Unable to save event and participant data.");
					} catch (Exception ex) {
						errorResponse.add("Something went wrong!! Unable to save event and participant data.");
					}
				}
			} else {
				errorResponse.add("Invalid excel file version.Available versions are v1 and v2.1");
			}
		}
		
		response.setErrorMsg(errorResponse);
		return response;
	}

	/**
	 * Method to validate the preceptor Id card number and email ID, if either
	 * of one is valid persist program details in DB.
	 * 
	 * @param program
	 * @param response
	 * @param errorResponse
	 */
	private void validatePreceptorIdandCoordinatorEmailIdAndPersistProgram(Program program,
			ExcelUploadResponse response, List<String> errorResponse) {

		PMPAPIAccessLog accessLog = null;
		accessLog = new PMPAPIAccessLog(null, null, PMPConstants.CREATED_SOURCE_EXCEL, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(program
						.getAutoGeneratedEventId()), null);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		// coordinator emailID validation
		String isCoordinatorEmailIdvalid =null;
		if(null == program.getCoordinatorEmail() || program.getCoordinatorEmail().isEmpty()){
			isCoordinatorEmailIdvalid = "Coordinator email Id is empty";
		}else{
			isCoordinatorEmailIdvalid = coordinatorAccessControlService.validateCoordinatorEmailID(program, accessLog.getId());
		}
		LOGGER.info("Coordinator email Id validation status :{}",isCoordinatorEmailIdvalid);
		if (null == isCoordinatorEmailIdvalid) {
			programRepository.save(program);
			// persist coordinator details
			ProgramCoordinators programCoordinators = new ProgramCoordinators(program.getProgramId(), 0,
					program.getCoordinatorName(), program.getCoordinatorEmail(), 1);
			coordinatorAccessControlService.saveCoordinatorDetails(programCoordinators);

			// preceptor ID card number validation
			validatePreceptorIDAsyncronously(program, accessLog.getId());
			response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);

			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setResponseBody("Email:" + program.getCoordinatorEmail() + " ,EventID:"+ program.getAutoGeneratedEventId());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);

		} else {
			String isPreceptorIdValid = coordinatorAccessControlService.validatePreceptorIDCardNumberandCreateUser(program, accessLog.getId() , PMPConstants.CREATED_SOURCE_EXCEL);
			LOGGER.info("Preceptor Id validation status :{}",isPreceptorIdValid);

			if (null != isPreceptorIdValid) {

				/*if(null == program.getSendersEmailAddress() || program.getSendersEmailAddress().isEmpty()){
					response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
					errorResponse.add("Both coordinator and preceptor email address are invalid. Please provide sender's email address to continue upload");
					response.setErrorMsg(errorResponse);
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				}else{*/
				//add  sender email as secondary coordinator
				if(!program.getSendersEmailAddress().isEmpty()){
					ProgramCoordinators programCoordinators = new ProgramCoordinators(program.getProgramId(), 0, null, program.getSendersEmailAddress(), 0);
					try{
						coordinatorAccessControlService.saveCoordinatorDetails(programCoordinators);
					} catch(Exception ex){
						LOGGER.error("Failed to update secondary coordinator "+program.getSendersEmailAddress() +" while uploading excel ");
					}
				}else{
					//if sender's email is not available set coordinator email as primary coordinator
					ProgramCoordinators programCoordinators = new ProgramCoordinators(program.getProgramId(), 0, null, program.getCoordinatorEmail(), 1);
					try{
						coordinatorAccessControlService.saveCoordinatorDetails(programCoordinators);
					} catch(Exception ex){
						LOGGER.error("Failed to update secondary coordinator "+program.getSendersEmailAddress() +" while uploading excel ");
					}
				}
				//update ewelcome ID status
				participantService.updateParticipantEWelcomeIDStatuswithProgramID(program.getProgramId(),PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, isPreceptorIdValid);

				//send him mail with create profile template
				if(!program.getSendersEmailAddress().isEmpty()){
					CoordinatorAccessControlEmail coordinator = new CoordinatorAccessControlEmail();
					coordinator.setCoordinatorName("Friend");
					coordinator.setEventID(program.getAutoGeneratedEventId());
					coordinator.setEventName(program.getProgramChannel());
					coordinator.setEventPlace(program.getEventPlace());
					SimpleDateFormat inputsdf = new SimpleDateFormat(ExpressionConstants.SQL_DATE_FORMAT);
					coordinator.setProgramCreateDate(inputsdf.format(program.getProgramStartDate()));
					coordinator.setCoordinatorEmail(program.getSendersEmailAddress());
					coordinator.setProgramId(String.valueOf(program.getProgramId()));

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
					new Thread(task, "Service-Thread").start();
				}
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);
				//}

				accessLog.setErrorMessage(StackTraceUtils.convertPojoToJson(response));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);

			} else {

				participantService.updateParticipantEWelcomeIDStatuswithProgramID(program.getProgramId(),PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, isPreceptorIdValid);
				response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);

				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setResponseBody("Email:" + program.getCoordinatorEmail() + " ,EventID:"+ program.getAutoGeneratedEventId());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			}
		}
	}

	/**
	 * Async Method to call method to validate the preceptor ID.
	 * 
	 * @param program
	 * @param id
	 */
	private void validatePreceptorIDAsyncronously(Program program, int id) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					String isValid = coordinatorAccessControlService.validatePreceptorIDCardNumberandCreateUser(program, id,null);
					if (null != isValid) {
						participantService.updateParticipantEWelcomeIDStatuswithProgramID(program.getProgramId(),
								PMPConstants.EWELCOMEID_FAILED_STATE, isValid);
						Runnable task = new Runnable() {
							@Override
							public void run() {
								try {
									sendMailToCoordinatorToUpdatePreceptorID(program);
								} catch (Exception ex) {
									LOGGER.error("Error while sending email to the coordinator - {} ",
											program.getCoordinatorEmail());
								}
							}
						};
						new Thread(task, "ServiceThread").start();
					} else {
						participantService.updateParticipantEWelcomeIDStatuswithProgramID(program.getProgramId(),
								PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, null);
					}

				} catch (Exception ex) {
					LOGGER.error("error while validating preceptor ID : ",
							StackTraceUtils.convertStackTracetoString(ex));
				}
			}
		};
		new Thread(task, "ServiceThread").start();
	}

	@Async
	private void sendMailToCoordinatorToUpdatePreceptorID(Program program) {
		try {
			CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
			coordinatorEmail.setCoordinatorEmail(program.getCoordinatorEmail());
			coordinatorEmail.setCoordinatorName(program.getCoordinatorName());
			coordinatorEmail.setEventName(program.getProgramChannel());
			coordinatorEmail.setProgramCreateDate(program.getProgramStartDate());
			coordinatorEmail.setEventID(program.getAutoGeneratedEventId());
			coordinatorAccessControlMail.sendMailToCoordinatorToUpdatePreceptorID(coordinatorEmail);
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);

			LOGGER.info("END        :Completed sending email to " + program.getCoordinatorEmail());
		} catch (AddressException e) {
			LOGGER.error("Address Exception : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("Address Exception : Coordinator Email : Exception : {} ", program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("UnsupportedEncodingException : Coordinator Email : Exception : {} ",
					program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("MessagingException : Coordinator Email : Exception : {} ", program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (ParseException e) {
			LOGGER.error("ParseException : Coordinator Email : {} : Error while parsing date : {}  ",
					program.getCoordinatorEmail(), program.getProgramStartDate());
			LOGGER.error("ParseException : Error while parsing date  : Exception : {} ", program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception e) {
			LOGGER.error("Exception : Coordinator Email : {} ", program.getCoordinatorEmail(),
					program.getProgramStartDate());
			LOGGER.error("Exception : Error while sending mail to coordinator : Exception : {} ",
					program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		}
	}

	@Override
	// every 15 minutes
	// @Scheduled(cron = "0 0/15 * * * *")
	// @Scheduled(cron = "0/5 * * * * *")
	public void normalizeStagingRecords() {

		// Find out all the program records that are updated after the
		// batchProcessingTime
		LOGGER.info("normalizedStagingRecords ... invoked at:[" + new Date() + "]");

		// Find all program objects that have been modified since last
		// normalized run.
		List<Integer> programIds = programRepository.findUpdatedProgramIdsSince(new Date());
		for (Integer programId : programIds) {
			Program program = programRepository.findById(programId);
			normalizeProgram(program);
		}
	}

	private void normalizeProgram(Program program) {
		// Look up Organisation based on name and address_line1
		@SuppressWarnings("unused")
		Organisation organisation = organisationRepository.findByNameAndWebsite(program.getOrganizationName(),
				program.getOrganizationWebSite());

	}

	/**
	 * This method is used to parse the excel files.
	 * 
	 * @param excelFiles
	 * @return List of ExcelUploadResponse
	 * @see {@link ExcelUploadResponse}
	 */
	@Override
	public List<ExcelUploadResponse> parseAndPersistExcelFile(MultipartFile[] excelFiles, String eWelcomeIdCheckbox)
			throws IOException {

		List<ExcelUploadResponse> responseList = new LinkedList<ExcelUploadResponse>();
		// sorting the files based on excel file name
		Arrays.sort(excelFiles, new Comparator<MultipartFile>() {
			@Override
			public int compare(MultipartFile mpf1, MultipartFile mpf2) {
				return mpf1.getOriginalFilename().compareTo(mpf2.getOriginalFilename());
			}
		});
		for (MultipartFile multipartFile : excelFiles) {
			responseList.add(parseAndPersistExcelFile(multipartFile.getOriginalFilename(), multipartFile.getBytes(),
					eWelcomeIdCheckbox));
		}
		return responseList;
	}

}
