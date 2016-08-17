package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.ParticipantIntroductionRequest;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.model.json.response.EWelcomeIDErrorResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.UpdateIntroductionResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.validator.EventDashboardValidator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class holds the web service end points for participant related dashboard
 * services.
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/participant")
public class ParticipantsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantsController.class);

	@Autowired
	private ProgramService programService;

	@Autowired
	private PmpParticipantService participantService;

	@Autowired
	EventDashboardValidator eventDashboardValidator;

	@Autowired
	private UserProfileService userProfileService;

	/**
	 * Web service endpoint to fetch list of participants.
	 * 
	 * If list of participants are found successfully, the service returns an
	 * success response body with HTTP status 200.
	 * 
	 * If list of events are not found, the service returns an empty response
	 * body with HTTP status 200.
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param encryptedProgramId
	 *            to check whether the program already exists or not.
	 * @param token
	 *            Token to be validated against MySRCM endpoint.
	 * @return A ResponseEntity containing participant details if found, and a
	 *         HTTP status code as described in the method comment.
	 */
	@RequestMapping(value = "/getparticipantlist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getParticipantList(@RequestHeader(value = "Authorization") String token,
			@RequestBody(required = true) Event event) {
		LOGGER.error("Fetching participant List....");
		List<ParticipantRequest> participantList = new ArrayList<ParticipantRequest>();
		try {
			if (null == event.getAutoGeneratedEventId() || event.getAutoGeneratedEventId().isEmpty()) {
				LOGGER.debug("Invalid event ID hasbeen sent in the request");
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid Event ID");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			UserProfile userProfile = eventDashboardValidator.validateToken(token);
			if (null == userProfile) {
				LOGGER.debug("UserProfile doesnot exists in MySrcm database");
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}
			participantList = programService.getParticipantByEventId(event.getAutoGeneratedEventId());
			return new ResponseEntity<List<ParticipantRequest>>(participantList, HttpStatus.OK);

		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid authorization token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid client credentials");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Invalid request");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Web service endpoint to fetch participant details.
	 * 
	 * @param request
	 *            contains seqId and eventId to get the participant details
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing participant details if found, and a
	 *         HTTP status code as described in the method comment.
	 */
	@RequestMapping(value = "/getparticipantdetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getPaticipantDetails(@RequestBody ParticipantRequest request,
			@RequestHeader(value = "Authorization") String token) {
		try {
			LOGGER.error("Fetching participant details....");
			if (null == eventDashboardValidator.validateToken(token)) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			}
			if (null == request.getSeqId() || request.getSeqId().isEmpty() || null == request.getEventId()
					|| request.getEventId().isEmpty()) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, "event ID and Seq Id is required");
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.PRECONDITION_FAILED);
			} else {
				ParticipantRequest participant = participantService.getParticipantBySeqId(request);
				return new ResponseEntity<ParticipantRequest>(participant, HttpStatus.OK);
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to create new participant.
	 * 
	 * @param request
	 *            contains eventId and participant information to create new
	 *            participant
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing participant details after creating
	 *         participant, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createParticipant(@RequestBody ParticipantRequest participant,
			@RequestHeader(value = "Authorization") String token) {
		try {
			if (null == eventDashboardValidator.validateToken(token)) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				Map<String, String> map = eventDashboardValidator.checkPartcicipantMandatoryFields(participant);
				if (!map.isEmpty()) {
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					ParticipantRequest newparticipant = participantService.createParticipant(participant);
					return new ResponseEntity<ParticipantRequest>(newparticipant, HttpStatus.OK);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to update participant details.
	 * 
	 * @param request
	 *            contains seqId, eventId and other participant details which
	 *            need to be updated
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing participant details after updating
	 *         participant details, and a HTTP status code as described in the
	 *         method comment.
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateParticipant(@RequestBody ParticipantRequest participant,
			@RequestHeader(value = "Authorization") String token) {
		try {
			if (null == eventDashboardValidator.validateToken(token)) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			}
			if (null == participant.getPrintName()) {
				Map<String, String> errors = new HashMap<>();
				errors.put(ErrorConstants.STATUS_FAILED, "print name cannot be null");
				return new ResponseEntity<Map<String, String>>(errors, HttpStatus.PRECONDITION_FAILED);
			}
			ParticipantRequest newparticipant = participantService.createParticipant(participant);
			return new ResponseEntity<ParticipantRequest>(newparticipant, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Web service endpoint to delete participant from participant details and
	 * logs the deleted participant information in the deleted_participants
	 * table.
	 * 
	 * @param request
	 *            contains eventId and its list of seqIds to delete the
	 *            participant from participant table
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing status and its description based on
	 *         deletion, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteParticipant(@RequestBody ParticipantIntroductionRequest participantRequest,
			@RequestHeader(value = "Authorization") String token) {
		try {
			UserProfile userprofile = eventDashboardValidator.validateToken(token);
			List<String> description = null;
			if (null == userprofile) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				Map<String, String> map = eventDashboardValidator.checkDeleteRequestMandatoryFields(participantRequest);
				if (!map.isEmpty()) {
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					List<UpdateIntroductionResponse> result = new ArrayList<UpdateIntroductionResponse>();
					for (ParticipantRequest participant : participantRequest.getParticipantIds()) {
						if (null == participant.getSeqId() || participant.getSeqId().isEmpty()) {
							description = new ArrayList<String>();
							description.add("Invalid SeqID");
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participant.getSeqId(), ErrorConstants.STATUS_FAILED, description);

							result.add(response);
						} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
							description = new ArrayList<String>();
							description.add("Invalid eventID");
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participantRequest.getEventId(), ErrorConstants.STATUS_FAILED, description);
							result.add(response);
						} else if (0 != programService.getProgramIdByEventId(participantRequest.getEventId())
								&& null == programService.findParticipantBySeqId(participant.getSeqId(),
										programService.getProgramIdByEventId(participantRequest.getEventId()))) {
							description = new ArrayList<String>();
							description.add("Invalid seqId");
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participant.getSeqId(), ErrorConstants.STATUS_FAILED, description);
							result.add(response);
						} else {
							Participant deletedParticipant = programService.deleteParticipant(participant.getSeqId(),
									participantRequest.getEventId());
							description = new ArrayList<String>();
							description.add("Participant deleted successfully");
							programService.updateDeletedParticipant(deletedParticipant, userprofile.getEmail());
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participant.getSeqId(), ErrorConstants.STATUS_SUCCESS, description);
							result.add(response);
						}
					}
					return new ResponseEntity<List<UpdateIntroductionResponse>>(result, HttpStatus.OK);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"client-error : Invalid auth token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"json mapping-error : json data is not mapped properly");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Web service endpoint to update participant introduction status for the
	 * list of participants.
	 * 
	 * @param request
	 *            contains eventId and its list of seqIds to update the
	 *            participant from participant table
	 * @param token
	 *            is for the authorizing the user
	 * @return ResponseEntity containing status and its description based on
	 *         updation, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/updateintroductionstatus", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> UpdateParticipantIntoroducedStatus(
			@RequestBody ParticipantIntroductionRequest participantRequest,
			@RequestHeader(value = "Authorization") String token) {
		List<String> description = null;
		try {
			if (null == eventDashboardValidator.validateToken(token)) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				Map<String, String> map = eventDashboardValidator
						.checkIntroductionRequestMandatoryFields(participantRequest);
				if (!map.isEmpty()) {
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					List<UpdateIntroductionResponse> result = new ArrayList<UpdateIntroductionResponse>();
					for (ParticipantRequest participant : participantRequest.getParticipantIds()) {
						String eWelcomeID = null;
						if (null == participant.getSeqId() || participant.getSeqId().isEmpty()) {
							description = new ArrayList<String>();
							description.add("Seq Id is required.");
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participant.getSeqId(), ErrorConstants.STATUS_FAILED, description);
							result.add(response);
						} else if (0 == programService.getProgramIdByEventId(participantRequest.getEventId())) {
							description = new ArrayList<String>();
							description.add("Invalid eventID");
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participantRequest.getEventId(), ErrorConstants.STATUS_FAILED, description);
							result.add(response);
						} else if (0 != programService.getProgramIdByEventId(participantRequest.getEventId())
								&& null == programService.findParticipantBySeqId(participant.getSeqId(),
										programService.getProgramIdByEventId(participantRequest.getEventId()))) {
							description = new ArrayList<String>();
							description.add("Invalid seqId");
							UpdateIntroductionResponse response = new UpdateIntroductionResponse(
									participant.getSeqId(), ErrorConstants.STATUS_FAILED, description);
							result.add(response);
						} else {
							try {
								if ("Y".equalsIgnoreCase(participantRequest.getIntroduced())) {

									int programID = programService.getProgramIdByEventId(participantRequest
											.getEventId());
									Participant participantInput = programService.findParticipantBySeqId(
											participant.getSeqId(), programID);
									UpdateIntroductionResponse response = null;
									List<String> errorResult = eventDashboardValidator
											.checkParticipantIntroductionMandatoryFields(participantInput);
									if (!errorResult.isEmpty()) {
										response = new UpdateIntroductionResponse(participant.getSeqId(),
												ErrorConstants.STATUS_FAILED, errorResult);
										result.add(response);
									} else {
										eWelcomeID = programService.generateeWelcomeID(participantInput);
										programService.UpdateParticipantsStatus(participant.getSeqId(),
												participantRequest.getEventId(), participantRequest.getIntroduced());
										if ("success".equalsIgnoreCase(eWelcomeID)) {
											description = new ArrayList<String>();
											description.add("Participant eWelcomeID : "
													+ participantInput.getWelcomeCardNumber());
											response = new UpdateIntroductionResponse(participant.getSeqId(),
													ErrorConstants.STATUS_SUCCESS, description);
										} else {
											description = new ArrayList<String>();
											description.add("Participant eWelcomeID : " + eWelcomeID);
											response = new UpdateIntroductionResponse(participant.getSeqId(),
													ErrorConstants.STATUS_SUCCESS, description);
										}
										result.add(response);
									}
								} else {
									programService.UpdateParticipantsStatus(participant.getSeqId(),
											participantRequest.getEventId(), participantRequest.getIntroduced());
									description = new ArrayList<String>();
									description.add("Participant introduced status updated successfully.");
									UpdateIntroductionResponse response = new UpdateIntroductionResponse(
											participant.getSeqId(), ErrorConstants.STATUS_SUCCESS, description);
									result.add(response);
								}
							} catch (HttpClientErrorException e) {
								description = new ArrayList<String>();
								ObjectMapper mapper = new ObjectMapper();
								EWelcomeIDErrorResponse eWelcomeIDErrorResponse = mapper.readValue(
										e.getResponseBodyAsString(), EWelcomeIDErrorResponse.class);
								description.add(eWelcomeIDErrorResponse.getEmail().get(0));
								UpdateIntroductionResponse response = new UpdateIntroductionResponse(
										participant.getSeqId(), ErrorConstants.STATUS_FAILED, description);
								result.add(response);
							}

						}
					}
					return new ResponseEntity<List<UpdateIntroductionResponse>>(result, HttpStatus.OK);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"client-error : Invalid auth token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"json mapping-error : json data is not mapped properly");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * webservice end point to search the participants based on the given search
	 * field and search text and based on program start date
	 * 
	 * @param token
	 * @param searchRequest
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> searchEvents(@RequestHeader(value = "Authorization") String token,
			@RequestBody SearchRequest searchRequest) {
		try {
			List<ParticipantRequest> participantList = new ArrayList<>();
			if (null == eventDashboardValidator.validateToken(token)) {
				ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				participantList = participantService.searchParticipants(searchRequest);
				return new ResponseEntity<List<ParticipantRequest>>(participantList, HttpStatus.OK);
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"client-error : Invalid auth token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse(ErrorConstants.STATUS_FAILED, ErrorConstants.INVALID_AUTH_TOKEN);
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"json mapping-error : json data is not mapped properly");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,
					"input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED, "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
