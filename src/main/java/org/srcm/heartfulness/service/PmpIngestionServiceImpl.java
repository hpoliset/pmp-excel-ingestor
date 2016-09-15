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
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ExcelType;
import org.srcm.heartfulness.excelupload.transformer.ExcelDataExtractorFactory;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.Organisation;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.OrganisationRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
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
						Program program = ExcelDataExtractorFactory.extractProgramDetails(workBook, version);
						program.setCreatedSource("Excel");
						programRepository.save(program);
						// preceptor ID card number validation
						try {
							if (null != programService.validatePreceptorIDCardNumber(program, 0)) {
								sendMailToCoordinatorToUpdatePreceptorID(program);
								participantService.updatePartcipantEWelcomeIDStatuswithParticipantID(program.getProgramId(),PMPConstants.EWELCOMEID_FAILED_STATE,"Invalid PreceptorID");
							}else{
								participantService.updatePartcipantEWelcomeIDStatuswithParticipantID(program.getProgramId(),PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE,null);
							}
						} catch (Exception ex) {
							LOGGER.debug("Error while validating preceptor ID : {}",program.getPreceptorIdCardNumber());
							LOGGER.debug("Error while validating preceptor ID : Exception: {}",StackTraceUtils.convertStackTracetoString(ex));
						}
						response.setStatus(EventDetailsUploadConstants.SUCCESS_STATUS);
					} catch (InvalidExcelFileException ex) {
						errorResponse.add(ex.getMessage());
						response.setErrorMsg(errorResponse);
						response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
					} catch (Exception ex) {
						errorResponse.add(ex.getMessage());
						response.setErrorMsg(errorResponse);
						response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
					}

				}
			} else {
				errorResponse.add("Invalid file contents.");
				response.setErrorMsg(errorResponse);
				response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
			}
		} catch (IOException | InvalidExcelFileException | POIXMLException ex) {
			// To show the error message to the end user.
			LOGGER.error(ex.getMessage());
			errorResponse.add(ex.getMessage());
			response.setErrorMsg(errorResponse);
			response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
		} catch(Exception ex){
			LOGGER.error(ex.getMessage());
			errorResponse.add("Error while uploading excel file.Please contact Administrator");
			response.setErrorMsg(errorResponse);
			response.setStatus(EventDetailsUploadConstants.FAILURE_STATUS);
		}
		return response;
	}

	@Async
	private void sendMailToCoordinatorToUpdatePreceptorID(Program program) {
		try {
			CoordinatorEmail coordinatorEmail = new CoordinatorEmail();
			coordinatorEmail.setCoordinatorEmail(program.getCoordinatorEmail());
			coordinatorEmail.setCoordinatorName(program.getCoordinatorName());
			coordinatorEmail.setEventName(program.getProgramChannel());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			coordinatorEmail.setProgramCreateDate(inputsdf.format(program.getProgramStartDate()));
			coordinatorEmail.setEventID(program.getAutoGeneratedEventId());
			sendMail.sendMailToCoordinatorToUpdatePreceptorID(coordinatorEmail);
		} catch (AddressException e) {
			LOGGER.debug("Address Exception : Coordinator Email : {} ",program.getCoordinatorEmail());
			LOGGER.debug("Address Exception : Coordinator Email : Exception : {} ",program.getCoordinatorEmail());
		} catch (UnsupportedEncodingException e) {
			LOGGER.debug("UnsupportedEncodingException : Coordinator Email : {} ",program.getCoordinatorEmail());
			LOGGER.debug("UnsupportedEncodingException : Coordinator Email : Exception : {} ",program.getCoordinatorEmail());
		} catch (MessagingException e) {
			LOGGER.debug("MessagingException : Coordinator Email : {} ",program.getCoordinatorEmail());
			LOGGER.debug("MessagingException : Coordinator Email : Exception : {} ",program.getCoordinatorEmail());
		} catch (ParseException e) {
			LOGGER.debug("ParseException : Coordinator Email : {} : Error while parsing date : {}  ",program.getCoordinatorEmail(),program.getProgramStartDate());
			LOGGER.debug("ParseException : Error while parsing date  : Exception : {} ",program.getCoordinatorEmail());
		} catch (Exception e) {
			LOGGER.debug("Exception : Coordinator Email : {} ",program.getCoordinatorEmail(),program.getProgramStartDate());
			LOGGER.debug("Exception : Error while sending mail to coordinator : Exception : {} ",program.getCoordinatorEmail());
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
