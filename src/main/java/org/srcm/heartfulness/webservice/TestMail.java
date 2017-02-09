/**
 * 
 */
package org.srcm.heartfulness.webservice;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.ReadOnlyFolderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.StoreClosedException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ParseException;
import javax.mail.search.SearchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.sun.mail.smtp.SMTPMessage;

/**
 * @author koustavd
 *
 */
@RestController
@RequestMapping("/api/mail")
public class TestMail {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestMail.class);

	@RequestMapping(value = "/testexception", method = RequestMethod.GET)
	public ResponseEntity<?> mailException(@RequestParam(name = "mailId") String mailID ) {

		LOGGER.info("Mail api triggered - sending mail to "+mailID);

		try {
			
			Properties props = System.getProperties();
			props.put(EmailLogConstants.MAIL_DEBUG_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
			props.put(EmailLogConstants.MAIL_SMTP_HOST_PROPERTY, "smtp.gmail.com");
			props.put(EmailLogConstants.MAIL_SMTP_PORT_PROPERTY, 587);
			props.put(EmailLogConstants.MAIL_SMTP_SSL_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
			props.put(EmailLogConstants.MAIL_SMTP_AUTH_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);
			props.put(EmailLogConstants.MAIL_SMTP_STARTTLS_PROPERTY, EmailLogConstants.MAIL_PROPERTY_TRUE);

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("heartfulness.pmp@gmail.com", "123Welcome");
				}
			});
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress("heartfulness.pmp@gmail.com", "heartfulness.pmp"));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailID));
			message.setSubject("Test mail from PMP");
			message.setContent("This is a test mail from Heartfulness PMP.Please ignore this mail.", "text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.info("Succssfully sent mail to "+mailID);
			return new ResponseEntity<String>("Succssfully sent mail to "+mailID,HttpStatus.OK);

		} catch (AuthenticationFailedException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (FolderClosedException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (FolderNotFoundException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (IllegalWriteException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (MessageRemovedException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (MethodNotSupportedException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (NoSuchProviderException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		}  catch (ReadOnlyFolderException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (SearchException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (SendFailedException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (StoreClosedException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (AddressException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (ParseException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (UnsupportedEncodingException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		} catch (MessagingException e) {
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(e),HttpStatus.OK);
		}
		
	}
}
