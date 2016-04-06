package org.srcm.heartfulness.repository;

import java.util.List;

import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.WelcomeMailDetails;


public interface SendyMailRepository {
   
    List<Participant> getParticipantsToSendWelcomeMail();

	int getIntroducedParticipantCount(String printName, String email);

	void save(WelcomeMailDetails welcomeMailDetails);

	List<WelcomeMailDetails> getSubscribersToUnsubscribe();

	void updateParticipant();

}
