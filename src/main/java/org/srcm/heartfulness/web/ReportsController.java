package org.srcm.heartfulness.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.service.ReportingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by MaheshK on 11/12/15.
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
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Content-disposition", "attachment; filename=Report_by_Channel_" + channel + "_" +
                        new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) +".txt");
        response.getWriter().print(sb.toString());

        try
        {
                for (ParticipantFullDetails participant: participants ){
                response.getWriter().println(participant.toString());
                }

        } catch (IOException e) {

            System.out.println("ERROR IN WRITING RESPONSE in ReportsController" + e.getMessage());
            e.printStackTrace();
        }
        
    }


}
