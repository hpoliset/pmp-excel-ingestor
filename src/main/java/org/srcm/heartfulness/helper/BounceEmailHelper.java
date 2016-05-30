/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.EventConstants;

/**
 * This is a helper class to parse the bounced email content
 * and find out the email from the body content.
 * @author Koustav Dutta
 *
 */

@Component
public class BounceEmailHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(BounceEmailHelper.class);

	/**
	 * This method checks whether the mail content is instance of String or multipart.
	 * If the mail content is of type multipart it calls parseEmailContent 
	 * method and passes the mail content. 
	 * @param message contains the mail content to parse.
	 * @return bounced email parsed from mail content.
	 */
	public String getBouncedEmail(Message message){
		String recipientEmail = "";
		try {
			Object content = message.getContent();
			if(content instanceof String){
				recipientEmail = parseEmailContent((String)content);
			}else if(content instanceof Multipart){
				Multipart multipart = (Multipart)content;
				String multipartContent = convertMultipartToTextPlain(multipart);

				//String multipartContent = multipart.getBodyPart(0).getContent().toString();
				recipientEmail = parseEmailContent(multipartContent);
			}
		} catch (IOException | MessagingException e) {
			LOGGER.debug("EXCEPTION: Unable to parse Mail content");
			LOGGER.debug("EXCEPTION: "+e.getMessage());
		} 
		return recipientEmail;
	}

	private String convertMultipartToTextPlain(Multipart multipart) {
		String stringContent = "";
		try {
			if(multipart.getBodyPart(0).isMimeType("TEXT/PLAIN")){
				stringContent = multipart.getBodyPart(0).getContent().toString();
			}else if(multipart.getBodyPart(0).isMimeType("multipart/REPORT")){
				stringContent = multipart.getBodyPart(0).getContent().toString();
				LOGGER.debug("Mail Content: "+stringContent);
				//convertMultipartToTextPlain((Multipart)multipart.getBodyPart(0).getContent());
			}else if(multipart.getBodyPart(0).isMimeType("multipart/MIXED")){
				stringContent = multipart.getBodyPart(0).getContent().toString();
				//convertMultipartToTextPlain((Multipart)multipart.getBodyPart(0).getContent());
			}else if(multipart.getBodyPart(0).isMimeType("multipart/ALTERNATIVE")){
				stringContent = multipart.getBodyPart(0).getContent().toString();
				LOGGER.debug("Mail Content: "+stringContent);
				//convertMultipartToTextPlain((Multipart)multipart.getBodyPart(0).getContent());
			}
		} catch (IOException e) {
			LOGGER.debug("IO Exception,cannot convert multipart to String");
			//e.printStackTrace();
		} catch (MessagingException e) {
			LOGGER.debug("Messaging Exception,cannot convert multipart to String");
			//e.printStackTrace();
		}
		return stringContent;
	}

	/**
	 * It parses the mail content and searches for an email in the content body.
	 * @param content email content to parse and find out if 
	 * any email is present in the content body.
	 * @return email if found in email content.
	 */
	private String parseEmailContent(String content) {
		String[] contentPart = content.split(" ");
		String emailMatches = "";
		Pattern pattern = Pattern.compile(EventConstants.EMAIL_REGEX,Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		for(String matchContent:contentPart){

			if(matchContent.contains("<")){
				String subcontent = matchContent.substring(matchContent.indexOf("<") + 1, matchContent.indexOf(">"));
				LOGGER.debug("Mail from message content contains <> format");
				//if(subcontent.matches(EventConstants.EMAIL_REGEX))
				matcher = pattern.matcher(subcontent);
			}else{
				//LOGGER.debug("Mail from message content doesnot contain <> format");
				matcher = pattern.matcher(matchContent);
			}

			if(matcher.matches()){
				emailMatches = matcher.group();
				break;
			}
		}
		return emailMatches;
	}

}
