package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.repository.MailLogRepository;
import com.sun.mail.smtp.SMTPMessage;

/**
 * This class is to hold the Mailing functionality.
 * 
 * @author Gopinath
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.mail.api.properties", ignoreUnknownFields = true, prefix = "mail.api")
public class SendNotificationMailToCoordinators {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendNotificationMailToCoordinators.class);
	
	private String username;
	private String password;
	private String hostname;
	private String port;
	private String name;
	private String frommail;
	private String eventfollowuptemplatename;
	private String eventfollowupsubject;
	
	@Autowired
	private MailLogRepository mailLogRepository;

	@Autowired
	Environment env;

	public String getUsername() {
		return username;
	}
	
	public String getFrommail() {
		return frommail;
	}

	public void setFrommail(String frommail) {
		this.frommail = frommail;
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
	
	public String getEventfollowuptemplatename() {
		return eventfollowuptemplatename;
	}

	public void setEventfollowuptemplatename(String eventfollowuptemplatename) {
		this.eventfollowuptemplatename = eventfollowuptemplatename;
	}

	public String getEventfollowupsubject() {
		return eventfollowupsubject;
	}

	public void setEventfollowupsubject(String eventfollowupsubject) {
		this.eventfollowupsubject = eventfollowupsubject;
	}

	private VelocityEngine velocityEngine = new VelocityEngine();

	private VelocityContext context;

	public SendNotificationMailToCoordinators() {
		context = new VelocityContext();
	}

	public void addParameter(String name, String value) {
		this.context.put(name, value);
	}

	public VelocityContext getParameter() {
		return this.context;
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
	 * Method to send email to coordinator with the programs/events details
	 * 
	 * @param coordinatorEmail
	 * @param programList
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public void sendNotificationMailToCoordinators(String coordinatorName, String coordinatorEmail,	List<Program> programList) throws MessagingException, UnsupportedEncodingException {
		
			Session session = getSession();
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			addParameter(EmailLogConstants.COORDINATOR_NAME_PARAMETER, coordinatorName != null ? coordinatorName: "Friend");

			SimpleDateFormat outputsdf = new SimpleDateFormat(ExpressionConstants.MAIL_DATE_FORMAT);
			StringBuilder sb = new StringBuilder();
			if (!programList.isEmpty()) {
				
				/*sb.append("<p>The following events need follow up ");
				sb.append(programList.get(0).getProgramZone() != null ? "for the Zone: "+programList.get(0).getProgramZone() : ":");
				sb.append("</p>");*/
				
				sb.append("<table border=\"1\" style=\"width: 75%;border-collapse: collapse;\">");
				sb.append("<tr>");
				sb.append("<td  align=middle><b>S.No</b></td>");
				sb.append("<td  align=middle><b>Batch Id</b></td>");
				sb.append("<td  align=middle><b>Connect Type</b></td>");
				sb.append("<td  align=middle><b>Program Start Date</b></td>");
				sb.append("<td  align=middle><b>Coordinator Name</b></td>");
				sb.append("<td  align=middle><b>Coordinator Email</b></td>");
				sb.append("<td  align=middle><b>Coordinator Abhiyasi Id</b></td>");
				sb.append("<td  align=middle><b>Jira Issue Number</b></td>");
				sb.append("</tr>");
				int serialNo = 1;
				for (Program program : programList) {
					sb.append("<tr><td>");
					sb.append(serialNo++);
					sb.append("</td><td>");
					sb.append(program.getAutoGeneratedEventId() != null ? "<a href=\""+SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL+"?id="+program.getAutoGeneratedEventId()+"\">"+program.getAutoGeneratedEventId()+"</a>" : "");
					sb.append("</td><td>");
					sb.append(program.getProgramChannel() != null ? program.getProgramChannel() : "");
					sb.append("</td><td>");
					sb.append(program.getProgramStartDate() != null ? outputsdf.format(program.getProgramStartDate()) : "");
					sb.append("</td><td>");
					sb.append(program.getCoordinatorName() != null ? program.getCoordinatorName() : "");
					sb.append("</td><td>");
					sb.append(program.getCoordinatorEmail() != null ? program.getCoordinatorEmail() : "");
					sb.append("</td><td>");
					sb.append(program.getAbyasiRefNo() != null ? program.getAbyasiRefNo() : "");
					sb.append("</td></td>");
					sb.append(program.getJiraIssueNumber() != null ? outputsdf.format(program.getJiraIssueNumber()) : "");
					sb.append("</td></tr>");
				}
				sb.append("</table>");
			}
			
			addParameter(EmailLogConstants.PROGRAM_DETAILS_PARAMETER, sb.toString());
			
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(coordinatorEmail));
			message.setSubject(eventfollowupsubject);
			message.setContent(getMessageContentbyTemplateName(eventfollowuptemplatename),EmailLogConstants.MAIL_CONTENT_TYPE_TEXT_HTML);
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.send(message);
			transport.close();
			
			PMPMailLog pmpMailLog = new PMPMailLog("0", coordinatorEmail, 
					EmailLogConstants.NOTIFICATION_MAIL_TO_ZONE_CENTER_COORDINATOR,EmailLogConstants.STATUS_SUCCESS, null);
			mailLogRepository.createMailLog(pmpMailLog);
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
	
}