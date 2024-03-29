/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.mail.SendMail;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.repository.WelcomeMailRepository;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * @author Koustav Dutta
 *
 */

@Component
public class SendWelcomeMailToParticipant {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendWelcomeMailToParticipant.class);

	@Autowired
	private WelcomeMailRepository welcomeMailRepository;

	@Autowired
	SendMail sendMail;

	@Autowired
	private MailLogRepository mailLogRepository;

	//@PostConstruct
	/*public void startDaemonThread(){
		LOGGER.info("Daemon-Thread started for sending welcome mail to the participants at"+new Date());
		ThreadFactory threadFactory = new DaemonThreadFactory();
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(threadFactory);

		Runnable periodicTask = new Runnable() {
			public void run() {
				try{
					sendWelcomeMailToParticipant();
				} catch(Exception ex){
					LOGGER.error("Exception while sending welcome mails to participant {}",ex);
				}
			}
		};
		executor.scheduleAtFixedRate(() -> sendWelcomeMailToParticipant(),
        2, 2, TimeUnit.MINUTES);
		executor.scheduleAtFixedRate(periodicTask, 5, 90, TimeUnit.MINUTES);
	}*/

	/**
	 * This method is used to send the welcome mails to the participant.
	 * The daemon process calls for this method every 2 minutes and if new 
	 * participant record is found in PMP database, PMP will send an welcome mail 
	 * to the participant.
	 */
	public void sendWelcomeMailToParticipant(){
		LOGGER.info("Sending welcome mail to the participants at "+new Date());
		SendySubscriber sendySubscriber = null;
		List<SendySubscriber> subscriberList = new ArrayList<SendySubscriber>();
		List<Participant> participants = new ArrayList<Participant>();
		try{
			participants = welcomeMailRepository.getParticipantsToSendWelcomeEmails();
		} catch(Exception ex){
			LOGGER.error("Exception while fetching list of participants data {}",ex);
		}
		int validEmailSubscribersCount = 0;

		if (participants.size() > 0) {

			LOGGER.info("Total partcipant size {}", participants.size());
			Session session = sendMail.getSession();

			for (Participant participant : participants) {
				try{
					if (null != participant.getEmail() && !participant.getEmail().isEmpty()
							&& participant.getEmail().matches(ExpressionConstants.EMAIL_REGEX)) {

						PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0), participant.getEmail(),
								EmailLogConstants.PARTICIPANT_WELCOME_MAIL, EmailLogConstants.STATUS_SUCCESS, null);

						final int emailAlreadyAvailableCount = welcomeMailRepository.checkForMailIdInWelcomeLog(participant.getEmail());

						sendySubscriber = new SendySubscriber();
						if (emailAlreadyAvailableCount == 0) {
							validEmailSubscribersCount++;
							try {
								sendMail.sendWelcomeMailToParticipant(session,participant.getEmail());
								sendySubscriber.setUserName(participant.getPrintName());
								sendySubscriber.setEmail(participant.getEmail());
								sendySubscriber.setIsCoOrdinatorInformed(0);
							} catch(AddressException aex){
								sendySubscriber.setUserName(participant.getPrintName());
								sendySubscriber.setEmail(participant.getEmail());
								sendySubscriber.setIsCoOrdinatorInformed(0);
								pmpMailLog.setEmailSentStatus(EmailLogConstants.STATUS_FAILED);
								pmpMailLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(aex));
							} catch (UnsupportedEncodingException usee) {
								pmpMailLog.setEmailSentStatus(EmailLogConstants.STATUS_FAILED);
								pmpMailLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(usee));
							} catch (MessagingException me) {
								pmpMailLog.setEmailSentStatus(EmailLogConstants.STATUS_FAILED);
								pmpMailLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(me));
							} catch( Exception ex){
								pmpMailLog.setEmailSentStatus(EmailLogConstants.STATUS_FAILED);
								pmpMailLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
							}
						} else {
							sendySubscriber.setUserName(participant.getPrintName());
							sendySubscriber.setEmail(participant.getEmail());
							sendySubscriber.setIsCoOrdinatorInformed(1);
							pmpMailLog.setErrorMessage("Welcome mail hasbeen already sent to "+participant.getEmail());
						}

						if(null != sendySubscriber.getEmail()){
							subscriberList.add(sendySubscriber);
						}
						mailLogRepository.createMailLog(pmpMailLog);
					}
				} catch(Exception ex){
					LOGGER.error("Failed to send mail to {} : {}",participant.getPrintName(),participant.getEmail());
				}
			}

			LOGGER.info("{} participants have already received welcome mail ", participants.size() - validEmailSubscribersCount);
			LOGGER.info("{} new participants.", validEmailSubscribersCount);
			if (subscriberList.size() > 0) {
				for (SendySubscriber subscriber : subscriberList) {
					try{
						welcomeMailRepository.updateWelcomeMailLog(subscriber.getUserName(), subscriber.getEmail());
						welcomeMailRepository.updateParticipantByMailId(subscriber);
					} catch(Exception ex){
						LOGGER.error("Exception while updating welcome mail log and participant tables for {} : {}",subscriber.getUserName(),subscriber.getEmail());
					}
				}
			}
		}else{
			LOGGER.info("No participant found.");
		}

	}

}
