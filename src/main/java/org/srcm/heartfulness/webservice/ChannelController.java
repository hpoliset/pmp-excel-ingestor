package org.srcm.heartfulness.webservice;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.ChannelService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * 
 * @author himasreev
 *
 */
@RestController
@RequestMapping("/api/")
public class ChannelController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelController.class);

	@Autowired
	ChannelService channelService;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@RequestMapping(value = "channel", method = RequestMethod.POST)
	public ResponseEntity<?> getchannelList(ModelMap model, @Context HttpServletRequest httpRequest) {
		LOGGER.info("START : Get channel list called.");
		PMPAPIAccessLog accessLog = null;
		try {
			accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
					DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null, null, null);
			apiAccessLogService.createPmpAPIAccessLog(accessLog);
		} catch (Exception e) {
			LOGGER.info("Error occured while creating log details. Exception : {}", e.getMessage());
		}
		try {
			List<String> channelList = channelService.findAllActiveChannelNames();
			try {
				accessLog.setStatus(ErrorConstants.STATUS_SUCCESS);
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(channelList));
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			} catch (Exception e) {
				LOGGER.info("Error occured while updating log details. Exception : {}", e.getMessage());
			}
			LOGGER.info("END : Fetching channel list completed.");
			return new ResponseEntity<List<String>>(channelList, HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.info("END : Error occured while Fetching channel list. Exception : {}", ex.getMessage());
			Response response = new Response(ErrorConstants.STATUS_FAILED, ex.getMessage());
			try {
				accessLog.setStatus(ErrorConstants.STATUS_FAILED);
				accessLog.setResponseBody(StackTraceUtils.convertPojoToJson(response));
				accessLog.setErrorMessage(StackTraceUtils.convertStackTracetoString(ex));
				accessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
				apiAccessLogService.updatePmpAPIAccessLog(accessLog);
			} catch (Exception e) {
				LOGGER.info("Error occured while updating log details. Exception : {}", e.getMessage());
			}
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
