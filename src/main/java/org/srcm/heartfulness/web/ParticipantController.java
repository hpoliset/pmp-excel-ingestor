package org.srcm.heartfulness.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.service.WelcomeMailService;

@Controller
public class ParticipantController {

	@Autowired
	private WelcomeMailService sendyAPIService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantController.class);

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model) {
		try {
			LOGGER.debug("Unsubcribe user called.");
			sendyAPIService.unsubscribeUsers();
			model.addAttribute("message", "unsubcribed successfully.");
			return "eventsuccess";
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while Unsubscribe - {} " + e.getMessage());
			model.addAttribute("message", "Failed to unsubscribe. Please try after sometime.");
			return "eventsuccess";
		}
	}

}
