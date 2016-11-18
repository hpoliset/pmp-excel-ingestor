package org.srcm.heartfulness.mail;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.CoordinatorAccessControlConstants;
import org.srcm.heartfulness.constants.EmailLogConstants;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.model.CoordinatorAccessControlEmail;
import org.srcm.heartfulness.model.CoordinatorEmail;
import org.srcm.heartfulness.model.PMPMailLog;
import org.srcm.heartfulness.repository.MailLogRepository;
import org.srcm.heartfulness.util.StackTraceUtils;

import com.sun.mail.smtp.SMTPMessage;

/**
 * @author Koustav Dutta
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:dev.cac.mail.properties", ignoreUnknownFields = false, prefix = "mail.cac")
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
	private String mailtemplatetocreateprofileandaccessdashboard;
	private String mailsubjecttocreateprofileandaccessdashboard;
	private String coordinatormailtemplatetocreateaccount;
	private String coordinatormailsubjecttocreateaccount;
	private String coordinatormailtemplatetoaccessdashbrd;
	private String coordinatormailsubjecttoaccessdashbrd;
	private String coordinatormailforupdatingevent;
	private String coordinatormailforupdatingeventsubject;

	@Autowired
	private MailLogRepository mailLogRepository;

	private VelocityEngine velocityEngine = new VelocityEngine();

	private VelocityContext context;
	
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

	public String getMailtemplatetocreateprofileandaccessdashboard() {
		return mailtemplatetocreateprofileandaccessdashboard;
	}

	public void setMailtemplatetocreateprofileandaccessdashboard(String mailtemplatetocreateprofileandaccessdashboard) {
		this.mailtemplatetocreateprofileandaccessdashboard = mailtemplatetocreateprofileandaccessdashboard;
	}

	public String getMailsubjecttocreateprofileandaccessdashboard() {
		return mailsubjecttocreateprofileandaccessdashboard;
	}

	public void setMailsubjecttocreateprofileandaccessdashboard(String mailsubjecttocreateprofileandaccessdashboard) {
		this.mailsubjecttocreateprofileandaccessdashboard = mailsubjecttocreateprofileandaccessdashboard;
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

	public String getCoordinatormailtemplatetoaccessdashbrd() {
		return coordinatormailtemplatetoaccessdashbrd;
	}

	public void setCoordinatormailtemplatetoaccessdashbrd(String coordinatormailtemplatetoaccessdashbrd) {
		this.coordinatormailtemplatetoaccessdashbrd = coordinatormailtemplatetoaccessdashbrd;
	}

	public String getCoordinatormailsubjecttoaccessdashbrd() {
		return coordinatormailsubjecttoaccessdashbrd;
	}

	public void setCoordinatormailsubjecttoaccessdashbrd(String coordinatormailsubjecttoaccessdashbrd) {
		this.coordinatormailsubjecttoaccessdashbrd = coordinatormailsubjecttoaccessdashbrd;
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

	/**
	 * To get the email content as string from the vm template.
	 * 
	 * @param templateName
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
				if (name[i].length() > 2 && !name[i].equalsIgnoreCase("mrs") && !name[i].equalsIgnoreCase("smt")) {
					return name[i].substring(0, 1).toUpperCase() + name[i].substring(1).toLowerCase();
				}
			}
		}
		return printName;
	}

	public void sendMailToPreceptorToUpdateCoordinatorEmailID(
			CoordinatorAccessControlEmail coordinatorAccessControlEmail) throws AddressException, MessagingException,
			UnsupportedEncodingException, ParseException {
		try {

			Properties props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", hostname);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			addParameter("PRECEPTOR_NAME", getName(coordinatorAccessControlEmail.getPreceptorName()));
			addParameter("UPDATE_EVENT_LINK", SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL + "?id="
					+ coordinatorAccessControlEmail.getEventID());
			addParameter("EVENT_NAME", coordinatorAccessControlEmail.getEventName());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat outputsdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date pgrmCreateDate = inputsdf.parse(coordinatorAccessControlEmail.getProgramCreateDate());
			addParameter("PROGRAM_CREATE_DATE", outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(coordinatorAccessControlEmail.getPreceptorEmailId()));
			message.setSubject(emptycoordinatoremailidsubject + " - " + coordinatorAccessControlEmail.getEventName());
			message.setContent(getMessageContentbyTemplateName(emptycoordinatoremailidtemplate), "text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.info("Mail sent successfully to Coordinator : {} ",
					coordinatorAccessControlEmail.getPreceptorEmailId());

			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID,
						EmailLogConstants.STATUS_SUCCESS, null);
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (Exception e) {
			LOGGER.error("Exception : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.PRECEPTOR_EMAIL_TO_UPDATE_COORDINATOR_EMAIL_ID,
						EmailLogConstants.STATUS_FAILED, StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		}

	}

	public void sendMailToPreceptorandCoordinatorToCreateProfileAndAccessDashboard(
			CoordinatorAccessControlEmail coordinatorAccessControlEmail) throws AddressException, MessagingException,
			UnsupportedEncodingException, ParseException {
		try {
			Properties props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", hostname);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			addParameter("PRECEPTOR_NAME", getName(coordinatorAccessControlEmail.getPreceptorName()));
			addParameter("UPDATE_EVENT_LINK", SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL + "?id="
					+ coordinatorAccessControlEmail.getEventID());
			addParameter("CREATE_PROFILE_LINK", CoordinatorAccessControlConstants.HEARTFULNESS_CREATE_PROFILE_URL);
			addParameter("EVENT_NAME", coordinatorAccessControlEmail.getEventName());
			addParameter("COORDINATOR_EMAILID", coordinatorAccessControlEmail.getCoordinatorEmail());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat outputsdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date pgrmCreateDate = inputsdf.parse(coordinatorAccessControlEmail.getProgramCreateDate());
			addParameter("PROGRAM_CREATE_DATE", outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(coordinatorAccessControlEmail.getCoordinatorEmail()));
			message.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(coordinatorAccessControlEmail.getPreceptorEmailId()));
			message.setSubject(mailsubjecttocreateprofileandaccessdashboard + " - "
					+ coordinatorAccessControlEmail.getEventName());
			message.setContent(getMessageContentbyTemplateName(mailtemplatetocreateprofileandaccessdashboard),
					"text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.info("Mail sent successfully to Coordinator : {} ",
					coordinatorAccessControlEmail.getCoordinatorEmail());
			LOGGER.info("Mail sent successfully to Coordinator : {} ",
					coordinatorAccessControlEmail.getPreceptorEmailId());

			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.EMAIL_CREATE_PROFILE_AND_DASHBOARD_LINK, EmailLogConstants.STATUS_SUCCESS,
						null);
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (MessagingException e) {
			e.printStackTrace();
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.EMAIL_CREATE_PROFILE_AND_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				e.printStackTrace();
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.EMAIL_CREATE_PROFILE_AND_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.EMAIL_CREATE_PROFILE_AND_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (Exception e) {
			LOGGER.error("Exception : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.EMAIL_CREATE_PROFILE_AND_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		}
	}

	public void sendMailToCoordinatorToUpdatePreceptorID(CoordinatorEmail coordinatorAccessControlEmail)
			throws AddressException, MessagingException, UnsupportedEncodingException, ParseException {
		try {
			Properties props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", hostname);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));

			addParameter("COORDINATOR_NAME", getName(coordinatorAccessControlEmail.getCoordinatorName()));
			addParameter("UPDATE_EVENT_LINK", SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL + "?id="
					+ coordinatorAccessControlEmail.getEventID());
			addParameter("EVENT_NAME", coordinatorAccessControlEmail.getEventName());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat outputsdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date pgrmCreateDate = inputsdf.parse(coordinatorAccessControlEmail.getProgramCreateDate());
			addParameter("PROGRAM_CREATE_DATE", outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(coordinatorAccessControlEmail.getCoordinatorEmail()));
			message.setSubject(coordinatormailforupdatingeventsubject + " - "
					+ coordinatorAccessControlEmail.getEventName());
			message.setContent(getMessageContentbyTemplateName(coordinatormailforupdatingevent), "text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.info("Mail sent successfully to Coordinator : {} ",
					coordinatorAccessControlEmail.getCoordinatorEmail());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID, EmailLogConstants.STATUS_SUCCESS,
						null);
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinatorAccessControlEmail.getProgramId(),
						coordinatorAccessControlEmail.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_TO_UPDATE_PRECEPTOR_ID, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		}
	}

	public void sendMailToCoordinatorWithLinktoAccessDashboard(CoordinatorAccessControlEmail coordinator) {
		try {
			Properties props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", hostname);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));
			addParameter("COORDINATOR_NAME", getName(coordinator.getCoordinatorName()));
			addParameter("UPDATE_EVENT_LINK",
					SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL + "?id=" + coordinator.getEventID());
			addParameter("EVENT_NAME", coordinator.getEventName());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat outputsdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date pgrmCreateDate = inputsdf.parse(coordinator.getProgramCreateDate());
			addParameter("PROGRAM_CREATE_DATE", outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(coordinator.getCoordinatorEmail()));
			message.setSubject(coordinatormailsubjecttoaccessdashbrd + " - " + coordinator.getEventName());
			message.setContent(getMessageContentbyTemplateName(coordinatormailtemplatetoaccessdashbrd), "text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.info("Mail sent successfully to Coordinator : {} ", coordinator.getCoordinatorEmail());

			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_DASHBOARD_LINK, EmailLogConstants.STATUS_SUCCESS, null);
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (Exception e) {
			LOGGER.error("Exception : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_DASHBOARD_LINK, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		}

	}

	public void sendMailToCoordinatorWithLinktoCreateProfile(CoordinatorAccessControlEmail coordinator) {
		try {
			Properties props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.host", hostname);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			SMTPMessage message = new SMTPMessage(session);
			message.setFrom(new InternetAddress(frommail, name));

			addParameter("COORDINATOR_NAME", getName(coordinator.getCoordinatorName()));
			addParameter("UPDATE_EVENT_LINK",
					SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL + "?id=" + coordinator.getEventID());
			addParameter("CREATE_PROFILE_LINK", CoordinatorAccessControlConstants.HEARTFULNESS_CREATE_PROFILE_URL);
			addParameter("EVENT_NAME", coordinator.getEventName());
			SimpleDateFormat inputsdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat outputsdf = new SimpleDateFormat("dd-MMM-yyyy");
			Date pgrmCreateDate = inputsdf.parse(coordinator.getProgramCreateDate());
			addParameter("PROGRAM_CREATE_DATE", outputsdf.format(pgrmCreateDate));
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(coordinator.getCoordinatorEmail()));
			message.setSubject(coordinatormailsubjecttocreateaccount + " - " + coordinator.getEventName());
			message.setContent(getMessageContentbyTemplateName(coordinatormailtemplatetocreateaccount), "text/html");
			message.setAllow8bitMIME(true);
			message.setSentDate(new Date());
			message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
			Transport.send(message);
			LOGGER.info("Mail sent successfully to Coordinator : {} ", coordinator.getCoordinatorEmail());

			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(String.valueOf(0), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_SUCCESS, null);
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (MessagingException e) {
			LOGGER.error("MessagingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (ParseException e) {
			LOGGER.error("ParseException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		} catch (Exception e) {
			LOGGER.error("Exception : Sending Mail Failed : {} " + e.getMessage());
			try {
				LOGGER.info("START        :Inserting mail log details in table");
				PMPMailLog pmpMailLog = new PMPMailLog(coordinator.getProgramId(), coordinator.getCoordinatorEmail(),
						EmailLogConstants.COORDINATOR_EMAIL_CREATE_PROFILE, EmailLogConstants.STATUS_FAILED,
						StackTraceUtils.convertStackTracetoString(e));
				mailLogRepository.createMailLog(pmpMailLog);
				LOGGER.info("END        :Completed inserting mail log details in table");
			} catch (Exception ex) {
				LOGGER.error("END        :Exception while inserting mail log details in table");
			}
		}

	}
	
}
