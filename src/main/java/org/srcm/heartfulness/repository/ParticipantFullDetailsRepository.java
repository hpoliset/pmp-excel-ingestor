package org.srcm.heartfulness.repository;

import java.util.Collection;
import java.util.List;

import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.vo.ReportVO;

/**
 * Created by vsonnathi on 11/23/15.
 */
public interface ParticipantFullDetailsRepository {
 
 
	/**
	 * Fetches the list of participant based on the filter conditions
	 * 
	 * @param reportVO - Filter conditions
	 * @return collection of participant's
	 */
	Collection<ParticipantFullDetails> getParticipants(ReportVO reportVO);

	/**
	 * Fetches the list of country's to be used in the Report parameter screen.
	 * 
	 * @return the list of country
	 */
	List<String> getCountries();

	/**
	 * Fetches the list of states for the given country, to be used in the
	 * Report parameter screen.
	 * 
	 * @param country - Event country
	 * @return the list of state 
	 */
	List<String> getStatesForCountry(String country);

	/**
	 * Fetches the list of event types available.
	 * 
	 * @return the list of event types
	 */
	List<String> getEventTypes();

	 
}
