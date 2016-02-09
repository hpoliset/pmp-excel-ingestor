package org.srcm.heartfulness.webservice;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.srcm.heartfulness.service.UserProfileService;

/**
 * 
 * @author HimaSree
 *
 */
@Controller
@RequestMapping("/api/sms/")
public class SmsGatewayController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsGatewayController.class);

	@Autowired
	private UserProfileService userProfileService;

	/**
	 * 
	 * @param who
	 * @param what
	 * @param operator
	 * @param carrier
	 * @param datetime
	 * @param phoneno
	 * @param time
	 * @return
	 */
	@RequestMapping(value = "receive", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String receiveSMS(@RequestParam(value = "who", required = false) String who,
			@RequestParam(value = "what", required = false) String what,
			@RequestParam(value = "operator", required = false) String operator,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "datetime", required = false) String datetime,
			HttpServletRequest request) {

		LOGGER.debug("Request param -> phone number {}", who);
		LOGGER.debug("Request param -> content {}", what);
		LOGGER.debug("Request param -> operator {}", operator);
		LOGGER.debug("Request param -> carrier {}", carrier);
		LOGGER.debug("Request param -> datetime {}", datetime);
		return "success";
	}
	
}
