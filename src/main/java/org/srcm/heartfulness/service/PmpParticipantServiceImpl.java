package org.srcm.heartfulness.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class PmpParticipantServiceImpl implements PmpParticipantService {

	@Autowired
	ParticipantRepository participantRepository;
	
	@Autowired
	ProgramRepository programrepository;
	
	@Override
	public ParticipantRequest createParticipant(ParticipantRequest participantRequest) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Participant participant;
		if ((null == participantRequest.getSeqId() || participantRequest.getSeqId().isEmpty()) && 0 == participantRequest.getId() ) {
			participant=new Participant();
			participant.setProgramId(programrepository.getProgramIdByEventId(participantRequest.getEventId()));
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if((participantRequest.getGender().equalsIgnoreCase("Male") || participantRequest.getGender().equalsIgnoreCase("Female")) && participantRequest.getGender() !=null && !participantRequest.getGender().isEmpty())
				participantRequest.setGender((participantRequest.getGender().equalsIgnoreCase("Male") && participantRequest.getGender() !=null && !participantRequest.getGender().isEmpty())?"M":"F");
			participant.setGender(participantRequest.getGender());
			participant.setDateOfBirth(null !=participantRequest.getDateOfBirth() ?sdf1.parse(sdf1.format(sdf.parse(participantRequest.getDateOfBirth()))):null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() && PMPConstants.REQUIRED_YES.equalsIgnoreCase(participantRequest.getIntroducedStatus()))?1:0);
			participant.setIntroductionDate(null != participantRequest.getIntroductionDate()?sdf1.parse(sdf1.format(sdf.parse(participantRequest.getIntroductionDate()))) : null);
		}else{
			participant=participantRepository.findBySeqId(participantRequest);
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if((participantRequest.getGender().equalsIgnoreCase("Male") || participantRequest.getGender().equalsIgnoreCase("Female") ) && participantRequest.getGender() !=null && !participantRequest.getGender().isEmpty())
				participantRequest.setGender(participantRequest.getGender().equalsIgnoreCase("Male") && participantRequest.getGender() !=null && !participantRequest.getGender().isEmpty() ?"M":"F");
			participant.setGender(participantRequest.getGender());
			participant.setDateOfBirth(null !=participantRequest.getDateOfBirth() ?sdf1.parse(sdf1.format(sdf.parse(participantRequest.getDateOfBirth()))):null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() && PMPConstants.REQUIRED_YES.equalsIgnoreCase(participantRequest.getIntroducedStatus()))?1:0);
			participant.setIntroductionDate(null != participantRequest.getIntroductionDate()?sdf1.parse(sdf1.format(sdf.parse(participantRequest.getIntroductionDate()))) : null);
		}
		participantRepository.save(participant);
		participantRequest.setSeqId(participant.getSeqId());
		participantRequest.setPrintName(participant.getPrintName());
		participantRequest.setEmail(participant.getEmail());
		participantRequest.setMobilePhone(participant.getMobilePhone());
		if((participant.getGender().equalsIgnoreCase("F") || participant.getGender().equalsIgnoreCase("M") )&& participant.getGender() !=null && !participant.getGender().isEmpty())
			participant.setGender(participant.getGender().equalsIgnoreCase("M")?"Male":"Female");
		participantRequest.setGender(participant.getGender());
		participantRequest.setDateOfBirth(null !=participant.getDateOfBirth() ?sdf.format(participant.getDateOfBirth()):null);
		participantRequest.setAddressLine1(participant.getAddressLine1());
		participantRequest.setAddressLine2(participant.getAddressLine2());
		participantRequest.setCity(participant.getCity());
		participantRequest.setState(participant.getState());
		participantRequest.setCountry(participant.getCountry());
		participantRequest.setIntroducedStatus(0 != participant.getIntroduced()?PMPConstants.REQUIRED_YES:PMPConstants.REQUIRED_NO);
		participantRequest.setIntroductionDate(null != participantRequest.getIntroductionDate()? sdf.format(participant.getIntroductionDate()) : null);
		return participantRequest;
	}

	@Override
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Participant participant=participantRepository.findBySeqId(participantRequest);
		participantRequest.setPrintName(participant.getPrintName());
		participantRequest.setEmail(participant.getEmail());
		participantRequest.setMobilePhone(participant.getMobilePhone());
		if((participant.getGender().equalsIgnoreCase("F") || participant.getGender().equalsIgnoreCase("M") )&& participant.getGender() !=null && !participant.getGender().isEmpty())
			participant.setGender(participant.getGender().equalsIgnoreCase("M")?"Male":"Female");
		participantRequest.setGender(participant.getGender());
		participantRequest.setDateOfBirth(null !=participant.getDateOfBirth() ?sdf.format(participant.getDateOfBirth()):null);
		participantRequest.setAddressLine1(participant.getAddressLine1());
		participantRequest.setAddressLine2(participant.getAddressLine2());
		participantRequest.setCity(participant.getCity());
		participantRequest.setState(participant.getState());
		participantRequest.setCountry(participant.getCountry());
		participantRequest.setIntroducedStatus(0 != participant.getIntroduced()?PMPConstants.REQUIRED_YES:PMPConstants.REQUIRED_NO);
		participantRequest.setIntroductionDate(null != participantRequest.getIntroductionDate()?sdf.format(participant.getIntroductionDate()) : null);
		return participantRequest;
	}

}
