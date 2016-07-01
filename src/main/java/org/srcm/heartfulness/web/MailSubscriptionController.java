package org.srcm.heartfulness.web;

import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.srcm.heartfulness.constants.EventConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.service.SubscriptionService;

@Controller
public class MailSubscriptionController {

	@Autowired
	AESEncryptDecrypt aesEncryptDecrypt;

	@Autowired
	private SubscriptionService subscriptionService;

	private static final Logger LOGGER = LoggerFactory.getLogger(MailSubscriptionController.class);

	@Autowired
	Environment env;

	/**
	 * Service method to confirm the subscription for the given email ID.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String subscribeToMailAlerts(@RequestParam(required = false, value = "id") String id, Model model) {
		try {
			String mailID = getMailIDfromEncryptedID(id);
			if (null == mailID || mailID.isEmpty() || !mailID.matches(EventConstants.EMAIL_REGEX)) {
				LOGGER.debug("Invalid ID. - id : {}", id);
				model.addAttribute("message", "Invalid ID.");
				return "eventsuccess";
			}
			String response = subscriptionService.updateconfirmSubscribedStatus(mailID);
			model.addAttribute("message", response);
			return "eventsuccess";
		} catch (IllegalBlockSizeException | NumberFormatException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
			LOGGER.debug("Invalid ID. - id : {}", id);
			model.addAttribute("message", "Invalid ID.");
			return "eventsuccess";
		}

	}

	/**
	 * Method to decrypt the id to extract the emailID.
	 * @param id
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws NumberFormatException
	 */
	private String getMailIDfromEncryptedID(String id) throws IllegalBlockSizeException, NumberFormatException {
		return aesEncryptDecrypt.decrypt(id, env.getProperty(PMPConstants.SECURITY_TOKEN_KEY));
	}

	/**
	 * Service method to unsubscribe the given email ID.
	 * 
	 * @param request
	 * @param model
	 * @param mail
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model,
			@RequestParam(required = false, value = "email") String mail,
			@RequestParam(required = false, value = "name") String name) {
		LOGGER.debug("Unsubcribe user called.");
		if (1 == subscriptionService.checkForMailSubcription(mail)) {
			LOGGER.debug("Already unsubscribed - mail : {} , name : {}", mail, name);
			model.addAttribute("message", "You've already unsubscribed.");
			return "eventsuccess";
		} else {
			LOGGER.debug("unsubscription - mail : {} , name : {}", mail, name);
			subscriptionService.unsubscribe(mail, name);
			model.addAttribute("message", "unsubcribed successfully.");
			return "eventsuccess";
		}
	}

	/**
	 * Service method to confirm the subscription for the given email ID.
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/validate", method = RequestMethod.GET)
	public String UpdateConfirmationMailStatus(@RequestParam(required = false, value = "id") String id, Model model) {
		try {
			String mailID = getMailIDfromEncryptedID(id);
			if (null == mailID || mailID.isEmpty() || !mailID.matches(EventConstants.EMAIL_REGEX)) {
				LOGGER.debug("Invalid ID. - id : {}", id);
				model.addAttribute("message", "Invalid ID.");
				return "eventsuccess";
			}
			String response = subscriptionService.updateValidationStatus(mailID);
			model.addAttribute("message", response);
			return "eventsuccess";
		} catch (IllegalBlockSizeException | NumberFormatException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
			LOGGER.debug("Invalid ID. - id : {}", id);
			model.addAttribute("message", "Invalid ID.");
			return "eventsuccess";
		}

	}
}
