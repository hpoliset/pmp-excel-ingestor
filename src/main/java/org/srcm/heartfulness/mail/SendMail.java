package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
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
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.Participant;

import com.sun.mail.smtp.SMTPMessage;

/**
 * This class is to hold the Mailing functionality.
 * 
 * @author HimaSree
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:mail.api.properties", ignoreUnknownFields = false, prefix = "mail.api")
public class SendMail {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);

	private String username;
	private String password;
	private String hostname;
	private String subject;
	private String defaultname;
	private String confirmationlink;
	private String unsubscribelink;
	private String smstemplatename;
	private String exceltemplatename;
	private String onlinetemplatename;
	private String noparticipantstemplatename;
	private String participantstemplatename;

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

	@Autowired
	private AESEncryptDecrypt aesEncryptDecrypt;

	@Autowired
	Environment env;

	/**
	 * Method to send confirmation mail to the newly registered participants
	 * through Excel upload and SMS.
	 * 
	 * @param participant
	 */
	public void SendConfirmationMailToParticipant(Participant participant) {
		if (null != participant.getPrintName() && !participant.getPrintName().isEmpty()) {
			addParameter("NAME", getName(participant.getPrintName()));
			addParameter("LINK",
					unsubscribelink + "?email=" + participant.getEmail() + "&name=" + participant.getPrintName());
		} else {
			addParameter("NAME", defaultname);
			addParameter("LINK", unsubscribelink + "?email=" + participant.getEmail() + "&name=" + "");
		}
		List<String> toEmailIDs = new ArrayList<String>();
		toEmailIDs.add(participant.getEmail());
		try {
			sendMail(toEmailIDs, new ArrayList<String>(), getWelcomeMailContent(participant.getCreatedSource()));
			LOGGER.debug("Mail sent successfully : {} ", participant.getEmail());
		} catch (MessagingException e) {
			LOGGER.error("Sending Mail Failed : {} ", participant.getEmail());
			throw new RuntimeException(e);

		}
	}

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
			// Session session = Session.getDefaultInstance(props);
			Properties props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", hostname);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			/*Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});*/
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(username, password);
			    }
			});
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(username));
			for (String toId : toIds) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toId));
			}
			for (String ccId : ccIds) {
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccId));
			}
			message.setSubject(subject);
			if (count == 0) {
				message.setContent(getMessageContentbyTemplateName(noparticipantstemplatename), "text/html");
			} else {
				addParameter("COUNT", String.valueOf(count));
				message.setContent(getMessageContentbyTemplateName(participantstemplatename), "text/html");
			}
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.debug("Mail sent successfully : {} ");

		} catch (MessagingException e) {
			LOGGER.error("Sending Mail Failed : {} " + e.getMessage());
			throw new RuntimeException(e);

		}
	}

	/**
	 * Method to get the personalized name of the given participant.
	 * 
	 * @param printName
	 * @return
	 */
	private String getName(String printName) {
		printName = printName.replace(".", " ");
		String[] name = printName.split(" ");
		if (name.length > 0) {
			for (int i = 0; i < name.length; i++) {
				if (name[i].length() > 2) {
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
	private String getWelcomeMailContent(String createdSource) {
		Template velocityTemplate = null;
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		if ("Excel".equalsIgnoreCase(createdSource)) {
			velocityTemplate = velocityEngine.getTemplate(exceltemplatename);
		} else if ("SMS".equalsIgnoreCase(createdSource)) {
			velocityTemplate = velocityEngine.getTemplate(smstemplatename);
		}
		StringWriter stringWriter = new StringWriter();
		velocityTemplate.merge(getParameter(), stringWriter);
		return stringWriter.toString();
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
	 */
	public void sendMail(List<String> toEmailIDs, List<String> ccEmailIDs, String messageContent)
			throws MessagingException {
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props);
		SMTPMessage message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(username));
		for (String toemailID : toEmailIDs) {
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toemailID));
		}
		for (String ccemailID : ccEmailIDs) {
			message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccemailID));
		}
		message.setSubject(subject);
		message.setContent(messageContent, "text/html");
		message.setAllow8bitMIME(true);
		message.setSentDate(new Date());
		message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		Transport.send(message);

	}

	/**
	 * Method to send confirm subscription mail to the online users.
	 * 
	 * @param mail
	 * @param name
	 */
	public void sendConfirmSubscriptionMail(String mail, String name) {
		if (null != name && !name.isEmpty()) {
			addParameter("NAME", getName(name));
		} else {
			addParameter("NAME", defaultname);
		}
		addParameter(
				"CONFIRMATION_LINK",
				confirmationlink + "?id="
						+ aesEncryptDecrypt.encrypt(mail, env.getProperty(PMPConstants.SECURITY_TOKEN_KEY)));
		List<String> toEmailIDs = new ArrayList<String>();
		toEmailIDs.add(mail);
		try {
			sendMail(toEmailIDs, new ArrayList<String>(), getMessageContentbyTemplateName(onlinetemplatename));
			LOGGER.debug("Mail sent successfully : {} ", mail);
		} catch (MessagingException e) {
			LOGGER.error("Sending Mail Failed : {} ", mail);
			throw new RuntimeException(e);

		}
	}
}
