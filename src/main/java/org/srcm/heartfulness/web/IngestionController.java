package org.srcm.heartfulness.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.srcm.heartfulness.authorizationservice.PmpAuthorizationService;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Created by vsonnathi on 11/12/15.
 */
@Controller
public class IngestionController {

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	private PmpAuthorizationService pmpAuthService;

	@Autowired
	private PmpIngestionService pmpIngestionService;

	@RequestMapping(value = "/ingest/inputForm", method = RequestMethod.GET)
	public String showUploadForm(HttpServletRequest request,RedirectAttributes redirectAttributes) {
		try{
			authHelper.setCurrentUsertoContext(request.getSession());
			return pmpAuthService.showInputForm();
		}catch(AccessDeniedException e){
			return "accessdenied";
		}catch (NullPointerException e) {
			redirectAttributes.addFlashAttribute("redirecturl", "/ingest/inputForm");
			return "redirect:/login";
		}
	}

	@RequestMapping("/")
	@ResponseBody
	public String index() {
		return "Greetings from Ingestion Controller";
	}

	@RequestMapping(value = "/ingest/processUpload", method = RequestMethod.POST)
	public String processFileUpload(@Context HttpServletRequest httpRequest, @RequestParam MultipartFile excelDataFile, ModelMap modelMap
			,@ModelAttribute("generateEWelcomeId") String eWelcomeIdCheckbox)
					throws InvalidExcelFileException, IOException {

		MultipartFile[] uploadedFile = new MultipartFile[]{excelDataFile};
		UserDetails userDetails = null;
		try{
			userDetails = (UserDetails)httpRequest.getSession().getAttribute("Authentication");
			List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(uploadedFile,eWelcomeIdCheckbox,userDetails.getUsername());
			modelMap.addAttribute("uploadReponse", responseList);
			return "success";
		} catch(Exception ex){
			return "redirect:/login";
		}
	}

	/**
	 * Mapping method called to display the bulk upload form.
	 * 
	 * @param request
	 * @see {@link HttpServletRequest}
	 * @return The bulk upload form.
	 */
	@RequestMapping(value = "/ingest/bulkUploadForm", method = RequestMethod.GET)
	public String showBulkUploadForm(HttpServletRequest request,RedirectAttributes redirectAttributes) {
		try{
			authHelper.setCurrentUsertoContext(request.getSession());
			return pmpAuthService.showBulkUploadForm();
		}catch(AccessDeniedException e){
			return "accessdenied";
		}catch (NullPointerException e) {
			redirectAttributes.addFlashAttribute("redirecturl", "/ingest/bulkUploadForm");
			return "redirect:/login";
		}
	}

	/**
	 * This method is used to process multiple excel file upload.
	 * 
	 * @param uploadedExcelFiles
	 * @param modelMap
	 * @return bulkUploadResponse.jsp
	 * @throws IOException
	 */
	@RequestMapping(value = "/ingest/processBulkUpload", method = RequestMethod.POST)
	public String processFileUpload(@RequestParam MultipartFile uploadedExcelFiles[], ModelMap modelMap,
			HttpServletResponse response, HttpServletRequest httpRequest,@ModelAttribute("generateEWelcomeId") String eWelcomeIdCheckbox) throws IOException {

		UserDetails userDetails = null;
		try{
			userDetails = (UserDetails)httpRequest.getSession().getAttribute("Authentication");
			List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(uploadedExcelFiles,eWelcomeIdCheckbox,userDetails.getUsername());
			modelMap.addAttribute("uploadReponse", responseList);
			return "bulkUploadResponse";
		} catch(Exception ex){
			return "redirect:/login";
		}
	}

}
