package org.srcm.heartfulness.validator.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EventConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
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
 * Validator Implementation to streamline all Event Dashboard validation implementation.
 * 
 */
@Component
public class EventDashboardValidatorImpl implements EventDashboardValidator {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	private ProgramService programService;

	@Autowired
	private PmpParticipantService participantService;

	@Autowired
	Environment env;

	/**
	 * Method to validate mandatory fields in the participant request before creating participant.
	 * @param participant
	 * @return
	 */
	@Override
	public Map<String, String> checkPartcicipantMandatoryFields(ParticipantRequest participant) {
		Map<String, String> errors = new HashMap<>();
		if (null == participant.getEventId()) {
			errors.put("eventId", "program ID is required");
		} else if (null != participant.getEventId() && !participant.getEventId().matches("^E[0-9]{6}$")) {
			errors.put("eventId", "event Id Format is invalid");
		} else {
			if (0 == programService.getProgramIdByEventId(participant.getEventId()))
				errors.put("eventId", "No event is available for the provided event ID");
		}
		if (null != participant.getGender()
				&& !participant.getGender().isEmpty()
				&& !(participant.getGender().equalsIgnoreCase("M") || participant.getGender().equalsIgnoreCase("F")
						|| participant.getGender().equalsIgnoreCase("Male") || participant.getGender()
						.equalsIgnoreCase("Female"))) {
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
	 * Method to validate the values given in <code>ParticipantIntroductionRequest</code> before updating introductory status.
	 * @param participantRequest
	 * @return
	 */
	@Override
	public Map<String, String> checkIntroductionRequestMandatoryFields(ParticipantIntroductionRequest participantRequest) {
		Map<String, String> errors = new HashMap<>();
		if (null == participantRequest.getEventId() || participantRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else if (null != participantRequest.getEventId() && !participantRequest.getEventId().matches("^E[0-9]{6}$")) {
			errors.put("eventId", "event Id invalid");
		} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
			errors.put("eventId", "Invalid EventId - No event exists for the given event Id");
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
	 * Method to validate the values given in <code>EventAdminChangeRequest</code> before updating event admin.
	 * @param eventAdminChangeRequest
	 * @return errors
	 */
	@Override
	public Map<String, String> checkUpdateEventAdminMandatoryFields(EventAdminChangeRequest eventAdminChangeRequest) {
		Map<String, String> errors = new HashMap<>();
		if (null == eventAdminChangeRequest.getEventId() || eventAdminChangeRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else if (null != eventAdminChangeRequest.getEventId()
				&& !eventAdminChangeRequest.getEventId().matches("^E[0-9]{6}$")) {
			errors.put("eventId", "event Id invalid");
		} else if (0 == programService.getProgramIdByEventId(eventAdminChangeRequest.getEventId())) {
			errors.put("eventId", "Invalid EventId - No event exists for the given event Id");
		}
		if (null == eventAdminChangeRequest.getNewCoordinatorEmail()
				|| eventAdminChangeRequest.getNewCoordinatorEmail().isEmpty()) {
			errors.put("newCoOrdinatorEmail", "new co-Ordinator email is required");
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
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sdf.parse(event.getProgramStartDate());
			} catch (ParseException e) {
				errors.put("programStartDate", "Invalid date format,correct format is dd-MM-yyyy");
			}

			if (!event.getProgramStartDate().matches(EventConstants.DATE_REGEX)) {
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
			if (!event.getProgramEndDate().matches(EventConstants.DATE_REGEX)) {
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
		} else if (!event.getCoordinatorEmail().matches(EventConstants.EMAIL_REGEX)) {
			errors.put("coordinatorEmail", "Invalid email format");
		}
		if (null == event.getCoordinatorMobile() || event.getCoordinatorMobile().isEmpty()) {
			errors.put("coordinatorMobile", "Coordinator mobile is required");
		} else if (!event.getCoordinatorMobile().matches(EventConstants.MOBILE_REGEX)) {
			errors.put("coordinatorMobile", "Invalid mobile number format");
		}

		if (null == event.getOrganizationName() || event.getOrganizationName().isEmpty()) {
			errors.put("organizationName", "Organization name is required");
		}
		if (null == event.getOrganizationContactName() || event.getOrganizationContactName().isEmpty()) {
			errors.put("organizationContactName", "Organization contact name is required");
		}
		if (null == event.getOrganizationContactMobile() || event.getOrganizationContactMobile().isEmpty()) {
			errors.put("organizationContactMobile", "Organization contact mobile is required");
		} else if (!event.getOrganizationContactMobile().matches(EventConstants.MOBILE_REGEX)) {
			errors.put("organizationContactMobile", "Invalid mobile number format");
		}

		if (null == event.getOrganizationContactEmail() || event.getOrganizationContactEmail().isEmpty()) {
			errors.put("organizationContactEmail", "Organization contact email is required");
		} else if (!event.getOrganizationContactEmail().matches(EventConstants.EMAIL_REGEX)) {
			errors.put("organizationContactEmail", "Invalid email format");
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
	 */
	@Override
	public UserProfile validateToken(String token) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException, IllegalBlockSizeException, NumberFormatException, BadPaddingException {
		System.out.println(token);
		Result result = userProfileService.getUserProfile(encryptDecryptAES.decrypt(token,env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)));
		return result.getUserProfile()[0];
	}

	/**
	 * Method to validate the values given in ParticipantIntroductionRequest before deleting the participant.
	 * @param participantRequest
	 * @return errors
	 */
	@Override
	public Map<String, String> checkDeleteRequestMandatoryFields(ParticipantIntroductionRequest participantRequest) {
		Map<String, String> errors = new HashMap<>();
		if (null == participantRequest.getEventId() || participantRequest.getEventId().isEmpty()) {
			errors.put("eventId", "event Id is required");
		} else if (null != participantRequest.getEventId() && !participantRequest.getEventId().matches("^E[0-9]{6}$")) {
			errors.put("eventId", "event Id invalid");
		} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
			errors.put("eventId", "Invalid EventId - No event exists for the given event Id");
		}
		if (0 == participantRequest.getParticipantIds().size()) {
			errors.put("partcipantIds", "No participant Ids are available to delete");
		}
		return errors;
	}

}