package org.srcm.heartfulness.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.service.response.ExcelUploadResponse;
import org.srcm.heartfulness.util.InvalidExcelFileException;

/**
 * Created by vsonnathi on 11/12/15.
 */
@Controller
public class IngestionController {


	@Autowired
	private PmpIngestionService pmpIngestionService;

	@RequestMapping(value = "/ingest/inputForm", method = RequestMethod.GET)
	public String showUploadForm(HttpServletRequest request) {
		return "ingestionForm";
	}

	@RequestMapping("/")
	@ResponseBody
	public String index() {
		return "Greetings from Ingestion Controller";
	}

	@RequestMapping(value = "/ingest/processUpload", method = RequestMethod.POST)
	/*
    public String processFileUpload(HttpServletRequest request,
                                    @RequestParam MultipartFile[] excelDataFile)
            throws InvalidExcelFileException, IOException {
	 */
	public String processFileUpload(HttpServletRequest request,
			@RequestParam MultipartFile excelDataFile)
					throws InvalidExcelFileException, IOException {

		MultipartFile uploadedFile = excelDataFile;

		pmpIngestionService.parseAndPersistExcelFile(uploadedFile.getOriginalFilename(), uploadedFile.getBytes());

		return "success";
	}

	@RequestMapping(value = "/ingest/bulkUploadForm", method = RequestMethod.GET)
	public String showBulkUploadForm(HttpServletRequest request) {
		return "bulkUploadIngestionForm";
	}
	/**
	 * This method is used to process multiple excel file and call the service method.
	 * 
	 * @param excelDataFiles
	 * @param modelMap
	 * @return bulkUploadResponse.jsp
	 * @throws IOException
	 */
	@RequestMapping(value="/ingest/processBulkUpload", method = RequestMethod.POST)
	public String processFileUpload(@RequestParam MultipartFile excelDataFiles[],ModelMap modelMap) throws IOException {
		
		List<ExcelUploadResponse> responseList = pmpIngestionService.parseAndPersistExcelFile(excelDataFiles);
		modelMap.addAttribute("uploadReponse",responseList);
		return "bulkUploadResponse";
		
	}

}
