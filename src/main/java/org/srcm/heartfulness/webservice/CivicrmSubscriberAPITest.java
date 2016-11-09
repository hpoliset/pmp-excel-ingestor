/**
 * 
 */
package org.srcm.heartfulness.webservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.srcm.heartfulness.constants.RestTemplateConstants;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * @author Koustav Dutta
 *
 */
@RestController
@RequestMapping("/api")
public class CivicrmSubscriberAPITest extends RestTemplate{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CivicrmSubscriberAPITest.class);
	
	@RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	public ResponseEntity<?> subscribeViaCivicrm(){
		
		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<String, String>();
		bodyParams.add(RestTemplateConstants.FIRST_NAME, "Test");
		bodyParams.add(RestTemplateConstants.LAST_NAME, "User");
		bodyParams.add(RestTemplateConstants.USER_EMAIL, "test.user123@htcindia.com");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<?> httpEntity = new HttpEntity<Object>(bodyParams, httpHeaders);
		LOGGER.error(String.valueOf(httpEntity));
		ResponseEntity<String> response = null;
		try{
			response = this.exchange("http://en.staging.heartfulness.org/civi/subscribeProcess-pmp.php", HttpMethod.POST, httpEntity, String.class);
		}catch(Exception ex){
			return new ResponseEntity<String>(StackTraceUtils.convertStackTracetoString(ex),HttpStatus.OK);
		}
		LOGGER.error(String.valueOf(response));
		return new ResponseEntity<String>(String.valueOf(response),HttpStatus.OK);
	}

}
