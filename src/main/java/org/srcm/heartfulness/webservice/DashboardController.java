/**
 * 
 */
package org.srcm.heartfulness.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.DashboardConstants;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.DashboardResponse;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.PMPResponse;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.DashboardService;
import org.srcm.heartfulness.service.UserProfileService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.validator.impl.PMPAuthTokenValidatorImpl;

/**
 * @author Koustav Dutta
 *
 */
@RestController
@RequestMapping(value = "/api/dashboard/")
public class DashboardController {

	private static Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	PMPAuthTokenValidatorImpl authTokenVldtr;

	@Autowired
	APIAccessLogService apiAccessLogService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	DashboardService dashboardService;


	@RequestMapping(value = "/getcount",
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE )

	public ResponseEntity<?> getEventDetails(@RequestHeader(value = "Authorization") String authToken,@Context HttpServletRequest httpRequest) {
		
		//need to built a new pojo to take zone,center,country,date range filters

		//save request details in PMP
		PMPAPIAccessLog accessLog = createPMPAPIAccessLog(null,httpRequest,authToken);

		//validate token details
		PMPResponse pmpResponse = authTokenVldtr.validateAuthToken(authToken, accessLog);
		if(pmpResponse instanceof ErrorResponse){
			return new ResponseEntity<PMPResponse>(pmpResponse, HttpStatus.OK);
		}

		User user = userProfileService.loadUserByEmail(accessLog.getUsername());
		if (null == user) {
			LOGGER.info(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			ErrorResponse eResponse = new ErrorResponse(ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			accessLog.setErrorMessage(DashboardConstants.USER_UNAVAILABLE_IN_PMP);
			updatePMPAPIAccessLog(accessLog,ErrorConstants.STATUS_FAILED,DashboardConstants.USER_UNAVAILABLE_IN_PMP, StackTraceUtils.convertPojoToJson(eResponse));
			return new ResponseEntity<ErrorResponse>(eResponse, HttpStatus.BAD_REQUEST);
		}


		LOGGER.info("Trying to fetch dashboard data for log in user {}",accessLog.getUsername());
		DashboardResponse dresponse = dashboardService.getDashboardDataCounts(authToken);
		updatePMPAPIAccessLog(accessLog, ErrorConstants.STATUS_SUCCESS, null, StackTraceUtils.convertPojoToJson(dresponse));
		return new ResponseEntity<DashboardResponse>(dresponse,HttpStatus.OK);

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
