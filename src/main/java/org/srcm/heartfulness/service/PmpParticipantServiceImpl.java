package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ParticipantSearchField;
import org.srcm.heartfulness.excelupload.transformer.impl.ExcelDataExtractorV2Impl;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.UpdateIntroductionResponse;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.ExcelParserUtils;
import org.srcm.heartfulness.util.InvalidExcelFileException;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.validator.impl.ExcelV2ValidatorImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class is service Implementation for the participant related actions.
 * 
 * @author himasreev
 *
 */
@Service
public class PmpParticipantServiceImpl implements PmpParticipantService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PmpParticipantServiceImpl.class);

	@Autowired
	ParticipantRepository participantRepository;

	@Autowired
	ProgramRepository programrepository;

	@Autowired
	ProgramService programService;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	/**
	 * Service to create new participant or to update the existing participant
	 * of the event.
	 * 
	 * @param participant
	 *            has the participant related details to create or update the
	 *            participant.
	 * @return participant details
	 * @throws ParseException
	 */
	@Override
	public ParticipantRequest createParticipant(ParticipantRequest participantRequest) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		SimpleDateFormat sdf1 = new SimpleDateFormat(PMPConstants.SQL_DATE_FORMAT);
		Participant participant;
		if ((null == participantRequest.getSeqId() || participantRequest.getSeqId().isEmpty())
				&& 0 == participantRequest.getId()) {
			participant = new Participant();
			participant.setProgramId(programrepository.getProgramIdByEventId(participantRequest.getEventId()));
			participant.setProgram(programrepository.findById(participant.getProgramId()));
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if (participantRequest.getGender() != null
					&& !participantRequest.getGender().isEmpty()
					&& (participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE) || participantRequest
							.getGender().equalsIgnoreCase(PMPConstants.FEMALE)))
				participantRequest
						.setGender((participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE)
								&& participantRequest.getGender() != null && !participantRequest.getGender().isEmpty()) ? PMPConstants.GENDER_MALE
								: PMPConstants.GENDER_FEMALE);
			participant.setGender(participantRequest.getGender());
			participant
					.setDateOfBirth((null != participantRequest.getDateOfBirth() && !participantRequest
							.getDateOfBirth().isEmpty()) ? sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getDateOfBirth()))) : null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setAbhyasiId(participantRequest.getAbhyasiId());
			participant.setIntroducedBy(participantRequest.getIntroducedBy());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getIntroducedStatus())) ? 1 : 0);
			participant.setIntroductionDate((null != participantRequest.getIntroductionDate() && !participantRequest
					.getIntroductionDate().isEmpty()) ? sdf1.parse(sdf1.format(sdf.parse(participantRequest
					.getIntroductionDate()))) : null);

			if (null == participantRequest.getFirstSittingDate()) {
				participant.setFirstSittingDate(null);
			} else {
				try {
					participant.setFirstSittingDate(sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getFirstSittingDate()))));
				} catch (Exception e) {
					participant.setFirstSittingDate(null);
				}
			}
			if (null == participantRequest.getSecondSittingDate()) {
				participant.setSecondSittingDate(null);
			} else {
				try {
					participant.setSecondSittingDate(sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getSecondSittingDate()))));
				} catch (Exception e) {
					participant.setSecondSittingDate(null);
				}
			}
			if (null == participantRequest.getThirdSittingDate()) {
				participant.setThirdSittingDate(null);
			} else {
				try {
					participant.setThirdSittingDate(sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getThirdSittingDate()))));
				} catch (Exception e) {
					participant.setThirdSittingDate(null);
				}
			}
			participant.setFirstSitting((null != participantRequest.getFirstSitting() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getFirstSitting())) ? 1 : 0);
			participant.setSecondSitting((null != participantRequest.getSecondSitting() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getSecondSitting())) ? 1 : 0);
			participant.setThirdSitting((null != participantRequest.getThirdSitting() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getThirdSitting())) ? 1 : 0);
			participant.setWelcomeCardNumber((null != participantRequest.geteWelcomeID() && !participantRequest
					.geteWelcomeID().isEmpty()) ? participantRequest.geteWelcomeID() : null);
			participant.setCreatedSource(PMPConstants.CREATED_SOURCE_DASHBOARD);
			participant.setEwelcomeIdRemarks(participantRequest.getEwelcomeIdRemarks());
			setParticipantEWelcomeIDStatus(participant.getProgram(), participant,
					PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, null);
		} else {
			participant = findBySeqId(participantRequest);
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if ((null != participantRequest.getGender() && !participantRequest.getGender().isEmpty() && (participantRequest
					.getGender().equalsIgnoreCase(PMPConstants.MALE) || participantRequest.getGender()
					.equalsIgnoreCase(PMPConstants.FEMALE))))
				participantRequest
						.setGender(participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE) ? PMPConstants.GENDER_MALE
								: PMPConstants.GENDER_FEMALE);
			participant.setGender(participantRequest.getGender());
			participant
					.setDateOfBirth((null != participantRequest.getDateOfBirth() && !participantRequest
							.getDateOfBirth().isEmpty()) ? sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getDateOfBirth()))) : null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setAbhyasiId(participantRequest.getAbhyasiId());
			participant.setIntroducedBy(participantRequest.getIntroducedBy());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getIntroducedStatus())) ? 1 : 0);
			participant.setIntroductionDate((null != participantRequest.getIntroductionDate() && !participantRequest
					.getIntroductionDate().isEmpty()) ? sdf1.parse(sdf1.format(sdf.parse(participantRequest
					.getIntroductionDate()))) : null);
			participant.setEwelcomeIdRemarks(participantRequest.getEwelcomeIdRemarks());
			if (null == participantRequest.getFirstSittingDate()) {
				participant.setFirstSittingDate(null);
			} else {
				try {
					participant.setFirstSittingDate(sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getFirstSittingDate()))));
				} catch (Exception e) {
					participant.setFirstSittingDate(null);
				}
			}
			if (null == participantRequest.getSecondSittingDate()) {
				participant.setSecondSittingDate(null);
			} else {
				try {
					participant.setSecondSittingDate(sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getSecondSittingDate()))));
				} catch (Exception e) {
					participant.setSecondSittingDate(null);
				}
			}
			if (null == participantRequest.getThirdSittingDate()) {
				participant.setThirdSittingDate(null);
			} else {
				try {
					participant.setThirdSittingDate(sdf1.parse(sdf1.format(sdf.parse(participantRequest
							.getThirdSittingDate()))));
				} catch (Exception e) {
					participant.setThirdSittingDate(null);
				}
			}
			participant.setFirstSitting((null != participantRequest.getFirstSitting() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getFirstSitting())) ? 1 : 0);
			participant.setSecondSitting((null != participantRequest.getSecondSitting() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getSecondSitting())) ? 1 : 0);
			participant.setThirdSitting((null != participantRequest.getThirdSitting() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getThirdSitting())) ? 1 : 0);
			participantRequest.seteWelcomeID((null != participant.getWelcomeCardNumber() && !participant
					.getWelcomeCardNumber().isEmpty()) ? participant.getWelcomeCardNumber() : null);
			setParticipantEWelcomeIDStatus(participant.getProgram(), participant,
					PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE, null);
		}
		participantRepository.save(participant);
		participantRequest.setSeqId(participant.getSeqId());
		participantRequest.setPrintName(participant.getPrintName());
		participantRequest.setEmail(participant.getEmail());
		participantRequest.setMobilePhone(participant.getMobilePhone());
		if (participant.getGender() != null && !participant.getGender().isEmpty()) {
			if ((participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE) || participant.getGender()
					.equalsIgnoreCase(PMPConstants.GENDER_MALE)))
				participant
						.setGender(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) ? PMPConstants.MALE
								: PMPConstants.FEMALE);
			participantRequest.setGender(participant.getGender());
		}

		participantRequest.setDateOfBirth((null != participant.getDateOfBirth() && !participantRequest.getDateOfBirth()
				.isEmpty()) ? sdf.format(participant.getDateOfBirth()) : null);
		participantRequest.setAddressLine1(participant.getAddressLine1());
		participantRequest.setAddressLine2(participant.getAddressLine2());
		participantRequest.setCity(participant.getCity());
		participantRequest.setState(participant.getState());
		participantRequest.setCountry(participant.getCountry());
		participantRequest.setIntroducedStatus(0 != participant.getIntroduced() ? PMPConstants.REQUIRED_YES
				: PMPConstants.REQUIRED_NO);
		participantRequest.setIntroductionDate((null != participantRequest.getIntroductionDate() && !participantRequest
				.getIntroductionDate().isEmpty()) ? sdf.format(participant.getIntroductionDate()) : null);
		participantRequest.setAbhyasiId(participant.getAbhyasiId());
		participantRequest.setIntroducedBy(participant.getIntroducedBy());
		if (null == participant.getFirstSittingDate()) {
			participantRequest.setFirstSittingDate("");
		} else {
			try {
				participantRequest.setFirstSittingDate(sdf.format(participant.getFirstSittingDate()));
			} catch (Exception e) {
				participantRequest.setFirstSittingDate("");
			}
		}
		if (null == participant.getSecondSittingDate()) {
			participantRequest.setSecondSittingDate("");
		} else {
			try {
				participantRequest.setSecondSittingDate(sdf.format(participant.getSecondSittingDate()));
			} catch (Exception e) {
				participantRequest.setSecondSittingDate("");
			}
		}
		if (null == participant.getThirdSittingDate()) {
			participantRequest.setThirdSittingDate("");
		} else {
			try {
				participantRequest.setThirdSittingDate(sdf.format(participant.getThirdSittingDate()));
			} catch (Exception e) {
				participantRequest.setThirdSittingDate("");
			}
		}
		participantRequest
				.setFirstSitting((null != participant.getFirstSitting() && 1 == participant.getFirstSitting()) ? PMPConstants.REQUIRED_YES
						: PMPConstants.REQUIRED_NO);
		participantRequest.setSecondSitting((null != participant.getSecondSitting() && 1 == participant
				.getSecondSitting()) ? PMPConstants.REQUIRED_YES : PMPConstants.REQUIRED_NO);
		participantRequest
				.setThirdSitting((null != participant.getThirdSitting() && 1 == participant.getThirdSitting()) ? PMPConstants.REQUIRED_YES
						: PMPConstants.REQUIRED_NO);
		participantRequest.seteWelcomeID((null != participant.getWelcomeCardNumber() && !participant
				.getWelcomeCardNumber().isEmpty()) ? participant.getWelcomeCardNumber() : null);
		participantRequest.setEwelcomeIdRemarks(participant.getEwelcomeIdRemarks());
		return participantRequest;
	}

	/**
	 * Service to get the participant details depending on the seqId and event
	 * Id.
	 * 
	 * @param participantRequest
	 *            contains seqId and event Id
	 * @return participant details
	 */
	@Override
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest) {
		SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		Participant participant = findBySeqId(participantRequest);
		if (null != participant) {
			participantRequest.setPrintName(participant.getPrintName());
			participantRequest.setEmail(participant.getEmail());
			participantRequest.setMobilePhone(participant.getMobilePhone());
			if (participant.getGender() != null && !participant.getGender().isEmpty()) {
				if ((participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE) || participant.getGender()
						.equalsIgnoreCase(PMPConstants.GENDER_MALE)))
					participant
							.setGender(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) ? PMPConstants.MALE
									: PMPConstants.FEMALE);
			}

			participantRequest.setGender(participant.getGender());
			participantRequest.setDateOfBirth((null != participant.getDateOfBirth()) ? convertedsdf.format(participant
					.getDateOfBirth()) : null);
			participantRequest.setAddressLine1(participant.getAddressLine1());
			participantRequest.setAddressLine2(participant.getAddressLine2());
			participantRequest.setCity(participant.getCity());
			participantRequest.setState(participant.getState());
			participantRequest.setCountry(participant.getCountry());
			participantRequest.setIntroducedStatus(0 != participant.getIntroduced() ? PMPConstants.REQUIRED_YES
					: PMPConstants.REQUIRED_NO);
			participantRequest.setIntroductionDate((null != participant.getIntroductionDate()) ? convertedsdf
					.format(participant.getIntroductionDate()) : null);
			participantRequest.setAbhyasiId(participant.getAbhyasiId());
			participantRequest.setIntroducedBy(participant.getIntroducedBy());
			if (null == participant.getFirstSittingDate()) {
				participantRequest.setFirstSittingDate("");
			} else {
				try {
					participantRequest.setFirstSittingDate(convertedsdf.format(participant.getFirstSittingDate()));
				} catch (Exception e) {
					participantRequest.setFirstSittingDate("");
				}
			}
			if (null == participant.getSecondSittingDate()) {
				participantRequest.setSecondSittingDate("");
			} else {
				try {
					participantRequest.setSecondSittingDate(convertedsdf.format(participant.getSecondSittingDate()));
				} catch (Exception e) {
					participantRequest.setSecondSittingDate("");
				}
			}
			if (null == participant.getThirdSittingDate()) {
				participantRequest.setThirdSittingDate("");
			} else {
				try {
					participantRequest.setThirdSittingDate(convertedsdf.format(participant.getThirdSittingDate()));
				} catch (Exception e) {
					participantRequest.setThirdSittingDate("");
				}
			}
			participantRequest.setFirstSitting((null != participant.getFirstSitting() && 1 == participant
					.getFirstSitting()) ? PMPConstants.REQUIRED_YES : PMPConstants.REQUIRED_NO);
			participantRequest.setSecondSitting((null != participant.getSecondSitting() && 1 == participant
					.getSecondSitting()) ? PMPConstants.REQUIRED_YES : PMPConstants.REQUIRED_NO);
			participantRequest.setThirdSitting((null != participant.getThirdSitting() && 1 == participant
					.getThirdSitting()) ? PMPConstants.REQUIRED_YES : PMPConstants.REQUIRED_NO);

			participantRequest.seteWelcomeID((null != participant.getWelcomeCardNumber() && !participant
					.getWelcomeCardNumber().isEmpty()) ? participant.getWelcomeCardNumber() : null);
			participantRequest.setEwelcomeIdRemarks(participant.getEwelcomeIdRemarks());
			return participantRequest;
		} else {
			return null;
		}

	}

	/**
	 * Service to get the participant details depending on the values given in
	 * the search request.
	 * 
	 * @param searchRequest
	 * @return
	 */
	@Override
	public List<ParticipantRequest> searchParticipants(SearchRequest searchRequest) {
		List<Participant> participantList = new ArrayList<Participant>();
		List<ParticipantRequest> participantReqList = new ArrayList<ParticipantRequest>();
		SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		for (ParticipantSearchField searchField : ParticipantSearchField.values()) {
			if (searchField.name().equals(searchRequest.getSearchField())) {
				searchRequest.setSearchField(searchField.getValue());
			}
			if (searchField.name().equals(searchRequest.getSortBy())) {
				searchRequest.setSortBy(searchField.getValue());
			}
		}
		participantList = participantRepository.getParticipantList(searchRequest);
		for (Participant participant : participantList) {
			ParticipantRequest participantReq = new ParticipantRequest();
			participantReq.setSeqId(participant.getSeqId());
			participantReq.setEventId(programService.getEventIdByProgramID(participant.getProgramId()));
			participantReq.setPrintName(participant.getPrintName());
			participantReq.setEmail(participant.getEmail());
			participantReq.setMobilePhone(participant.getMobilePhone());

			if (null == participant.getGender()) {
				participantReq.setGender("");
			} else if (PMPConstants.GENDER_MALE.equals(participant.getGender())) {
				participantReq.setGender(PMPConstants.MALE);
			} else if (PMPConstants.GENDER_FEMALE.equals(participant.getGender())) {
				participantReq.setGender(PMPConstants.FEMALE);
			} else {
				participantReq.setGender("");
			}

			if (null == participant.getDateOfBirth()) {
				participantReq.setDateOfBirth("");
			} else {
				try {
					participantReq.setDateOfBirth(convertedsdf.format(participant.getDateOfBirth()));
				} catch (Exception e) {
					participantReq.setDateOfBirth("");
				}
			}

			participantReq.setAddressLine1(participant.getAddressLine1());
			participantReq.setAddressLine2(participant.getAddressLine2());
			participantReq.setCity(participant.getCity());
			participantReq.setState(participant.getState());
			participantReq.setCountry(participant.getCountry());
			participantReq.setAbhyasiId(participant.getAbhyasiId());
			participantReq.setIntroducedBy(participant.getIntroducedBy());

			if (1 == participant.getIntroduced()) {
				participantReq.setIntroducedStatus(PMPConstants.REQUIRED_YES);
			} else if (0 == participant.getIntroduced()) {
				participantReq.setIntroducedStatus(PMPConstants.REQUIRED_NO);
			} else {
				participantReq.setIntroducedStatus("");
			}

			if (null == participant.getIntroductionDate()) {
				participantReq.setIntroductionDate("");
			} else {
				try {
					participantReq.setIntroductionDate(convertedsdf.format(participant.getIntroductionDate()));
				} catch (Exception e) {
					participantReq.setIntroductionDate("");
				}
			}

			participantReqList.add(participantReq);
		}
		return participantReqList;
	}

	/**
	 * Retrieve <code>Participant</code> from the data store by SeqID.
	 * 
	 * @param participantRequest
	 * @return <code>Participant</code>
	 */
	@Override
	public Participant findBySeqId(ParticipantRequest participantRequest) {
		if (0 != programrepository.getProgramIdByEventId(participantRequest.getEventId())) {
			participantRequest.setProgramId(programrepository.getProgramIdByEventId(participantRequest.getEventId()));
			Participant newParticipant = programrepository.findParticipantBySeqId(participantRequest.getSeqId(),
					participantRequest.getProgramId());
			return newParticipant;
		} else {
			return null;
		}
	}

	@Override
	public List<UpdateIntroductionResponse> introduceParticipants(ParticipantIntroductionRequest participantRequest,
			String userEmailID, int id) throws HttpClientErrorException, JsonParseException, JsonMappingException,
			IOException, ParseException {
		List<UpdateIntroductionResponse> result = new ArrayList<UpdateIntroductionResponse>();
		List<String> description = null;
		for (ParticipantRequest participant : participantRequest.getParticipantIds()) {
			LOGGER.debug("Generating eWelcomeID for the seqID : {} ", participant.getSeqId());
			String eWelcomeID = null;
			if (null == participant.getSeqId() || participant.getSeqId().isEmpty()) {
				description = new ArrayList<String>();
				description.add("Seq Id is required.");
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participant.getSeqId(),
						participant.getPrintName(), ErrorConstants.STATUS_FAILED, description);
				result.add(response);
			} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
				description = new ArrayList<String>();
				description.add("Invalid eventID");
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participantRequest.getEventId(),
						participant.getPrintName(), ErrorConstants.STATUS_FAILED, description);
				result.add(response);
			} else if (0 != programService.getProgramIdByEventId(participantRequest.getEventId())
					&& null == programService.findParticipantBySeqId(participant.getSeqId(),
							programService.getProgramIdByEventId(participantRequest.getEventId()))) {
				description = new ArrayList<String>();
				description.add("Invalid seqId");
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participant.getSeqId(),
						participant.getPrintName(), ErrorConstants.STATUS_FAILED, description);
				result.add(response);
			} else {
				int programID = programService.getProgramIdByEventId(participantRequest.getEventId());
				Participant participantInput = programService.findParticipantBySeqId(participant.getSeqId(), programID);
				// try {
				if ("Y".equalsIgnoreCase(participantRequest.getIntroduced())) {
					UpdateIntroductionResponse response = null;
					List<String> errorResult = eventDashboardValidator.checkParticipantIntroductionMandatoryFields(
							participantInput, id);
					if (!errorResult.isEmpty()) {
						response = new UpdateIntroductionResponse(participant.getSeqId(),
								participantInput.getPrintName(), ErrorConstants.STATUS_FAILED, errorResult);
						result.add(response);
					} else {
						LOGGER.info("START - {} : Generating eWelcomeID for the participant : {} ",
								participantInput.getSeqId(), participantInput.getEmail());
						eWelcomeID = programService.generateeWelcomeID(participantInput, id);
						if ("success".equalsIgnoreCase(eWelcomeID)) {
							programService.updateParticipantsStatus(participant.getSeqId(),
									participantRequest.getEventId(), participantRequest.getIntroduced(), userEmailID);
							description = new ArrayList<String>();
							description.add("Participant eWelcomeID : " + participantInput.getWelcomeCardNumber());
							response = new UpdateIntroductionResponse(participant.getSeqId(),
									participantInput.getPrintName(), ErrorConstants.STATUS_SUCCESS, description);
						} else {
							participantInput.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
							participantInput.setIsEwelcomeIdInformed(0);
							participantInput.setEwelcomeIdRemarks(eWelcomeID);
							participantRepository.save(participantInput);
							description = new ArrayList<String>();
							description.add(eWelcomeID);
							response = new UpdateIntroductionResponse(participant.getSeqId(),
									participantInput.getPrintName(), ErrorConstants.STATUS_FAILED, description);
						}
						result.add(response);
						LOGGER.info("END - {} : Response of eWelcomeID Generation for the participant : {} ",
								participantInput.getSeqId(), result.toString());
					}
				} else {
					LOGGER.info("START - {} : Updating participant Status : {} ", participantInput.getSeqId(),
							participantInput.getEmail());
					programService.updateParticipantsStatus(participant.getSeqId(), participantRequest.getEventId(),
							participantRequest.getIntroduced(), userEmailID);
					description = new ArrayList<String>();
					description.add("Participant introduced status updated successfully.");
					UpdateIntroductionResponse response = new UpdateIntroductionResponse(participant.getSeqId(),
							participantInput.getPrintName(), ErrorConstants.STATUS_SUCCESS, description);
					result.add(response);
					LOGGER.info("END - {} : Response of update introduction status for the participant : {} ",
							participantInput.getSeqId(), result.toString());
				}
			}
		}
		return result;
	}

	@Override
	public List<UpdateIntroductionResponse> deleteParticipantsBySeqID(
			ParticipantIntroductionRequest participantRequest, String userEmailID) {
		List<UpdateIntroductionResponse> result = new ArrayList<UpdateIntroductionResponse>();
		List<String> description = null;
		for (ParticipantRequest participant : participantRequest.getParticipantIds()) {
			if (null == participant.getSeqId() || participant.getSeqId().isEmpty()) {
				description = new ArrayList<String>();
				description.add("Invalid SeqID");
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participant.getSeqId(),
						participant.getPrintName(), ErrorConstants.STATUS_FAILED, description);

				result.add(response);
			} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
				description = new ArrayList<String>();
				description.add("Invalid eventID");
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participantRequest.getEventId(),
						participant.getPrintName(), ErrorConstants.STATUS_FAILED, description);
				result.add(response);
			} else if (0 != programService.getProgramIdByEventId(participantRequest.getEventId())
					&& null == programService.findParticipantBySeqId(participant.getSeqId(),
							programService.getProgramIdByEventId(participantRequest.getEventId()))) {
				description = new ArrayList<String>();
				description.add("Invalid seqId");
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participant.getSeqId(),
						participant.getPrintName(), ErrorConstants.STATUS_FAILED, description);
				result.add(response);
			} else {
				Participant deletedParticipant = programService.deleteParticipant(participant.getSeqId(),
						participantRequest.getEventId());
				description = new ArrayList<String>();
				description.add("Participant deleted successfully");
				programService.updateDeletedParticipant(deletedParticipant, userEmailID);
				UpdateIntroductionResponse response = new UpdateIntroductionResponse(participant.getSeqId(),
						deletedParticipant.getPrintName(), ErrorConstants.STATUS_SUCCESS, description);
				result.add(response);
			}
		}
		return result;
	}

	/**
	 * Service to update the participant ewelcome Id status and remarks.
	 * 
	 * @param programId
	 * @param eWelcomeIDStatus
	 * @param remarks
	 */
	@Override
	public void updatePartcipantEWelcomeIDStatuswithProgramID(int programId, String eWelcomeIDStatus, String remarks) {
		List<Participant> participants = participantRepository.findByProgramId(programId);
		Program program = programrepository.findById(programId);
		for (Participant participant : participants) {
			setParticipantEWelcomeIDStatus(program, participant, eWelcomeIDStatus, remarks);
			participantRepository.updateParticipantEwelcomeIDDetails(participant);
		}
	}

	private void setParticipantEWelcomeIDStatus(Program program, Participant participant, String eWelcomeIDStatus,
			String remarks) {

		if (null != participant.getWelcomeCardNumber() && !participant.getWelcomeCardNumber().isEmpty()) {
			participant.setEwelcomeIdRemarks(null);
			participant.setIntroduced(1);
			if (null != participant.getWelcomeCardDate()) {
				participant.setIntroductionDate(participant.getWelcomeCardDate());
			} else {
				participant.setWelcomeCardDate(new Date());
				participant.setIntroductionDate(new Date());
			}
			participant.setIsEwelcomeIdInformed((null != participant.getIsEwelcomeIdInformed() && participant
					.getIsEwelcomeIdInformed() == 1) ? 1 : 0);
			participant.setIntroducedBy(program.getCoordinatorEmail());
			participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_COMPLETED_STATE);
		} else if (!eventDashboardValidator.validateParticipantCompletedPreliminarySittings(participant)) {
			participant.setEwelcomeIdRemarks((null != remarks && !remarks.isEmpty()) ? remarks
					: ErrorConstants.PRELIMINARY_SITTINGS_NOT_COMPLETED);
			participant.setIsEwelcomeIdInformed(0);
			participant.setEwelcomeIdState(PMPConstants.EWELCOMEID_FAILED_STATE);
		} else {
			if (eWelcomeIDStatus.equals(PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE)
					&& program.getIsEwelcomeIdGenerationDisabled().equals(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE)) {
				participant.setEwelcomeIdRemarks(remarks);
				participant.setEwelcomeIdState(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE);
				participant.setIsEwelcomeIdInformed(0);
			} else {
				participant.setEwelcomeIdRemarks(remarks);
				participant.setEwelcomeIdState(eWelcomeIDStatus);
				participant.setIsEwelcomeIdInformed(0);
			}
		}
	}
	
    @Override
    public ResponseEntity<?> validateExcelAndPersistParticipantData(String originalFilename, byte[] bytes,PMPAPIAccessLog accessLog,List<String> details) {

        Workbook workBook = null;
        try{
            workBook = ExcelParserUtils.getWorkbook(originalFilename, bytes);
        }catch(InvalidExcelFileException iefex){
            LOGGER.error("File extension should be xlsx/xlsm/xls",iefex);
        }catch(Exception ex){
            LOGGER.error("File extension should be xlsx/xlsm/xls",ex);
        }

        if(null == workBook){
            ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,"File extension should be xlsx/xlsm/xls");
            accessLog.setErrorMessage("File extension should be xlsx/xlsm/xls");
            accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
            return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
        }

        Sheet participantSheet = workBook.getSheet(EventDetailsUploadConstants.PARTICIPANT_SHEET_NAME);
        if (null == participantSheet) {
            ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,"Participants Details Sheet is not present/invalid or empty");
            accessLog.setErrorMessage("Participants Details Sheet is not present/invalid or empty");
            accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(eResponse));
            return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.PRECONDITION_FAILED);
        }

        List<String> errorList = new ArrayList<>();
        ExcelV2ValidatorImpl v2ValidatorImpl = new ExcelV2ValidatorImpl();
        v2ValidatorImpl.validateParticipantDetails(participantSheet,errorList);
        ExcelUploadResponse excelUploadResponse = new ExcelUploadResponse();
        excelUploadResponse.setFileName(originalFilename);
        if(!errorList.isEmpty()){
            excelUploadResponse.setStatus(ErrorConstants.STATUS_FAILED);
            excelUploadResponse.setErrorMsg(errorList);

            accessLog.setErrorMessage("Errors validating Participant Details sheet structure");
            accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
            return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.PRECONDITION_FAILED);
        }

        v2ValidatorImpl.checkParticipantMandatoryFields(participantSheet,errorList);
        if(!errorList.isEmpty()){
            excelUploadResponse.setStatus(ErrorConstants.STATUS_FAILED);
            excelUploadResponse.setErrorMsg(errorList);

            accessLog.setErrorMessage("Errors while validating Participant Details mandatory fields");
            accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
            accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
            return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.PRECONDITION_FAILED);
        }

        boolean disableEwelcomeIdGeneration = false;
        if(null != details.get(1)){
            if(details.get(1).equals(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE)){
                disableEwelcomeIdGeneration = true;
            }
        }
        List<Participant> participantList = null; 
        try{
            ExcelDataExtractorV2Impl v2ExtractorImpl = new ExcelDataExtractorV2Impl();
            participantList = v2ExtractorImpl.getParticipantList(participantSheet,disableEwelcomeIdGeneration,Integer.parseInt(details.get(2))+1);
        }catch (Exception ex){
            LOGGER.error("Error while extracting participant details",ex);
            ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,"Failed to save participant records");
            accessLog.setErrorMessage("Failed to extract participant records");
            accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
            accessLog.setResponseBody(StackTraceUtils.convertStackTracetoString(ex));
            return new ResponseEntity<ErrorResponse>(eResponse,HttpStatus.BAD_REQUEST);
        }

        Program pgrm = new Program();
        pgrm.setIsEwelcomeIdGenerationDisabled(details.get(1));
        pgrm.setCoordinatorEmail(details.get(3));
        for (Participant participant : participantList) {
            participant.setProgramId(Integer.parseInt(details.get(0)));
            participant.setCreatedSource(PMPConstants.CREATED_SOURCE_EXCEL_VIA_DASHBOARD);
            setParticipantEWelcomeIDStatus(pgrm,participant,PMPConstants.EWELCOMEID_TO_BE_CREATED_STATE,null);
            try{
                participantRepository.save(participant);
            }catch(Exception ex){
                errorList.add("Failed to persist participant "+participant.getPrintName());
            }
            
        }

        excelUploadResponse.setStatus(ErrorConstants.STATUS_SUCCESS);
        excelUploadResponse.setErrorMsg(errorList);

        accessLog.setErrorMessage("");
        accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
        accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(excelUploadResponse));
        return new ResponseEntity<ExcelUploadResponse>(excelUploadResponse,HttpStatus.OK);
    }


}
