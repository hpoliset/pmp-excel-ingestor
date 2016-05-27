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
			LOGGER.debug("Inside parse message part -- >");
			if(content instanceof String){
				LOGGER.debug("Inside String part -- >");
				//LOGGER.debug("Message Content: "+ content);
				recipientEmail = parseEmailContent((String)content);
			}else if(content instanceof Multipart){
				LOGGER.debug("Inside multi part -- >");
				Multipart multiparts = (Multipart)content;
				LOGGER.debug("Before taking multi part -- >");
				LOGGER.debug("Multipart body content type -- >"+multiparts.getBodyPart(0).getContentType());
				if(multiparts.getBodyPart(0).isMimeType("TEXT/PLAIN")){
					LOGGER.debug("TEXT PLAIN TYPE"+parseEmailContent(multiparts.getBodyPart(0).getContent().toString()));
				}else if(multiparts.getBodyPart(0).isMimeType("multipart/ALTERNATIVE")){
					LOGGER.debug("TEXT PLAIN TYPE"+parseEmailContent((String)multiparts.getBodyPart(0).getContent()));
				}else if(multiparts.getBodyPart(0).isMimeType("multipart/MIXED")){
					LOGGER.debug("TEXT PLAIN TYPE"+parseEmailContent((String)multiparts.getBodyPart(0).getContent()));
				}else if(multiparts.getBodyPart(0).isMimeType("multipart/REPORT")){
					LOGGER.debug("TEXT PLAIN TYPE"+parseEmailContent((String)multiparts.getBodyPart(0).getContent()));
				}else{
					LOGGER.debug("Message Sub content type not in");
					
				}
				String multipartContent = multiparts.getBodyPart(0).getContent().toString();
				//LOGGER.debug("Message Content: "+ multipartContent);
				recipientEmail = parseEmailContent(multipartContent);
			}else{
				LOGGER.debug("Else part...."+message.getContentType());
				LOGGER.debug("Message Content"+message.getContent());
			}
		} catch (IOException | MessagingException e) {
			LOGGER.debug("EXCEPTION: Unable to parse Mail content");
			LOGGER.debug("EXCEPTION: "+e.getMessage());
		} 
		return recipientEmail;
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
		Matcher matcher = null;
		for(String matchContent:contentPart){

			/*if(matchContent.contains("<")){
				matchContent.replaceAll("<", "");
			}else if(matchContent.contains(">")){
				matchContent.replaceAll(">", "");
			}*/
			
			if(matchContent.contains("<")){
				String subcontent=matchContent.substring(matchContent.indexOf("<") + 1, matchContent.indexOf(">"));
				LOGGER.debug("Substring value..."+subcontent);
				//if(subcontent.matches(EventConstants.EMAIL_REGEX))
				matcher = pattern.matcher(subcontent);
			}else if(matchContent.contains("@")){
				LOGGER.debug("Substring value..."+matchContent);
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
