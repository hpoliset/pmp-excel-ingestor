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
import org.srcm.heartfulness.repository.ParticipantRepository;
import org.srcm.heartfulness.service.WelcomeMailService;

@Controller
public class ParticipantController {

	@Autowired
	private WelcomeMailService sendyAPIService;
	
	@Autowired
	private ParticipantRepository participantRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantController.class);

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model,
			@RequestParam(required = false, value = "email") String mail,
			@RequestParam(required = false, value = "name") String name) {
		LOGGER.debug("unsubscription - mail : {} , name : {}",mail , name);
		if(1 == participantRepository.checkForMailSubcription(mail)){
			model.addAttribute("message", "You've already unsubscribed.");
			return "eventsuccess";
		}else{
		LOGGER.debug("Unsubcribe user called.");
		sendyAPIService.unsubscribe(mail, name);
		model.addAttribute("message", "unsubcribed successfully.");
		return "eventsuccess";
		}
	}
}
