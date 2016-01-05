package org.srcm.heartfulness.service;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.repository.ParticipantFullDetailsRepository;
import org.srcm.heartfulness.vo.ReportVO;

/**
 * Created by vsonnathi on 11/19/15.
 */
@Service
public class ReportServiceImpl implements ReportService {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

	@Autowired
	private ParticipantFullDetailsRepository participantFullDetailsRepository;

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ReportService#getParticipants(org.srcm.heartfulness.vo.ReportVO)
	 */
	@Override
	@Transactional (readOnly= true)
	public Collection<ParticipantFullDetails> getParticipants(ReportVO reportVO) {

		return participantFullDetailsRepository.getParticipants(reportVO);

	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ReportingService#getCountries()
	 */
	@Override
	public List<String> getCountries() {
		return participantFullDetailsRepository.getCountries();
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ReportingService#getStatesForCountry(java.lang.String)
	 */
	@Override
	public List<String> getStatesForCountry(String country) {
		return participantFullDetailsRepository.getStatesForCountry(country);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.service.ReportingService#getEventTypes()
	 */
	@Override
	public List<String> getEventTypes() {
		return participantFullDetailsRepository.getEventTypes();
	}
}
