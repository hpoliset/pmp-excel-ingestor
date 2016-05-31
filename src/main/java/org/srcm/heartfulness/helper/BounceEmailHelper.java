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
				Multipart multipart = (Multipart)part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++){
					textContent = convertMultipartToTextPlain(multipart.getBodyPart(i));
					if(!textContent.isEmpty()){
						recipientEmail = parseEmailContent(textContent);
						break;
					}
				}
			}else{
				LOGGER.debug("Email-Content-Type doesnot match text/plain,multipart/*,message/* format");
				Multipart multipart = (Multipart)part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++){
					textContent = convertMultipartToTextPlain(multipart.getBodyPart(i));
					if(!textContent.isEmpty()){
						recipientEmail = parseEmailContent(textContent);
						break;
					}
				}
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
			LOGGER.debug("Sub-part Mime-type: "+part.getContentType());
			if (part.isMimeType("text/plain")) {
				stringContent = (String) part.getContent();
			}else if(part.isMimeType("multipart/ALTERNATIVE")){
				Multipart multiPart = (Multipart) part.getContent();
				for(int i=0;i<multiPart.getCount();i++){
					String content = convertMultipartToTextPlain(multiPart.getBodyPart(i));
					if(!content.isEmpty()){
						stringContent = content;
						break;
					}
				}
			}
			
			/*else if(part.isMimeType("message/*")){
				Object o = part.getContent();
				if (o instanceof String) {
					//LOGGER.debug("String Type: "+(String)o);
				}
				else if (o instanceof InputStream) {
					 InputStream is = (InputStream) o;
			            is = (InputStream) o;
			            int c;
			            //while ((c = is.read()) != -1)
			             // LOGGER.debug(""+c);
					 				
				}
				else {
					//LOGGER.debug("Unknown Type :"+o.toString());
				}
			}*/
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
				LOGGER.debug("Message content contains <> format,subcontent found: "+subcontent);
				String[] emailArray = subcontent.split(":");
				String emailContent = emailArray[emailArray.length - 1];
				matcher = pattern.matcher(emailContent);
			}else{
				matcher = pattern.matcher(matchContent);
			}

			if(matcher.matches()){
				emailMatches = matcher.group();
				break;
			}
		}
		LOGGER.debug("Email Found from content: "+emailMatches);
		return emailMatches;
	}

}
