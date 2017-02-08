package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.ParseException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.WelcomeMailService;

/**
 * 
 * @author rramesh
 *
 */
@RestController
@RequestMapping("/api/welcomemail/")
public class WelcomeMailController {

	@Autowired
	private WelcomeMailService WelcomeMailService;

	@Autowired
	private SendMail sendEmailNotification;

	private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeMailController.class);

	/* @Scheduled(cron = "${welcome.mail.subscribe.cron.time}") */
	public void subscribeUser() {
		try {
			LOGGER.info("Scheduler started at - " + new Date());
			WelcomeMailService.addNewSubscriber();
		} catch (HttpClientErrorException | IOException | MessagingException e) {
			LOGGER.error("Exception while Subscribe - {} " + e.getMessage());
		}
	}

	/* @Scheduled(cron = "${welcome.mail.unsubscribe.cron.time}") */
	public void unsubscribeUser() {
		try {
			LOGGER.info("Unsubcribe user called.");
			WelcomeMailService.unsubscribeUsers();
		} catch (HttpClientErrorException | IOException e) {
			LOGGER.error("Exception while Unsubscribe - {} " + e.getMessage());
		}
	}

	/* @Scheduled(cron = "${welcome.mailids.file.upload.cron.time}") */
	public void uploadDailyWelcomeMailidsToFTP() {
		try {
			LOGGER.info("Upload File to FTP called.");
			WelcomeMailService.uploadParticipantEmailidsToFTP();
			LOGGER.info("Process Completed.");
		} catch (Exception e) {
			LOGGER.error("Exception while uploading file - {} " + e.getMessage());
		}
	}

	/**
	 * Controller is used to send email to the coordinators with event details
	 * about the participants who have received welcome emails.It is a crob job
	 * running at a scheduled time.
	 */
	/* @Scheduled(cron = "${welcome.mailids.coordinator.inform.cron.time}") */
	public void sendEmailToCoordinator() {
		LOGGER.info("START		:Cron job started to fetch participants to whom welcome mail already sent");
		WelcomeMailService.getCoordinatorListAndSendMail();
		LOGGER.info("END		:Cron job completed to fetch participants to whom welcome mail already sent");
	}

	@RequestMapping(value = "/coordinator/welcomemail", method = RequestMethod.GET)
	public void sendEmailToCoordinatorWithWelcomeMailDetails() {
		LOGGER.info("START		:Cron job started to fetch participants to whom welcome mail already sent");
		WelcomeMailService.getCoordinatorListAndSendMail();
		LOGGER.info("END		:Cron job completed to fetch participants to whom welcome mail already sent");
	}

	/* @Scheduled(cron = "${ewelcomeid.generate.coordinator.inform.cron.time}") */
	public void sendGeneratedEwelcomeIdToCoordinators() {
		LOGGER.info("START		:Cron job started to send mails to coordinator to inform participant ewelcomeid's");
		WelcomeMailService.getGeneratedEwelcomeIdAndSendToCoordinators();
		LOGGER.info("END		:Cron job completed to send mails to coordinator to inform participant ewelcomeid's");
	}

	@RequestMapping(value = "/coordinator/ewelcomeidmail", method = RequestMethod.GET)
	public void sendGeneratedEwelcomeIdToCoordinatorswithEwelcomeId() {
		LOGGER.info("START		:Cron job started to send mails to coordinator to inform participant ewelcomeid's");
		WelcomeMailService.getGeneratedEwelcomeIdAndSendToCoordinators();
		LOGGER.info("END		:Cron job completed to send mails to coordinator to inform participant ewelcomeid's");
	}

	/* @Scheduled(cron = "${welcome.mail.to.hfnlist.cron.time}") */
	public void sendWelcomeMail() {
		try {
			LOGGER.info("Sending mail to hfn list called.");
			WelcomeMailService.sendWelcomeMailToHfnList();
		} catch (Exception e) {
			LOGGER.error("Exception while sending mail - {} " + e.getMessage());
		}
	}

	@RequestMapping(value = "/testException", method = RequestMethod.POST)
	public String mailException(@RequestParam(value = "mailID", required = false) String mailID,
			@RequestParam(value = "ccMailID", required = false) String ccMailID, HttpServletRequest request) {
		try {
			LOGGER.info("Sending mail to test Exception ...!.");
			List<String> mailIds = new ArrayList<String>();
			mailIds.add(mailID);
			List<String> ccMailIds = new ArrayList<String>();
			ccMailIds.add(ccMailID);
			sendEmailNotification.sendMail(mailIds, ccMailIds, "This is a Test Mail from PMP");
		} catch (AddressException e) {
			return "Failed-Address Exception...!  " + e;
		} catch (ParseException e) {
			return  "Failed-Encode Exception...!  " + e;
		} catch (AuthenticationFailedException e) {
			return "Failed-Auth Failed Exception...!  " + e;
		} catch (MessagingException e) {
			return  "Failed-Messaging Exception...!" + e;
		} catch (UnsupportedEncodingException e) {
			return  "Failed-Encode Exception...!  " + e;
		} catch (Exception e) {
			return  "Failed-Encode Exception...!  " + e;
		}
		return  "Success-Mail sent successfully...!";
	}

}