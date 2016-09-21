/**
 * 
 */
package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.srcm.heartfulness.authorizationservice.PmpAuthorizationService;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.PMPLogData;
import org.srcm.heartfulness.model.PMPLogDetailsData;
import org.srcm.heartfulness.service.APIAccessLogService;

/**
 * @author Koustav Dutta
 *
 */
@Controller
@RequestMapping(value = "/api/log")
public class PMPAPILogController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PMPAPILogController.class);

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	private PmpAuthorizationService pmpAuthService;

	@Autowired
	APIAccessLogService accessLogService;

	@RequestMapping(value = "/loadlogform", method = RequestMethod.GET)
	public String loadPmpApiLogForm(HttpServletRequest request,RedirectAttributes redirectAttributes){
		try{
			authHelper.setcurrentUsertoContext(request.getSession());
			return pmpAuthService.showPmpApiLogForm();
		} catch(AccessDeniedException ade){
			return "accessdenied";
		} catch(Exception ex){
			redirectAttributes.addFlashAttribute("redirecturl", "/api/log/loadlogform");
			return "redirect:/login";
		}

	}
	
	@RequestMapping(value = "/loaderrorlogform", method = RequestMethod.GET)
	public String loadPmpApiErrorLogForm(HttpServletRequest request,RedirectAttributes redirectAttributes){
		try{
			authHelper.setcurrentUsertoContext(request.getSession());
			return pmpAuthService.showPmpApiErrorLogForm();
		} catch(AccessDeniedException ade){
			return "accessdenied";
		} catch(Exception ex){
			redirectAttributes.addFlashAttribute("redirecturl", "/api/log/loadlogform");
			return "redirect:/login";
		}

	}

	@RequestMapping(value = "/loadlogdetailsform", method = RequestMethod.GET)
	public String loadPmpApiPopupForm(HttpServletRequest request,RedirectAttributes redirectAttributes){
		try{
			authHelper.setcurrentUsertoContext(request.getSession());
			return pmpAuthService.showPmpApiPopupForm();
		} catch(AccessDeniedException ade){
			return "accessdenied";
		} catch(Exception ex){
			redirectAttributes.addFlashAttribute("redirecturl", "/api/log/loadlogform");
			return "redirect:/login";
		}
	}
	
	@RequestMapping(value = "/loaderrorlogdetailsform", method = RequestMethod.GET)
	public String loadPmpApiErrorPopupForm(HttpServletRequest request,RedirectAttributes redirectAttributes){
		try{
			authHelper.setcurrentUsertoContext(request.getSession());
			return pmpAuthService.showPmpApiErrorPopupForm();
		} catch(AccessDeniedException ade){
			return "accessdenied";
		} catch(Exception ex){
			redirectAttributes.addFlashAttribute("redirecturl", "/api/log/loadlogform");
			return "redirect:/login";
		}
	}
    
	@ResponseBody
	@RequestMapping(value = "/loadlogdata",
	method = RequestMethod.POST,
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadPmpApiAccessLogData(){
		LOGGER.debug("START  :  Fetching details from PMP Log table");
		PMPLogData logData = new PMPLogData();
		logData.setData(accessLogService.loadPmpApiAccessLogData());
		LOGGER.debug("END  :  Completed fetching details from PMP Log table");
		return new ResponseEntity<PMPLogData>(logData,HttpStatus.OK);
	}

	
	@ResponseBody
	@RequestMapping(value = "/loaderrorlogdata",
	method = RequestMethod.GET,
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadPmpApiAccessErrorLogData(@RequestParam(name = "id",required = true) String accessLogId){
		LOGGER.debug("START  :  Fetching Error details from PMP Log table");
		PMPLogData logData = new PMPLogData();
		logData.setData(accessLogService.loadPmpApiAccessErrorLogData(accessLogId));
		LOGGER.debug("END  :  Completed fetching Error details from PMP Log table");
		return new ResponseEntity<PMPLogData>(logData,HttpStatus.OK);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/loadlogdetailsdata",
	method = RequestMethod.GET,
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadPmpApiLogDetailsData(@RequestParam(name = "id",required = true) String accessLogId ){
		LOGGER.debug("START  :  Fetching details from PMP Log details table");
		PMPLogDetailsData logDetailsData = new PMPLogDetailsData();
		logDetailsData.setData(accessLogService.loadPmpApiLogDetailsData(accessLogId));
		LOGGER.debug("END  :  Completed fetching details from PMP Log details table");
		return new ResponseEntity<PMPLogDetailsData>(logDetailsData,HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/loaderrorlogdetailsdata",
	method = RequestMethod.GET,
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadPmpApiErrorLogDetailsData(@RequestParam(name = "id",required = true) String logDetailsId ){
		LOGGER.debug("START  :  Fetching Error details from PMP Log details table");
		PMPLogDetailsData logDetailsData = new PMPLogDetailsData();
		logDetailsData.setData(accessLogService.loadPmpApiErrorLogDetailsData(logDetailsId));
		LOGGER.debug("END  :  Completed fetching Error details from PMP Log details table");
		return new ResponseEntity<PMPLogDetailsData>(logDetailsData,HttpStatus.OK);
	}

}
