package org.srcm.heartfulness.webservice;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.service.SendyAPIService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author rramesh
 *
 */
@RestController
@RequestMapping("/api/sendy/")
public class SendyAPIController {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SendyAPIController.class);


	@Autowired
	private SendyAPIService sendyAPIService;

	@RequestMapping(value = "subscribe", method ={ RequestMethod.POST, RequestMethod.GET })
	public String addNewSubscriber(@RequestBody SendySubscriber sendySubcriber) {
		String response = "";
		response = sendyAPIService.subscribe(sendySubcriber.getUserName(),sendySubcriber.getEmail());
		return response;
	}

	@RequestMapping(value = "unsubscribe", method ={ RequestMethod.POST, RequestMethod.GET })
	public String unsubscribeUser(@RequestBody SendySubscriber sendySubcriber){
		String response = "";
		response = sendyAPIService.unsubscribe(sendySubcriber.getEmail());
		return response;
	}
	
	@RequestMapping(value = "subscribescheduler", method ={ RequestMethod.POST, RequestMethod.GET })
	public String addSubscriber() {
		sendyAPIService.addNewSubscriber();
		return "completed";
	}

	@RequestMapping(value = "unsubscribescheduler", method ={ RequestMethod.POST, RequestMethod.GET })
	public String unsubscribe(){
		sendyAPIService.unsubscribeUsers();
		return "completed";
	}
	
	
	/*@RequestMapping(value = "invitemail", method ={ RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<?> addSubcribertoInviteMail() {
		try {
			sendyAPIService.addSubcribertoInviteMail();
			return new ResponseEntity<String> ("Mail Sent",HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			LOGGER.debug("Error while adding subscriber - " + e.getMessage());
			return new ResponseEntity<String> ("Error while adding subscriber",e.getStatusCode());
		} catch (JsonParseException e) {
			e.printStackTrace();
			LOGGER.debug("Error while adding subscriber - " + e.getMessage());
			return new ResponseEntity<String> ("Error while adding subscriber",HttpStatus.BAD_REQUEST);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			LOGGER.debug("Error while adding subscriber - " + e.getMessage());
			return new ResponseEntity<String> ("Error while adding subscriber",HttpStatus.BAD_REQUEST);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.debug("Error while adding subscriber - " + e.getMessage());
			return new ResponseEntity<String> ("Error while adding subscriber",HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug("Error while adding subscriber - " + e.getMessage());
			return new ResponseEntity<String> ("Internal server error",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/

}
