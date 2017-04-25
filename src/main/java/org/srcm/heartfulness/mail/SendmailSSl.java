package org.srcm.heartfulness.mail;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.smtp.SMTPTransport;

@RestController
public class SendmailSSl {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendmailSSl.class);

	@RequestMapping(value = "/smtpmail", method = RequestMethod.POST)
	public String sendMail() throws ParseException, IOException {
		LOGGER.info(" smtpmail method called");
		Properties props = System.getProperties();
		props.put(EmailLogConstants.MAIL_DEBUG_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_HOST_PROPERTY, "smtp.gmail.com");
		props.put(EmailLogConstants.MAIL_SMTP_PORT_PROPERTY, 587);
		props.put(EmailLogConstants.MAIL_SMTP_SSL_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_AUTH_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_STARTTLS_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_PROTOCOL_PROPERTY, EmailLogConstants.MAIL_SMTP_PROPERTY);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("heartfulness.pmp@gmail.com", "123Welcome");
			}
		});
		for (int i = 1; i < 21; i++) {
			LOGGER.info(" smtpmail method called loop: {}" , i);
			try {
				SMTPMessage message = new SMTPMessage(session);
				message.setFrom(new InternetAddress("heartfulness.pmp@gmail.com"));

				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));

				message.setSubject("Testing Subject");
				message.setText("HI this is test java mail ");
				// message.setAllow8bitMIME(true);
				message.setSentDate(new Date());

				message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
				Transport transport = new SMTPTransport(session, new URLName("mail.htcindia.com"));
				transport.connect("mail.htcindia.com", "koustav.dipak@htcindia.com", "Kv9fHCRA");
				System.out.println(message.getAllRecipients());
				try {
					transport.sendMessage(message, message.getAllRecipients());
				} finally {
					transport.close();
				}
				// Transport.send(message);

			} catch (MessagingException e) {
				LOGGER.error(StackTraceUtils.convertStackTracetoString(e));

			} catch (Exception e) {
				LOGGER.error(StackTraceUtils.convertStackTracetoString(e));
			}
		}
		return "sent";
	}

	@RequestMapping(value = "/prodmail", method = RequestMethod.POST)
	public String sendProdMail() throws ParseException, IOException {
		LOGGER.info(" prodmail method called");
		Properties props = System.getProperties();
		props.put(EmailLogConstants.MAIL_DEBUG_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_HOST_PROPERTY, "sahajmarg.info");
		props.put(EmailLogConstants.MAIL_SMTP_PORT_PROPERTY, 25);
		props.put(EmailLogConstants.MAIL_SMTP_SSL_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_AUTH_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_STARTTLS_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
		props.put(EmailLogConstants.MAIL_SMTP_PROTOCOL_PROPERTY, EmailLogConstants.MAIL_SMTP_PROPERTY);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("heartfulness.newsletter@heartfulness.org", "send2all");
			}
		});

		for (int i = 1; i < 31; i++) {
			LOGGER.info(" prodmail method called loop: {}" , i);
			try {
				SMTPMessage message = new SMTPMessage(session);
				message.setFrom(new InternetAddress("heartfulness.newsletter@heartfulness.org"));

				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));

				message.setSubject("Testing prod Subject");
				message.setText("HI this is test java with prod properties ");
			//	message.setAllow8bitMIME(true);
				message.setSentDate(new Date());

				message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
				Transport transport = new SMTPTransport(session, new URLName( "sahajmarg.info"));
				transport.connect("sahajmarg.info","heartfulness.newsletter@heartfulness.org", "send2all");
				System.out.println(message.getAllRecipients());
				try {
					transport.sendMessage(message, message.getAllRecipients());
				} finally {
					transport.close();
				}

			} catch (MessagingException e) {
				LOGGER.error(StackTraceUtils.convertStackTracetoString(e));

			} catch (Exception e) {
				LOGGER.error(StackTraceUtils.convertStackTracetoString(e));
			}
		}
		return "completed";
	}
}