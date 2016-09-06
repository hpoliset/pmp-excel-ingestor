package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.EndpointConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.model.Aspirant;
import org.srcm.heartfulness.model.PMPAPIAccessLogDetails;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.json.response.CitiesAPIResponse;
import org.srcm.heartfulness.model.json.response.GeoSearchResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.repository.ProgramRepository;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.SmsUtil;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class EWelcomeIDGenerationHelper {

	@Autowired
	SrcmRestTemplate srcmRestTemplate;

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	APIAccessLogService apiAccessLogService;

	/**
	 * Method to generate e-welcome ID by calling the SRCM API
	 * 
	 * @param participant
	 * @param id
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void generateEWelcomeId(Participant participant, int id) throws HttpClientErrorException,
			JsonParseException, JsonMappingException, IOException, ParseException {

		PMPAPIAccessLogDetails accessLogDetails = new PMPAPIAccessLogDetails(id, EndpointConstants.GEOSEARCH_URI,
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(participant.getCity() + "," + participant.getState() + ","
						+ participant.getCountry()), null);
		apiAccessLogService.createPmpAPIAccesslogDetails(accessLogDetails);
		GeoSearchResponse geoSearchResponse = srcmRestTemplate.geoSearch(participant.getCity() + ","
				+ participant.getState() + "," + participant.getCountry());
		accessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		accessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		accessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(geoSearchResponse));
		apiAccessLogService.updatePmpAPIAccesslogDetails(accessLogDetails);

		PMPAPIAccessLogDetails citiesAPIAccessLogDetails = new PMPAPIAccessLogDetails(id, EndpointConstants.CITIES_API,
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(geoSearchResponse.getCityId()), null);
		apiAccessLogService.createPmpAPIAccesslogDetails(citiesAPIAccessLogDetails);
		CitiesAPIResponse citiesAPIResponse = srcmRestTemplate.getCityName(geoSearchResponse.getCityId());
		citiesAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		citiesAPIAccessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(citiesAPIResponse));
		citiesAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		apiAccessLogService.updatePmpAPIAccesslogDetails(citiesAPIAccessLogDetails);

		Aspirant aspirant = new Aspirant();
		aspirant.setCity(citiesAPIResponse.getName());
		aspirant.setState(String.valueOf(geoSearchResponse.getStateId()));
		aspirant.setCountry(String.valueOf(geoSearchResponse.getCountryId()));
		SimpleDateFormat sdf = new SimpleDateFormat(PMPConstants.SQL_DATE_FORMAT);
		aspirant.setDateOfBirth((null != participant.getDateOfBirth()) ? sdf.format(participant.getDateOfBirth())
				: null);
		aspirant.setDateOfJoining((null != participant.getProgram().getProgramStartDate()) ? sdf.format(participant
				.getProgram().getProgramStartDate()) : null);
		aspirant.setEmail((null != participant.getEmail() && !participant.getEmail().isEmpty()) ? participant
				.getEmail() : null);
		System.out.println(participant.getProgram().toString());
		aspirant.setFirstSittingBy((null != participant.getProgram().getPrefectId() && !participant.getProgram()
				.getPrefectId().isEmpty()) ? participant.getProgram().getPrefectId() : null);
		aspirant.setSrcmGroup(0 != geoSearchResponse.getNearestCenter() ? String.valueOf(geoSearchResponse
				.getNearestCenter()) : null);
		aspirant.setMobile((null != participant.getMobilePhone() && !participant.getMobilePhone().isEmpty()) ? participant
				.getMobilePhone() : null);
		aspirant.setName((null != participant.getPrintName() && !participant.getPrintName().isEmpty()) ? participant
				.getPrintName() : null);
		aspirant.setFirstName((null != participant.getFirstName() && !participant.getFirstName().isEmpty()) ? participant
				.getFirstName() : participant.getPrintName());
		aspirant.setStreet((null != participant.getAddressLine1() && !participant.getAddressLine1().isEmpty()) ? participant
				.getAddressLine1() : null);
		aspirant.setStreet2((null != participant.getAddressLine2() && !participant.getAddressLine2().isEmpty()) ? participant
				.getAddressLine2() : null);

		PMPAPIAccessLogDetails aspirantAPIAccessLogDetails = new PMPAPIAccessLogDetails(id,
				EndpointConstants.CREATE_ASPIRANT_URI, DateUtils.getCurrentTimeInMilliSec(), null,
				ErrorConstants.STATUS_FAILED, null, StackTraceUtils.convertPojoToJson(aspirant), null);
		apiAccessLogService.createPmpAPIAccesslogDetails(aspirantAPIAccessLogDetails);
		UserProfile userProfile = srcmRestTemplate.createAspirant(aspirant);
		aspirantAPIAccessLogDetails.setResponseTime(DateUtils.getCurrentTimeInMilliSec());
		aspirantAPIAccessLogDetails.setResponseBody(StackTraceUtils.convertPojoToJson(userProfile));
		aspirantAPIAccessLogDetails.setStatus(ErrorConstants.STATUS_SUCCESS);
		apiAccessLogService.updatePmpAPIAccesslogDetails(aspirantAPIAccessLogDetails);

		participant.getProgram().setSrcmGroup(String.valueOf(geoSearchResponse.getNearestCenter()));
		participant.setWelcomeCardNumber(userProfile.getRef());
		participant.setWelcomeCardDate(new Date());
	}

	/**
	 * Method to generate e-welcome ID by calling the SRCM API (SMS INTEGRATION
	 * WITHOUT LOG)
	 * 
	 * @param participant
	 * @param id
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void generateEWelcomeId(Participant participant) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {

		GeoSearchResponse geoSearchResponse = srcmRestTemplate.geoSearch(participant.getCity() + ","
				+ participant.getState() + "," + participant.getCountry());
		CitiesAPIResponse citiesAPIResponse = srcmRestTemplate.getCityName(geoSearchResponse.getCityId());
		Aspirant aspirant = new Aspirant();
		aspirant.setCity(citiesAPIResponse.getName());
		aspirant.setState(String.valueOf(geoSearchResponse.getStateId()));
		aspirant.setCountry(String.valueOf(geoSearchResponse.getCountryId()));
		SimpleDateFormat sdf = new SimpleDateFormat(PMPConstants.SQL_DATE_FORMAT);
		aspirant.setDateOfBirth((null != participant.getDateOfBirth()) ? sdf.format(participant.getDateOfBirth())
				: null);
		aspirant.setDateOfJoining((null != participant.getProgram().getProgramStartDate()) ? sdf.format(participant
				.getProgram().getProgramStartDate()) : null);
		aspirant.setEmail((null != participant.getEmail() && !participant.getEmail().isEmpty()) ? participant
				.getEmail() : null);
		System.out.println(participant.getProgram().toString());
		aspirant.setFirstSittingBy((null != participant.getProgram().getPrefectId() && !participant.getProgram()
				.getPrefectId().isEmpty()) ? participant.getProgram().getPrefectId() : null);
		aspirant.setSrcmGroup(0 != geoSearchResponse.getNearestCenter() ? String.valueOf(geoSearchResponse
				.getNearestCenter()) : null);
		aspirant.setMobile((null != participant.getMobilePhone() && !participant.getMobilePhone().isEmpty()) ? participant
				.getMobilePhone() : null);
		aspirant.setName((null != participant.getPrintName() && !participant.getPrintName().isEmpty()) ? participant
				.getPrintName() : null);
		aspirant.setFirstName((null != participant.getFirstName() && !participant.getFirstName().isEmpty()) ? participant
				.getFirstName() : participant.getPrintName());
		aspirant.setStreet((null != participant.getAddressLine1() && !participant.getAddressLine1().isEmpty()) ? participant
				.getAddressLine1() : null);
		aspirant.setStreet2((null != participant.getAddressLine2() && !participant.getAddressLine2().isEmpty()) ? participant
				.getAddressLine2() : null);
		UserProfile userProfile = srcmRestTemplate.createAspirant(aspirant);
		participant.getProgram().setSrcmGroup(String.valueOf(geoSearchResponse.getNearestCenter()));
		participant.setWelcomeCardNumber(userProfile.getRef());
		participant.setWelcomeCardDate(new Date());
	}

	/**
	 * Method to save the program details into the pmp database
	 * 
	 * @param program
	 * @param userProfile
	 * @return
	 */
	public Program saveProgram(Program program, UserProfile userProfile) {
		program.setCreatedBy("admin");
		program.setCreateTime(new Date());
		program.setPrefectId(String.valueOf(userProfile.getPrefect_id()));
		program.setSrcmGroup(String.valueOf(userProfile.getSrcm_group()));
		program.setCoordinatorEmail(userProfile.getUser_email());
		program.setCoordinatorName(userProfile.getName());
		program.setAutoGeneratedEventId(SMSConstants.SMS_EVENT_ID_PREFIX + SmsUtil.generateRandomNumber(6));
		program.setAutoGeneratedIntroId(SMSConstants.SMS_INTRO_ID_PREFIX + SmsUtil.generateRandomNumber(7));
		program.setCreatedSource("SMS");
		programRepository.saveWithProgramName(program);
		return program;

	}

}
