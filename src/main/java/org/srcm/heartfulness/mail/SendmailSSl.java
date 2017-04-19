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
import javax.mail.internet.InternetAddress;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.srcm.heartfulness.constants.EmailLogConstants;

import com.sun.mail.smtp.SMTPMessage;

@RequestMapping
public class SendmailSSl {

	@RequestMapping(value = "/smtpmail", method = RequestMethod.POST)
	public String sendMail() throws ParseException, IOException {
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

		try {
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress("heartfulness.pmp@gmail.com"));
						
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));

			message.setSubject("Testing Subject");
			message.setText("HI this is test java mail ");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());

			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			int returnOption = message.getReturnOption();

			System.out.println(returnOption);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();

		} catch (MessagingException e) {
			throw new RuntimeException(e);

		}
		return "sent";
	}
	
	
	@RequestMapping(value = "/prodmail", method = RequestMethod.POST)
	public String sendProdMail() throws ParseException, IOException {
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

		try {
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress("heartfulness.pmp@gmail.com"));
						
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("himasree.vemuru@htcindia.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("koustav.dipak@htcindia.com"));

			message.setSubject("Testing Subject");
			message.setText("HI this is test java with prod properties ");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());

			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			int returnOption = message.getReturnOption();

			System.out.println(returnOption);
			Transport transport =session.getTransport(EmailLogConstants.MAIL_SMTP_PROPERTY);
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();

		} catch (MessagingException e) {
			throw new RuntimeException(e);

		}
		return "sent";
	}
}