package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.Participant;

import com.sun.mail.smtp.SMTPMessage;

@Component
public class SendMail {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);

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

	public void SendConfirmationMailToParticipant(Participant participant) {
		addParameter("NAME", participant.getFirstName() + participant.getLastName());
		Properties props = System.getProperties();
		/*
		 * props.put("mail.debug", "true"); props.put("mail.smtp.host",
		 * "test.local"); props.put("mail.smtp.ssl.enable", "true");
		 * props.put("mail.smtp.auth", "true");
		 * 
		 * Session session = Session.getDefaultInstance(props, new
		 * javax.mail.Authenticator() {
		 * 
		 * @Override protected PasswordAuthentication
		 * getPasswordAuthentication() { return new
		 * PasswordAuthentication("receiver1openerp@test.local", "Rec1@123"); }
		 * });
		 */

		try {
			/*
			 * SMTPMessage message = new SMTPMessage(session);
			 * message.setFrom(new
			 * InternetAddress("receiver1openerp@test.local"));
			 * message.setRecipients(Message.RecipientType.TO,
			 * InternetAddress.parse(participant.getEmail()));
			 */
			Session session = Session.getDefaultInstance(props);
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress("heartfulness.org"));

			message.setSubject("Test Mail Subject");
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			Template template = velocityEngine.getTemplate("templates" + "/MailConfirmationTemplate.vm");
			StringWriter stringWriter = new StringWriter();
			template.merge(getParameter(), stringWriter);
			message.setContent(stringWriter.toString(), "text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());

			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			int returnOption = message.getReturnOption();

			System.out.println(returnOption);
			Transport.send(message);
			System.out.println("sent");

		} catch (MessagingException e) {
			LOGGER.error("Sending Mail Failed : {} ", participant.getEmail());
			throw new RuntimeException(e);

		}
	}

}
