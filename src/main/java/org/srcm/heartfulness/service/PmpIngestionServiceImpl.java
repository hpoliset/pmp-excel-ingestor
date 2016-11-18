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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.mail.CoordinatorAccessControlMail;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.ProgramCoordinators;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.repository.OrganisationRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
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
	SendMail sendMail;

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
	public ExcelUploadResponse parseAndPersistExcelFile(String fileName, byte[] fileContent) {
		LOGGER.info("Started parsing and persisting Excel file.");
		ExcelUploadResponse response = new ExcelUploadResponse();
		response.setFileName(fileName);
		response.setExcelVersion(ExcelType.INVALID);
		List<String> errorResponse = new ArrayList<String>();
		try {
			Workbook workBook = ExcelParserUtils.getWorkbook(fileName, fileContent);
			// Validate and Parse the excel file
			ExcelType version = versionIdentifier.findVersion(workBook);
			response.setExcelVersion(version);
			if (version != version.INVALID) {
				errorResponse = EventDetailsExcelValidatorFactory.validateExcel(workBook, version);
				if (!errorResponse.isEmpty()) {
					response.setErrorMsg(errorResponse);
					response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
				} else {
					try {
						Program program = ExcelDataExtractorFactory.extractProgramDetails(workBook, version);
						program.setCreatedSource("Excel");
						// validate coordinator emailId and preceptorId
						// Persist the program
						validatepreceptorIdandCoordinatorEmailIdandPersistProgram(program, response, errorResponse);
					} catch (InvalidExcelFileException ex) {
						errorResponse.add("File you are trying to upload is invalid.Please contact Administrator");
						response.setErrorMsg(errorResponse);
						response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
					} catch (Exception ex) {
						errorResponse.add("Failed to upload excel file.Please contact Administrator");
						response.setErrorMsg(errorResponse);
						response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
					}
				}
			} else {
				errorResponse.add("Invalid file contents.Please contact Administrator");
				response.setErrorMsg(errorResponse);
				response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
			}
		} catch (IOException | InvalidExcelFileException | POIXMLException ex) {
			// To show the error message to the end user.
			LOGGER.error(ex.getMessage());
			errorResponse.add("Error while uploading excel file.Please contact Administrator");
			response.setErrorMsg(errorResponse);
			response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			errorResponse.add("Exception while uploading excel file.Please contact Administrator");
			response.setErrorMsg(errorResponse);
			response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
		}
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
	private void validatepreceptorIdandCoordinatorEmailIdandPersistProgram(Program program,
			ExcelUploadResponse response, List<String> errorResponse) {

		PMPAPIAccessLog accessLog = null;
		int id = 0;
		try {
			accessLog = new PMPAPIAccessLog(null, null, "EXCEL UPLOAD", DateUtils.getCurrentTimeInMilliSec(), null,
					ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(program
							.getAutoGeneratedEventId()), null);
			id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		} catch (Exception e) {
			LOGGER.error("Error while inserting data into pmp api access log table. Exception: {} ",
					StackTraceUtils.convertStackTracetoString(e));
		}
		// coordinator emailID validation
		String isCoordinatorEmailIdvalid =null;
		if(null == program.getCoordinatorEmail() || program.getCoordinatorEmail().isEmpty()){
			isCoordinatorEmailIdvalid = "Coordinator email Id is empty";
		}else{
			isCoordinatorEmailIdvalid = coordinatorAccessControlService.validateCoordinatorEmailID(program, id);
		}
		if (null == isCoordinatorEmailIdvalid) {
			programRepository.save(program);
			// persist coordinator details
			ProgramCoordinators programCoordinators = new ProgramCoordinators(program.getProgramId(), 0,
					program.getCoordinatorName(), program.getCoordinatorEmail(), 1);
			coordinatorAccessControlService.savecoordinatorDetails(programCoordinators);
			// preceptor ID card number validation
			validatePreceptorIDAsyncronously(program, id);
			response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);
			try {
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setResponseBody("Email:" + program.getCoordinatorEmail() + " ,EventID:"
						+ program.getAutoGeneratedEventId());
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			} catch (Exception e) {
				LOGGER.error("Exception while inserting PMP API log details in table : {} ",
						StackTraceUtils.convertPojoToJson(e));
			}
		} else {
			String isPreceptorIdValid = coordinatorAccessControlService.validatePreceptorIDCardNumberandCreateUser(program, id , PMPConstants.CREATED_SOURCE_EXCEL);
			if (null != isPreceptorIdValid) {
				errorResponse.add(isCoordinatorEmailIdvalid + " and " +isPreceptorIdValid);
				response.setErrorMsg(errorResponse);
				response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
				try {
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setErrorMessage(errorResponse.toString());
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				} catch (Exception e) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(e));
				}
			} else {
				participantService.updatePartcipantEWelcomeIDStatuswithParticipantID(program.getProgramId(),
						PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, isPreceptorIdValid);
				response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);
				try {
					accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
					accessLog.setResponseBody("Email:" + program.getCoordinatorEmail() + " ,EventID:"
							+ program.getAutoGeneratedEventId());
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				} catch (Exception e) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(e));
				}
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
					String isValid = coordinatorAccessControlService.validatePreceptorIDCardNumberandCreateUser(program, id,PMPConstants.CREATED_SOURCE_EXCEL);
					if (null != isValid) {
						participantService.updatePartcipantEWelcomeIDStatuswithParticipantID(program.getProgramId(),
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
						participantService.updatePartcipantEWelcomeIDStatuswithParticipantID(program.getProgramId(),
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

	/**
	 * Method to send mail to coordinator when preceptor Id is invalid.
	 * 
	 * @param program
	 */
	private void sendMailToCoordinatorToUpdatePreceptorID(Program program) {
		try {
			CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
			coordinatorEmail.setCoordinatorEmail(program.getCoordinatorEmail());
			coordinatorEmail.setCoordinatorName(program.getCoordinatorName());
			coordinatorEmail.setEventName(program.getProgramChannel());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			coordinatorEmail.setProgramCreateDate(inputsdf.format(program.getProgramStartDate()));
			coordinatorEmail.setEventID(program.getAutoGeneratedEventId());
			coordinatorAccessControlMail.sendMailToCoordinatorToUpdatePreceptorID(coordinatorEmail);
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
						program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
						EmailLogConstants.STATUS_SUCCESS, null);
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
			LOGGER.info("END        :Completed sending email to " + program.getCoordinatorEmail());
		} catch (AddressException e) {
			LOGGER.error("Address Exception : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("Address Exception : Coordinator Email : Exception : {} ", program.getCoordinatorEmail());
			try {
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
						program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
			} catch (Exception ex) {
				LOGGER.error("EXCEPTION  :Failed to update mail log table");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("UnsupportedEncodingException : Coordinator Email : Exception : {} ",
					program.getCoordinatorEmail());
			try {
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
						program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
			} catch (Exception ex) {
				LOGGER.error("EXCEPTION  :Failed to update mail log table");
			}
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("MessagingException : Coordinator Email : Exception : {} ", program.getCoordinatorEmail());
			try {
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
						program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
			} catch (Exception ex) {
				LOGGER.error("EXCEPTION  :Failed to update mail log table");
			}
		} catch (ParseException e) {
			LOGGER.error("ParseException : Coordinator Email : {} : Error while parsing date : {}  ",
					program.getCoordinatorEmail(), program.getProgramStartDate());
			LOGGER.error("ParseException : Error while parsing date  : Exception : {} ", program.getCoordinatorEmail());
			try {
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
						program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
			} catch (Exception ex) {
				LOGGER.error("EXCEPTION  :Failed to update mail log table");
			}
		} catch (Exception e) {
			LOGGER.error("Exception : Coordinator Email : {} ", program.getCoordinatorEmail(),
					program.getProgramStartDate());
			LOGGER.error("Exception : Error while sending mail to coordinator : Exception : {} ",
					program.getCoordinatorEmail());
			try {
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
						program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
			} catch (Exception ex) {
				LOGGER.error("EXCEPTION  :Failed to update mail log table");
			}
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
	public List<ExcelUploadResponse> parseAndPersistExcelFile(MultipartFile[] excelFiles) throws IOException {

		List<ExcelUploadResponse> responseList = new LinkedList<ExcelUploadResponse>();
		// sorting the files based on excel file name
		Arrays.sort(excelFiles, new Comparator<MultipartFile>() {
			@Override
			public int compare(MultipartFile mpf1, MultipartFile mpf2) {
				return mpf1.getOriginalFilename().compareTo(mpf2.getOriginalFilename());
			}
		});
		for (MultipartFile multipartFile : excelFiles) {
			responseList.add(parseAndPersistExcelFile(multipartFile.getOriginalFilename(), multipartFile.getBytes()));
		}
		return responseList;
	}

}
