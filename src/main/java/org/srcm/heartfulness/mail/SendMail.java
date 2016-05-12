package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
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
import org.srcm.heartfulness.constants.PMPConstants;
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
		if (null != participant.getPrintName() && !participant.getPrintName().isEmpty()) {
			addParameter("NAME",getName( participant.getPrintName()));
			addParameter("LINK", PMPConstants.UNSUBSCRIBE_LINK+"?email="+participant.getEmail()+"&name="+participant.getPrintName());
		} else {
			addParameter("NAME", "Sir/Madam");
			addParameter("LINK", PMPConstants.UNSUBSCRIBE_LINK+"?email="+participant.getEmail()+"&name="+"");
		}
	
		Properties props = System.getProperties();
		try {
			Session session = Session.getDefaultInstance(props);
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress("heartfulness.org"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(participant.getEmail()));
			message.setSubject("Heartfulness confirmation mail - Test");
			message.setContent(getWelcomeMailContent(participant.getCreatedSource()), "text/html");
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
	 * to get the email content as string from the vm template
	 * 
	 * @param welcomemail
	 * @return
	 */
	private String getWelcomeMailContent(String createdSource){
		Template template = null;
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		if("Excel".equalsIgnoreCase(createdSource)){
			template = velocityEngine.getTemplate("templates"+"/MailConfirmationTemplate.vm");
		}else if("SMS".equalsIgnoreCase(createdSource)){
			template = velocityEngine.getTemplate("templates"+"/MailConfirmationTemplateForSMS.vm");
		}
		StringWriter stringWriter = new StringWriter();
		template.merge(getParameter(), stringWriter);
		return stringWriter.toString();
	}
}
