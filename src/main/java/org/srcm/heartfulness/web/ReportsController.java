package org.srcm.heartfulness.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.service.ReportingService;

/**
 * Created by MaheshK on 11/12/15.
 */
@Controller
public class ReportsController {

    @Autowired
    private ReportingService reportingService;

    @RequestMapping(value = "/reports/reportsForm", method = RequestMethod.GET)
    public String showReportsForm(HttpServletRequest request,ModelMap modelMap) 
    {
    	List<String> eventCountries = reportingService.getAllEventCountries();
    	List<String> eventTypes = reportingService.getAllUniqueEventTypes();
    	modelMap.addAttribute("eventCountries", eventCountries);
    	modelMap.addAttribute("eventTypes", eventTypes);
    	
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
                                    @RequestParam(required=false) String channel,@RequestParam(required=false) String fromDate,
                                    @RequestParam(required=false) String tillDate,@RequestParam(required=false) String city,
                                    @RequestParam(required=false) String state,@RequestParam(required=false) String country)
            throws IOException {

        Collection<ParticipantFullDetails> participants = reportingService.getParticipantsByChannel(channel,fromDate,
        		tillDate,city,state,country);

        StringBuilder sb = new StringBuilder();
        
        sb.append("Id\tprintName\tfirstName\tmiddleName\tlastName\temail\tmobilePhone\t")
          .append("gender\tdateOfBirth\tdateOfRegistration\tlanguage\tprofession\t")
          .append("abhyasiId\tidCardNumber\tstatus\taddressLine1\taddressLine2\tcity\tstate\tcountry\tremarks\t")
          .append("introduced\tintroducedBy\tintroductionDate\twelcomeCardNumber\twelcomeCardDate\tageGroup\t")
          .append("firstSittingTaken\tfirstSittingDate\tsecondSittingTaken\tsecondSittingDate\tthirdSittingTaken\tthirdSittingDate\t")
          .append("batch\treceiveUpdates\tsyncStatus\taimsSyncTime\tuploadStatus\t")
          .append("programId\tprogramChannel\tprogramStartDate\tprogramEndDate\t")
          .append("eventPlace\teventState\teventCity\teventCountry\t")
          .append("organizationId\torganizationName\torganizationDepartment\torganizationWebSite\torganizationContactName\torganizationContactEmail\torganizationContactMobile\t")
          .append("preceptorName\tpreceptorIdCardNumber\twelcomeCardSignedByName\twelcomeCardSignerIdCardNumber")
          .append("\n");
        
        response.reset();
        response.setContentType("text/plain");
        response.setHeader("Content-disposition", "attachment; filename=Report_" +
        		new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) +".txt");
        
        try 
        {
        	for (ParticipantFullDetails participant: participants ){
        		sb.append(participant.toString());
        		sb.append("\n");
        	}

        	response.getOutputStream().println(sb.toString());
        	//System.out.println(sb.toString());
        	
        } catch (IOException e) {
        		
            System.out.println("ERROR IN WRITING RESPONSE in ReportsController");
        }

        
    }

    @RequestMapping(value = "/reports/getStates", method = RequestMethod.POST)
    @ResponseBody
    public List<String> getEventStatesForCountry(HttpServletRequest request,ModelMap modelMap,
    		@RequestParam(required=false,name="eventCountry") String eventCountry){
    	List<String> eventStates = reportingService.getEventStatesForEventCountry(eventCountry);
    	return eventStates;
    }

}
