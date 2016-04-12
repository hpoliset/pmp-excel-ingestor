package org.srcm.heartfulness.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.helper.CreateEventHelper;
import org.srcm.heartfulness.model.Coordinator;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.repository.ProgramRepository;

@Service
public class ProgramServiceImpl implements ProgramService{

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	CreateEventHelper ceh;

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ProgramService#createProgram(java.lang.String)
	 */
	@Override
	@Transactional
	public Program createProgram(Program program) {
		programRepository.save(program);
		return program;
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ProgramService#isProgramExist(Program program)
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean isProgramExist(Program program) {
		return programRepository.isProgramExist(program);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ProgramService#findByAutoGeneratedEventId(java.lang.String)
	 */
	@Override
	public Program findByAutoGeneratedEventId(String autoGeneratedEventid) {

		return programRepository.findByAutoGeneratedEventId(autoGeneratedEventid);
	}

	@Override
	public List<Program> getProgramByEmail(String email,boolean isAdmin) {
		return programRepository.getEventByEmail(email,isAdmin);
	}

	@Override
	public Program getProgramById(int id) {
		return programRepository.getEventById(id);
	}

	@Override
	public List<Participant> getParticipantByProgramId(int decryptedProgramId) {
		return	programRepository.getParticipantList(decryptedProgramId);
	}

	@Override
	public List<Event> getEventListByEmail(String email,boolean isAdmin) {
		List<Event> eventList  = new ArrayList<Event>();
		List<Program> programList = programRepository.getEventsByEmail(email,isAdmin);
		SimpleDateFormat convertedsdf = new SimpleDateFormat("dd-MM-yyyy");
		for(Program program : programList){
			Event event = new Event();
			event.setAutoGeneratedEventId(program.getAutoGeneratedEventId());
			event.setProgramChannel(program.getProgramChannel());
			event.setProgramName(program.getProgramName());
			if(null == program.getProgramStartDate()){
				event.setProgramStartDate("");
			}else{
				try{
					event.setProgramStartDate(convertedsdf.format(program.getProgramStartDate()));
				}catch(Exception e){
					event.setProgramStartDate("");
				}
			}
			if(null == program.getProgramEndDate()){
				event.setProgramEndDate("");
			}else{
				try{
					event.setProgramEndDate(convertedsdf.format(program.getProgramEndDate()));
				}catch(Exception e){
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





	@Override
	public List<ParticipantRequest> getParticipantByEventId(String eventId) {
		List<Participant> participantList = new ArrayList<Participant>();
		List<ParticipantRequest> participantReqList = new ArrayList<ParticipantRequest>();

		int programId =  programRepository.getProgramIdByEventId(eventId);
		if( programId == 0){
			return participantReqList;
		}else{
			SimpleDateFormat convertedsdf = new SimpleDateFormat("dd-MM-yyyy");
			participantList = programRepository.getParticipantList(programId);

			for(Participant participant : participantList){
				ParticipantRequest participantReq = new ParticipantRequest();
				participantReq.setSeqId(participant.getSeqId());
				participantReq.setEventId(eventId);
				participantReq.setPrintName(participant.getPrintName());
				participantReq.setEmail(participant.getEmail());
				participantReq.setMobilePhone(participant.getMobilePhone());

				if(null == participant.getGender()){
					participantReq.setGender("");
				}else if("M".equals(participant.getGender())){
					participantReq.setGender("Male");
				}else if("F".equals(participant.getGender())){
					participantReq.setGender("Female");
				}else{
					participantReq.setGender("");
				}

				if(null == participant.getDateOfBirth()){
					participantReq.setDateOfBirth("");
				}else{
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

				if(1 == participant.getIntroduced()){
					participantReq.setIntroducedStatus("Y");
				}else if(0 == participant.getIntroduced()){
					participantReq.setIntroducedStatus("N");
				}else{
					participantReq.setIntroducedStatus("");
				}

				if(null == participant.getIntroductionDate()){
					participantReq.setIntroductionDate("");
				}else{
					try {
						participantReq.setIntroductionDate(convertedsdf.format(participant.getIntroductionDate()));
					} catch (Exception e) {
						participantReq.setIntroductionDate("");
					}
				}

				participantReqList.add(participantReq);
			}
			return	participantReqList;
		}
	}


	@Override
	public List<Event> createOrUpdateEvent(List<Event> events){
		SimpleDateFormat initialsdf = new SimpleDateFormat("dd-MM-yyyy");
		for(Event event :events){

			Map<String,String> errors = new HashMap<String, String>();

			errors = ceh.checkMandatoryEventFields(event);
			if(!errors.isEmpty()){
				event.setErrors(errors);
				event.setStatus("Failed");
			}else{

				Program program = new Program();
				program.setProgramChannel(event.getProgramChannel());
				try {
					program.setProgramStartDate(initialsdf.parse(event.getProgramStartDate()));
				} catch (ParseException e) {
					errors.put("programStartDate","Error while parsing Program Start Date");
					event.setErrors(errors);
				}

				if(null == event.getProgramEndDate()){
					program.setProgramEndDate(null);
				}else if(event.getProgramEndDate().isEmpty()){
					program.setProgramEndDate(null);
				}else{
					try {
						program.setProgramEndDate(initialsdf.parse(event.getProgramEndDate()));
					} catch (ParseException e) {
						errors.put("programEndDate","Error while parsing Program End Date");
						event.setErrors(errors);
					}
				}
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

				if(errors.isEmpty()){
					Program persistedPgrm = programRepository.saveProgram(program);
					event.setAutoGeneratedEventId(persistedPgrm.getAutoGeneratedEventId());
					event.setStatus("Success");	
				}else{
					event.setStatus("Failed");
				}

			}
		}

		return events;


	}

	@Override
	public int getEventCountByEmail(String username, boolean isAdmin) {
		return programRepository.getEventCountByEmail(username, isAdmin);
	}

	@Override
	public int getNonCategorizedEventsByEmail(String username, boolean isAdmin) {
		return programRepository.getNonCategorizedEventsByEmail(username, isAdmin);
	}

	@Override
	public Participant findParticipantBySeqId(String seqId, int programId) {
		return programRepository.findParticipantBySeqId(seqId, programId);
	}

	@Override
	public int getProgramIdByEventId(String eventID) {
		return programRepository.getProgramIdByEventId(eventID);
	}

	@Override
	public void UpdateParticipantsStatus(String participantIds, String eventId , String introduced) {
		programRepository.UpdateParticipantsStatus(participantIds,eventId,introduced);
	}

	@Override
	public List<String> getAllEventCategories() {
		return programRepository.getAllEventCategories();
	}

	@Override
	public int getEventCountByCategory(String email, boolean isAdmin,String eventCategory) {
		return programRepository.getEventCountByCategory(email,isAdmin,eventCategory);
	}

	@Override
	public int getMiscellaneousEventsByEmail(String email, boolean isAdmin, List<String> eventcategories) {
		return programRepository.getMiscellaneousEventsByEmail(email,isAdmin,eventcategories);
	}

	@Override
	public void updateEventAdmin(EventAdminChangeRequest eventAdminChangeRequest) {
		Program program=programRepository.findByAutoGeneratedEventId(eventAdminChangeRequest.getEventId());
		eventAdminChangeRequest.setOldCoordinatorEmail(program.getCoordinatorEmail());
		program.setCoordinatorEmail(eventAdminChangeRequest.getNewCoordinatorEmail());
		program.setCoordinatorMobile(eventAdminChangeRequest.getCoordinatorMobile());
		program.setCoordinatorName(eventAdminChangeRequest.getCoordinatorName());
		programRepository.save(program);
	}

	@Override
	public void updateCoOrdinatorStatistics(EventAdminChangeRequest eventAdminChangeRequest) {
		programRepository.updateCoOrdinatorStatistics(eventAdminChangeRequest);
	}

	@Override
	public List<Coordinator> getAllCoOrdinatorsList() {
		return programRepository.getAllCoOrdinatorsList();
	}

	@Override
	public List<String> getUncategorizedEvents(String username, boolean isAdmin) {
		return programRepository.getNonCategorizedEventListByEmail(username, isAdmin);
	}

	@Override
	public Participant deleteParticipant(String seqId, String eventId) {
		return programRepository.deleteParticipant(seqId,eventId);
	}

	@Override
	public void updateDeletedParticipant(Participant deletedParticipant, String deletedBy) {
		programRepository.updateDeletedParticipant(deletedParticipant,deletedBy);
	}

	@Override
	public Event getEventDetails(String agEventId) {
		Event eventDetails = new Event();
		SimpleDateFormat convertedsdf = new SimpleDateFormat("dd-MM-yyyy");
		Program program =
				programRepository.findByAutoGeneratedEventId(agEventId);

		eventDetails.setProgramChannel(program.getProgramChannel());
		eventDetails.setProgramName(program.getProgramName());
		if(null == program.getProgramStartDate()){
			eventDetails.setProgramStartDate("");
		}else{
			try{

				eventDetails.setProgramStartDate(convertedsdf.format(program.getProgramStartDate()));
			}catch(Exception e){
				eventDetails.setProgramStartDate("");
			}
		}
		if(null == program.getProgramEndDate()){
			eventDetails.setProgramEndDate("");
		}else{
			try{

				eventDetails.setProgramEndDate(convertedsdf.format(program.getProgramEndDate()));
			}catch(Exception e){
				eventDetails.setProgramEndDate("");
			}
		}
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
		return eventDetails;
	}


}
