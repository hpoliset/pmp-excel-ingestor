package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.service.WelcomeMailService;

@Controller
public class MailController {

	@Autowired
	private WelcomeMailService sendyAPIService;

	@Autowired
	private ParticipantRepository participantRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(MailController.class);

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model,
			@RequestParam(required = false, value = "email") String mail,
			@RequestParam(required = false, value = "name") String name) {
		LOGGER.debug("Unsubcribe user called.");
		if (1 == participantRepository.checkForMailSubcription(mail)) {
			LOGGER.debug("Already unsubscribed - mail : {} , name : {}", mail, name);
			model.addAttribute("message", "You've already unsubscribed.");
			return "eventsuccess";
		} else {
			LOGGER.debug("unsubscription - mail : {} , name : {}", mail, name);
			sendyAPIService.unsubscribe(mail, name);
			model.addAttribute("message", "unsubcribed successfully.");
			return "eventsuccess";
		}
	}
	
/*	@ResponseBody
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	private String sendAutomaticConfirmationMailToParticipants() {
		Participant participant = new Participant();
		participant.setPrintName("Hima");
		participant.setEmail("himasree.vemuru@htcindia.com");
		//participant.setSeqId("1234");
		if (null != participant.getEmail() && !participant.getEmail().isEmpty()) {
			LOGGER.debug("Mail subscription : {} ",
					participantRepository.checkForMailSubcription(participant.getEmail()) + "");
			LOGGER.debug("confirmation Mail sent  : {} ",
					participantRepository.CheckForConfirmationMailStatus(participant) + "");
			//Checks whether the participant unsubscribed for receiving mails.
			if (1 != participantRepository.checkForMailSubcription(participant.getEmail())) {
				// Checks whether the participant already received the confirmation mail or not.
				if (1 != participantRepository.CheckForConfirmationMailStatus(participant)) {
					//sendMail.SendConfirmationMailToParticipant(participant);
					participantRepository.updateConfirmationMailStatus(participant);
					return "success";
				}
			}
		}
		return "completed";
	}*/
}
