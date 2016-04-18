package org.srcm.heartfulness.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
			authHelper.setcurrentUsertoContext(request.getSession());
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
	public String processFileUpload(HttpServletRequest request, @RequestParam MultipartFile excelDataFile, ModelMap modelMap)
			throws InvalidExcelFileException, IOException {

		MultipartFile[] uploadedFile = new MultipartFile[]{excelDataFile};
		List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(uploadedFile);
		modelMap.addAttribute("uploadReponse", responseList);
		return "success";
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
			authHelper.setcurrentUsertoContext(request.getSession());
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
			HttpServletResponse response, HttpServletRequest request) throws IOException {

		List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(uploadedExcelFiles);
		modelMap.addAttribute("uploadReponse", responseList);
		return "bulkUploadResponse";

	}

}
