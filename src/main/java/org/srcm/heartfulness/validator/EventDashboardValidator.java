package org.srcm.heartfulness.validator;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.EventPagination;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.UserProfile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Validator to streamline all Event dashboard validation implementation.
 * 
 * @author Koustav Dutta
 * 
 */

@Component
public interface EventDashboardValidator {

	/**
	 * Method to validate mandatory fields in the participant request before
	 * creating participant.
	 * @param string 
	 * @param emailList 
	 * 
	 * @param participant
	 * @param accessLog 
	 * @param authToken 
	 * @return
	 */
	public Map<String, String> checkParticipantMandatoryFields(List<String> emailList, String userRole, ParticipantRequest participant, String authToken, PMPAPIAccessLog accessLog);

	/**
	 * Method to validate the values given in
	 * <code>ParticipantIntroductionRequest</code> before updating introductory
	 * status.
	 * 
	 * @param participantRequest
	 * @param id
	 * @return
	 */
	public Map<String, String> checkIntroductionRequestMandatoryFields(
			List<String> emailList,String userRole,ParticipantIntroductionRequest participantRequest,String authToken,PMPAPIAccessLog accessLog);

	/**
	 * Method to validate the values given in
	 * <code>EventAdminChangeRequest</code> before updating event admin.
	 * 
	 * @param eventAdminChangeRequest
	 * @return errors
	 */
	public Map<String, String> checkUpdateEventAdminMandatoryFields(EventAdminChangeRequest eventAdminChangeRequest);

	/**
	 * Method is used to validate the mandatory parameters before persisting an
	 * event. Other validations include date validation and mobile number
	 * validation.
	 * 
	 * @param program
	 *            is validated to check all the mandatory parameters.
	 * @return map of error if exists
	 */
	public Map<String, String> checkMandatoryEventFields(Event event);

	/**
	 * Token is validated against MySRCM endpoint.
	 * 
	 * @param token
	 *            need to be validated against MySRCM API.
	 * @throws HttpClientErrorException
	 *             if client exception occurs.
	 * @throws JsonParseException
	 *             while parsing JSON data.
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public UserProfile validateToken(String token, int id) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException, IllegalBlockSizeException, NumberFormatException, BadPaddingException,
			ParseException;

	/**
	 * Method to validate the values given in ParticipantIntroductionRequest
	 * before deleting the participant.
	 * @param userRole 
	 * @param emailList 
	 * 
	 * @param participantRequest
	 * @param accessLog 
	 * @param authToken 
	 * @return errors
	 */
	public Map<String, String> checkDeleteRequestMandatoryFields(List<String> emailList, String userRole, ParticipantIntroductionRequest participantRequest, String authToken, PMPAPIAccessLog accessLog);

	/**
	 * Method to validate the mandatory fields in the participant request before
	 * introducing the participants.
	 * 
	 * @param participantInput
	 * @param id
	 * @return
	 */
	public List<String> checkParticipantIntroductionMandatoryFields(Participant participantInput, int id);

	/**
	 * Method to validate whether participant completed preliminary sittings or
	 * not.
	 * 
	 * @param participantInput
	 * @return true,if valid.
	 */
	public boolean validateParticipantCompletedPreliminarySittings(Participant participantInput);

	/**
	 * Method to validate the mandatory fields in the <code>Participant</code>
	 * request before updating the particpant details.
	 * @param userRole 
	 * @param emailList 
	 * 
	 * @param participant
	 * @param accessLog 
	 * @param authToken 
	 * @return errors <code>Map<String, String</code>.
	 */
	public Map<String, String> checkUpdateParticipantMandatoryFields(List<String> emailList, String userRole, ParticipantRequest participant, String authToken, PMPAPIAccessLog accessLog);

	/**
	 * Method to validate the pagination properties before fetching the related
	 * results.
	 * 
	 * @param eventPagination
	 * @return error message, if invalid.
	 */
	public String validatePaginationProperties(EventPagination eventPagination);
	
	public String validateSearchParameters(SearchRequest searchRequest);

	public String checkProgramAccess(List<String> emailList, String role, String eventId, String token,PMPAPIAccessLog accessLog);
			

}
