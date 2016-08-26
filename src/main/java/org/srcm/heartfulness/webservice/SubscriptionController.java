package org.srcm.heartfulness.webservice;

import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.request.SubscriptionRequest;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.SubscriptionService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.validator.SubscriptionValidator;

/**
 * This class holds the web service end points to handle Subscription and
 * unsubscription services.
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/")
public class SubscriptionController {

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SubscriptionValidator subscriptionValidator;

	@Autowired
	APIAccessLogService apiAccessLogService;

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);

	/**
	 * Webservice endpoint to unsubscribe the user email ID.
	 * 
	 * If the unsubscription is successful, the service returns an success
	 * response body with HTTP status 200.
	 * 
	 * @param request
	 * @param model
	 * @param subscriptionRequest
	 * @return
	 */
	@RequestMapping(value = "/unsubscribemail", method = RequestMethod.POST)
	public ResponseEntity<?> unSubscribeToMailAlerts(HttpServletRequest request, Model model,
			@RequestBody SubscriptionRequest subscriptionRequest, @Context HttpServletRequest httpRequest)
			throws ParseException {
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(subscriptionRequest.getMailID(), httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, null, null);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		LOGGER.debug("Unsubcribe user called.");
		Map<String, String> map = subscriptionValidator.checkMandatoryFieldsinSubscriptionRequest(subscriptionRequest);
		if (!map.isEmpty()) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(map.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
		} else {
			Response response = subscriptionService.unsubscribe(subscriptionRequest.getMailID(),
					subscriptionRequest.getName());
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	}

	/**
	 * Webservice endpoint to subscribe the user emailID to recieve the emails
	 * from HFN.
	 * 
	 * If the subscription is successful, the service returns an success
	 * response body with HTTP status 200.
	 * 
	 * @param subscriptionRequest
	 * @return
	 */
	@RequestMapping(value = "/subscribemail", method = RequestMethod.POST)
	public ResponseEntity<?> subscribeToMailAlerts(@RequestBody SubscriptionRequest subscriptionRequest,
			@Context HttpServletRequest httpRequest) throws ParseException {
		LOGGER.debug("subcribe user called.");
		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(subscriptionRequest.getMailID(), httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, null, null);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		Map<String, String> map = subscriptionValidator.checkMandatoryFieldsinSubscriptionRequest(subscriptionRequest);
		if (!map.isEmpty()) {
			accessLog.setStatus(ErrorConstants.STATUS_FAILED);
			accessLog.setErrorMessage(map.toString());
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Map<String, String>>(map, HttpStatus.PRECONDITION_FAILED);
		} else {
			Response response = subscriptionService.subscribetoMailAlerts(subscriptionRequest);
			accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
			accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
			apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	}
}
