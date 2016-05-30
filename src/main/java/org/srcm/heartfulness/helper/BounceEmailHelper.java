/**
 * 
 */
package org.srcm.heartfulness.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

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
			}else if (part.isMimeType("multipart/*")) {
				LOGGER.debug("Mail-Content-Type : multipart/*");
				Multipart multipart = (Multipart) part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++){
					textContent = convertMultipartToTextPlain(multipart.getBodyPart(i));
					if(!textContent.isEmpty()){
						recipientEmail = parseEmailContent(textContent);
						break;
					}
				}
			}else if (part.isMimeType("MESSAGE/*")) {
				LOGGER.debug("Mail-Content-Type : message/rfc822");
				Multipart multipart = (Multipart)part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++){
					textContent = convertMultipartToTextPlain(multipart.getBodyPart(i));
					if(!textContent.isEmpty()){
						recipientEmail = parseEmailContent(textContent);
						break;
					}
				}
				//getBouncedEmail((Part) part.getContent());
			}else{
				LOGGER.debug("Email-Unknown-Content-Type");
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

	/**
	 * @param part mime type is used to check whether it 
	 * is of type text/plain and if it is so then that content
	 * is returned to be parsed later.
	 * @return body content.
	 */
	private String convertMultipartToTextPlain(Part part) {
		String stringContent = "";
		try {
			LOGGER.debug("Sub Part called......");
			LOGGER.debug("Sub-part Content-type: "+part.getContentType());
			if (part.isMimeType("text/plain")) {
				stringContent = (String) part.getContent();
				LOGGER.debug("Text/plain Type: "+stringContent);
			}else if(part.isMimeType("message/*")){
				Object o = part.getContent();
				if (o instanceof String) {
					LOGGER.debug("String Type: "+(String)o);
				}
				else if (o instanceof InputStream) {
					LOGGER.debug("Input stream Type: ");
					/* InputStream is = (InputStream) o;
			            is = (InputStream) o;
			            int c;
			            //while ((c = is.read()) != -1)
			             // LOGGER.debug(""+c);
					 */				}
				else {
					LOGGER.debug("Unknown Type :"+o.toString());
				}

				//LOGGER.debug("Mail Content: "+part.getContent());
				//LOGGER.debug("Mail Content: toString"+part.getContent().toString());
			}else if(part.isMimeType("multipart/ALTERNATIVE")){
				Multipart multiPart = (Multipart) part.getContent();
				for(int i=0;i<multiPart.getCount();i++){
					String content = convertMultipartToTextPlain(multiPart.getBodyPart(i));
					LOGGER.debug("Sub Content: "+content);
					if(!content.isEmpty()){
						stringContent = content;
						break;
					}
				}
				LOGGER.debug("Mail Content: "+multiPart.toString());
				LOGGER.debug("Mail Content: toString"+part.getContent().toString());
			}
		} catch (IOException e) {
			LOGGER.debug("IO Exception,cannot convert from multipart --> text/plain format");
		} catch (MessagingException e) {
			LOGGER.debug("Messaging Exception,cannot convert from multipart --> text/plain format");
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
