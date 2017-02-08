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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Program;
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
	public ExcelUploadResponse parseAndPersistExcelFile(String fileName, byte[] fileContent, String eWelcomeIdCheckbox) {
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
					// Persist the program
					try {
						Program program = ExcelDataExtractorFactory.extractProgramDetails(workBook, version,eWelcomeIdCheckbox);
						program.setCreatedSource(PMPConstants.CREATED_SOURCE_EXCEL);
						programRepository.save(program);
						// preceptor ID card number validation
						Runnable task = new Runnable() {
							@Override
							public void run() {
								try {
									validatePreceptorID(program);
								} catch (Exception ex) {
									LOGGER.debug("Error while Validating preceptor ID : {}", ex);
								}
							}
						};
						new Thread(task, "ServiceThread").start();
						response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);
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
	 * Method to validate preceptor ID by calling MySRCM API.
	 * 
	 * @param program
	 */
	private void validatePreceptorID(Program program) {
		PMPAPIAccessLog accessLog = null;
		int id = 0;
		LOGGER.info("Inserting PMP API log details in table");
		try {
			accessLog = new PMPAPIAccessLog(program.getCoordinatorEmail(), "EXCEL UPLOAD",
					EndpointConstants.ABHYASI_INFO_URI, DateUtils.getCurrentTimeInMilliSec(), null,
					ErrorConstants.STATUS_FAILED, null, "PreceptorId : " + program.getPreceptorIdCardNumber()
							+ " ,Event ID : " + program.getAutoGeneratedEventId(), null);
			id = apiAccessLogService.createPmpAPIAccessLog(accessLog);
		} catch (Exception e) {
			LOGGER.error("Exception while inserting PMP API log details in table : {} ",
					StackTraceUtils.convertPojoToJson(e));
		}
		try {
			String isValid = programService.validatePreceptorIDCardNumber(program, id);
			if (null != isValid) {
				Runnable task = new Runnable() {
					@Override
					public void run() {
						try {
							sendMailToCoordinatorToUpdatePreceptorID(program);
							LOGGER.info("Mail sent successfully to : {}", program.getCoordinatorEmail());
						} catch (Exception ex) {
						}
					}
				};
				new Thread(task, "ServiceThread").start();
				participantService.updateParticipantEWelcomeIDStatuswithProgramID(program.getProgramId(),
						PMPConstants.EWELCOMEID_FAILED_STATE, isValid);
				try {
					if (null != accessLog) {
						accessLog.setStatus(ErrorConstants.STATUS_FAILED);
						accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(isValid));
						accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
						apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					}
				} catch (Exception e) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(e));
				}

			} else {
				try {
					if (null != accessLog) {
						accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
						accessLog.setResponseBody("Valid PreceptorID");
						accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
						apiAccessLogService.updatePmpAPIAccessLog(accessLog);
					}
				} catch (Exception e) {
					LOGGER.error("Exception while inserting PMP API log details in table : {} ",
							StackTraceUtils.convertPojoToJson(e));
				}
				participantService.updateParticipantEWelcomeIDStatuswithProgramID(program.getProgramId(),
						PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, null);
			}
		} catch (Exception ex) {
			LOGGER.error("Error while validating preceptor ID : {}", program.getPreceptorIdCardNumber());
			LOGGER.error("Error while validating preceptor ID : Exception: {}",
					StackTraceUtils.convertStackTracetoString(ex));
			try {
				if (null != accessLog) {
					accessLog.setStatus(ErrorConstants.STATUS_FAILED);
					accessLog.setResponseBody(StackTraceUtils.convertPojoToJson("Error while validating preceptor ID"));
					accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
					apiAccessLogService.updatePmpAPIAccessLog(accessLog);
				}
			} catch (Exception e) {
				LOGGER.error("Exception while inserting PMP API log details in table : {} ",
						StackTraceUtils.convertPojoToJson(e));
			}
		}

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
			sendMail.sendMailToCoordinatorToUpdatePreceptorID(coordinatorEmail);
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);

			LOGGER.info("END        :Completed sending email to " + program.getCoordinatorEmail());
		} catch (AddressException e) {
			LOGGER.error("Address Exception : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("Address Exception : Coordinator Email : Exception : {} ", program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("UnsupportedEncodingException : Coordinator Email : Exception : {} ",
					program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Coordinator Email : {} ", program.getCoordinatorEmail());
			LOGGER.error("MessagingException : Coordinator Email : Exception : {} ", program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (ParseException e) {
			LOGGER.error("ParseException : Coordinator Email : {} : Error while parsing date : {}  ",
					program.getCoordinatorEmail(), program.getProgramStartDate());
			LOGGER.error("ParseException : Error while parsing date  : Exception : {} ", program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception e) {
			LOGGER.error("Exception : Coordinator Email : {} ", program.getCoordinatorEmail(),
					program.getProgramStartDate());
			LOGGER.error("Exception : Error while sending mail to coordinator : Exception : {} ",
					program.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()),
					program.getCoordinatorEmail(), EmailLogConstants.PCTPT_EMAIL_DETAILS,
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
