package org.srcm.heartfulness.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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


}
