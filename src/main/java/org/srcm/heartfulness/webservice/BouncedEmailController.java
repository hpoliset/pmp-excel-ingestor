/**
 * 
 */
package org.srcm.heartfulness.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.service.BouncedEmailService;

/**
 * The BouncedEmailController class is a RESTful web service controller 
 * used to handle bounced emails.
 *
 */

/**
 * @author Koustav Dutta
 *
 */

@RestController
public class BouncedEmailController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BouncedEmailController.class);
	
	@Autowired
	private BouncedEmailService bncdEmailSrcv;	
	
	/**
	 * This controller method is used to handle all the bounced emails
	 * from hfnbounce@srcm.org mailbox.
	 * 
	 */
	//@RequestMapping("/api/bouncedemail")
	//@Scheduled(cron = "${srcm.bounced.email.fetching.cron.time}")
	public void handleBouncedEmails(){
		LOGGER.debug("START: Handling bounced emails");
		bncdEmailSrcv.fetchBouncedEmails();
		LOGGER.debug("END: Handling bounced emails");
	}

}
