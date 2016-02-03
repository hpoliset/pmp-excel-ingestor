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
	 * @param phonecode
	 * @param keyword
	 * @param location
	 * @param carrier
	 * @param content
	 * @param phoneno
	 * @param time
	 * @return
	 */
	@RequestMapping(value = "receive", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public String receiveSMS(@RequestParam(value = "phonecode", required = false) String phonecode,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "carrier", required = false) String carrier,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "phoneno", required = false) String phoneno,
			@RequestParam(value = "time", required = false) String time, HttpServletRequest request) {

		LOGGER.debug("Request param -> phonecode {}", phonecode);
		LOGGER.debug("Request param -> keyword {}", keyword);
		LOGGER.debug("Request param -> location {}", location);
		LOGGER.debug("Request param -> carrier {}", carrier);
		LOGGER.debug("Request param -> content {}", content);
		LOGGER.debug("Request param -> phoneno {}", phoneno);
		LOGGER.debug("Request param -> time {}", time);
		return "success";
	}
}
