/**
 * 
 */
package org.srcm.heartfulness.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.srcm.heartfulness.helper.BounceEmailHelper;
import org.srcm.heartfulness.repository.BouncedEmailRepository;

/**
 * 
 * The BouncedEmailServiceImpl encapsulates all business behaviors operating on
 * the on fetching and handling bounced emails from hfnbounce@srcm.org.
 *
 *
 * @author Koustav Dutta
 *
 */
@Service
public class BouncedEmailServiceImpl implements BouncedEmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BouncedEmailServiceImpl.class);

	@Autowired
	Environment envProperties;

	@Autowired
	BouncedEmailRepository bncdEmailRepo;

	@Autowired
	BounceEmailHelper bounceEmailHlpr;

	/**
	 * This method fetches all the newly available bounced emails from
	 * hfnbounce@srcm.org and calls the dao classes to update those emails as
	 * bounced in the participant table.
	 * 
	 */
	@Override
	@Transactional
	public void readBouncedEmailsAndUpdateInDatabase() {
		LOGGER.debug("START: Fetching emails from hfnbounce@srcm.org mailbox");
		try {
			Session session = Session.getDefaultInstance(new Properties());
			Store store = session.getStore("imaps");

			store.connect(envProperties.getProperty("srcm.bounced.email.host"),
					Integer.valueOf(envProperties.getProperty("srcm.bounced.email.port")),
					envProperties.getProperty("srcm.bounced.email.from.address"),
					envProperties.getProperty("srcm.bounced.email.from.password"));
			Folder inbox = store.getFolder(envProperties.getProperty("srcm.bounced.email.mailbox"));
			inbox.open(Folder.READ_ONLY);
			LOGGER.debug("Total number of mails in inbox: " + inbox.getMessageCount());
			LOGGER.debug("Total number of unread mails in inbox: " + inbox.getUnreadMessageCount());
			int readMessagesCount = inbox.getMessageCount() - inbox.getUnreadMessageCount();
			LOGGER.debug("Total number of read mails in inbox: " + readMessagesCount);
			LOGGER.debug("Total number of new mails in inbox: " + inbox.getNewMessageCount());

			// Fetch unread mails from inbox folder
			Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

			for (Message message : messages) {
				LOGGER.debug("-----------------------START-----------------------");
				LOGGER.debug("Mail Number: " + message.getMessageNumber());
				LOGGER.debug("Message Content Type: " + message.getContentType());
				try {
					// read the message content
					String bouncedEmail = "";
					if (null != message.getHeader("Return-Path")) {
						LOGGER.debug("Mail Header: " + message.getHeader("Return-Path")[0]);
						String headerValue = message.getHeader("Return-Path")[0];
						if (headerValue.isEmpty() || "<>".equalsIgnoreCase(headerValue)) {
							bouncedEmail = bounceEmailHlpr.getBouncedEmail(message);
						}
					} else {
						LOGGER.debug("Mail Header: null");
						String rejectedEmailIds = envProperties.getProperty("srcm.bounced.email.rejected.emails");
						String[] emails = rejectedEmailIds.split(",");
						List<String> rejectedMailList = new ArrayList<String>();
						for (String email : emails) {
							rejectedMailList.add(email.trim());
						}
						if (!rejectedMailList.contains(message.getFrom()[0].toString().trim())) {
							LOGGER.debug(message.getFrom()[0].toString().trim()
									+ "doesnot belong to rejected email list");
							bouncedEmail = bounceEmailHlpr.getBouncedEmail(message);
						}
					}

					// Check if the bounced email is empty or not
					if (bouncedEmail.isEmpty()) {
						// mark message as read
						// Flags flags = new Flags(Flag.SEEN);
						// message.setFlags(flags, true);
						LOGGER.debug("Unable to find bounced email from mail content for mail number "
								+ message.getMessageNumber());
						LOGGER.debug("Mail-->" + message.getFrom()[0].toString() + "<-- with mail number "
								+ message.getMessageNumber() + " is marked as read ");
					} else {
						// mark message as read
						// Flags flags = new Flags(Flag.SEEN);
						// message.setFlags(flags, true);
						LOGGER.debug("Mail-->" + bouncedEmail + "<-- found from mail content for mail number "
								+ message.getMessageNumber());
						LOGGER.debug("Mail-->" + message.getFrom()[0].toString() + "<-- with mail number "
								+ message.getMessageNumber() + " is marked as read ");

						// call dao layer to update is_bounced as 1.
						int isEmailBounced = bncdEmailRepo.updateEmailAsBounced(bouncedEmail);
						// update status and set unsubscribed flag to 1
						int updateStatus = bncdEmailRepo.updateEmailStatusAsBounced(bouncedEmail);
						if (updateStatus != 1) {
							bncdEmailRepo.createEmailAsBounced(bouncedEmail);
						}
					}
					// LOGGER.debug("-----------------------FINISH-----------------------");
				} catch (Exception ex) {
					// if any exceptions occurs mark the message as unread
					// Flags flags = new Flags(Flag.SEEN);
					// message.setFlags(flags, false);
					LOGGER.debug("Mail-->" + message.getFrom()[0] + "<-- with mail number "
							+ message.getMessageNumber() + " is marked as unread ");
				}
			}

		} catch (MessagingException ex) {
			LOGGER.debug("EXCEPTION: Error while fetching mails from "
					+ envProperties.getProperty("srcm.bounced.email.from.address"));
			LOGGER.debug("EXCEPTION: " + ex.getMessage());
		}
		LOGGER.debug("END: Completed processing emails from hfnbounce@srcm.org mailbox");
	}

}
