package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.model.SMS;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.SMSIntegrationService;

/**
 * 
 * @author HimaSree
 *
 */
@RestController
@RequestMapping("/api/sms/")
public class SmsGatewayController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsGatewayController.class);

	@Autowired
	private ProgramService programService;
	
	
	@Autowired
	private SMSIntegrationService smsIntegrationService;
	
	/**
	 * 	To create new event via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "createevent", method = { RequestMethod.POST, RequestMethod.GET })
	public String createEvent(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		LOGGER.debug("--------------- Start Create Event----------------------");
		//LOGGER.debug("Host Name"+hostName);
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.createEvent(sms);
		LOGGER.debug("Response : "+response);
		LOGGER.debug("--------------- End Create Event----------------------");
		return response;
	}
	
	/**
	 * 	To create new event via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "updateevent", method = { RequestMethod.POST, RequestMethod.GET })
	public String updateEvent(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		LOGGER.debug("--------------- Start Update Event----------------------");
		//LOGGER.debug("Host Name"+hostName);
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.updateEvent(sms);
		LOGGER.debug("--------------- End Update Event----------------------");
		return response;
	}
	
	/**
	 * 	To register for the particular event via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "registerevent", method = { RequestMethod.POST, RequestMethod.GET })
	public String registerEvent(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.createParticipant(sms);
		return response;
	}
	
	/**
	 * 	To introduce participant for the particular event via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "introduceparticipant", method = { RequestMethod.POST, RequestMethod.GET })
	public String introduceParticipant(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.updateParticipant(sms);
		return response;
	}
	/**
	 * 	To get the registered participants count via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "registeredparticipantscount", method = { RequestMethod.POST, RequestMethod.GET })
	public String getCountOfRegisteredParticipants(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.getCountOfRegisteredParticipants(sms);
		return response;
	}
	
	/**
	 * 	To get the introduced participants count via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "introducedparticipantscount", method = { RequestMethod.POST, RequestMethod.GET })
	public String getCountOfIntroducedParticipants(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.getCountOfIntroducedParticipants(sms);
		return response;
	}
	
	/**
	 * 	To get the introduced participants count via SMS
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "gethelpsms", method = { RequestMethod.POST, RequestMethod.GET })
	public String getHelpSMS(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.getHelpContent(sms);
		return response;
	}
	
	/**
	 * 	To hanldle the invlalid subkeyword
	 * 
	 * @param mobileNo		- Sender mobile number
	 * @param smsContent	- Content of the SMS
	 * @param operator		- Mobile operator
	 * @param carrier		- Mobile carrier
	 * @param datetime		- Date & time of message
	 * @param hostName		- host
	 * @param request		- HTTP request
	 * @return the response
	 */
	@RequestMapping(value = "invalidformat", method = { RequestMethod.POST, RequestMethod.GET })
	public String invalidFormat(@RequestParam(value = "who", required = false) String mobileNo,
			@RequestParam(value = "what", required = false) String smsContent,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request){
		String response= "FAILURE";
		SMS sms = new SMS(mobileNo,smsContent,operator,carrier,datetime);
		response = smsIntegrationService.handleInvalidSubkeyword(sms);
		return response;
	}
}
