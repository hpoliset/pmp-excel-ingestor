package org.srcm.heartfulness.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.enumeration.ParticipantSearchField;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.json.request.ParticipantRequest;
import org.srcm.heartfulness.model.json.request.SearchRequest;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.repository.ProgramRepository;

/**
 * This class is service Implementation for the participant related actions.
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

	@Autowired
	ProgramService programService;

	/**
	 * Service to create new participant or to update the existing participant
	 * of the event.
	 * 
	 * @param participant
	 *            has the participant related details to create or update the
	 *            participant.
	 * @return participant details
	 * @throws ParseException
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
			if ((participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE) || participantRequest.getGender()
					.equalsIgnoreCase(PMPConstants.FEMALE))
					&& participantRequest.getGender() != null
					&& !participantRequest.getGender().isEmpty())
				participantRequest
				.setGender((participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE)
						&& participantRequest.getGender() != null && !participantRequest.getGender().isEmpty()) ? PMPConstants.GENDER_MALE
								: PMPConstants.GENDER_FEMALE);
			participant.setGender(participantRequest.getGender());
			participant.setDateOfBirth((null != participantRequest.getDateOfBirth() && !participantRequest.getDateOfBirth().isEmpty())? sdf1.parse(sdf1.format(sdf
					.parse(participantRequest.getDateOfBirth()))) : null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setAbhyasiId(participantRequest.getAbhyasiId());
			participant.setIntroducedBy(participantRequest.getIntroducedBy());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getIntroducedStatus())) ? 1 : 0);
			participant.setIntroductionDate((null != participantRequest.getIntroductionDate()  && !participantRequest.getIntroductionDate().isEmpty())? sdf1.parse(sdf1
					.format(sdf.parse(participantRequest.getIntroductionDate()))) : null);

			if (null == participantRequest.getFirstSittingDate()) {
				participant.setFirstSittingDate(null);
			} else {
				try {
					participant.setFirstSittingDate( sdf1.parse(sdf1
							.format(sdf.parse(participantRequest.getFirstSittingDate()))));
				} catch (Exception e) {
					participant.setFirstSittingDate(null);
				}
			}
			if (null == participantRequest.getSecondSittingDate()) {
				participant.setSecondSittingDate(null);
			} else {
				try {
					participant.setSecondSittingDate( sdf1.parse(sdf1
							.format(sdf.parse(participantRequest.getSecondSittingDate()))));
				} catch (Exception e) {
					participant.setSecondSittingDate(null);
				}
			}
			if (null == participantRequest.getThirdSittingDate()) {
				participant.setThirdSittingDate(null);
			} else {
				try {
					participant.setThirdSittingDate( sdf1.parse(sdf1
							.format(sdf.parse(participantRequest.getThirdSittingDate()))));
				} catch (Exception e) {
					participant.setThirdSittingDate(null);
				}
			}
			participant.setFirstSitting((null!=participantRequest.getFirstSitting() && "Y".equalsIgnoreCase(participantRequest.getFirstSitting())) ?1:0);
			participant.setSecondSitting((null!=participantRequest.getSecondSitting() && "Y".equalsIgnoreCase(participantRequest.getSecondSitting())) ?1:0);
			participant.setThirdSitting((null!=participantRequest.getThirdSitting() && "Y".equalsIgnoreCase(participantRequest.getThirdSitting())) ?1:0);
		} else {
			participant = findBySeqId(participantRequest);
			participant.setPrintName(participantRequest.getPrintName());
			participant.setEmail(participantRequest.getEmail());
			participant.setMobilePhone(participantRequest.getMobilePhone());
			if ((null !=participantRequest.getGender()&& !participantRequest.getGender().isEmpty() && (participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE) || participantRequest.getGender()
					.equalsIgnoreCase(PMPConstants.FEMALE))))
				participantRequest.setGender(participantRequest.getGender().equalsIgnoreCase(PMPConstants.MALE)
						? PMPConstants.GENDER_MALE : PMPConstants.GENDER_FEMALE);
			participant.setGender(participantRequest.getGender());
			participant.setDateOfBirth((null != participantRequest.getDateOfBirth() && !participantRequest.getDateOfBirth().isEmpty()) ? sdf1.parse(sdf1.format(sdf
					.parse(participantRequest.getDateOfBirth()))) : null);
			participant.setAddressLine1(participantRequest.getAddressLine1());
			participant.setAddressLine2(participantRequest.getAddressLine2());
			participant.setCity(participantRequest.getCity());
			participant.setState(participantRequest.getState());
			participant.setCountry(participantRequest.getCountry());
			participant.setAbhyasiId(participantRequest.getAbhyasiId());
			participant.setIntroducedBy(participantRequest.getIntroducedBy());
			participant.setIntroduced((null != participantRequest.getIntroducedStatus() && PMPConstants.REQUIRED_YES
					.equalsIgnoreCase(participantRequest.getIntroducedStatus())) ? 1 : 0);
			participant.setIntroductionDate((null != participantRequest.getIntroductionDate() && !participantRequest.getIntroductionDate().isEmpty())? sdf1.parse(sdf1
					.format(sdf.parse(participantRequest.getIntroductionDate()))) : null);
			if (null == participantRequest.getFirstSittingDate()) {
				participant.setFirstSittingDate(null);
			} else {
				try {
					participant.setFirstSittingDate( sdf1.parse(sdf1
							.format(sdf.parse(participantRequest.getFirstSittingDate()))));
				} catch (Exception e) {
					participant.setFirstSittingDate(null);
				}
			}
			if (null == participantRequest.getSecondSittingDate()) {
				participant.setSecondSittingDate(null);
			} else {
				try {
					participant.setSecondSittingDate( sdf1.parse(sdf1
							.format(sdf.parse(participantRequest.getSecondSittingDate()))));
				} catch (Exception e) {
					participant.setSecondSittingDate(null);
				}
			}
			if (null == participantRequest.getThirdSittingDate()) {
				participant.setThirdSittingDate(null);
			} else {
				try {
					participant.setThirdSittingDate( sdf1.parse(sdf1
							.format(sdf.parse(participantRequest.getThirdSittingDate()))));
				} catch (Exception e) {
					participant.setThirdSittingDate(null);
				}
			}
			participant.setFirstSitting((null!=participantRequest.getFirstSitting() && "Y".equalsIgnoreCase(participantRequest.getFirstSitting())) ?1:0);
			participant.setSecondSitting((null!=participantRequest.getSecondSitting() && "Y".equalsIgnoreCase(participantRequest.getSecondSitting())) ?1:0);
			participant.setThirdSitting((null!=participantRequest.getThirdSitting() && "Y".equalsIgnoreCase(participantRequest.getThirdSitting())) ?1:0);
		}
		participantRepository.save(participant);
		participantRequest.setSeqId(participant.getSeqId());
		participantRequest.setPrintName(participant.getPrintName());
		participantRequest.setEmail(participant.getEmail());
		participantRequest.setMobilePhone(participant.getMobilePhone());
		if (participant.getGender() != null && !participant.getGender().isEmpty()) {
			if ((participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE) || participant.getGender()
					.equalsIgnoreCase(PMPConstants.GENDER_MALE)))
				participant
				.setGender(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) ? PMPConstants.MALE
						: PMPConstants.FEMALE);
			participantRequest.setGender(participant.getGender());
		}

		participantRequest.setDateOfBirth((null != participant.getDateOfBirth()&& !participantRequest.getDateOfBirth().isEmpty()) ? sdf.format(participant
				.getDateOfBirth()) : null);
		participantRequest.setAddressLine1(participant.getAddressLine1());
		participantRequest.setAddressLine2(participant.getAddressLine2());
		participantRequest.setCity(participant.getCity());
		participantRequest.setState(participant.getState());
		participantRequest.setCountry(participant.getCountry());
		participantRequest.setIntroducedStatus(0 != participant.getIntroduced() ? PMPConstants.REQUIRED_YES
				: PMPConstants.REQUIRED_NO);
		participantRequest.setIntroductionDate((null != participantRequest.getIntroductionDate() && !participantRequest.getIntroductionDate().isEmpty()) ? sdf
				.format(participant.getIntroductionDate()) : null);
		participantRequest.setAbhyasiId(participant.getAbhyasiId());
		participantRequest.setIntroducedBy(participant.getIntroducedBy());
		if (null == participant.getFirstSittingDate()) {
			participantRequest.setFirstSittingDate("");
		} else {
			try {
				participantRequest.setFirstSittingDate(sdf.format(participant.getFirstSittingDate()));
			} catch (Exception e) {
				participantRequest.setFirstSittingDate("");
			}
		}
		if (null == participant.getSecondSittingDate()) {
			participantRequest.setSecondSittingDate("");
		} else {
			try {
				participantRequest.setSecondSittingDate(sdf.format(participant.getSecondSittingDate()));
			} catch (Exception e) {
				participantRequest.setSecondSittingDate("");
			}
		}
		if (null == participant.getThirdSittingDate()) {
			participantRequest.setThirdSittingDate("");
		} else {
			try {
				participantRequest.setThirdSittingDate(sdf.format(participant.getThirdSittingDate()));
			} catch (Exception e) {
				participantRequest.setThirdSittingDate("");
			}
		}
		participantRequest.setFirstSitting((null!=participant.getFirstSitting() && 1 == participant.getFirstSitting())? PMPConstants.REQUIRED_YES
				: PMPConstants.REQUIRED_NO);
		participantRequest.setSecondSitting((null!=participant.getSecondSitting() && 1 == participant.getSecondSitting()) ? PMPConstants.REQUIRED_YES
				: PMPConstants.REQUIRED_NO);
		participantRequest.setThirdSitting((null!=participant.getThirdSitting() && 1 == participant.getThirdSitting()) ? PMPConstants.REQUIRED_YES
				: PMPConstants.REQUIRED_NO);

		return participantRequest;
	}

	/**
	 * Service to get the participant details depending on the seqId and event
	 * Id.
	 * 
	 * @param participantRequest
	 *            contains seqId and event Id
	 * @return participant details
	 */
	@Override
	public ParticipantRequest getParticipantBySeqId(ParticipantRequest participantRequest) {
		SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		Participant participant = findBySeqId(participantRequest);
		if (null != participant) {
			participantRequest.setPrintName(participant.getPrintName());
			participantRequest.setEmail(participant.getEmail());
			participantRequest.setMobilePhone(participant.getMobilePhone());
			if (participant.getGender() != null && !participant.getGender().isEmpty()) {
				if ((participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_FEMALE) || participant.getGender()
						.equalsIgnoreCase(PMPConstants.GENDER_MALE)))
					participant
					.setGender(participant.getGender().equalsIgnoreCase(PMPConstants.GENDER_MALE) ? PMPConstants.MALE
							: PMPConstants.FEMALE);
			}

			participantRequest.setGender(participant.getGender());
			participantRequest.setDateOfBirth((null != participant.getDateOfBirth()) ? convertedsdf.format(participant
					.getDateOfBirth()) : null);
			participantRequest.setAddressLine1(participant.getAddressLine1());
			participantRequest.setAddressLine2(participant.getAddressLine2());
			participantRequest.setCity(participant.getCity());
			participantRequest.setState(participant.getState());
			participantRequest.setCountry(participant.getCountry());
			participantRequest.setIntroducedStatus(0 != participant.getIntroduced() ? PMPConstants.REQUIRED_YES
					: PMPConstants.REQUIRED_NO);
			participantRequest.setIntroductionDate((null != participant.getIntroductionDate())  ?
					convertedsdf.format(participant.getIntroductionDate()) : null);
			participantRequest.setAbhyasiId(participant.getAbhyasiId());
			participantRequest.setIntroducedBy(participant.getIntroducedBy());
			if (null == participant.getFirstSittingDate()) {
				participantRequest.setFirstSittingDate("");
			} else {
				try {
					participantRequest.setFirstSittingDate(convertedsdf.format(participant.getFirstSittingDate()));
				} catch (Exception e) {
					participantRequest.setFirstSittingDate("");
				}
			}
			if (null == participant.getSecondSittingDate()) {
				participantRequest.setSecondSittingDate("");
			} else {
				try {
					participantRequest.setSecondSittingDate(convertedsdf.format(participant.getSecondSittingDate()));
				} catch (Exception e) {
					participantRequest.setSecondSittingDate("");
				}
			}
			if (null == participant.getThirdSittingDate()) {
				participantRequest.setThirdSittingDate("");
			} else {
				try {
					participantRequest.setThirdSittingDate(convertedsdf.format(participant.getThirdSittingDate()));
				} catch (Exception e) {
					participantRequest.setThirdSittingDate("");
				}
			}
			participantRequest.setFirstSitting((null!=participant.getFirstSitting() && 1 == participant.getFirstSitting())? PMPConstants.REQUIRED_YES
					: PMPConstants.REQUIRED_NO);
			participantRequest.setSecondSitting((null!=participant.getSecondSitting() && 1 == participant.getSecondSitting()) ? PMPConstants.REQUIRED_YES
					: PMPConstants.REQUIRED_NO);
			participantRequest.setThirdSitting((null!=participant.getThirdSitting() && 1 == participant.getThirdSitting()) ? PMPConstants.REQUIRED_YES
					: PMPConstants.REQUIRED_NO);
		} else {
			participantRequest = new ParticipantRequest();
		}
		return participantRequest;
	}

	/**
	 * Service to get the participant details depending on the values given in
	 * the search request.
	 * 
	 * @param searchRequest
	 * @return
	 */
	@Override
	public List<ParticipantRequest> searchParticipants(SearchRequest searchRequest) {
		List<Participant> participantList = new ArrayList<Participant>();
		List<ParticipantRequest> participantReqList = new ArrayList<ParticipantRequest>();
		SimpleDateFormat convertedsdf = new SimpleDateFormat(PMPConstants.DATE_FORMAT);
		for (ParticipantSearchField searchField : ParticipantSearchField.values()) {
			if (searchField.name().equals(searchRequest.getSearchField())) {
				searchRequest.setSearchField(searchField.getValue());
			}
			if (searchField.name().equals(searchRequest.getSortBy())) {
				searchRequest.setSortBy(searchField.getValue());
			}
		}
		participantList = participantRepository.getParticipantList(searchRequest);
		for (Participant participant : participantList) {
			ParticipantRequest participantReq = new ParticipantRequest();
			participantReq.setSeqId(participant.getSeqId());
			participantReq.setEventId(programService.getEventIdByProgramID(participant.getProgramId()));
			participantReq.setPrintName(participant.getPrintName());
			participantReq.setEmail(participant.getEmail());
			participantReq.setMobilePhone(participant.getMobilePhone());

			if (null == participant.getGender()) {
				participantReq.setGender("");
			} else if (PMPConstants.GENDER_MALE.equals(participant.getGender())) {
				participantReq.setGender(PMPConstants.MALE);
			} else if (PMPConstants.GENDER_FEMALE.equals(participant.getGender())) {
				participantReq.setGender(PMPConstants.FEMALE);
			} else {
				participantReq.setGender("");
			}

			if (null == participant.getDateOfBirth()) {
				participantReq.setDateOfBirth("");
			} else {
				try {
					participantReq.setDateOfBirth(convertedsdf.format(participant.getDateOfBirth()));
				} catch (Exception e) {
					participantReq.setDateOfBirth("");
				}
			}

			participantReq.setAddressLine1(participant.getAddressLine1());
			participantReq.setAddressLine2(participant.getAddressLine2());
			participantReq.setCity(participant.getCity());
			participantReq.setState(participant.getState());
			participantReq.setCountry(participant.getCountry());
			participantReq.setAbhyasiId(participant.getAbhyasiId());
			participantReq.setIntroducedBy(participant.getIntroducedBy());

			if (1 == participant.getIntroduced()) {
				participantReq.setIntroducedStatus(PMPConstants.REQUIRED_YES);
			} else if (0 == participant.getIntroduced()) {
				participantReq.setIntroducedStatus(PMPConstants.REQUIRED_NO);
			} else {
				participantReq.setIntroducedStatus("");
			}

			if (null == participant.getIntroductionDate()) {
				participantReq.setIntroductionDate("");
			} else {
				try {
					participantReq.setIntroductionDate(convertedsdf.format(participant.getIntroductionDate()));
				} catch (Exception e) {
					participantReq.setIntroductionDate("");
				}
			}

			participantReqList.add(participantReq);
		}
		return participantReqList;
	}

	/**
	 * Retrieve <code>Participant</code> from the data store by SeqID.
	 * 
	 * @param participantRequest
	 * @return <code>Participant</code>
	 */
	@Override
	public Participant findBySeqId(ParticipantRequest participantRequest) {
		if (0 != programrepository.getProgramIdByEventId(participantRequest.getEventId())) {
			participantRequest.setProgramId(programrepository.getProgramIdByEventId(participantRequest.getEventId()));
			Participant newParticipant = programrepository.findParticipantBySeqId(participantRequest.getSeqId(),
					participantRequest.getProgramId());
			return newParticipant;
		} else {
			return new Participant();
		}
	}

}
