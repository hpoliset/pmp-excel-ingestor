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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.exception.InvalidDateException;
import org.srcm.heartfulness.helper.CreateEventHelper;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.Event;
import org.srcm.heartfulness.model.json.request.EventAdminChangeRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.PmpParticipantService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.UserProfileService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/api/event")
public class EventsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventsController.class);

	@Autowired
	private ProgramService programService;

	@Autowired
	Environment env;

	@Autowired
	private AESEncryptDecrypt aesEncryptDecrypt;

	@Autowired
	CreateEventHelper ceh;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private PmpParticipantService participantService;

	/**
	 * Web service endpoint to fetch list of events.
	 * 
	 * If list of events are found successfully, the service returns an success
	 * response body with HTTP status 200.
	 * 
	 * If list of events are not found, the service returns an empty response
	 * body with HTTP status 200.
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param token
	 *            ,Token to be validated against mysrcm endpoint.
	 * @return A ResponseEntity containing success message, if created
	 *         successfully, and a HTTP status code as described in the method
	 *         comment.
	 */
	@RequestMapping(value = "/geteventlist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEventList(@RequestHeader(value = "Authorization") String token) {
		List<Event> eventList = new ArrayList<>();
		boolean isAdmin = false;
		try {

			UserProfile userProfile = ceh.validateToken(token);
			if (null == userProfile) {
				LOGGER.debug("UserProfile doesnot exists in MySrcm database");
				ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}

			User user = userProfileService.loadUserByEmail(userProfile.getEmail());
			if (null == user) {
				LOGGER.debug("UserProfile doesnot exists in PMP database");
				ErrorResponse eResponse = new ErrorResponse("Failed", "User unavailable in pmp database");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
			}

			if (PMPConstants.LOGIN_ROLE_ADMIN.equals(user.getRole())) {
				isAdmin = true;
			}
			eventList = programService.getEventListByEmail(user.getEmail(), isAdmin);
			return new ResponseEntity<List<Event>>(eventList, HttpStatus.OK);

		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid authorization token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid request");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Web service endpoint to create an event.
	 * 
	 * If event is created successfully, the service returns an event with
	 * eventId in response body with HTTP status 200.
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param Event
	 *            to create an event in the pmp database.
	 * @param token
	 *            Token to be validated against mysrcm endpoint.
	 * @return A ResponseEntity containing success message if found, and a HTTP
	 *         status code as described in the method comment.
	 * @param program
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createProgram(@RequestBody Event event,
			@RequestHeader(value = "Authorization") String token) {
		try {

			List<Event> eventList = new ArrayList<Event>();
			UserProfile userProfile = ceh.validateToken(token);
			if (null == userProfile) {
				LOGGER.debug("UserProfile doesnot exists in MySrcm database");
				ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}

			eventList.add(event);
			programService.createOrUpdateEvent(eventList);
			return new ResponseEntity<Event>(event, HttpStatus.OK);

		} catch (InvalidDateException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", e.getMessage());
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid authorization token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid request");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Web service endpoint to update event.
	 * 
	 * If event is updated successfully, the service returns an success message
	 * in response body with HTTP status 200.
	 * 
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param Event
	 *            to update an existing event in the pmp database.
	 * @param token
	 *            Token to be validated against mysrcm endpoint.
	 * @return A ResponseEntity containing success message if found, and a HTTP
	 *         status code as described in the method comment.
	 * @param program
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateProgram(@RequestBody Event event,
			@RequestHeader(value = "Authorization") String token) {
		try {

			List<Event> eventList = new ArrayList<Event>();
			UserProfile userProfile = ceh.validateToken(token);
			if (null == userProfile) {
				LOGGER.debug("UserProfile doesnot exists in MySrcm database");
				ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}
			Map<String, String> errors = new HashMap<String, String>();
			if (null == event.getAutoGeneratedEventId() || event.getAutoGeneratedEventId().isEmpty()) {
				errors.put("eventId", "Invalid Event ID");
				event.setErrors(errors);
				event.setStatus("Failed");
			} else {

				int programId = programService.getProgramIdByEventId(event.getAutoGeneratedEventId());
				if (programId > 0) {
					eventList.add(event);
					programService.createOrUpdateEvent(eventList);
				} else {
					errors.put("eventId", "Event ID does not exists");
					event.setErrors(errors);
					event.setStatus("Failed");
				}
			}
			return new ResponseEntity<Event>(event, HttpStatus.OK);

		} catch (InvalidDateException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", e.getMessage());
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid authorization token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid request");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 
	 * Web service endpoint to update list of events .
	 * 
	 * If event is updated successfully status is set to Success else to Failed
	 * and the service returns a list of events with HTTP status 200.
	 * 
	 * 
	 * If some exception is raised while processing the request, error response
	 * is returned with respective HttpStatus code.
	 * 
	 * @param events
	 *            List of events to be updated.
	 * @param token
	 *            Need to be authenticated against mysrcm.
	 * @return list of events with success ot failed status.
	 */

	@RequestMapping(value = "/updateevents", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updatePrograms(@RequestBody List<Event> events,
			@RequestHeader(value = "Authorization") String token) {
		try {

			UserProfile userProfile = ceh.validateToken(token);
			if (null == userProfile) {
				LOGGER.debug("UserProfile doesnot exists in MySrcm database");
				ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
			}

			List<Event> eventList = new ArrayList<>();
			for (Event event : events) {
				Map<String, String> errors = new HashMap<String, String>();
				if (null == event.getAutoGeneratedEventId() || event.getAutoGeneratedEventId().isEmpty()) {
					errors.put("eventId", "Invalid Event ID");
					event.setErrors(errors);
					event.setStatus("Failed");
					eventList.add(event);
				} else {
					int programId = programService.getProgramIdByEventId(event.getAutoGeneratedEventId());
					if (programId > 0) {
						List<Event> eventsList = new ArrayList<>();
						eventsList.add(event);
						eventList.addAll(programService.createOrUpdateEvent(eventsList));
					} else {
						errors.put("eventId", "Event ID does not exists");
						event.setErrors(errors);
						event.setStatus("Failed");
						eventList.add(event);
					}
				}
			}

			return new ResponseEntity<List<Event>>(eventList, HttpStatus.OK);

		} catch (InvalidDateException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", e.getMessage());
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid authorization token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.UNAUTHORIZED);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid client credentials");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Error while fetching profile from mysrcm");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid request");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateeventadmin", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateEventAdmin(@RequestHeader(value = "Authorization") String token,
			@RequestBody EventAdminChangeRequest eventAdminChangeRequest) {
		try {
			UserProfile userprofile = ceh.validateToken(token);
			if (null == userprofile) {
				ErrorResponse error = new ErrorResponse("Failed", "Invalid Auth token");
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				Map<String, String> map = ceh.checkUpdateEventAdminMandatoryFields(eventAdminChangeRequest);
				if (!map.isEmpty()) {
					return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
				} else {
					eventAdminChangeRequest.setCreatedBy(userprofile.getEmail());
					programService.updateEventAdmin(eventAdminChangeRequest);
					programService.updateCoOrdinatorStatistics(eventAdminChangeRequest);
					return new ResponseEntity<EventAdminChangeRequest>(eventAdminChangeRequest, HttpStatus.OK);
				}
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid auth token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse("Failed", "Invalid Auth token");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed",
					"json mapping-error : json data is not mapped properly");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/geteventcountbycategory", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCount(@RequestHeader(value = "Authorization") String token) {
		try {
			boolean isAdmin = false;
			UserProfile userprofile = ceh.validateToken(token);
			if (null == userprofile) {
				ErrorResponse error = new ErrorResponse("Failed", "Invalid Auth token");
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
			} else {
				User user = userProfileService.loadUserByEmail(userprofile.getEmail());
				if (null != user && PMPConstants.LOGIN_ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
					isAdmin = true;
				}
				Map<String, Integer> resultMap = new HashMap();
				List<String> Eventcategories = programService.getAllEventCategories();
				resultMap.put("Total Events", programService.getEventCountByEmail(userprofile.getEmail(), isAdmin));
				resultMap.put("Un-Categorized",
						programService.getNonCategorizedEventsByEmail(userprofile.getEmail(), isAdmin));
				for (String eventCategory : Eventcategories) {
					resultMap.put(eventCategory.toUpperCase(),
							programService.getEventCountByCategory(userprofile.getEmail(), isAdmin, eventCategory));
				}
				resultMap.put("Miscellaneous",
						programService.getMiscellaneousEventsByEmail(userprofile.getEmail(), isAdmin, Eventcategories));
				return new ResponseEntity<Map<String, Integer>>(resultMap, HttpStatus.OK);

			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Invalid auth token");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.REQUEST_TIMEOUT);
		} catch (IllegalBlockSizeException | NumberFormatException | BadPaddingException e) {
			ErrorResponse error = new ErrorResponse("Failed", "Invalid Auth token");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		} catch (JsonParseException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "parse-error : error while parsing json data");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed",
					"json mapping-error : json data is not mapped properly");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "input/output-error ; Please try after sometime");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error("Exception    :" + e.getMessage());
			ErrorResponse eResponse = new ErrorResponse("Failed", "Please try after sometime.");
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}