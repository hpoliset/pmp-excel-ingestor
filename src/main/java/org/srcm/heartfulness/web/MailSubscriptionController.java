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
			if (1 == subscriptionService.checkMailSubscribedStatus(mailID)) {
				if (1 == subscriptionService.checkForconfirmStatusOfSubscription(mailID)) {
					LOGGER.debug("Already you have confirmed your mail ID. - mail : {}", mailID);
					model.addAttribute("message", "Already you have confirmed your mail ID.");
					return "eventsuccess";
				} else {
					subscriptionService.updateconfirmSubscribedStatus(mailID);
					LOGGER.debug("You have successfully confirmed your mail ID. - mail : {}", mailID);
					model.addAttribute("message", "Thank you for confirming your email address.");
					return "eventsuccess";
				}
			}else{
				LOGGER.debug("Please subscribe your emailID in Heartfulness Website to confirm your mailID. Invalid mailID. - mailID : {}", mailID);
				model.addAttribute("message", "Please subscribe your emailID in Heartfulness Website to confirm your mailID.");
				return "eventsuccess";
			}
		} catch (IllegalBlockSizeException | NumberFormatException e) {
			LOGGER.debug("Exception while decrypting {} ", e.getMessage());
			LOGGER.debug("Invalid ID. - id : {}", id);
			model.addAttribute("message", "Invalid ID.");
			return "eventsuccess";
		}

	}

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

	/*
	 * public static void main(String[] args) { AESEncryptDecrypt aes = new
	 * AESEncryptDecrypt(); String mail = "himasree@htcindia.com"; String id =
	 * aes.encrypt(mail, "h2ItE7t6kp+I/R8kJBteRw=="); System.out.println(id); }
	 */

}
