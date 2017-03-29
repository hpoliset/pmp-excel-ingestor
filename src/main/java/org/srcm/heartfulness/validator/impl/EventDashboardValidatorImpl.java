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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.IssueeWelcomeId;
import org.srcm.heartfulness.model.EventPagination;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.validator.EventDashboardValidator;

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

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private PmpParticipantService participantService;

	@Autowired
	Environment env;

	/**
	 * Method to validate mandatory fields in the participant request before
	 * creating participant.
	 * 
	 * @param participant
	 * @return
	 */
	@Override
	public Map<String, String> checkParticipantMandatoryFields(ParticipantRequest participant) {
		Map<String, String> errors = new HashMap<>();
		if (null == participant.getEventId()) {
			errors.put("eventId", "program ID is required");
		} else if (null != participant.getEventId() && !participant.getEventId().matches(ExpressionConstants.EVENT_ID_REGEX)) {
			errors.put("eventId", "event Id Format is invalid");
		} else {
			if (0 == programService.getProgramIdByEventId(participant.getEventId()))
				errors.put("eventId", "No event is available for the provided event ID");
		}
		if (null != participant.getGender()
				&& !participant.getGender().isEmpty()
				&& !(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) || participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE)
						|| participant.getGender().equalsIgnoreCase(PMPConstants.MALE) || participant.getGender()
						.equalsIgnoreCase(PMPConstants.FEMALE))) {
			errors.put("gender", "gender is not valid.");
		}

		if (null == participant.getPrintName() || participant.getPrintName().isEmpty()) {
			errors.put("printName", "name is required");
		}
		if (null == participant.getCity() || participant.getCity().isEmpty()) {
			errors.put("city", "city is required");
		}
		if (null == participant.getState() || participant.getState().isEmpty()) {
			errors.put("state", "state is required");
		}
		if (null == participant.getCountry() || participant.getCountry().isEmpty()) {
			errors.put("country", "country is required");
		}
		if (null != participant.getEmail()) {
			if (!participant.getEmail().matches(
					"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
				errors.put("email", "email is invalid");
			}
		}
		if (null != participant.getIntroductionDate()) {
			if (!participant.getIntroductionDate().matches("^\\d{2}-\\d{2}-\\d{4}$")) {
				errors.put("introductionDate", "date is invalid. Valid format DD-MM-YYYY");
			}
		}
		if (null != participant.getDateOfBirth()) {
			if (!participant.getDateOfBirth().matches("^\\d{2}-\\d{2}-\\d{4}$")) {
				errors.put("dateOfBirth", "date is invalid. Valid format DD-MM-YYYY");
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
	@Override
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

		if (null == event.getProgramStartDate()) {
			errors.put("programStartDate", "Program Start Date is required");
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
		}

		if (null == event.getEventPlace() || event.getEventPlace().isEmpty()) {
			errors.put("eventPlace", "Event place is required");
		}
		if (null == event.getEventCity() || event.getEventCity().isEmpty()) {
			errors.put("eventCity", "Event city is required");
		}
		if (null == event.getEventState() || event.getEventState().isEmpty()) {
			errors.put("eventState", "Event state is required");
		}
		if (null == event.getEventCountry() || event.getEventCountry().isEmpty()) {
			errors.put("eventCountry", "Event country is required");
		}

		if (null == event.getCoordinatorName() || event.getCoordinatorName().isEmpty()) {
			errors.put("coordinatorName", "Coordinator name is required");
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

		if (null == event.getOrganizationName() || event.getOrganizationName().isEmpty()) {
			errors.put("organizationName", "Organization name is required");
		}
		if (null == event.getOrganizationContactName() || event.getOrganizationContactName().isEmpty()) {
			errors.put("organizationContactName", "Organization contact name is required");
		}
		if (null == event.getOrganizationContactMobile() || event.getOrganizationContactMobile().isEmpty()) {
			errors.put("organizationContactMobile", "Organization contact mobile is required");
		} else if (!event.getOrganizationContactMobile().matches(ExpressionConstants.MOBILE_REGEX)) {
			errors.put("organizationContactMobile", "Organization contact person mobile number is invalid");
		}

		if (null == event.getOrganizationContactEmail() || event.getOrganizationContactEmail().isEmpty()) {
			errors.put("organizationContactEmail", "Organization contact email is required");
		} else if (!event.getOrganizationContactEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("organizationContactEmail", "Organization contact person email is invalid");
		}

		if (null != event.getOrganizationDecisionMakerEmail()
				&& !event.getOrganizationDecisionMakerEmail().matches(ExpressionConstants.EMAIL_REGEX)) {
			errors.put("organizationDecisionMakerEmail", "Head of the department email is invalid");
		}

		if (null != event.getOrganizationDecisionMakerPhoneNo()
				&& !event.getOrganizationDecisionMakerPhoneNo().matches(ExpressionConstants.MOBILE_REGEX)) {
			errors.put("organizationDecisionMakerPhoneNo", "Head of the department mobile number is invalid");
		}
		
		if (null == event.getPreceptorIdCardNumber() || event.getPreceptorIdCardNumber().isEmpty()) {
			errors.put("preceptorIdCardNumber", "Preceptor Id card number is required");
		}

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
	public Map<String, String> checkDeleteRequestMandatoryFields(ParticipantIntroductionRequest participantRequest) {
		Map<String, String> errors = new HashMap<>();
		if (null == participantRequest.getEventId() || participantRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else if (null != participantRequest.getEventId() && !participantRequest.getEventId().matches(ExpressionConstants.EVENT_ID_REGEX)) {
			errors.put("eventId", "event Id invalid");
		} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
			errors.put("eventId", "Invalid EventId - No event exists for the given event Id");
		}
		if (0 == participantRequest.getParticipantIds().size()) {
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
				&& (null == participantInput.getWelcomeCardNumber() || participantInput.getWelcomeCardNumber()
						.isEmpty())) {
			isValid=true;
		} else if (null != participantInput.getWelcomeCardNumber() && !participantInput.getWelcomeCardNumber() .isEmpty() ){
			for (IssueeWelcomeId field : IssueeWelcomeId.values()) {
				if (participantInput.getWelcomeCardNumber().equalsIgnoreCase(field.getValue())) {
					isValid=true;
				}
			}
		} else if (!(null == participantInput.getThirdSittingDate()	&& (null == participantInput.getThirdSitting() || 0 == participantInput.getThirdSitting()))) {
			isValid=true;
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
	public Map<String, String> checkUpdateParticipantMandatoryFields(ParticipantRequest participant) {
		Map<String, String> errors = new HashMap<String, String>();
		if (null == participant.getPrintName() || participant.getPrintName().isEmpty()) {
			errors.put(ErrorConstants.STATUS_FAILED, "print name is required ");
		}
		if (null == participant.getSeqId() || participant.getSeqId().isEmpty()) {
			errors.put(ErrorConstants.STATUS_FAILED, "SeqID is required ");
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

}
