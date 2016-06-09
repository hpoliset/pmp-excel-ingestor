package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.Aspirant;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.response.GeoSearchResponse;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.rest.template.SrcmRestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
public class EWelcomeIDGenerationHelper {
	
	@Autowired
	SrcmRestTemplate srcmRestTemplate;
	
	
	/**
	 * Method to generate e-welcome ID by calling the SRCM API
	 * 
	 * @param participant
	 * @throws HttpClientErrorException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void generateEWelcomeId(Participant participant) throws HttpClientErrorException, JsonParseException,
			JsonMappingException, IOException {

		GeoSearchResponse geoSearchResponse = srcmRestTemplate.geoSearch(participant.getCity() + ","
				+ participant.getState() + "," + participant.getCountry());
		Aspirant aspirant = new Aspirant();
		aspirant.setCity(String.valueOf(geoSearchResponse.getCityId()));
		aspirant.setState(String.valueOf(geoSearchResponse.getStateId()));
		aspirant.setCountry(String.valueOf(geoSearchResponse.getCountryId()));
		SimpleDateFormat sdf = new SimpleDateFormat(PMPConstants.SQL_DATE_FORMAT);
		aspirant.setDateOfBirth((null != participant.getDateOfBirth()) ? sdf.format(participant.getDateOfBirth())
				: null);
		aspirant.setDateOfJoining((null != participant.getDateOfRegistration()) ? sdf.format(participant
				.getDateOfRegistration()) : null);
		aspirant.setEmail((null != participant.getEmail() && !participant.getEmail().isEmpty()) ? participant
				.getEmail() : null);
		System.out.println(participant.getProgram().toString());
		aspirant.setFirstSittingBy((null != participant.getProgram().getPrefectId() && !participant.getProgram()
				.getPrefectId().isEmpty()) ? participant.getProgram().getPrefectId() : null);
		aspirant.setSrcmGroup((null != participant.getProgram().getSrcmGroup() && !participant.getProgram()
				.getSrcmGroup().isEmpty()) ? participant.getProgram().getSrcmGroup() : null);
		aspirant.setMobile((null != participant.getMobilePhone() && !participant.getMobilePhone().isEmpty()) ? participant
				.getMobilePhone() : null);
		aspirant.setName((null != participant.getPrintName() && !participant.getPrintName().isEmpty()) ? participant
				.getPrintName() : null);
		aspirant.setFirstName((null != participant.getFirstName() && !participant.getFirstName().isEmpty()) ? participant
				.getFirstName() : null);
		aspirant.setStreet((null != participant.getAddressLine1() && !participant.getAddressLine1().isEmpty()) ? participant
				.getAddressLine1() : null);
		aspirant.setStreet2((null != participant.getAddressLine2() && !participant.getAddressLine2().isEmpty()) ? participant
				.getAddressLine2() : null);
		System.out.println(aspirant.toString());
		UserProfile userProfile = srcmRestTemplate.createAspirant(aspirant);
		participant.setWelcomeCardNumber(userProfile.getRef());
		participant.setWelcomeCardDate(new Date());
	}
	
	

}
