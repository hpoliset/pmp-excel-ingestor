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
 * service implementation class for the participant services
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

	/**
	 * method to create or update the participant of the particular event
	 */
	@Override
	public ParticipantRequest createParticipant(ParticipantRequest participantRequest) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		SimpleDateFormat sdf1 = new SimpleDateFormat(PMPConstants.SQL_DATE_FORMAT);
		Participant participant;
		if ((null == participantRequest.getSeqId() || participantRequest.getSeqId().isEmpty())
				&& 0 == participantRequest.getId()) {
			participant = new Participant();
			participant.setProgramId(programrepository.getProgramIdByEventId(participantRequest.getEventId()));
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if ((participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE) 
					|| participantRequest.getGender().equalsIgnoreCase(PMPConstants.FEMALE))
					&& participantRequest.getGender() != null
					&& !participantRequest.getGender().isEmpty())
				participantRequest.setGender((participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE)
								&& participantRequest.getGender() != null && !participantRequest.getGender().isEmpty())
								? PMPConstants.GENDER_MALE	: PMPConstants.GENDER_FEMALE);
			participant.setGender(participantRequest.getGender());
			participant.setDateOfBirth(null != participantRequest.getDateOfBirth()
					? sdf1.parse(sdf1.format(sdf.parse(participantRequest.getDateOfBirth()))) : null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() 
					&& PMPConstants.REQUIRED_YES.equalsIgnoreCase(participantRequest.getIntroducedStatus())) ? 1 : 0);
			participant.setIntroductionDate(null != participantRequest.getIntroductionDate() 
					? sdf1.parse(sdf1.format(sdf.parse(participantRequest.getIntroductionDate()))) : null);
		} else {
			participant = participantRepository.findBySeqId(participantRequest);
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if ((participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE) || participantRequest.getGender()
					.equalsIgnoreCase(PMPConstants.FEMALE))
					&& participantRequest.getGender() != null
					&& !participantRequest.getGender().isEmpty())
				participantRequest.setGender(participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE)
								&& participantRequest.getGender() != null && !participantRequest.getGender().isEmpty()
								? PMPConstants.GENDER_MALE: PMPConstants.GENDER_FEMALE);
			participant.setGender(participantRequest.getGender());
			participant.setDateOfBirth(null != participantRequest.getDateOfBirth() 
					? sdf1.parse(sdf1.format(sdf.parse(participantRequest.getDateOfBirth()))) : null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() 
					&& PMPConstants.REQUIRED_YES.equalsIgnoreCase(participantRequest.getIntroducedStatus())) ? 1 : 0);
			participant.setIntroductionDate(null != participantRequest.getIntroductionDate()
					? sdf1.parse(sdf1.format(sdf.parse(participantRequest.getIntroductionDate()))) : null);
		}
		participantRepository.save(participant);
		participantRequest.setSeqId(participant.getSeqId());
		participantRequest.setPrintName(participant.getPrintName());
		participantRequest.setEmail(participant.getEmail());
		participantRequest.setMobilePhone(participant.getMobilePhone());
		if ((participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE) || participant.getGender()
				.equalsIgnoreCase(PMPConstants.GENDER_MALE))
				&& participant.getGender() != null
				&& !participant.getGender().isEmpty())
			participant.setGender(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE)
					? PMPConstants.MALE	: PMPConstants.FEMALE);
		participantRequest.setGender(participant.getGender());
		participantRequest.setDateOfBirth(null != participant.getDateOfBirth() 
				? sdf.format(participant.getDateOfBirth()) : null);
		participantRequest.setAddressLine1(participant.getAddressLine1());
		participantRequest.setAddressLine2(participant.getAddressLine2());
		participantRequest.setCity(participant.getCity());
		participantRequest.setState(participant.getState());
		participantRequest.setCountry(participant.getCountry());
		participantRequest.setIntroducedStatus(0 != participant.getIntroduced()
				? PMPConstants.REQUIRED_YES : PMPConstants.REQUIRED_NO);
		participantRequest.setIntroductionDate(null != participantRequest.getIntroductionDate()
				? sdf.format(participant.getIntroductionDate()) : null);
		return participantRequest;
	}

	/**
	 * method to get the participant details based on the seqId and event Id
	 */
	@Override
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		Participant participant = participantRepository.findBySeqId(participantRequest);
		participantRequest.setPrintName(participant.getPrintName());
		participantRequest.setEmail(participant.getEmail());
		participantRequest.setMobilePhone(participant.getMobilePhone());
		if ((participant.getGender().equalsIgnoreCase("F") 
				|| participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE))
				&& participant.getGender() != null && !participant.getGender().isEmpty())
			participant.setGender(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) 
					? PMPConstants.MALE	: PMPConstants.FEMALE);
		participantRequest.setGender(participant.getGender());
		participantRequest.setDateOfBirth(null != participant.getDateOfBirth() 
				? sdf.format(participant.getDateOfBirth()) : null);
		participantRequest.setAddressLine1(participant.getAddressLine1());
		participantRequest.setAddressLine2(participant.getAddressLine2());
		participantRequest.setCity(participant.getCity());
		participantRequest.setState(participant.getState());
		participantRequest.setCountry(participant.getCountry());
		participantRequest.setIntroducedStatus(0 != participant.getIntroduced()
				? PMPConstants.REQUIRED_YES	: PMPConstants.REQUIRED_NO);
		participantRequest.setIntroductionDate(null != participantRequest.getIntroductionDate()
				? sdf.format(participant.getIntroductionDate()) : null);
		return participantRequest;
	}

}
