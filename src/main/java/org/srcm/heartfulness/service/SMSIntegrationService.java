package org.srcm.heartfulness.service;

import org.srcm.heartfulness.model.SMS;

public interface SMSIntegrationService {
	
	/**
	 * To create an event and persist.
	 * 
	 * @param sms - SMS details
	 * @return the response
	 */
	public String createEvent(SMS sms);
	
	/**
	 * To update the existing event.
	 * 
	 * @param sms - SMS details
	 * @return the response
	 */
	public String updateEvent(SMS sms);
	
	/**
	 * To create the participants.
	 * 
	 * @param sms - SMS details
	 * @return the response
	 */
	public String createParticipant(SMS sms);
	
	/**
	 * To get the registered participants count.
	 * 
	 * @param sms - SMS details
	 * @return the response
	 */
	public String getCountOfRegisteredParticipants(SMS sms);
	
	/**
	 * To get the introduced participants count.
	 * 
	 * @param sms - SMS details
	 * @return the response
	 */
	public String getCountOfIntroducedParticipants(SMS sms);

}
