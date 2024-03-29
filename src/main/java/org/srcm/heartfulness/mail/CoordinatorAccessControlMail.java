package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.ExpressionConstants;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.model.CoordinatorAccessControlEmail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.sun.mail.smtp.SMTPMessage;

/**
 * @author Koustav Dutta
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:prod.cac.mail.properties", ignoreUnknownFields = false, prefix = "mail.cac")
public class CoordinatorAccessControlMail {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatorAccessControlMail.class);

	private String username;
	private String password;
	private String hostname;
	private String port;
	private String name;
	private String frommail;
	private String emptycoordinatoremailidtemplate;
	private String emptycoordinatoremailidsubject;
	private String coordinatormailtemplatetocreateaccount;
	private String coordinatormailsubjecttocreateaccount;
	private String coordinatormailforupdatingevent;
	private String coordinatormailforupdatingeventsubject;
	private String requestMailSubject;
	private String requestMailTemplate;
	private String approvalMailSubject;
	private String approvalMailTemplate;

	@Autowired
	private MailLogRepository mailLogRepository;

	private VelocityEngine velocityEngine = new VelocityEngine();

	private VelocityContext context;

	@Autowired
	SendMail sendMail;

	public CoordinatorAccessControlMail() {
		context = new VelocityContext();
	}

	public void addParameter(String name, String value) {
		this.context.put(name, value);
	}

	public VelocityContext getParameter() {
		return this.context;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getFrommail() {
		return frommail;
	}

	public void setFrommail(String frommail) {
		this.frommail = frommail;
	}

	public String getEmptycoordinatoremailidtemplate() {
		return emptycoordinatoremailidtemplate;
	}

	public void setEmptycoordinatoremailidtemplate(String emptycoordinatoremailidtemplate) {
		this.emptycoordinatoremailidtemplate = emptycoordinatoremailidtemplate;
	}

	public String getEmptycoordinatoremailidsubject() {
		return emptycoordinatoremailidsubject;
	}

	public void setEmptycoordinatoremailidsubject(String emptycoordinatoremailidsubject) {
		this.emptycoordinatoremailidsubject = emptycoordinatoremailidsubject;
	}

	public String getCoordinatormailtemplatetocreateaccount() {
		return coordinatormailtemplatetocreateaccount;
	}

	public void setCoordinatormailtemplatetocreateaccount(String coordinatormailtemplatetocreateaccount) {
		this.coordinatormailtemplatetocreateaccount = coordinatormailtemplatetocreateaccount;
	}

	public String getCoordinatormailsubjecttocreateaccount() {
		return coordinatormailsubjecttocreateaccount;
	}

	public void setCoordinatormailsubjecttocreateaccount(String coordinatormailsubjecttocreateaccount) {
		this.coordinatormailsubjecttocreateaccount = coordinatormailsubjecttocreateaccount;
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

	public MailLogRepository getMailLogRepository() {
		return mailLogRepository;
	}

	public void setMailLogRepository(MailLogRepository mailLogRepository) {
		this.mailLogRepository = mailLogRepository;
	}

	public String getRequestMailSubject() {
		return requestMailSubject;
	}

	public void setRequestMailSubject(String requestMailSubject) {
		this.requestMailSubject = requestMailSubject;
	}

	public String getRequestMailTemplate() {
		return requestMailTemplate;
	}

	public void setRequestMailTemplate(String requestMailTemplate) {
		this.requestMailTemplate = requestMailTemplate;
	}

	public String getApprovalMailSubject() {
		return approvalMailSubject;
	}

	public void setApprovalMailSubject(String approvalMailSubject) {
		this.approvalMailSubject = approvalMailSubject;
	}

	public String getApprovalMailTemplate() {
		return approvalMailTemplate;
	}

	public void setApprovalMailTemplate(String approvalMailTemplate) {
		this.approvalMailTemplate = approvalMailTemplate;
	}

	/**
	 * To get the email content as string from the vm template.
	 * 
	 * @param welcomemail
	 * @return
	 */
	public String getMessageContentbyTemplateName(String templateName) {
		Template template = null;
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		template = velocityEngine.getTemplate(templateName);
		StringWriter stringWriter = new StringWriter();
		template.merge(getParameter(), stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Method to send mail to preceptor of the event stating to update the valid
	 * coordinator email Id to the event.
	 * 
	 * @param coordinatorAccessControlEmail
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	public void sendMailToPreceptorToUpdateCoordinatorEmailID(
			CoordinatorAccessControlEmail coordinatorAccessControlEmail) throws AddressException, MessagingException,
	UnsupportedEncodingException, ParseException {
		try {
			Session session = sendMail.getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			addParameter(EmailLogConstants.PRECEPTOR_NAME_PARAMETER,sendMail.getName(coordinatorAccessControlEmail.getPreceptorName()));
			String dashboardUrl = "";
			if(null != coordinatorAccessControlEmail.getPgrmCreatedSource() && coordinatorAccessControlEmail.getPgrmCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2)){
				dashboardUrl= SMSConstants.DASHBOARD_v2_HEARTFULNESS_UPDATEEVENT_URL;
			}else {
				dashboardUrl= SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL;
			}
			
			addParameter(EmailLogConstants.UPDATE_EVENT_LINK_PARAMETER, dashboardUrl + coordinatorAccessControlEmail.getEventID());
			addParameter(EmailLogConstants.EVENT_PLACE_PARAMETER, coordinatorAccessControlEmail.getEventPlace());
			addParameter(EmailLogConstants.EVENT_NAME_PARAMETER, coordinatorAccessControlEmail.getEventName());
			SimpleDateFormat inputsdf = new SimpleDateFormat(ExpressionConstants.SQL_DATE_FORMAT);
			SimpleDateFormat outputsdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
			Date pgrmCreateDate = inputsdf.parse(coordinatorAccessControlEmail.getProgramCreateDate());
			addParameter(EmailLogConstants.PROGRAM_CREATE_DATE_PARAMETER, outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO,InternetAddress.parse(coordinatorAccessControlEmail.getPreceptorEmailId()));
			message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			message.setSubject(emptycoordinatoremailidsubject + " - " + coordinatorAccessControlEmail.getEventName());
			message.setContent(getMessageContentbyTemplateName(emptycoordinatoremailidtemplate),
					EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();
			LOGGER.info("Mail sent successfully to Coordinator : {} ",
					coordinatorAccessControlEmail.getPreceptorEmailId());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
					coordinatorAccessControlEmail.getCoordinatorEmail(),
					EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID, EmailLogConstants.STATUS_SUCCESS,
					null);
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
					coordinatorAccessControlEmail.getCoordinatorEmail(),
					EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
					coordinatorAccessControlEmail.getCoordinatorEmail(),
					EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception e) {
			LOGGER.error("Exception : Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
					coordinatorAccessControlEmail.getCoordinatorEmail(),
					EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		}

	}

	/**
	 * Method to send mail to the coordinator of the event inorder to update the
	 * valid preceptor Id to the event.
	 * 
	 * @param coordinator
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws ParseException
	 */
	public void sendMailToCoordinatorToUpdatePreceptorID(CoordinatorEmail coordinator) throws AddressException,
	MessagingException, UnsupportedEncodingException, ParseException {

		LOGGER.error("Sending mail to coordinator to update Preceptor Id ");
		addParameter(EmailLogConstants.COORDINATOR_NAME_PARAMETER, sendMail.getName(coordinator.getCoordinatorName()));
		String dashboardUrl = "";
		if(null != coordinator.getPgrmCreatedSource() && coordinator.getPgrmCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2)){
			dashboardUrl= SMSConstants.DASHBOARD_v2_HEARTFULNESS_UPDATEEVENT_URL;
		}else {
			dashboardUrl= SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL;
		}
		addParameter(EmailLogConstants.UPDATE_EVENT_LINK_PARAMETER, dashboardUrl + coordinator.getEventID());
		addParameter(EmailLogConstants.EVENT_NAME_PARAMETER, coordinator.getEventName());
		SimpleDateFormat outputsdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
		addParameter(EmailLogConstants.PROGRAM_CREATE_DATE_PARAMETER,null != coordinator.getProgramCreateDate() ? outputsdf.format(coordinator.getProgramCreateDate()) : "");
		Session session = sendMail.getSession();
		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(frommail, name));
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(coordinator.getCoordinatorEmail()));
		message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
		
		if(null != coordinator.getJiraNumber() && !coordinator.getJiraNumber().isEmpty()){
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			message.setSubject(EmailLogConstants.JIRA_NO_PREFIX_SUBJECT + coordinator.getJiraNumber() +") "+ coordinatormailforupdatingeventsubject + " - " + coordinator.getEventName());
		}else{
			message.setSubject(coordinatormailforupdatingeventsubject + " - " + coordinator.getEventName());
		}
		if(null != coordinator.getUploaderMail() && !coordinator.getUploaderMail().isEmpty()){
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(coordinator.getUploaderMail()));	
		}

		message.setContent(getMessageContentbyTemplateName(coordinatormailforupdatingevent),EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
		transport.send(message);
		transport.close();
		LOGGER.info("Mail sent successfully to Coordinator : {} ", coordinator.getCoordinatorEmail());
	}

	/**
	 * Method to send mail to the coordinator of the event to associate his
	 * email address with MYSRCM in order to manage the events in HFN.
	 * 
	 * @param <code>CoordinatorAccessControlEmail</code> coordinator
	 */
	public void sendMailToCoordinatorWithLinktoCreateProfile(CoordinatorAccessControlEmail coordinator) {

		LOGGER.error("Sending mail to coordinator with a link to create profile");
		try {
			Session session = sendMail.getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			addParameter(EmailLogConstants.COORDINATOR_NAME_PARAMETER,sendMail.getName(coordinator.getCoordinatorName()));
			String dashboardUrl = "";
			if(null != coordinator.getPgrmCreatedSource() && coordinator.getPgrmCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2)){
				dashboardUrl= SMSConstants.DASHBOARD_v2_HEARTFULNESS_UPDATEEVENT_URL;
			}else {
				dashboardUrl= SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL;
			}
			addParameter(EmailLogConstants.UPDATE_EVENT_LINK_PARAMETER, dashboardUrl + coordinator.getEventID());
			addParameter(EmailLogConstants.CREATE_PROFILE_LINK_PARAMETER,CoordinatorAccessControlConstants.HEARTFULNESS_CREATE_PROFILE_URL);
			addParameter(EmailLogConstants.EVENT_NAME_PARAMETER, coordinator.getEventName());
			addParameter(EmailLogConstants.EVENT_PLACE_PARAMETER, coordinator.getEventPlace());
			SimpleDateFormat inputsdf = new SimpleDateFormat(ExpressionConstants.SQL_DATE_FORMAT);
			SimpleDateFormat outputsdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
			Date pgrmCreateDate = inputsdf.parse(coordinator.getProgramCreateDate());
			addParameter(EmailLogConstants.PROGRAM_CREATE_DATE_PARAMETER, outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(coordinator.getCoordinatorEmail()));
			message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
			
			if(null != coordinator.getJiraNumber() && !coordinator.getJiraNumber().isEmpty()){
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
				message.setSubject(EmailLogConstants.JIRA_NO_PREFIX_SUBJECT + coordinator.getJiraNumber() +") "+ coordinatormailsubjecttocreateaccount + " - " + coordinator.getEventName());
			}else{
				message.setSubject(coordinatormailsubjecttocreateaccount + " - " + coordinator.getEventName());
			}
			if(null != coordinator.getUploaderMail() && !coordinator.getUploaderMail().isEmpty()){
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(coordinator.getUploaderMail()));
			}

			message.setContent(getMessageContentbyTemplateName(coordinatormailtemplatetocreateaccount),
					EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();
			LOGGER.info("Mail sent successfully to Coordinator : {} ", coordinator.getCoordinatorEmail());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
					EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
					EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
					EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		} catch (Exception e) {
			PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
					EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(e));
			mailLogRepository.createMailLog(pmpMailLog);
		}

	}

	public void sendApprovalMailToPrimaryCoordinator(Program program, User user, String eventId, String secondaryCoordinatorNotes,String uploaderEmail)
			throws MessagingException, UnsupportedEncodingException {
		try{
			addParameter(EmailLogConstants.COORDINATOR_NAME_PARAMETER, null != program.getCoordinatorName() ? program.getCoordinatorName() : "");
			addParameter(EmailLogConstants.EVENT_ID_PARAMETER, eventId);
			String dashboardUrl = "";
			if(null != program.getCreatedSource() && program.getCreatedSource().equals(PMPConstants.CREATED_SOURCE_DASHBOARD_v2)){
				dashboardUrl= SMSConstants.DASHBOARD_v2_HEARTFULNESS_UPDATEEVENT_URL;
			}else {
				dashboardUrl= SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL;
			}
			addParameter(EmailLogConstants.UPDATE_EVENT_LINK_PARAMETER, dashboardUrl + eventId);
			addParameter(EmailLogConstants.SECONDARY_COORDINATOR_NAME_PARAMETER, null != user.getName() ? user.getName() : "");
			addParameter(EmailLogConstants.SECONDARY_COORDINATOR_EMAIL_PARAMETER, null != user.getEmail() ? user.getEmail() : "");
			addParameter(EmailLogConstants.SECONDARY_COORDINATOR_ABHYASIID_PARAMETER, null != user.getAbyasiId() ? user.getAbyasiId() :"");
			addParameter(EmailLogConstants.SECONDARY_COORDINATOR_NOTES, null != secondaryCoordinatorNotes ? secondaryCoordinatorNotes : "");
			Session session = sendMail.getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(program.getCoordinatorEmail()));
			message.setReplyTo(InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));

			if(null != program.getJiraIssueNumber() && !program.getJiraIssueNumber().isEmpty()){
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailLogConstants.HFN_JIRA_EMAIL));
				message.setSubject(EmailLogConstants.JIRA_NO_PREFIX_SUBJECT + program.getJiraIssueNumber() +") "+ requestMailSubject + eventId);
			}else{
				message.setSubject(requestMailSubject + eventId);
			}
			if(null != uploaderEmail && !uploaderEmail.isEmpty()){
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(uploaderEmail));
			}

			message.setContent(getMessageContentbyTemplateName(approvalMailTemplate),
					EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();

			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()), program.getCoordinatorEmail(),
					EmailLogConstants.MAIL_TO_PRIMARY_COORDINATOR_FOR_APPROVAL, EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);

		} catch(Exception ex){
			PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(program.getProgramId()), program.getCoordinatorEmail(),
					EmailLogConstants.MAIL_TO_PRIMARY_COORDINATOR_FOR_APPROVAL, EmailLogConstants.STATUS_FAILED,
					StackTraceUtils.convertStackTracetoString(ex));
			mailLogRepository.createMailLog(pmpMailLog);
		}


	}

	/*public void sendMailToNewSecondaryCoordinator(String recipientName, String recipientEmail, String eventId)
			throws UnsupportedEncodingException, MessagingException {
		addParameter(EmailLogConstants.NAME_PARAMETER, recipientName);
		Session session = sendMail.getSession();
		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(frommail, name));
		message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
		message.setSubject(approvalMailSubject + eventId);
		message.setContent(getMessageContentbyTemplateName(approvalMailTemplate),
				EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
		transport.send(message);
		transport.close();
	}*/

}
