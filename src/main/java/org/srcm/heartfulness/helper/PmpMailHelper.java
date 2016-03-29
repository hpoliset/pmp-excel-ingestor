package org.srcm.heartfulness.helper;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.IntroductionDetails;
import org.srcm.heartfulness.model.User;

import com.sun.mail.smtp.SMTPMessage;

/**
 * 
 * @author himasreev
 *
 */
@Component
@ConfigurationProperties(locations = "classpath:mail.properties", ignoreUnknownFields = false, prefix = "pmp.mail")
public class PmpMailHelper {

	private String host;
	private String username;
	private String password;

	private String webmaster;
	private String meditate;
	private String nsn;
	private String jira;

	private String delhi;
	private String haryana;
	private String punjab;
	private String up;
	private String uk;
	private String mp;
	private String chattisghar;
	private String tamilnadu;
	private String karnataka;
	private String kerala;
	private String maharashtra;
	private String gujrat;
	private String rajasthan;
	private String telangana;
	private String seemandhra;
	private String westbengal;
	private String otherstates;
	private String welcomemailtemplate;
	private String welcomemailsubject;
	private String requestmailtemplate;
	private String requestmailsubject;
	
	boolean sendWithTemplate=false;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PmpMailHelper.class);

	//@Autowired
	private VelocityEngine velocityEngine=new VelocityEngine();

	private VelocityContext context;


	public PmpMailHelper() {
		context = new VelocityContext();
	}

	/**
	 * method to send email to HFN team with user details
	 * @param newUser
	 * @param introdet
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public void sendEmailtoHfnTeam(User newUser, IntroductionDetails introdet) throws AddressException, MessagingException {
		addParameter("NAME", newUser.getName());
		addParameter("EMAIL", newUser.getEmail());
		addParameter("MOBILE", newUser.getMobile());
		addParameter("CITY", newUser.getCity());
		addParameter("STATE", newUser.getState());
		addParameter("COUNTRY", newUser.getCountry());
		addParameter("MESSAGE", introdet.getMessage());
		List<String> toMailIds=new ArrayList<String>();
		List<String> ccMailIds=new ArrayList<String>();
		if(PMPConstants.COUNTRY_INDIA.equalsIgnoreCase(newUser.getCountry().toUpperCase())){
			ccMailIds.add(webmaster);
			ccMailIds.add(meditate);
			ccMailIds.add(nsn);
			ccMailIds.add(jira);
			switch (newUser.getState().toUpperCase()) {
			case (PMPConstants.STATE_DELHI):
				toMailIds.add(delhi);
			break;
			case PMPConstants.STATE_HARYANA:
				toMailIds.add(haryana);
				break;
			case PMPConstants.STATE_PUNJAB:
				toMailIds.add(punjab);
				break;
			case PMPConstants.STATE_UP:
				toMailIds.add(up);
				break;
			case PMPConstants.STATE_UK:
				toMailIds.add(uk);
				break;
			case PMPConstants.STATE_MP:
				toMailIds.add(mp);
				break;
			case PMPConstants.STATE_CHATTISGHAR:
				toMailIds.add(chattisghar);
				break;
			case PMPConstants.STATE_TAMILNADU:
				toMailIds.add(tamilnadu);
				break;
			case PMPConstants.STATE_KARNATAKA:
				toMailIds.add(karnataka);
				break;
			case PMPConstants.STATE_KERALA:
				toMailIds.add(kerala);
				break;
			case PMPConstants.STATE_MAHARASHTRA:
				toMailIds.add(maharashtra);
				break;
			case PMPConstants.STATE_GUJRAT:
				toMailIds.add(gujrat);
				break;
			case PMPConstants.STATE_RAJASTHAN:
				toMailIds.add(rajasthan);
				break;
			case PMPConstants.STATE_TELANGANA:
				toMailIds.add(telangana);
				break;
			case PMPConstants.STATE_SEEMANDHRA:
				toMailIds.add(seemandhra);
				break;
			case PMPConstants.STATE_WESTBENGAL:
				toMailIds.add(westbengal);
				break;
			default: toMailIds.add(nsn);
			ccMailIds.remove(nsn);
			break;
			}
			sendMail(toMailIds,ccMailIds,PMPConstants.REGISTRATION);
		}else{
			toMailIds.add(webmaster);
			toMailIds.add(meditate);
			ccMailIds.add(jira);
			sendMail(toMailIds,ccMailIds,PMPConstants.REGISTRATION);

		}
	}
	
	/**
	 * method to send email to HFN members with Introduction details
	 * @param toMailIds
	 * @param ccMailIds
	 * @param newUser
	 * @param introdet
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	private void sendMail(List<String> toMailIds, List<String> ccMailIds,String recieverType) throws AddressException, MessagingException {
		
		LOGGER.debug("Trying to send mail to {} ",recieverType);
		if(toMailIds.size()>0){
			
			//  String from = "heartfulness.org";
			  
			Properties props = System.getProperties();
			setProperties(props);
			/*Session session =Session.getDefaultInstance(props,new javax.mail.Authenticator(){
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{				
					return new PasswordAuthentication(username,password);
				}
			});*/
			Session session=Session.getDefaultInstance(props);
				SMTPMessage message = new SMTPMessage(session);
				message.setFrom(new InternetAddress(username));
				for (String toMailId : toMailIds) {
					message.addRecipients(Message.RecipientType.TO,InternetAddress.parse(toMailId));
				}
				for (String ccMailId : ccMailIds) {
					message.addRecipients(Message.RecipientType.CC,InternetAddress.parse(ccMailId));
				}
				message.setSubject(PMPConstants.SEEKER.equalsIgnoreCase(recieverType)?welcomemailsubject:requestmailsubject);
				URL url = this.getClass().getResource("/org/srcm/heartfulness/resource/mail");
				File file=new File(url.getFile());
				velocityEngine = new VelocityEngine();
				velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
				velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, file.getAbsolutePath());
				velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
				velocityEngine.init();
				if ("seeker".equalsIgnoreCase(recieverType)) {
					message.setContent(getWelcomeMailContent(requestmailtemplate),"text/html");
				}else{
					if("welcometemplate".equalsIgnoreCase(welcomemailtemplate)){
						message.setContent(getWelcomeMailContent(welcomemailtemplate),"text/html");
					}else{
						message.setContent(getWelcomeMailtemplate(welcomemailtemplate));
					}
				}
				message.setAllow8bitMIME(true);
				message.setSentDate(new Date());
				message.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
				message.getReturnOption();
				//int returnOption = message.getReturnOption();
				Transport.send(message);
				LOGGER.debug("Mail Sent successfully to {} ",recieverType);
		}
	}
	
	/**
	 * method to send welcome email to the seeker
	 * @param email
	 * @throws MessagingException 
	 */
	public void sendEmailtoSeeker(String email) throws MessagingException,AuthenticationFailedException,AddressException{
		List<String> toMailIds=new ArrayList<String>();
		toMailIds.add(email);
		sendMail(toMailIds,null,PMPConstants.SEEKER);
	}
	
	public void addParameter(String name, String value ){
		this.context.put(name, value);
	}

	public VelocityContext getParameter() {
		return this.context;
	}

	/**
	 * method to set the message content for the welcome mail  (development use)
	 * @param welcomemail 
	 * @return
	 * @throws MessagingException
	 */
	private MimeMultipart getWelcomeMailtemplate(String welcomemail) throws MessagingException {
		MimeMultipart multipart = new MimeMultipart("related");
		//(the html)
		BodyPart messageBodyPart = new MimeBodyPart();
		String htmlText=getWelcomeMailContent(welcomemail);
		
		messageBodyPart.setContent(htmlText, "text/html");
		multipart.addBodyPart(messageBodyPart);
		
		//(images)
		//setImages(messageBodyPart,multipart,"http://10.1.29.23:7080/pmp/images/banner.jpg","<logo>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\mail_logo.jpg","<logo>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\banner.jpg","<banner>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\instagram.jpg","<instagram>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\icon1.jpg","<icon1>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\icon2.jpg","<icon2>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\icon4.jpg","<icon4>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\linkedin.jpg","<linkedin>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\rss.jpg","<rss>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\twitter.jpg","<twitter>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\youtube.jpg","<youtube>");
		setImages(messageBodyPart,multipart,"D:\\Workspace\\PMP\\pmp-security-impl-svn\\src\\main\\webapp\\images\\facebook.jpg","<facebook>");
		return multipart;
	}
	
	/**
	 * to get the email content as string from the vm template
	 * @param welcomemail
	 * @return
	 */
	private String getWelcomeMailContent(String welcomemail){
		Template template = velocityEngine.getTemplate("/"+welcomemail+".vm");
		StringWriter stringWriter = new StringWriter();
		template.merge(getParameter(), stringWriter);
		return stringWriter.toString();
	}

	/**
	 * method to set the mail properties
	 * @param props
	 */
	private void setProperties(Properties props) {
		props.put("mail.debug", "true");
		props.put("mail.smtp.host", host);
		//props.put("mail.smtp.ssl.enable", "true");
		//props.put("mail.smtp.auth", "false");
	}

	/**
	 * method to set the images in the html content of welcome mail
	 * @param messageBodyPart
	 * @param multipart
	 * @param filePath
	 * @param contentId
	 */
	private static void setImages(BodyPart messageBodyPart, MimeMultipart multipart, String filePath, String contentId){
		messageBodyPart = new MimeBodyPart();
		try {
			DataSource banner = new FileDataSource(filePath);
			messageBodyPart.setDataHandler(new DataHandler(banner));
			messageBodyPart.setHeader("Content-ID",contentId);
			multipart.addBodyPart(messageBodyPart);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getWebmaster() {
		return webmaster;
	}

	public void setWebmaster(String webmaster) {
		this.webmaster = webmaster;
	}

	public String getMeditate() {
		return meditate;
	}

	public void setMeditate(String meditate) {
		this.meditate = meditate;
	}

	public String getNsn() {
		return nsn;
	}

	public void setNsn(String nsn) {
		this.nsn = nsn;
	}

	public String getJira() {
		return jira;
	}

	public void setJira(String jira) {
		this.jira = jira;
	}

	public String getDelhi() {
		return delhi;
	}

	public void setDelhi(String delhi) {
		this.delhi = delhi;
	}

	public String getHaryana() {
		return haryana;
	}

	public void setHaryana(String haryana) {
		this.haryana = haryana;
	}

	public String getPunjab() {
		return punjab;
	}

	public void setPunjab(String punjab) {
		this.punjab = punjab;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getUk() {
		return uk;
	}

	public void setUk(String uk) {
		this.uk = uk;
	}

	public String getMp() {
		return mp;
	}

	public void setMp(String mp) {
		this.mp = mp;
	}

	public String getChattisghar() {
		return chattisghar;
	}

	public void setChattisghar(String chattisghar) {
		this.chattisghar = chattisghar;
	}

	public String getTamilnadu() {
		return tamilnadu;
	}

	public void setTamilnadu(String tamilnadu) {
		this.tamilnadu = tamilnadu;
	}

	public String getKarnataka() {
		return karnataka;
	}

	public void setKarnataka(String karnataka) {
		this.karnataka = karnataka;
	}

	public String getKerala() {
		return kerala;
	}

	public void setKerala(String kerala) {
		this.kerala = kerala;
	}

	public String getMaharashtra() {
		return maharashtra;
	}

	public void setMaharashtra(String maharashtra) {
		this.maharashtra = maharashtra;
	}

	public String getGujrat() {
		return gujrat;
	}

	public void setGujrat(String gujrat) {
		this.gujrat = gujrat;
	}

	public String getRajasthan() {
		return rajasthan;
	}

	public void setRajasthan(String rajasthan) {
		this.rajasthan = rajasthan;
	}

	public String getTelangana() {
		return telangana;
	}

	public void setTelangana(String telangana) {
		this.telangana = telangana;
	}

	public String getSeemandhra() {
		return seemandhra;
	}

	public void setSeemandhra(String seemandhra) {
		this.seemandhra = seemandhra;
	}

	public String getWestbengal() {
		return westbengal;
	}

	public void setWestbengal(String westbengal) {
		this.westbengal = westbengal;
	}

	public String getOtherstates() {
		return otherstates;
	}

	public void setOtherstates(String otherstates) {
		this.otherstates = otherstates;
	}

	public String getWelcomemailtemplate() {
		return welcomemailtemplate;
	}

	public void setWelcomemailtemplate(String welcomemailtemplate) {
		this.welcomemailtemplate = welcomemailtemplate;
	}

	public String getRequestmailtemplate() {
		return requestmailtemplate;
	}

	public void setRequestmailtemplate(String requestmailtemplate) {
		this.requestmailtemplate = requestmailtemplate;
	}

	public boolean isSendWithTemplate() {
		return sendWithTemplate;
	}

	public void setSendWithTemplate(boolean sendWithTemplate) {
		this.sendWithTemplate = sendWithTemplate;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public VelocityContext getContext() {
		return context;
	}

	public void setContext(VelocityContext context) {
		this.context = context;
	}

	public String getWelcomemailsubject() {
		return welcomemailsubject;
	}

	public void setWelcomemailsubject(String welcomemailsubject) {
		this.welcomemailsubject = welcomemailsubject;
	}

	public String getRequestmailsubject() {
		return requestmailsubject;
	}

	public void setRequestmailsubject(String requestmailsubject) {
		this.requestmailsubject = requestmailsubject;
	}
	
}
