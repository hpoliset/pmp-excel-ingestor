package org.srcm.heartfulness.webservice;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.ProgramChannelType;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.ChannelService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;

/**
 * Controller for managing <code>Channel</code> domain objects.
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

	/**
	 * Web service end point to get the list of channels from the Heartfulness
	 * backend.
	 * 
	 * @param model
	 * @param httpRequest
	 * @return <code>List<String></code>
	 */
	@RequestMapping(value = "channel", 
			method = RequestMethod.GET)

	public ResponseEntity<?> getChannelList(ModelMap model, @Context HttpServletRequest httpRequest) {
		LOGGER.info("START : Get channel list called.");

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,null);
		try {
			List<String> channelList = channelService.findAllActiveChannelNames();
			LOGGER.info("END : Fetching channel list completed.");
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS,null, StackTraceUtils.convertPojoToJson(channelList));
			return new ResponseEntity<List<String>>(channelList, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.info("END : Error occured while fetching channel list {}", e);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "channeltype", 
			method = RequestMethod.GET)
	public ResponseEntity<?> getChannelType(@RequestParam(name = "channel",defaultValue="")String channel, @Context HttpServletRequest httpRequest) {

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,channel);
		
		if(channel.isEmpty()){
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.EMPTY_CHANNEL);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED, DashboardConstants.EMPTY_CHANNEL, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.PRECONDITION_REQUIRED);
		}else{
			try{
				List<ProgramChannelType> channelTypes = channelService.getListOfChannelTypes(channel);
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_SUCCESS,null, StackTraceUtils.convertPojoToJson(channelTypes));
				return new ResponseEntity<List<ProgramChannelType>>(channelTypes, HttpStatus.OK);
			} catch(Exception e){
				LOGGER.info("END : Error occured while fetching channel type for channel {}{}",channel,e);
				ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.PROCESSING_FAILED);
				updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,StackTraceUtils.convertStackTracetoString(e), StackTraceUtils.convertPojoToJson(eResponse));
				return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private PMPAPIAccessLog createPMPAPIAccessLog(String username,HttpServletRequest httpRequest,String requestBody){

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(username, httpRequest.getRemoteAddr(), 
				httpRequest.getRequestURI(),DateUtils.getCurrentTimeInMilliSec(), null, 
				ErrorConstants.STATUS_FAILED, null,requestBody);
		apiAccessLogService.createPmpAPIAccessLog(accessLog);
		return accessLog;
	}


	private void updatePMPAPIAccessLog(PMPAPIAccessLog pmpApiAccessLog, String status, String errorMessage, String responseBody){

		pmpApiAccessLog.setStatus(status);
		pmpApiAccessLog.setErrorMessage(errorMessage);
		pmpApiAccessLog.setTotalResponseTime(DateUtils.getCurrentTimeInMilliSec());
		pmpApiAccessLog.setResponseBody(responseBody);
		apiAccessLogService.updatePmpAPIAccessLog(pmpApiAccessLog);
	}
}
