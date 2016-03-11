package org.srcm.heartfulness.repository;

public interface SMSIntegrationRepository {
	
	/**
	 * Fetches the count of registered participants via SMS
	 * 
	 * @param eventId - event ID
	 * @return registered participants count
	 */
	int getRegisteredParticipantsCount(String eventId);
	
	/**
	 * Fetches the count of introduced participants via SMS
	 * 
	 * @param introId - Introduction ID
	 * @return introduced participants count
	 */
	int getIntroducedParticipantsCount(String introId);
	

}
