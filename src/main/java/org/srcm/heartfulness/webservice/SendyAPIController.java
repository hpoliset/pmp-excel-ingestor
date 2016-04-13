package org.srcm.heartfulness.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.model.SendySubscriber;
import org.srcm.heartfulness.service.SendyAPIService;

/**
 * 
 * @author rramesh
 *
 */
@RestController
@RequestMapping("/api/sendy/")
public class SendyAPIController {


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
/*	
	@RequestMapping(value = "subscribescheduler", method ={ RequestMethod.POST, RequestMethod.GET })
	public void addSubscriber() {
		sendyAPIService.addNewSubscriber();
	}

	@RequestMapping(value = "unsubscribescheduler", method ={ RequestMethod.POST, RequestMethod.GET })
	public void unsubscribe(){
		sendyAPIService.unsubscribeUsers();
	}*/
	
}
