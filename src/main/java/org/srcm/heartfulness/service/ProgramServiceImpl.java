package org.srcm.heartfulness.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.EventSearchField;
import org.srcm.heartfulness.helper.EWelcomeIDGenerationHelper;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.AbhyasiResult;
import org.srcm.heartfulness.model.json.response.AbhyasiUserProfile;
import org.srcm.heartfulness.model.json.response.EWelcomeIDErrorResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.validator.EventDashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProgramServiceImpl implements ProgramService {

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	ParticipantRepository participantRepository;

	@Autowired
	EWelcomeIDGenerationHelper eWelcomeIDGenerationHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.ProgramService#createProgram(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public Program createProgram(Program program) {
		programRepository.save(program);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.srcm.heartfulness.service.ProgramService#isProgramExist(Program
	 * program)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isProgramExist(Program program) {
		return programRepository.isProgramExist(program);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.srcm.heartfulness.service.ProgramService#findByAutoGeneratedEventId
	 * (java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedEventId(String autoGeneratedEventid) {

		return programRepository.findByAutoGeneratedEventId(autoGeneratedEventid);
	}

	@Override
	public Program getProgramById(int id) {
		return programRepository.getEventById(id);
	}

	@Override
	public List<Program> getProgramByEmail(String email, boolean isAdmin) {
		return programRepository.getEventsByEmail(email, isAdmin);
	}

	/**
	 * Retrieve <code>List<Participant></code> from the data store by ProgramId.
	 * 
	 * @param decryptedProgramId
	 * @return
	 */
	@Override
	public List<Participant> getParticipantByProgramId(int decryptedProgramId) {
		return programRepository.getParticipantList(decryptedProgramId);
	}

	/**
	 * Get the list of events depending on the coordinator email
	 * 
	 * @param email
	 * @param isAdmin
	 * @return List<Event>
	 */
	@Override
	public List<Event> getEventListByEmail(String email, boolean isAdmin) {
		List<Event> eventList = new ArrayList<Event>();
		List<Program> programList = programRepository.getEventsByEmail(email, isAdmin);
		SimpleDateFormat convertedsdf = new SimpleDateFormat("dd-MM-yyyy");
		for (Program program : programList) {
			Event event = new Event();
			event.setAutoGeneratedEventId(program.getAutoGeneratedEventId());
			event.setProgramChannel(program.getProgramChannel());
			event.setProgramName(program.getProgramName());
			if (null == program.getProgramStartDate()) {
				event.setProgramStartDate("");
			} else {
				try {
					event.setProgramStartDate(convertedsdf.format(program.getProgramStartDate()));
				} catch (Exception e) {
					event.setProgramStartDate("");
				}
			}
			if (null == program.getProgramEndDate()) {
				event.setProgramEndDate("");
			} else {
				try {
					event.setProgramEndDate(convertedsdf.format(program.getProgramEndDate()));
				} catch (Exception e) {
					event.setProgramEndDate("");
				}
			}
			event.setCoordinatorName(program.getCoordinatorName());
			event.setCoordinatorMobile(program.getCoordinatorMobile());
			event.setCoordinatorEmail(program.getCoordinatorEmail());
			event.setEventPlace(program.getEventPlace());
			event.setEventCity(program.getEventCity());
			event.setEventState(program.getEventState());
			event.setEventCountry(program.getEventCountry());
			event.setPreceptorName(program.getPreceptorName());
			event.setPreceptorIdCardNumber(program.getPreceptorIdCardNumber());
			event.setRemarks(program.getRemarks());
			eventList.add(event);
		}

		return eventList;
	}

	/**
	 * Returns the list of Participant details for a given auto
	 * GeneratedEventId.
	 * 
	 * @param eventId
	 * @return List<ParticipantRequest>
	 */
	@Override
	public List<ParticipantRequest> getParticipantByEventId(String eventId) {
		List<Participant> participantList = new ArrayList<Participant>();
		List<ParticipantRequest> participantReqList = new ArrayList<ParticipantRequest>();

		int programId = programRepository.getProgramIdByEventId(eventId);
		if (programId == 0) {
			return participantReqList;
		} else {
			SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
			participantList = programRepository.getParticipantList(programId);

			for (Participant participant : participantList) {
				ParticipantRequest participantReq = new ParticipantRequest();
				participantReq.setSeqId(participant.getSeqId());
				participantReq.setEventId(eventId);
				participantReq.setPrintName(participant.getPrintName());
				participantReq.setEmail(participant.getEmail());
				participantReq.setMobilePhone(participant.getMobilePhone());
				if (null == participant.getIntroductionDate()) {
					participantReq.setIntroductionDate("");
				} else {
					try {
						participantReq.setIntroductionDate(convertedsdf.format(participant.getIntroductionDate()));
					} catch (Exception e) {
						participantReq.setIntroductionDate("");
					}
				}
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

				if (null == participant.getFirstSittingDate()) {
					participantReq.setFirstSittingDate("");
				} else {
					try {
						participantReq.setFirstSittingDate(convertedsdf.format(participant.getFirstSittingDate()));
					} catch (Exception e) {
						participantReq.setFirstSittingDate("");
					}
				}
				if (null == participant.getSecondSittingDate()) {
					participantReq.setSecondSittingDate("");
				} else {
					try {
						participantReq.setSecondSittingDate(convertedsdf.format(participant.getSecondSittingDate()));
					} catch (Exception e) {
						participantReq.setSecondSittingDate("");
					}
				}
				if (null == participant.getThirdSittingDate()) {
					participantReq.setThirdSittingDate("");
				} else {
					try {
						participantReq.setThirdSittingDate(convertedsdf.format(participant.getThirdSittingDate()));
					} catch (Exception e) {
						participantReq.setThirdSittingDate("");
					}
				}
				participantReq.setFirstSitting((null!=participant.getFirstSitting() && 1 == participant.getFirstSitting())? PMPConstants.REQUIRED_YES
						: PMPConstants.REQUIRED_NO);
				participantReq.setSecondSitting((null!=participant.getSecondSitting() && 1 == participant.getSecondSitting()) ? PMPConstants.REQUIRED_YES
						: PMPConstants.REQUIRED_NO);
				participantReq.setThirdSitting((null!=participant.getThirdSitting() && 1 == participant.getThirdSitting()) ? PMPConstants.REQUIRED_YES
						: PMPConstants.REQUIRED_NO);
				participantReq.setAddressLine1(participant.getAddressLine1());
				participantReq.setAddressLine2(participant.getAddressLine2());
				participantReq.setCity(participant.getCity());
				participantReq.setState(participant.getState());
				participantReq.setCountry(participant.getCountry());
				participantReq.setAbhyasiId(participant.getAbhyasiId());
				participantReq.setIntroducedBy(participant.getIntroducedBy());
				participantReq.setIntroducedStatus(1 == participant.getIntroduced() ? PMPConstants.REQUIRED_YES
						: PMPConstants.REQUIRED_NO);
				participantReq.seteWelcomeID((null != participant.getWelcomeCardNumber() && !participant
						.getWelcomeCardNumber().isEmpty()) ? participant.getWelcomeCardNumber() : null);
				participantReqList.add(participantReq);
			}
			return participantReqList;
		}
	}

	/**
	 * This service method is used to create a new record or update an existing
	 * record.
	 * 
	 * @param events
	 *            List<Event> is sent to this service method as an argument
	 *            against which mandatory,duplicate eventId and other
	 *            validations are performed.
	 * @return List<Event>
	 * @throws InvalidDateException
	 *             if the program_start_date is in invalid format.
	 */
	@Override
	public List<Event> createOrUpdateEvent(List<Event> events) {
		SimpleDateFormat initialsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		for (Event event : events) {

			Map<String, String> errors = new HashMap<String, String>();

			errors = eventDashboardValidator.checkMandatoryEventFields(event);
			if (!errors.isEmpty()) {
				event.setErrors(errors);
				event.setStatus(ErrorConstants.STATUS_FAILED);
			} else {

				Program program = new Program();
				program.setProgramChannel(event.getProgramChannel());
				try {
					program.setProgramStartDate(initialsdf.parse(event.getProgramStartDate()));
				} catch (ParseException e) {
					errors.put("programStartDate", "Error while parsing Program Start Date");
					event.setErrors(errors);
				}

				if (null == event.getProgramEndDate()) {
					program.setProgramEndDate(null);
				} else if (event.getProgramEndDate().isEmpty()) {
					program.setProgramEndDate(null);
				} else {
					try {
						program.setProgramEndDate(initialsdf.parse(event.getProgramEndDate()));
					} catch (ParseException e) {
						errors.put("programEndDate", "Error while parsing Program End Date");
						event.setErrors(errors);
					}
				}
				program.setAutoGeneratedEventId(null != event.getAutoGeneratedEventId() ? event
						.getAutoGeneratedEventId() : null);
				program.setProgramName(event.getProgramName());
				program.setCoordinatorName(event.getCoordinatorName());
				program.setCoordinatorEmail(event.getCoordinatorEmail());
				program.setCoordinatorMobile(event.getCoordinatorMobile());
				program.setEventPlace(event.getEventPlace());
				program.setEventCity(event.getEventCity());
				program.setEventState(event.getEventState());
				program.setEventCountry(event.getEventCountry());
				program.setOrganizationDepartment(event.getOrganizationDepartment());
				program.setOrganizationName(event.getOrganizationName());
				program.setOrganizationWebSite(event.getOrganizationWebSite());
				program.setOrganizationContactName(event.getOrganizationContactName());
				program.setOrganizationContactEmail(event.getOrganizationContactEmail());
				program.setOrganizationContactMobile(event.getOrganizationContactMobile());
				program.setPreceptorName(event.getPreceptorName());
				program.setPreceptorIdCardNumber(event.getPreceptorIdCardNumber());
				program.setWelcomeCardSignedByName(event.getWelcomeCardSignedByName());
				program.setWelcomeCardSignerIdCardNumber(event.getWelcomeCardSignerIdCardNumber());
				program.setRemarks(event.getRemarks());

				/*
				 * if (null != event.getAbhyasiID() &&
				 * !event.getAbhyasiID().isEmpty()) { try { Result result =
				 * srcmRestTemplate.getAbyasiProfile(event.getAbhyasiID()); if
				 * (result.getUserProfile().length > 0) { UserProfile
				 * userProfile = result.getUserProfile()[0]; if (null !=
				 * userProfile) { if (true == userProfile.isIs_prefect() && 0 !=
				 * userProfile.getPrefect_id()) {
				 * program.setAbyasiRefNo(event.getAbhyasiID());
				 * program.setPrefectId
				 * (String.valueOf(userProfile.getPrefect_id()));
				 * program.setSrcmGroup
				 * (String.valueOf(userProfile.getSrcm_group())); if (null ==
				 * event.getCoordinatorEmail() ||
				 * event.getCoordinatorEmail().isEmpty()) {
				 * program.setCoordinatorEmail(userProfile.getUser_email()); }
				 * if (null == event.getCoordinatorName() ||
				 * event.getCoordinatorName().isEmpty()) {
				 * program.setCoordinatorName(userProfile.getName()); } if (null
				 * == event.getCoordinatorMobile() ||
				 * event.getCoordinatorMobile().isEmpty()) { if (null !=
				 * userProfile.getMobile() &&
				 * !userProfile.getMobile().isEmpty()) {
				 * program.setCoordinatorMobile(userProfile.getMobile()); } else
				 * if (null != userProfile.getMobile2() &&
				 * !userProfile.getMobile2().isEmpty()) {
				 * program.setCoordinatorMobile(userProfile.getMobile2()); }
				 * else if (null != userProfile.getPhone() &&
				 * !userProfile.getPhone().isEmpty()) {
				 * program.setCoordinatorMobile(userProfile.getPhone()); } else
				 * if (null != userProfile.getPhone2() &&
				 * !userProfile.getPhone2().isEmpty()) {
				 * program.setCoordinatorMobile(userProfile.getPhone2()); } } }
				 * else { errors.put("abhyasi_ID", "Invalid abhyasiID");
				 * event.setErrors(errors); } } } else {
				 * errors.put("abhyasi_ID", "Invalid abhyasiID");
				 * event.setErrors(errors); } } catch (HttpClientErrorException
				 * | IOException e) { e.printStackTrace();
				 * errors.put("abhyasi_ID", "Invalid abhyasiID");
				 * event.setErrors(errors); } }
				 */

				if (errors.isEmpty()) {
					Program persistedPgrm = programRepository.saveProgram(program);
					event.setAutoGeneratedEventId(persistedPgrm.getAutoGeneratedEventId());
					event.setStatus(ErrorConstants.STATUS_SUCCESS);
				} else {
					event.setStatus(ErrorConstants.STATUS_FAILED);
				}

			}
		}

		return events;

	}

	/**
	 * Service to get the total number of available events count based on the
	 * user email and the user role.
	 * 
	 * @param username
	 * @param isAdmin
	 * @return count(int)
	 */
	@Override
	public int getEventCountByEmail(String username, boolean isAdmin) {
		return programRepository.getEventCountByEmail(username, isAdmin);
	}

	/**
	 * Service to get the non categorized events count based on the user email
	 * and the user role.
	 * 
	 * @param coOrdinator
	 * @param isAdmin
	 * @return count(int)
	 */
	@Override
	public int getNonCategorizedEventsByEmail(String username, boolean isAdmin) {
		return programRepository.getNonCategorizedEventsByEmail(username, isAdmin);
	}

	/**
	 * Service to get the participant for the given programID and seq ID.
	 * 
	 * @param seqId
	 * @param programId
	 * @return Participant
	 */
	@Override
	public Participant findParticipantBySeqId(String seqId, int programId) {
		return programRepository.findParticipantBySeqId(seqId, programId);
	}

	/**
	 * Service to the programId for the given auto generated eventID.
	 * 
	 * @param eventId
	 * @return programId
	 */
	@Override
	public int getProgramIdByEventId(String eventID) {
		return programRepository.getProgramIdByEventId(eventID);
	}

	/**
	 * Service to update the participant introduced status for the given
	 * participant Ids of an given eventID.
	 * 
	 * @param participantIds
	 * @param eventId
	 * @param introduced
	 */
	@Override
	public void UpdateParticipantsStatus(String participantIds, String eventId, String introduced) {
		programRepository.UpdateParticipantsStatus(participantIds, eventId, introduced);
	}

	/**
	 * Service to get the all available event categories from the database.
	 * 
	 * @return
	 */
	@Override
	public List<String> getAllEventCategories() {
		return programRepository.getAllEventCategories();
	}

	/**
	 * Service to get the event count based on the user email and the user role
	 * and event category.
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventCategory
	 * @return count of events
	 */
	@Override
	public int getEventCountByCategory(String email, boolean isAdmin, String eventCategory) {
		return programRepository.getEventCountByCategory(email, isAdmin, eventCategory);
	}

	/**
	 * Service to get the miscillaneous event count based on the user email and
	 * the user role and event eventcategories.
	 * 
	 * @param email
	 * @param isAdmin
	 * @param eventcategories
	 * @return
	 */
	@Override
	public int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories) {
		return programRepository.getMiscellaneousEventsByEmail(email, isAdmin, eventcategories);
	}

	/**
	 * Service to update the admin for the event.
	 * 
	 * @param eventAdminChangeRequest
	 */
	@Override
	public void updateEventAdmin(EventAdminChangeRequest eventAdminChangeRequest) {
		Program program = programRepository.findByAutoGeneratedEventId(eventAdminChangeRequest.getEventId());
		eventAdminChangeRequest.setOldCoordinatorEmail(program.getCoordinatorEmail());
		program.setCoordinatorEmail(eventAdminChangeRequest.getNewCoordinatorEmail());
		program.setCoordinatorMobile(eventAdminChangeRequest.getCoordinatorMobile());
		program.setCoordinatorName(eventAdminChangeRequest.getCoordinatorName());
		programRepository.save(program);
	}

	/**
	 * Service to update the co-ordinator details in the database after changing
	 * admin for the event.
	 * 
	 * @param eventAdminChangeRequest
	 */
	@Override
	public void updateCoOrdinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest) {
		programRepository.updateCoOrdinatorStatistics(eventAdminChangeRequest);
	}

	/**
	 * Service to get all the available co-ordinators list from the database.
	 * 
	 * @return List<Coordinator>
	 */
	@Override
	public List<Coordinator> getAllCoOrdinatorsList() {
		return programRepository.getAllCoOrdinatorsList();
	}

	/**
	 * Service to get the uncategorized event list for the given email and user
	 * role.
	 * 
	 * @param email
	 * @param isAdmin
	 * @return
	 */
	@Override
	public List<String> getUncategorizedEvents(String username, boolean isAdmin) {
		return programRepository.getNonCategorizedEventListByEmail(username, isAdmin);
	}

	/**
	 * Service to delete the participant for the given eventId and seqId.
	 * 
	 * @param seqId
	 * @param eventId
	 * @return
	 */
	@Override
	public Participant deleteParticipant(String seqId, String eventId) {
		return programRepository.deleteParticipant(seqId, eventId);
	}

	/**
	 * Service to update the deleted participant details to the database.
	 * 
	 * @param deletedParticipant
	 * @param deletedBy
	 */
	@Override
	public void updateDeletedParticipant(Participant deletedParticipant, String deletedBy) {
		programRepository.updateDeletedParticipant(deletedParticipant, deletedBy);
	}

	/**
	 * Service method to get the event details for the given eventID.
	 * 
	 * @param EventId
	 * @return Event
	 */
	@Override
	public Event getEventDetails(String agEventId) {
		Event eventDetails = new Event();
		SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		Program program = programRepository.findByAutoGeneratedEventId(agEventId);

		eventDetails.setProgramChannel(program.getProgramChannel());
		eventDetails.setProgramName(program.getProgramName());
		if (null == program.getProgramStartDate()) {
			eventDetails.setProgramStartDate("");
		} else {
			try {
				eventDetails.setProgramStartDate(convertedsdf.format(program.getProgramStartDate()));
			} catch (Exception e) {
				eventDetails.setProgramStartDate("");
			}
		}
		if (null == program.getProgramEndDate()) {
			eventDetails.setProgramEndDate("");
		} else {
			try {

				eventDetails.setProgramEndDate(convertedsdf.format(program.getProgramEndDate()));
			} catch (Exception e) {
				eventDetails.setProgramEndDate("");
			}
		}
		eventDetails.setAutoGeneratedEventId(program.getAutoGeneratedEventId());
		eventDetails.setCoordinatorName(program.getCoordinatorName());
		eventDetails.setCoordinatorMobile(program.getCoordinatorMobile());
		eventDetails.setCoordinatorEmail(program.getCoordinatorEmail());

		eventDetails.setEventPlace(program.getEventPlace());
		eventDetails.setEventCity(program.getEventCity());
		eventDetails.setEventState(program.getEventState());
		eventDetails.setEventCountry(program.getEventCountry());

		eventDetails.setOrganizationName(program.getOrganizationName());
		eventDetails.setOrganizationWebSite(program.getOrganizationWebSite());

		eventDetails.setOrganizationContactName(program.getOrganizationContactName());

		eventDetails.setOrganizationDepartment(program.getOrganizationDepartment());

		eventDetails.setOrganizationContactMobile(program.getOrganizationContactMobile());

		eventDetails.setOrganizationContactEmail(program.getOrganizationContactEmail());

		eventDetails.setPreceptorName(program.getPreceptorName());

		eventDetails.setPreceptorIdCardNumber(program.getPreceptorIdCardNumber());

		eventDetails.setWelcomeCardSignedByName(program.getWelcomeCardSignedByName());

		eventDetails.setWelcomeCardSignerIdCardNumber(program.getWelcomeCardSignerIdCardNumber());

		eventDetails.setRemarks(program.getRemarks());
		return eventDetails;
	}

	/**
	 * Retrieve <code>List<Event></code> from the data store by values given in
	 * the search request.
	 * 
	 * @param searchRequest
	 * @return
	 */
	@Override
	public List<Event> searchEvents(SearchRequest searchRequest) {
		List<Event> eventList = new ArrayList<Event>();
		for (EventSearchField searchField : EventSearchField.values()) {
			if (searchField.name().equals(searchRequest.getSearchField())) {
				searchRequest.setSearchField(searchField.getValue());
			}
			if (searchField.name().equals(searchRequest.getSortBy())) {
				searchRequest.setSortBy(searchField.getValue());
			}
		}

		List<Program> programList = programRepository.searchEvents(searchRequest);
		SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		for (Program program : programList) {
			Event event = new Event();
			event.setAutoGeneratedEventId(program.getAutoGeneratedEventId());
			event.setProgramChannel(program.getProgramChannel());
			event.setProgramName(program.getProgramName());
			if (null == program.getProgramStartDate()) {
				event.setProgramStartDate("");
			} else {
				try {
					event.setProgramStartDate(convertedsdf.format(program.getProgramStartDate()));
				} catch (Exception e) {
					event.setProgramStartDate("");
				}
			}
			if (null == program.getProgramEndDate()) {
				event.setProgramEndDate("");
			} else {
				try {
					event.setProgramEndDate(convertedsdf.format(program.getProgramEndDate()));
				} catch (Exception e) {
					event.setProgramEndDate("");
				}
			}
			event.setCoordinatorName(program.getCoordinatorName());
			event.setCoordinatorMobile(program.getCoordinatorMobile());
			event.setCoordinatorEmail(program.getCoordinatorEmail());
			event.setEventPlace(program.getEventPlace());
			event.setEventCity(program.getEventCity());
			event.setEventState(program.getEventState());
			event.setEventCountry(program.getEventCountry());
			event.setPreceptorName(program.getPreceptorName());
			event.setPreceptorIdCardNumber(program.getPreceptorIdCardNumber());
			eventList.add(event);
		}

		return eventList;
	}

	/**
	 * Retrieve <code>Auto generated eventId</code> from the data store by
	 * programId.
	 * 
	 * @param programId
	 * @return
	 */
	@Override
	public String getEventIdByProgramID(int programId) {
		return programRepository.getEventIdByProgramID(programId);
	}

	/**
	 * Retrieve <code>e-Welcome ID</code> generated in MySRCM and persist in
	 * data store for the given eventID and seqID.
	 * 
	 * @param seqID
	 * @param eventId
	 * @return
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@Override
	public String generateeWelcomeID(Participant participant) throws HttpClientErrorException, JsonParseException,
	JsonMappingException, IOException {
		try{
			if (participant.getId() > 0 && participant.getProgramId() > 0) {
				if (participant.getSeqId() != null && participant.getSeqId().length() == 4) {
					if (participant.getWelcomeCardNumber() == null || participant.getWelcomeCardNumber().isEmpty() ||
							!participant.getWelcomeCardNumber().matches(EventConstants.EWELCOME_ID_REGEX)) {
						if (null == participant.getProgram().getPrefectId()
								|| participant.getProgram().getPrefectId().isEmpty()) {
							if (null == participant.getProgram().getPreceptorIdCardNumber()
									|| participant.getProgram().getPreceptorIdCardNumber().isEmpty()) {
								return "Preceptor ID is required for the Event";
							} else {
								AbhyasiResult result = srcmRestTemplate.getAbyasiProfile(participant.getProgram()
										.getPreceptorIdCardNumber());
								if (result.getUserProfile().length > 0) {
									AbhyasiUserProfile userProfile = result.getUserProfile()[0];
									if (null != userProfile) {
										if (true == userProfile.isIs_prefect() && 0 != userProfile.getPrefect_id()) {
											Program program = participantRepository.findOnlyProgramById(participant
													.getProgram().getProgramId());
											program.setAbyasiRefNo(participant.getProgram().getPreceptorIdCardNumber());
											program.setPrefectId(String.valueOf(userProfile.getPrefect_id()));
											programRepository.save(program);
										}
									} else {
										return "Invalid preceptor ID";
									}
								} else {
									return "Invalid preceptor ID";
								}
							}
						}
						eWelcomeIDGenerationHelper.generateEWelcomeId(participant);
						participant.setIsEwelcomeIdInformed(0);
						participantRepository.save(participant);
						return "success";
						
					} else {
						return "success";
					}
				} else {
					return "Invalid SeqID";
				}
			} else {
				return "Invalid SeqID/eventID";
			}
		} catch (HttpClientErrorException e) {
			boolean errorMessageParsed=false;
			ObjectMapper mapper = new ObjectMapper();
			EWelcomeIDErrorResponse eWelcomeIDErrorResponse = mapper.readValue(
					e.getResponseBodyAsString(), EWelcomeIDErrorResponse.class);
			if ((null != eWelcomeIDErrorResponse.getEmail() && !eWelcomeIDErrorResponse.getEmail()
					.isEmpty())) {
				errorMessageParsed=true;
				return eWelcomeIDErrorResponse.getEmail().get(0);
			}
			if ((null != eWelcomeIDErrorResponse.getValidation() && !eWelcomeIDErrorResponse
					.getValidation().isEmpty())) {
				errorMessageParsed=true;
				return eWelcomeIDErrorResponse.getValidation().get(0);
			}
			if ((null != eWelcomeIDErrorResponse.getError() && !eWelcomeIDErrorResponse.getError()
					.isEmpty())) {
				errorMessageParsed=true;
				return eWelcomeIDErrorResponse.getError();
			}
			if (!errorMessageParsed) {
				return e.getResponseBodyAsString();
			}
		}
		return null;
	}

}
