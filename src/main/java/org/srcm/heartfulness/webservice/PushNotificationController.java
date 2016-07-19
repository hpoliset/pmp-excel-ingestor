/**
 * 
 */
package org.srcm.heartfulness.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.helper.PushNotificationHelper;

/**
 * @author Koustav Dutta
 *
 */

@RestController
@RequestMapping("/api/v1")
public class PushNotificationController {
		
	private static final Logger log = LoggerFactory.getLogger(PushNotificationController.class);
	
	@Autowired
	PushNotificationHelper pnHelper;
	
	@RequestMapping(value = "/push/notification", method = RequestMethod.GET)
	public ResponseEntity<?> sendAndroidNotification(){
		pnHelper.sendNotification();
		return new ResponseEntity<String>("Sending notification to android device completed",HttpStatus.OK);
	}
	
	
}
