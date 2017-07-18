package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.EventDetailsUploadConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.CoordinatorPosition;
import org.srcm.heartfulness.enumeration.IssueeWelcomeId;
import org.srcm.heartfulness.model.EventPagination;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.DashboardRequest;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.CoordinatorPositionResponse;
import org.srcm.heartfulness.model.json.response.PositionAPIResult;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.ChannelRepository;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.DashboardRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.DashboardService;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.EventDashboardValidator;
import org.srcm.heartfulness.webservice.ParticipantsController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Validator Implementation to streamline all Event Dashboard validation
 * implementation.
 * 
 * @author Koustav Dutta
 * 
 * 
 */
@Component
public class EventDashboardValidatorImpl implements EventDashboardValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventDashboardValidatorImpl.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private PmpParticipantService participantService;

	@Autowired
	Environment env;

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	ParticipantRepository participantRepository;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	DashboardRestTemplate dashboardRestTemplate;

	@Autowired
	DashboardService dashboardService;

	/**
	 * Method to validate mandatory fields in the participant request before
	 * creating participant.
	 * 
	 * @param participant
	 * @return
	 */
	@Override
	public Map<String, String> checkParticipantMandatoryFields(List<String> emailList, String userRole, ParticipantRequest participant, String authToken, PMPAPIAccessLog accessLog) {
		Map<String, String> errors = new HashMap<>();
		Program program = null;
		if (null == participant.getEventId() || participant.getEventId().isEmpty()) {
			errors.put("eventId", DashboardConstants.INVALID_OR_EMPTY_EVENTID);
		}else {

			//program = getProgram(emailList, userRole, participant.getEventId(), authToken, accessLog);
			program = programRepository.getProgramByEmailAndRoleForParticipant(emailList, userRole, participant.getEventId());
			if( null != program  && program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE)){
				participant.setProgramId(program.getProgramId());
			}else{
				errors.put("eventId", ErrorConstants.UNAUTHORIZED_CREATE_PARTICIPANT_ACCESS + participant.getEventId());
			}
		}
		if (null != participant.getGender()
				&& !participant.getGender().isEmpty()
				&& !(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) || participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE)
						|| participant.getGender().equalsIgnoreCase(PMPConstants.MALE) || participant.getGender()
						.equalsIgnoreCase(PMPConstants.FEMALE))) {
			errors.put("gender", DashboardConstants.INVALID_GENDER);
		}

		if (null == participant.getPrintName() || participant.getPrintName().isEmpty()) {
			errors.put("printName", DashboardConstants.PRINT_NAME_REQUIRED);
		}
		if (null == participant.getCity() || participant.getCity().isEmpty()) {
			errors.put("city", DashboardConstants.PARTICIPANT_City_REQ);
		}
		if (null == participant.getState() || participant.getState().isEmpty()) {
			errors.put("state", DashboardConstants.PARTICIPANT_STATE_REQ);
		}
		if (null == participant.getCountry() || participant.getCountry().isEmpty()) {
			errors.put("country", DashboardConstants.PARTICIPANT_COUNTRY_REQ);
		}
		if(null == participant.getDistrict() || participant.getDistrict().isEmpty()){
			errors.put("district", DashboardConstants.PARTICIPANT_DISTRICT_REQ);
		}

		if (null != participant.getEmail()) {
			if (!participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
				errors.put("email", DashboardConstants.INVALID_PARTICIPANT_EMAIL);
			}
		}
		if (null != participant.getIntroductionDate()) {
			if (!participant.getIntroductionDate().matches(ExpressionConstants.DATE_REGEX)) {
				errors.put("introductionDate", DashboardConstants.INVALID_INTRODUCED_DATE);
			}
		}
		if (null != participant.getDateOfBirth()) {
			if (!participant.getDateOfBirth().matches(ExpressionConstants.DATE_REGEX)) {
				errors.put("dateOfBirth", DashboardConstants.INVALID_DOB);
			}
		}
		return errors;
	}

	/**
	 * Method to validate the values given in
	 * <code>ParticipantIntroductionRequest</code> before updating introductory
	 * status.
	 * 
	 * @param participantRequest
	 * @return
	 */
	/*@Override
	public Map<String, String> checkIntroductionRequestMandatoryFields(
			ParticipantIntroductionRequest participantRequest, int id) {
		Map<String, String> errors = new HashMap<>();
		if (null == participantRequest.getEventId() || participantRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else if (null != participantRequest.getEventId() && !participantRequest.getEventId().matches(ExpressionConstants.EVENT_ID_REGEX)) {
			errors.put("eventId", "event Id invalid");
		} else {
			int programID = programService.getProgramIdByEventId(participantRequest.getEventId());
			if (0 == programID) {
				errors.put("eventId", "Invalid EventId - No event exists for the given event Id");
			} else {
				Program program = programService.getProgramById(programID);
				String errorMessage = programService.validatePreceptorIDCardNumber(program, id);
				if (null != errorMessage) {
					errors.put("Preceptor ID card number", errorMessage);
				}
			}
		}
		if (null == participantRequest.getIntroduced() || participantRequest.getIntroduced().isEmpty()) {
			errors.put("introduced", "Introduced status is required");
		}
		if (0 == participantRequest.getParticipantIds().size()) {
			errors.put("partcipantIds", "No participant Ids are available to update the status");
		}
		return errors;
	}*/

	@Override
	public Map<String, String> checkIntroductionRequestMandatoryFields(List<String> emailList,String userRole,ParticipantIntroductionRequest participantRequest,String authToken,PMPAPIAccessLog accessLog) {
		Map<String, String> errors = new HashMap<>();
		Program program = null;
		if (null == participantRequest.getEventId() || participantRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else {
			program = programRepository.getProgramByEmailAndRoleForParticipant(emailList, userRole, participantRequest.getEventId());
			if( null != program  && program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE)){
				String errorMessage = programService.validatePreceptorIDCardNumber(program, accessLog.getId());
				if (null != errorMessage) {
					errors.put("Preceptor ID card number", errorMessage);
				}
			}else{
				errors.put("eventId", ErrorConstants.UNAUTHORIZED_INTRODUCED_PARTICIPANT_ACCESS + participantRequest.getEventId());
			}
		}
		if (null == participantRequest.getIntroduced() || participantRequest.getIntroduced().isEmpty()) {
			errors.put("introduced", "Introduced status is required");
		}
		if ( null == participantRequest.getParticipantIds() || 0 == participantRequest.getParticipantIds().size()) {
			errors.put("partcipantIds", "No participant Ids are available to update the status");
		}
		return errors;
	}

	/**
	 * Method to validate the values given in
	 * <code>EventAdminChangeRequest</code> before updating event admin.
	 * 
	 * @param eventAdminChangeRequest
	 * @return errors
	 */
	@Override
	public Map<String, String> checkUpdateEventAdminMandatoryFields(EventAdminChangeRequest eventAdminChangeRequest) {
		Map<String, String> errors = new HashMap<>();
		if (null == eventAdminChangeRequest.getEventId() || eventAdminChangeRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else if (null != eventAdminChangeRequest.getEventId()
				&& !eventAdminChangeRequest.getEventId().matches(ExpressionConstants.EVENT_ID_REGEX)) {
			errors.put("eventId", "event Id invalid");
		} else if (0 == programService.getProgramIdByEventId(eventAdminChangeRequest.getEventId())) {
			errors.put("eventId", "Invalid EventId - No event exists for the given event Id");
		}
		if (null == eventAdminChangeRequest.getNewCoordinatorEmail()
				|| eventAdminChangeRequest.getNewCoordinatorEmail().isEmpty()) {
			errors.put("newCoOrdinatorEmail", "new co-ordinator email is required");
		} else if (!eventAdminChangeRequest.getNewCoordinatorEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("newCoOrdinatorEmail", "Invalid new co-ordinator email format");
		}
		if (null == eventAdminChangeRequest.getCoordinatorMobile()
				|| eventAdminChangeRequest.getCoordinatorMobile().isEmpty()) {
			errors.put("CoOrdinatorMobile", "Co-Ordinator mobile is required");
		} else if (!eventAdminChangeRequest.getCoordinatorMobile().matches(ExpressionConstants.MOBILE_REGEX)) {
			errors.put("CoOrdinatorMobile", "Invalid Co-Ordinator mobile number format");
		}

		return errors;
	}

	/**
	 * Method is used to validate the mandatory parameters before persisting an
	 * event. Other validations include date validation and mobile number
	 * validation.
	 * 
	 * @param program
	 *            is validated to check all the mandatory parameters.
	 * @return map of error if exists
	 */
	@Override
	public Map<String, String> checkMandatoryEventFields(Event event) {
		Map<String, String> errors = new HashMap<>();
		Date startDate = null;

		if (event.getCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2) && (null == event.getBatchDescription() || event.getBatchDescription().isEmpty())) {
			errors.put("batchDescription", "Batch description is required");
		}

		if (null == event.getProgramChannel() || event.getProgramChannel().isEmpty()) {
			errors.put("programChannel", "Program channel is required");
		}else if(event.getCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2) && event.getProgramChannel().equals(DashboardConstants.G_CONNECT_CHANNEL)){
			if (0 == event.getProgramChannelType()) {
				errors.put("programChannelType", "Program channel type is required");
			}else{
				if(!channelRepository.validateChannelType(event.getProgramChannelType())){
					errors.put("programChannelType", "Program channel type is not available");
				}
			}
		}else{
			event.setProgramChannelType(0);
		}

		if (null == event.getOrganizationName() || event.getOrganizationName().isEmpty()) {
			errors.put("organizationName", "Organization name is required");
		}
		if (null == event.getEventPlace() || event.getEventPlace().isEmpty()) {
			errors.put("eventPlace", "Event place is required");
		}
		if (null == event.getEventCity() || event.getEventCity().isEmpty()) {
			errors.put("eventCity", "Event city is required");
		}
		if (event.getCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2) && (null == event.getProgramDistrict() || event.getProgramDistrict().isEmpty())) {
			errors.put("programDistrict", "Program district is required");
		}
		if (null == event.getEventState() || event.getEventState().isEmpty()) {
			errors.put("eventState", "Event state is required");
		}
		if (null == event.getEventCountry() || event.getEventCountry().isEmpty()) {
			errors.put("eventCountry", "Event country is required");
		}
		if(null == event.getProgramZone() || event.getProgramZone().isEmpty()){
			errors.put("programZone", "Program zone is required");
		}
		if(null == event.getProgramCenter() || event.getProgramCenter().isEmpty()){
			errors.put("programCenter", "Program center is required");
		}

		if (null == event.getProgramStartDate()) {
			errors.put("programStartDate", "Program start date is required");
		} else {

			if (!event.getProgramStartDate().matches(ExpressionConstants.DATE_REGEX)) {
				errors.put("programStartDate", "Invalid date format,correct format is dd-MM-yyyy");
			}else{
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					startDate = sdf.parse(event.getProgramStartDate());
					if(startDate.after(new Date())){
						errors.put("programStartDate", "Program start date cannot be a future date");
					}
				} catch (ParseException e) {
					errors.put("programStartDate", "Invalid date format,correct format is dd-MM-yyyy");
				}
			}

		}
		if (null != event.getProgramEndDate()) {

			if (!event.getProgramEndDate().matches(ExpressionConstants.DATE_REGEX)) {
				errors.put("programEndDate", "Invalid date format,correct format is dd-MM-yyyy");
			}else{
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					Date endDate = sdf.parse(event.getProgramEndDate());
					if (startDate != null) {
						if(!endDate.after(startDate)){
							errors.put("programEndDate", "Program end date should be after program start date");
						}
					}
				} catch (ParseException e) {
					errors.put("programEndDate", "Invalid date format,correct format is dd-MM-yyyy");
				}
			}

		} 

		if(null != event.getAutoGeneratedEventId()){
			String programId = "",maxSessionDate = "";
			ArrayList<String> dateAndId = programRepository.getProgramIdAndMaxSessionDate(event.getAutoGeneratedEventId());
			if(!dateAndId.isEmpty()){

				programId = dateAndId.get(0);
				maxSessionDate =dateAndId.get(1) ;

				if(null != maxSessionDate){
					try {
						if(DateUtils.parseDate(event.getProgramEndDate()).equals(DateUtils.parseDate(maxSessionDate)) ? false
								: DateUtils.parseDate(event.getProgramEndDate()).before(DateUtils.parseDate(maxSessionDate))){
							errors.put("programEndDate", "Program end date should be after last session date");
						}
					} catch (ParseException e) {
						errors.put("programEndDate", "Invalid date format,correct format is dd-MM-yyyy");
					}
				}

			}
		}

		/*if (null == event.getProgramStartDate()) {
			errors.put("programStartDate", "Program start date is required");
		} else {

			if (!event.getProgramStartDate().matches(ExpressionConstants.DATE_REGEX)) {
				errors.put("programStartDate", "Invalid date format,correct format is dd-MM-yyyy");
			}

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				if(sdf.parse(event.getProgramStartDate()).after(new Date())){
					errors.put("programStartDate", "Program start date cannot be a future date");
				}
			} catch (ParseException e) {
				errors.put("programStartDate", "Invalid date format,correct format is dd-MM-yyyy");
			}

		}
		if (null != event.getProgramEndDate()) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sdf.parse(event.getProgramEndDate());
			} catch (ParseException e) {
				errors.put("programEndDate", "Invalid date format,correct format is dd-MM-yyyy");
			}
			if (!event.getProgramEndDate().matches(ExpressionConstants.DATE_REGEX)) {
				errors.put("programEndDate", "Invalid date format,correct format is dd-MM-yyyy");
			}
		}*/
		if (null == event.getOrganizationContactName() || event.getOrganizationContactName().isEmpty()) {
			errors.put("organizationContactName", "Organization contact name is required");
		}
		if (null == event.getOrganizationContactEmail() || event.getOrganizationContactEmail().isEmpty()) {
			errors.put("organizationContactEmail", "Organization contact email is required");
		} else if (!event.getOrganizationContactEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("organizationContactEmail", "Organization contact person email is invalid");
		}
		if (null == event.getOrganizationContactMobile() || event.getOrganizationContactMobile().isEmpty()) {
			errors.put("organizationContactMobile", "Organization contact mobile is required");
		} else if (!event.getOrganizationContactMobile().matches(ExpressionConstants.MOBILE_REGEX)) {
			errors.put("organizationContactMobile", "Organization contact person mobile number is invalid");
		}
		if (null == event.getCoordinatorName() || event.getCoordinatorName().isEmpty()) {
			errors.put("coordinatorName", "Coordinator name is required");
		}
		if (null == event.getCoordinatorAbhyasiId() || event.getCoordinatorAbhyasiId().isEmpty()) {
			errors.put("coordinatorAbhyasiId", "Coordinator abhyasi Id is required");
		}
		if (null == event.getCoordinatorEmail() || event.getCoordinatorEmail().isEmpty()) {
			errors.put("coordinatorEmail", "Coordinator email is required");
		} else if (!event.getCoordinatorEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("coordinatorEmail", "Coordinator email is invalid");
		}
		if (null == event.getCoordinatorMobile() || event.getCoordinatorMobile().isEmpty()) {
			errors.put("coordinatorMobile", "Coordinator mobile is required");
		} else if (!event.getCoordinatorMobile().matches(ExpressionConstants.MOBILE_REGEX)) {
			errors.put("coordinatorMobile", "Coordinator mobile number is invalid");
		}
		if (null == event.getPreceptorIdCardNumber() || event.getPreceptorIdCardNumber().isEmpty()) {
			errors.put("preceptorIdCardNumber", "Preceptor Id card number is required");
		}
		if(null == event.getIsEwelcomeIdGenerationDisabled() || event.getIsEwelcomeIdGenerationDisabled().isEmpty()){
			errors.put("isEwelcomeIdGenerationDisabled", "eWelcome Id generation status is required");
		}else if(!event.getIsEwelcomeIdGenerationDisabled().equals(EventDetailsUploadConstants.EWELCOME_ID_ENABLED_STATE)){
			if(!event.getIsEwelcomeIdGenerationDisabled().equals(EventDetailsUploadConstants.EWELCOME_ID_DISABLED_STATE)){
				errors.put("isEwelcomeIdGenerationDisabled", "eWelcome Id generation status can only be E or D ");
			}
		}

		/*if (null != event.getOrganizationDecisionMakerEmail()
				&& !event.getOrganizationDecisionMakerEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("organizationDecisionMakerEmail", "Head of the department email is invalid");
		}

		if (null != event.getOrganizationDecisionMakerPhoneNo()
				&& !event.getOrganizationDecisionMakerPhoneNo().matches(ExpressionConstants.MOBILE_REGEX)) {
			errors.put("organizationDecisionMakerPhoneNo", "Head of the department mobile number is invalid");
		}*/

		return errors;
	}

	/**
	 * Token is validated against mysrcm endpoint.
	 * 
	 * @param token
	 *            need to be validated against mysrcm api.
	 * @throws HttpClientErrorException
	 *             if client exception occurs.
	 * @throws JsonParseException
	 *             while parsing json data.
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@Override
	public UserProfile validateToken(String token, int id) throws HttpClientErrorException, JsonParseException,
	JsonMappingException, IOException, IllegalBlockSizeException, NumberFormatException, BadPaddingException,
	ParseException {
		Result result = userProfileService.getUserProfile(token, id);
		return result.getUserProfile()[0];
	}

	/**
	 * Method to validate the values given in ParticipantIntroductionRequest
	 * before deleting the participant.
	 * 
	 * @param participantRequest
	 * @return errors
	 */
	@Override
	public Map<String, String> checkDeleteRequestMandatoryFields(List<String> emailList, String userRole, ParticipantIntroductionRequest participantRequest, String authToken, PMPAPIAccessLog accessLog) {
		Map<String, String> errors = new HashMap<>();
		Program program = null;

		if (null == participantRequest.getEventId() || participantRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else{

			//program = getProgram(emailList, userRole, participantRequest.getEventId(), authToken, accessLog);
			program = programRepository.getProgramByEmailAndRoleForParticipant(emailList, userRole, participantRequest.getEventId());
			if( null == program  || program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_TRUE)){
				errors.put("eventId", ErrorConstants.UNAUTHORIZED_DELETE_PARTICIPANT_ACCESS + participantRequest.getEventId());
			}
		}

		if (null == participantRequest.getParticipantIds() ||  0 == participantRequest.getParticipantIds().size()) {
			errors.put("partcipantIds", "No participant Ids are available to delete");
		}
		return errors;
	}

	/**
	 * Method to validate the mandatory fields in the participant request before
	 * introducing the participants.
	 * 
	 * @param participantInput
	 * @param id
	 * @return
	 */
	@Override
	public List<String> checkParticipantIntroductionMandatoryFields(Participant participantInput, int id) {
		List<String> errors = new ArrayList<String>();
		if (null == participantInput.getCity() || participantInput.getCity().isEmpty()) {
			if (null == participantInput.getProgram().getEventCity()
					|| participantInput.getProgram().getEventCity().isEmpty()) {
				errors.add("City is required.");
			} else {
				participantInput.setCity(participantInput.getProgram().getEventCity());
			}
		}

		if (null == participantInput.getState() || participantInput.getState().isEmpty()) {
			if (null == participantInput.getProgram().getEventState()
					|| participantInput.getProgram().getEventState().isEmpty()) {
				errors.add("State is required.");
			} else {
				participantInput.setState(participantInput.getProgram().getEventState());
			}
		}

		if (null == participantInput.getCountry() || participantInput.getCountry().isEmpty()) {
			participantInput.setCountry(PMPConstants.COUNTRY_INDIA);
		}

		if (null == participantInput.getProgram().getProgramStartDate()) {
			errors.add("Program start date is required.");
		}

		if (participantInput.getProgram().getFirstSittingBy() == 0) {
			String isValid = programService.validatePreceptorIDCardNumber(participantInput.getProgram(), id);
			if (null != isValid) {
				errors.add(isValid);
			}
		}

		return errors;
	}

	/**
	 * Method to validate whether participant completed preliminary sittings or
	 * not.
	 * 
	 * @param participantInput
	 * @return true,if valid.
	 */
	@Override
	public boolean validateParticipantCompletedPreliminarySittings(Participant participantInput) {
		boolean isValid=false;
		if (1 == participantInput.getIntroduced()
				&& (null == participantInput.getWelcomeCardNumber() || participantInput.getWelcomeCardNumber().isEmpty())) {

			isValid=true;
		} else if (null != participantInput.getWelcomeCardNumber() && !participantInput.getWelcomeCardNumber() .isEmpty() ){
			for (IssueeWelcomeId field : IssueeWelcomeId.values()) {
				if (participantInput.getWelcomeCardNumber().equalsIgnoreCase(field.getValue())) {
					isValid=true;
					break;
				}
			}
		} else if (!(null == participantInput.getThirdSittingDate()	&& (null == participantInput.getThirdSitting() || 0 == participantInput.getThirdSitting()))
				&& !(null == participantInput.getFirstSittingDate() && (null == participantInput.getFirstSitting() || 0 == participantInput.getFirstSitting()))
				&& !(null == participantInput.getSecondSittingDate() && (null == participantInput.getSecondSitting() || 0 == participantInput.getSecondSitting()))) {
			isValid=true;
		}else if( null != participantInput.getTotalDays() ? participantInput.getTotalDays() > 2 : false  ){
			isValid = true;
		}

		return isValid;
	}

	/**
	 * Method to validate the mandatory fields in the <code>Participant</code>
	 * request before updating the participant details.
	 * 
	 * @param participant
	 * @return errors <code>Map<String, String</code>.
	 */
	@Override
	public Map<String, String> checkUpdateParticipantMandatoryFields(List<String> emailList, String userRole, ParticipantRequest participant, String authToken, PMPAPIAccessLog accessLog) {
		Map<String, String> errors = new HashMap<String, String>();
		Program program = null;
		if (null == participant.getPrintName() || participant.getPrintName().isEmpty()) {
			errors.put(ErrorConstants.STATUS_FAILED, DashboardConstants.PRINT_NAME_REQUIRED);
		}
		if (null == participant.getEventId() || participant.getEventId().isEmpty()) {
			errors.put(ErrorConstants.STATUS_FAILED, DashboardConstants.INVALID_OR_EMPTY_EVENTID);
		}else {

			//program = getProgram(emailList, userRole, participant.getEventId(), authToken, accessLog);
			program = programRepository.getProgramByEmailAndRoleForParticipant(emailList, userRole, participant.getEventId());
			if( null != program  && program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_FALSE)){
				participant.setProgramId(program.getProgramId());
			}else{
				errors.put("eventId", ErrorConstants.UNAUTHORIZED_UPDATE_PARTICIPANT_ACCESS + participant.getEventId());
			}
		}

		if (null == participant.getSeqId() || participant.getSeqId().isEmpty()) {
			errors.put(ErrorConstants.STATUS_FAILED, DashboardConstants.SEQ_ID_REQUIRED);
		}else if(0 == participantRepository.getParticipantCountByProgIdAndSeqId(participant.getProgramId(),participant.getSeqId())){
			errors.put(ErrorConstants.STATUS_FAILED, DashboardConstants.INVALID_SEQ_ID);
		}
		return errors;
	}

	/**
	 * Method to validate the pagination properties before fetching the related
	 * results.
	 * 
	 * @param eventPagination
	 * @return error message, if invalid.
	 */
	@Override
	public String validatePaginationProperties(EventPagination eventPagination) {
		if (eventPagination.getPageIndex() <= 0) {
			return "Invalid page index";
		}
		if (eventPagination.getPageSize() <= 0) {
			return "Invalid page size";
		}
		return "";
	}

	@Override
	public String validateSearchParameters(SearchRequest searchRequest) {
		if(null == searchRequest.getSearchField())
			return DashboardConstants.INVALID_SEARCH_FIELD;
		if(null == searchRequest.getSearchText())
			return DashboardConstants.INVALID_SEARCH_TEXT;

		return "";
	}

	@Override
	public String checkProgramAccess(List<String> emailList, String userRole, String eventId, String token,PMPAPIAccessLog accessLog) {

		String errorMsg = "";
		Program program = null;
		if (null == eventId || eventId.isEmpty()) {
			errorMsg = DashboardConstants.INVALID_OR_EMPTY_EVENTID;
		} else {
			//program = getProgram(emailList, role, eventId, token, accessLog);
			program = programRepository.getProgramByEmailAndRoleForParticipant(emailList, userRole, eventId);
			if( null == program  || program.getIsReadOnly().equals(CoordinatorAccessControlConstants.IS_READ_ONLY_TRUE)){
				errorMsg = ErrorConstants.UNAUTHORIZED_CREATE_PARTICIPANT_ACCESS + eventId;
			}

		}
		return errorMsg;
	}

}
