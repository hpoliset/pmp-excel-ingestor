package org.srcm.heartfulness.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.srcm.heartfulness.model.ExcelMetaData;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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


	//in case to convert to restful service, just add @ResponseBody and return excelsMetaInfo
	//which contains success/failure list of uploaded excels
	@RequestMapping(value="/ingest/processBulkUpload", method = RequestMethod.POST)
	public String processFileUpload(@RequestParam MultipartFile excelDataFiles[],ModelMap modelMap, HttpServletResponse response,HttpServletRequest request) throws IOException {
		//sorting the files based on excel file name
		Arrays.sort(excelDataFiles,new Comparator<MultipartFile>() {
			@Override
			public int compare(MultipartFile mpf1, MultipartFile mpf2) {
				return mpf1.getOriginalFilename().compareTo(mpf2.getOriginalFilename());
			}
		});
		List<ExcelMetaData> excelsMetaInfo = new LinkedList<ExcelMetaData>();
		for(MultipartFile multipartFile : excelDataFiles){
			try {
				excelsMetaInfo.add(pmpIngestionService.parseAndPersistExcelFile(multipartFile.getOriginalFilename(),multipartFile.getBytes()));
			} catch (InvalidExcelFileException e) {
				e.printStackTrace();
				System.out.println("Error occured while uploading files.");
			}
		}
		modelMap.addAttribute("uploadReponse",excelsMetaInfo);
		return "bulkUploadResponse";
	}


}
