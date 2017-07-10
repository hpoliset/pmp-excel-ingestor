package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.sun.mail.smtp.SMTPMessage;

/**
 * This class is to hold the Mailing functionality.
 * 
 * @author HimaSree
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.mail.api.properties", ignoreUnknownFields = true, prefix = "mail.api")
public class SendMail {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);

	private String username;
	private String password;
	private String hostname;
	private String port;
	private String name;
	private String subject;
	private String defaultname;
	private String confirmationlink;
	private String unsubscribelink;
	private String smstemplatename;
	private String exceltemplatename;
	private String onlinetemplatename;
	private String noparticipantstemplatename;
	private String participantstemplatename;
	private String crdntrmailsubject;
	private String crdntrmailtemplatename;
	private String frommail;
	private String crdntrewlcomeidmailtemplatename;
	private String crdntrmailforewlcmidsubject;
	private String coordinatormailforupdatingevent;
	private String coordinatormailforupdatingeventsubject;
	private String welcomemailto;
	private String welcomemailsubject;
	private String welcomemailtemplatename;
	private String welcomemailbcc;
	private String welcomemailbcc2;

	public static class ProcessExecution {

		private String mailTemplate;
		private String toMailids;
		private String ftpUpload;
		private String ftpUploadSubject;
		private String welcomeMailToParticpants;
		private String welcomeMailToParticpantsSubject;
		private String ewelcomeidGeneration;
		private String ewelcomeidGenerationSubject;
		private String crdntrMailWithWlcmMailDtls;
		private String crdntrMailWithWlcmMailDtlsSbjct;
		private String crdntrMailWithewelcomeidGnrtnDetails;
		private String crdntrMailWithewelcomeidGnrtnDetailsSbjct;

		public String getMailTemplate() {
			return mailTemplate;
		}

		public void setMailTemplate(String mailTemplate) {
			this.mailTemplate = mailTemplate;
		}

		public String getToMailids() {
			return toMailids;
		}

		public void setToMailids(String toMailids) {
			this.toMailids = toMailids;
		}

		public String getFtpUpload() {
			return ftpUpload;
		}

		public void setFtpUpload(String ftpUpload) {
			this.ftpUpload = ftpUpload;
		}

		public String getFtpUploadSubject() {
			return ftpUploadSubject;
		}

		public void setFtpUploadSubject(String ftpUploadSubject) {
			this.ftpUploadSubject = ftpUploadSubject;
		}

		public String getWelcomeMailToParticpants() {
			return welcomeMailToParticpants;
		}

		public void setWelcomeMailToParticpants(String welcomeMailToParticpants) {
			this.welcomeMailToParticpants = welcomeMailToParticpants;
		}

		public String getWelcomeMailToParticpantsSubject() {
			return welcomeMailToParticpantsSubject;
		}

		public void setWelcomeMailToParticpantsSubject(String welcomeMailToParticpantsSubject) {
			this.welcomeMailToParticpantsSubject = welcomeMailToParticpantsSubject;
		}

		public String getEwelcomeidGeneration() {
			return ewelcomeidGeneration;
		}

		public void setEwelcomeidGeneration(String ewelcomeidGeneration) {
			this.ewelcomeidGeneration = ewelcomeidGeneration;
		}

		public String getEwelcomeidGenerationSubject() {
			return ewelcomeidGenerationSubject;
		}

		public void setEwelcomeidGenerationSubject(String ewelcomeidGenerationSubject) {
			this.ewelcomeidGenerationSubject = ewelcomeidGenerationSubject;
		}

		public String getCrdntrMailWithWlcmMailDtls() {
			return crdntrMailWithWlcmMailDtls;
		}

		public void setCrdntrMailWithWlcmMailDtls(String crdntrMailWithWlcmMailDtls) {
			this.crdntrMailWithWlcmMailDtls = crdntrMailWithWlcmMailDtls;
		}

		public String getCrdntrMailWithWlcmMailDtlsSbjct() {
			return crdntrMailWithWlcmMailDtlsSbjct;
		}

		public void setCrdntrMailWithWlcmMailDtlsSbjct(String crdntrMailWithWlcmMailDtlsSbjct) {
			this.crdntrMailWithWlcmMailDtlsSbjct = crdntrMailWithWlcmMailDtlsSbjct;
		}

		public String getCrdntrMailWithewelcomeidGnrtnDetails() {
			return crdntrMailWithewelcomeidGnrtnDetails;
		}

		public void setCrdntrMailWithewelcomeidGnrtnDetails(String crdntrMailWithewelcomeidGnrtnDetails) {
			this.crdntrMailWithewelcomeidGnrtnDetails = crdntrMailWithewelcomeidGnrtnDetails;
		}

		public String getCrdntrMailWithewelcomeidGnrtnDetailsSbjct() {
			return crdntrMailWithewelcomeidGnrtnDetailsSbjct;
		}

		public void setCrdntrMailWithewelcomeidGnrtnDetailsSbjct(String crdntrMailWithewelcomeidGnrtnDetailsSbjct) {
			this.crdntrMailWithewelcomeidGnrtnDetailsSbjct = crdntrMailWithewelcomeidGnrtnDetailsSbjct;
		}

	}

	@NotNull
	private ProcessExecution processExecution;

	public ProcessExecution getProcessExecution() {
		return processExecution;
	}

	public void setProcessExecution(ProcessExecution processExecution) {
		this.processExecution = processExecution;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDefaultname() {
		return defaultname;
	}

	public void setDefaultname(String defaultname) {
		this.defaultname = defaultname;
	}

	public String getConfirmationlink() {
		return confirmationlink;
	}

	public void setConfirmationlink(String confirmationlink) {
		this.confirmationlink = confirmationlink;
	}

	public String getUnsubscribelink() {
		return unsubscribelink;
	}

	public void setUnsubscribelink(String unsubscribelink) {
		this.unsubscribelink = unsubscribelink;
	}

	public String getSmstemplatename() {
		return smstemplatename;
	}

	public void setSmstemplatename(String smstemplatename) {
		this.smstemplatename = smstemplatename;
	}

	public String getExceltemplatename() {
		return exceltemplatename;
	}

	public void setExceltemplatename(String exceltemplatename) {
		this.exceltemplatename = exceltemplatename;
	}

	public String getOnlinetemplatename() {
		return onlinetemplatename;
	}

	public void setOnlinetemplatename(String onlinetemplatename) {
		this.onlinetemplatename = onlinetemplatename;
	}

	public String getNoparticipantstemplatename() {
		return noparticipantstemplatename;
	}

	public void setNoparticipantstemplatename(String noparticipantstemplatename) {
		this.noparticipantstemplatename = noparticipantstemplatename;
	}

	public String getParticipantstemplatename() {
		return participantstemplatename;
	}

	public void setParticipantstemplatename(String participantstemplatename) {
		this.participantstemplatename = participantstemplatename;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private VelocityEngine velocityEngine = new VelocityEngine();

	private VelocityContext context;

	public SendMail() {
		context = new VelocityContext();
	}

	public void addParameter(String name, String value) {
		this.context.put(name, value);
	}

	public VelocityContext getParameter() {
		return this.context;
	}

	public String getCrdntrmailsubject() {
		return crdntrmailsubject;
	}

	public void setCrdntrmailsubject(String crdntrmailsubject) {
		this.crdntrmailsubject = crdntrmailsubject;
	}

	public String getCrdntrmailtemplatename() {
		return crdntrmailtemplatename;
	}

	public void setCrdntrmailtemplatename(String crdntrmailtemplatename) {
		this.crdntrmailtemplatename = crdntrmailtemplatename;
	}

	public String getFrommail() {
		return frommail;
	}

	public void setFrommail(String frommail) {
		this.frommail = frommail;
	}

	public String getCrdntrewlcomeidmailtemplatename() {
		return crdntrewlcomeidmailtemplatename;
	}

	public void setCrdntrewlcomeidmailtemplatename(String crdntrewlcomeidmailtemplatename) {
		this.crdntrewlcomeidmailtemplatename = crdntrewlcomeidmailtemplatename;
	}

	public String getCrdntrmailforewlcmidsubject() {
		return crdntrmailforewlcmidsubject;
	}

	public void setCrdntrmailforewlcmidsubject(String crdntrmailforewlcmidsubject) {
		this.crdntrmailforewlcmidsubject = crdntrmailforewlcmidsubject;
	}

	public String getCoordinatormailforupdatingevent() {
		return coordinatormailforupdatingevent;
	}

	public void setCoordinatormailforupdatingevent(String coordinatormailforupdatingevent) {
		this.coordinatormailforupdatingevent = coordinatormailforupdatingevent;
	}

	public String getCoordinatormailforupdatingeventsubject() {
		return coordinatormailforupdatingeventsubject;
	}

	public void setCoordinatormailforupdatingeventsubject(String coordinatormailforupdatingeventsubject) {
		this.coordinatormailforupdatingeventsubject = coordinatormailforupdatingeventsubject;
	}

	public String getWelcomemailto() {
		return welcomemailto;
	}

	public void setWelcomemailto(String welcomemailto) {
		this.welcomemailto = welcomemailto;
	}

	public String getWelcomemailsubject() {
		return welcomemailsubject;
	}

	public void setWelcomemailsubject(String welcomemailsubject) {
		this.welcomemailsubject = welcomemailsubject;
	}

	public String getWelcomemailtemplatename() {
		return welcomemailtemplatename;
	}

	public void setWelcomemailtemplatename(String welcomemailtemplatename) {
		this.welcomemailtemplatename = welcomemailtemplatename;
	}

	public String getWelcomemailbcc() {
		return welcomemailbcc;
	}

	public void setWelcomemailbcc(String welcomemailbcc) {
		this.welcomemailbcc = welcomemailbcc;
	}

	public String getWelcomemailbcc2() {
		return welcomemailbcc2;
	}

	public void setWelcomemailbcc2(String welcomemailbcc2) {
		this.welcomemailbcc2 = welcomemailbcc2;
	}

	@Autowired
	private AESEncryptDecrypt aesEncryptDecrypt;

	@Autowired
	private MailLogRepository mailLogRepository;

	@Autowired
	Environment env;

	/**
	 * To send notification mail to the team if no new participants found to
	 * send mail for the day
	 * 
	 * @param toMailIds
	 *            -recipients TO
	 * @param ccMailIds
	 *            -recipients CC
	 */
	public void sendNotificationEmail(String toMailIds, String ccMailIds, String subject, int count) {
		String[] toIds = toMailIds.split(",");
		String[] ccIds = ccMailIds.split(",");
		try {
			Session session = getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			for (String toId : toIds) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toId));
			}
			for (String ccId : ccIds) {
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccId));
			}
			message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			SimpleDateFormat sdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
			message.setSubject(subject + " " + sdf.format(new Date()));
			addParameter(EmailLogConstants.DATE_PARAMETER, sdf.format(new Date()));
			if (count == 0) {
				message.setContent(getMessageContentbyTemplateName(noparticipantstemplatename),
						EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			} else {
				addParameter(EmailLogConstants.COUNT_PARAMETER, String.valueOf(count));
				message.setContent(getMessageContentbyTemplateName(participantstemplatename),
						EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			}
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();
			for (String toId : toIds) {
				LOGGER.info("Mail sent successfully : {} ", toId);
			}

			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0), toIds[0], EmailLogConstants.FTP_UPLOAD_DETAILS,
					EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);

		} catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.error("Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf("0"), toIds[0], EmailLogConstants.FTP_UPLOAD_DETAILS,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);

		}
	}

	/**
	 * Method to get the personalized name of the given participant.
	 * 
	 * @param printName
	 * @return
	 */
	public String getName(String printName) {
		printName = printName.replace(".", " ");
		String[] name = printName.split(" ");
		if (name.length > 0) {
			for (int i = 0; i < name.length; i++) {
				if (name[i].length() > 2 && !name[i].equalsIgnoreCase("mrs") && !name[i].equalsIgnoreCase("smt")) {
					return name[i].substring(0, 1).toUpperCase() + name[i].substring(1).toLowerCase();
				}
			}
		}
		return printName;
	}

	/**
	 * To get the email content as string from the vm template.
	 * 
	 * @param welcomemail
	 * @return
	 */
	private String getMessageContentbyTemplateName(String templateName) {
		Template template = null;
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		template = velocityEngine.getTemplate(templateName);
		StringWriter stringWriter = new StringWriter();
		template.merge(getParameter(), stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Method to send mail to the given to and cc mailIDs with the provided
	 * message content.
	 * 
	 * @param toEmailIDs
	 * @param ccEmailIDs
	 * @param messageContent
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public void sendMail(List<String> toEmailIDs, List<String> ccEmailIDs, String messageContent)
			throws MessagingException, UnsupportedEncodingException {
		Session session = getSession();
		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(frommail, name));
		for (String toemailID : toEmailIDs) {
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toemailID));
		}
		for (String ccemailID : ccEmailIDs) {
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccemailID));
		}
		message.setSubject(subject);
		message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
		message.setContent(messageContent, EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
		transport.send(message);
		transport.close();
	}

	/**
	 * Method to send confirm subscription mail to the online users.
	 * 
	 * @param mail
	 * @param name
	 */
	public void sendConfirmSubscriptionMail(String mail, String name) {
		if (null != name && !name.isEmpty()) {
			addParameter(EmailLogConstants.NAME_PARAMETER, getName(name));
		} else {
			addParameter(EmailLogConstants.NAME_PARAMETER, defaultname);
		}
		addParameter(
				EmailLogConstants.CONFIRMATION_LINK_PARAMETER,
				confirmationlink + "?id="
						+ aesEncryptDecrypt.encrypt(mail, env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)));
		List<String> toEmailIDs = new ArrayList<String>();
		toEmailIDs.add(mail);
		try {
			sendMail(toEmailIDs, new ArrayList<String>(), getMessageContentbyTemplateName(onlinetemplatename));
			LOGGER.info("Mail sent successfully : {} ", mail);
		} catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.error("Sending Mail Failed : {} ", mail);
			throw new RuntimeException(e);

		}
	}

	/**
	 * This method is used to send email to the coordinator of an event with
	 * details about the participant count who have received welcome email.
	 * 
	 * @param crdntrEmail
	 * @param session
	 * @throws AddressException
	 *             if coordinator email address in not valid.
	 * @throws MessagingException
	 *             if not able to send email.
	 * @throws UnsupportedEncodingException
	 */
	public void sendMailNotificationToCoordinator(CoordinatorEmail crdntrEmail, Session session,String uploaderEmail,String jiraIssueNumber)
			throws AddressException, MessagingException, UnsupportedEncodingException, ParseException {

		addParameter(EmailLogConstants.COORDINATOR_NAME_PARAMETER,
				null != crdntrEmail.getCoordinatorName() ? getName(crdntrEmail.getCoordinatorName()) : "Friend");
		addParameter(EmailLogConstants.TOTAL_PARTICIPANT_COUNT_PARAMETER,
				null != crdntrEmail.getTotalParticipantCount() ? crdntrEmail.getTotalParticipantCount() : "0");
		addParameter(EmailLogConstants.WLCM_MAIL_ALRDY_RCVD_PARTICIPANT_COUNT_PARAMETER,
				null != crdntrEmail.getPctptAlreadyRcvdWlcmMailCount() ? crdntrEmail.getPctptAlreadyRcvdWlcmMailCount()
						: "0");
		addParameter(EmailLogConstants.WLCM_MAIL_RCVD_YSTRDY_PARTICIPANT_COUNT_PARAMETER,
				null != crdntrEmail.getPctptRcvdWlcmMailYstrdayCount() ? crdntrEmail.getPctptRcvdWlcmMailYstrdayCount()
						: "0");
		addParameter(EmailLogConstants.EVENT_NAME_PARAMETER,
				null != crdntrEmail.getEventName() ? "- " + crdntrEmail.getEventName() : "");
		addParameter(EmailLogConstants.EVENT_PLACE_PARAMETER, null != crdntrEmail.getEventPlace() ? "conducted at "
				+ crdntrEmail.getEventPlace() : "");
		addParameter(EmailLogConstants.EVENT_CITY_PARAMETER,
				null != crdntrEmail.getEventCity() ? ", " + crdntrEmail.getEventCity() : "");

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat outputsdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
		addParameter(
				EmailLogConstants.PROGRAM_CREATE_DATE_PARAMETER,
				null != crdntrEmail.getProgramCreateDate() ? "held on "
						+ outputsdf.format(crdntrEmail.getProgramCreateDate()) : "");
		addParameter(EmailLogConstants.WELCOME_MAIL_SENT_DATE_PARAMETER, outputsdf.format(cal.getTime()));
		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(frommail, name));
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(crdntrEmail.getCoordinatorEmail()));
		message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
		
		if(null != uploaderEmail && !uploaderEmail.isEmpty()){
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(uploaderEmail));
		}
		if(null != jiraIssueNumber && !jiraIssueNumber.isEmpty()){
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			message.setSubject(EmailLogConstants.JIRA_NO_PREFIX_SUBJECT + jiraIssueNumber +") "+ crdntrmailsubject + outputsdf.format(cal.getTime()));

		}else{
			message.setSubject(crdntrmailsubject + outputsdf.format(cal.getTime()));

		}

		message.setContent(getMessageContentbyTemplateName(crdntrmailtemplatename),EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
				
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
		transport.send(message);
		transport.close();
	}

	public void sendGeneratedEwelcomeIdDetailslToCoordinator(CoordinatorEmail coordinatorEmail,
			List<Participant> participants, List<Participant> failedParticipants, Session session,String uploaderEmail,String jiraIssueNumber,String pgrmCreatedSource)
					throws AddressException, MessagingException, UnsupportedEncodingException {

		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(frommail, name));
		addParameter(EmailLogConstants.COORDINATOR_NAME_PARAMETER,
				coordinatorEmail.getCoordinatorName() != null ? getName(coordinatorEmail.getCoordinatorName()): "Friend");

		addParameter(EmailLogConstants.EVENT_NAME_PARAMETER, null != coordinatorEmail.getEventName() ? "- "
				+ coordinatorEmail.getEventName() : "");
		addParameter(EmailLogConstants.EVENT_PLACE_PARAMETER,
				null != coordinatorEmail.getEventPlace() ? "conducted at " + coordinatorEmail.getEventPlace() : "");
		addParameter(EmailLogConstants.EVENT_CITY_PARAMETER, null != coordinatorEmail.getEventCity() ? ", "
				+ coordinatorEmail.getEventCity() : "");
		SimpleDateFormat outputsdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
		addParameter(
				EmailLogConstants.EVENT_START_DATE_PARAMETER,
				coordinatorEmail.getProgramCreateDate() != null ? "held on "
						+ outputsdf.format(coordinatorEmail.getProgramCreateDate()) : (coordinatorEmail
								.getProgramCreationDate() != null ? "held on "
										+ outputsdf.format(coordinatorEmail.getProgramCreationDate()) : ""));
		StringBuilder sb = new StringBuilder();
		if (!participants.isEmpty()) {
			sb.append("<p>The following e-welcome ID's has been generated for the below given participants : ");
			sb.append(coordinatorEmail.getEventName() != null ? coordinatorEmail.getEventName() : "");
			sb.append("</p>");
			sb.append("<table border=\"1\" style=\"width: 75%;border-collapse: collapse;\">");
			sb.append("<tr>");
			sb.append("<td  align=middle><b>S.No</b></td>");
			sb.append("<td  align=middle><b>Participant Name</b></td>");
			sb.append("<td  align=middle><b>Participant mail ID</b></td>");
			sb.append("<td  align=middle><b>Mobile</b></td>");
			sb.append("<td  align=middle><b>e-welcome ID</b></td>");
			sb.append("<td  align=middle><b>Introduced Date</b></td>");
			sb.append("</tr>");
			int i = 1;
			for (Participant participant : participants) {
				sb.append("<tr><td>");
				sb.append(i++);
				sb.append("</td><td>");
				sb.append(participant.getPrintName() != null ? participant.getPrintName() : "");
				sb.append("</td><td>");
				sb.append(participant.getEmail() != null ? participant.getEmail() : "");
				sb.append("</td><td>");
				sb.append((participant.getMobilePhone() != null && participant.getMobilePhone() != "0") ? participant
						.getMobilePhone() : "");
				sb.append("</td><td>");
				sb.append(participant.getWelcomeCardNumber() != null ? participant.getWelcomeCardNumber() : "");
				sb.append("</td><td>");
				sb.append(participant.getIntroductionDate() != null ? outputsdf.format(participant
						.getIntroductionDate()) : "");
				sb.append("</td></tr>");
			}
			if (!failedParticipants.isEmpty()) {
				sb.append("</table>");
				sb.append("</p>");
				sb.append("<p>The following participant's haven't received e-welcome ID : ");
				sb.append(coordinatorEmail.getEventName() != null ? coordinatorEmail.getEventName() : "");
				sb.append("</p>");
				sb.append("<table border=\"1\" style=\"width: 75%;border-collapse: collapse;\">");
				sb.append("<tr>");
				sb.append("<td  align=middle><b>S.No</b></td>");
				sb.append("<td  align=middle><b>Participant Name</b></td>");
				sb.append("<td  align=middle><b>Participant mail ID</b></td>");
				sb.append("<td  align=middle><b>Mobile</b></td>");
				sb.append("<td  align=middle><b>e-welcome ID Remarks</b></td>");
				sb.append("	</tr>");
				int j = 1;
				for (Participant failedParticipant : failedParticipants) {
					sb.append("<tr><td>");
					sb.append(j++);
					sb.append("</td><td>");
					sb.append(failedParticipant.getPrintName() != null ? failedParticipant.getPrintName() : "");
					sb.append("</td><td>");
					sb.append(failedParticipant.getEmail() != null ? failedParticipant.getEmail() : "");
					sb.append("</td><td>");
					sb.append((failedParticipant.getMobilePhone() != null && failedParticipant.getMobilePhone() != "0") ? failedParticipant
							.getMobilePhone() : "");
					sb.append("</td><td>");
					sb.append(failedParticipant.getEwelcomeIdRemarks() != null ? failedParticipant
							.getEwelcomeIdRemarks() : "");
					sb.append("</td></tr>");
				}
				sb.append("</table>");
				sb.append("</p>");
			} else {
				sb.append("</table>");
				sb.append("</p>");
			}
		} else if (!failedParticipants.isEmpty()) {
			sb.append("<p>The following participant's haven't received e-welcome ID : ");
			sb.append(coordinatorEmail.getEventName() != null ? coordinatorEmail.getEventName() : "");
			sb.append("</p>");
			sb.append("<table border=\"1\" style=\"width: 75%;border-collapse: collapse;\">");
			sb.append("<tr>");
			sb.append("<td  align=middle><b>S.No</b></td>");
			sb.append("<td  align=middle><b>Participant Name</b></td>");
			sb.append("<td  align=middle><b>Participant mail ID</b></td>");
			sb.append("<td  align=middle><b>Mobile</b></td>");
			sb.append("<td  align=middle><b>e-welcome ID Remarks</b></td>");
			sb.append("	</tr>");
			int j = 1;
			for (Participant failedParticipant : failedParticipants) {
				sb.append("<tr><td>");
				sb.append(j++);
				sb.append("</td><td>");
				sb.append(failedParticipant.getPrintName() != null ? failedParticipant.getPrintName() : "");
				sb.append("</td><td>");
				sb.append(failedParticipant.getEmail() != null ? failedParticipant.getEmail() : "");
				sb.append("</td><td>");
				sb.append((failedParticipant.getMobilePhone() != null && failedParticipant.getMobilePhone() != "0") ? failedParticipant
						.getMobilePhone() : "");
				sb.append("</td><td>");
				sb.append(failedParticipant.getEwelcomeIdRemarks() != null ? failedParticipant.getEwelcomeIdRemarks()
						: "");
				sb.append("</td></tr>");
			}
			sb.append("</table>");
			sb.append("</p>");

		}
		
		String dashboardUrl = "";
		if(null != pgrmCreatedSource && pgrmCreatedSource.equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2)){
			dashboardUrl= SMSConstants.DASHBOARD_v2_HEARTFULNESS_UPDATEEVENT_URL;
		}else {
			dashboardUrl= SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL;
		}
		
		addParameter(EmailLogConstants.EVENT_LINK_PARAMETER, dashboardUrl + "?id="+ coordinatorEmail.getEventID());
		addParameter(EmailLogConstants.PARTICIPANTS_DETAILS_PARAMETER, sb.toString());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(coordinatorEmail.getCoordinatorEmail()));
		message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
		
		if(null != uploaderEmail && !uploaderEmail.isEmpty()){
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(uploaderEmail));
		}

		if(null != jiraIssueNumber && !jiraIssueNumber.isEmpty()){
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			message.setSubject(EmailLogConstants.JIRA_NO_PREFIX_SUBJECT + jiraIssueNumber +") "
					+ crdntrmailforewlcmidsubject + (null != coordinatorEmail.getEventName() ? " - " 
							+ coordinatorEmail.getEventName() : ""));
		}
		else{
			message.setSubject(crdntrmailforewlcmidsubject+ (null != coordinatorEmail.getEventName() ? " - " + coordinatorEmail.getEventName() : ""));

		}
		message.setContent(getMessageContentbyTemplateName(crdntrewlcomeidmailtemplatename),EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
		transport.send(message);
		transport.close();
	}

	public void sendWelcomeMail() throws AddressException, MessagingException, UnsupportedEncodingException,
	ParseException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			SimpleDateFormat sdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
			Date date = new Date();
			String date_str = sdf.format(date);
			addParameter(EmailLogConstants.DATE_PARAMETER, date_str);
			Session session = getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(welcomemailto));
			message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(welcomemailbcc));
			message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(welcomemailbcc2));
			message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			message.setSubject(welcomemailsubject);
			message.setContent(getMessageContentbyTemplateName(welcomemailtemplatename),
					EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0), welcomemailto,
					EmailLogConstants.FTP_UPLOAD_DETAILS, EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);

		} catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.error("Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf("0"), welcomemailto,
					EmailLogConstants.FTP_UPLOAD_DETAILS, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		}
	}

	public void sendNotificationToInformProcessExecution(String processName) {
		String[] toIds = processExecution.toMailids.split(",");
		try {
			Session session = getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			for (String toId : toIds) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toId));
			}
			message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			SimpleDateFormat sdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
			addParameter(EmailLogConstants.DATE_PARAMETER, sdf.format(new Date()));

			if (processName.equalsIgnoreCase(EmailLogConstants.EWELCOME_ID_GENERATION)) {
				message.setSubject(processExecution.ewelcomeidGenerationSubject + ExpressionConstants.SPACE_SEPARATER
						+ sdf.format(new Date()));
				addParameter(EmailLogConstants.MAIL_CONTENT_PARAMETER, processExecution.ewelcomeidGeneration);
			} else if (processName.equalsIgnoreCase(EmailLogConstants.FTP_UPLOAD_DETAILS)) {
				message.setSubject(processExecution.ftpUploadSubject + ExpressionConstants.SPACE_SEPARATER
						+ sdf.format(new Date()));
				addParameter(EmailLogConstants.MAIL_CONTENT_PARAMETER, processExecution.ftpUpload);
			} else if (processName.equalsIgnoreCase(EmailLogConstants.WLCMID_EMAIL_DETAILS)) {
				message.setSubject(processExecution.crdntrMailWithewelcomeidGnrtnDetailsSbjct
						+ ExpressionConstants.SPACE_SEPARATER + sdf.format(new Date()));
				addParameter(EmailLogConstants.MAIL_CONTENT_PARAMETER,
						processExecution.crdntrMailWithewelcomeidGnrtnDetails);
			} else if (processName.equalsIgnoreCase(EmailLogConstants.WELCOME_MAIL_DETAILS)) {
				message.setSubject(processExecution.crdntrMailWithWlcmMailDtlsSbjct
						+ ExpressionConstants.SPACE_SEPARATER + sdf.format(new Date()));
				addParameter(EmailLogConstants.MAIL_CONTENT_PARAMETER, processExecution.crdntrMailWithWlcmMailDtls);
			} else if (processName.equalsIgnoreCase(EmailLogConstants.WELCOME_MAIL_TO_PARTICIPANTS)) {
				message.setSubject(processExecution.welcomeMailToParticpantsSubject
						+ ExpressionConstants.SPACE_SEPARATER + sdf.format(new Date()));
				addParameter(EmailLogConstants.MAIL_CONTENT_PARAMETER, processExecution.welcomeMailToParticpants);
			}
			message.setContent(getMessageContentbyTemplateName(processExecution.mailTemplate),
					EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();
			for (String toId : toIds) {
				LOGGER.info("Mail sent successfully : {} ", toId);
			}
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0), toIds.toString(), processName,
					EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (MessagingException ex) {
			LOGGER.error("Sending Mail Failed : {} " + StackTraceUtils.convertStackTracetoString(ex));
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf("0"), toIds.toString(), processName,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(ex));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception e) {
			LOGGER.error("Sending Mail Failed : {} " + StackTraceUtils.convertStackTracetoString(e));
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf("0"), toIds.toString(), processName,
					EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		}

	}

	/**
	 * Method used for authentication to the SMTP mail Server.
	 * 
	 * @return <code>Session</code>
	 */
	public Session getSession() {
		Properties props = System.getProperties();
		props.put(EmailLogConstants.MAIL_DEBUG_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_HOST_PROPERTY, hostname);
		props.put(EmailLogConstants.MAIL_SMTP_PORT_PROPERTY, port);
		props.put(EmailLogConstants.MAIL_SMTP_SSL_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_AUTH_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_STARTTLS_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_PROTOCOL_PROPERTY, EmailLogConstants.MAIL_SMTP_PROPERTY);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		return session;
	}

	/**
	 * This method is used to sent welcome mails to the 
	 * participants.
	 * @param session, MailSession 
	 * @param participantEmail, email of the participant
	 * @throws MessagingException if failed to connect to host.
	 * @throws UnsupportedEncodingException if email and name does not match
	 */
	public void sendWelcomeMailToParticipant(Session session,String participantEmail) throws MessagingException, UnsupportedEncodingException {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
		String date_str = sdf.format(date);
		addParameter(EmailLogConstants.DATE_PARAMETER, date_str);
		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(frommail, name));
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(participantEmail));
		message.setSubject(welcomemailsubject);
		message.setContent(getMessageContentbyTemplateName(welcomemailtemplatename),
				EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
		transport.send(message);
		transport.close();
	}

}
