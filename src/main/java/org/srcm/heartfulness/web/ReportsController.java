package org.srcm.heartfulness.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.service.PmpIngestionService;
import org.srcm.heartfulness.service.ReportingService;
import org.srcm.heartfulness.util.InvalidExcelFileException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by vsonnathi on 11/12/15.
 */
@Controller
public class ReportsController {

    @Autowired
    private ReportingService reportingService;

    @RequestMapping(value = "/reports/reportsForm", method = RequestMethod.GET)
    public String showReportsForm(HttpServletRequest request) {
        return "reportsForm";
    }

    @RequestMapping("/reports")
    @ResponseBody
    public String index() {
        return "Greetings from Reports Controller";
    }

    @RequestMapping(value = "/reports/generate", method = RequestMethod.POST)
    /*
    public String processFileUpload(HttpServletRequest request,
                                    @RequestParam MultipartFile[] excelDataFile)
            throws InvalidExcelFileException, IOException {
*/
    public void generateReport(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam String channel)
            throws IOException {

        Collection<ParticipantFullDetails> participants = reportingService.getParticipantsByChannel(channel);

        StringBuilder sb = new StringBuilder();
        sb.append("Channel,Date,State,City,Organization,FirstName,LastName,Email");
        
        response.reset();
        response.setContentType("text/csv"); //or whatever file type you want to send. 
        response.setHeader("Content-disposition", "attachment; filename=report.csv");
        
        try 
        {
        	for (ParticipantFullDetails participant: participants ){
        		sb.append(participant.toString());
        		sb.append("\n");
        	}

        	response.getOutputStream().println(sb.toString());
        	System.out.println(sb.toString());
        	
        } catch (IOException e) {
        		
            System.out.println("ERROR IN WRITING RESPONSE in ReportsController");
        }

        
    }


}
