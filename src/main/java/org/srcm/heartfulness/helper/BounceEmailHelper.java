/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

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
	 * @param part contains the mail content to parse.
	 * @return bounced email parsed from mail content.
	 */
	public String getBouncedEmail(Part part){
		String recipientEmail = "",textContent="";
		try {
			//check if the content is plain text
			if (part.isMimeType("text/plain")) {
				recipientEmail = parseEmailContent((String) part.getContent());
				LOGGER.debug("Mail Content: "+(String) part.getContent());
			}else if (part.isMimeType("multipart/*")) {
				LOGGER.debug("Mail-Content-Type : multipart/*");
				Multipart mp = (Multipart) part.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++){
					textContent = convertMultipartToTextPlain(mp.getBodyPart(i));
					if(!textContent.isEmpty()){
						recipientEmail = parseEmailContent(textContent);
						break;
					}
				}
			}
			//check if the content is a nested message
			else if (part.isMimeType("message/rfc822")) {
				LOGGER.debug("Mail-Content-Type : message/rfc822");
				getBouncedEmail((Part) part.getContent());
			}
		}catch (IOException e) {
			LOGGER.debug("IO Exception while reading mail content");
		}catch (MessagingException e) {
			LOGGER.debug("Messaging Exception while reading mail content");
		} catch (Exception e) {
			LOGGER.debug("Exception while reading mail content");
		} 
		return recipientEmail;
	}


	private String convertMultipartToTextPlain(Part part) {
		String stringContent = "";
		try {
			if (part.isMimeType("text/plain")) {
				stringContent = (String) part.getContent();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
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
