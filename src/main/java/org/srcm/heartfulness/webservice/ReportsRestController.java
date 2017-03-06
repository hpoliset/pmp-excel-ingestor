package org.srcm.heartfulness.webservice;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.ReportService;
import org.srcm.heartfulness.util.DateUtils;
import org.srcm.heartfulness.util.StackTraceUtils;
import org.srcm.heartfulness.util.ZipUtils;
import org.srcm.heartfulness.validator.ReportsValidator;
import org.srcm.heartfulness.vo.ReportVO;

@RestController
@RequestMapping("/api/")
public class ReportsRestController {
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	APIAccessLogService apiAccessLogService;
	
	@Autowired
	ReportsValidator reportsValidator;
	
	@RequestMapping(value = "/report/generate", method = RequestMethod.POST)
	public ResponseEntity<?> generateReport(@RequestHeader(value = "Authorization") String token,@RequestBody ReportVO reportVO,@Context HttpServletRequest httpRequest) {
		

		PMPAPIAccessLog accessLog = new PMPAPIAccessLog(null, httpRequest.getRemoteAddr(), httpRequest.getRequestURI(),
				DateUtils.getCurrentTimeInMilliSec(), null, ErrorConstants.STATUS_FAILED, null,
				StackTraceUtils.convertPojoToJson(reportVO.toString()));
		apiAccessLogService.createPmpAPIAccessLog(accessLog);

		try {
			Response eResponse = reportsValidator.validateGenerateReportsRequest(accessLog, token,reportVO);
			if (null != eResponse) {
				return new ResponseEntity<Response>(eResponse, HttpStatus.PRECONDITION_FAILED);
			}
			System.out.println(reportVO.toString());
		Collection<ParticipantFullDetails> participants = reportService.getParticipants(reportVO);

		StringBuilder sb = new StringBuilder();

		sb.append("Id\tprintName\tfirstName\tmiddleName\tlastName\temail\tmobilePhone\t")
				.append("gender\tdateOfBirth\tdateOfRegistration\tlanguage\tprofession\t")
				.append("abhyasiId\tidCardNumber\tstatus\taddressLine1\taddressLine2\tcity\tstate\tcountry\tremarks\t")
				.append("introduced\tintroducedBy\tintroductionDate\twelcomeCardNumber\twelcomeCardDate\tageGroup\t")
				.append("firstSittingTaken\tfirstSittingDate\tsecondSittingTaken\tsecondSittingDate\tthirdSittingTaken\tthirdSittingDate\t")
				.append("batch\treceiveUpdates\tsyncStatus\taimsSyncTime\tuploadStatus\t")
				.append("programId\tprogramChannel\tprogramName.\tprogramStartDate\tprogramEndDate\t")
				.append("eventPlace\teventState\teventCity\teventCountry\t")
				.append("organizationId\torganizationName\torganizationDepartment\torganizationWebSite\torganizationContactName\t")
				.append("organizationContactEmail\torganizationContactMobile\t")
				.append("preceptorName\tpreceptorIdCardNumber\twelcomeCardSignedByName\twelcomeCardSignerIdCardNumber")
				.append("\n");
		
		/*byte[] byteArray = ZipUtils.getByteArray(participants, sb);
		FileOutputStream fos = new FileOutputStream("D:\\test_Reports\\"+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) +".zip");
		fos.write(byteArray);
		fos.close();*/
		
		return new ResponseEntity<byte[]>(ZipUtils.getByteArray(participants, sb),HttpStatus.OK);
		
		} catch (Exception ex) {
			ex.printStackTrace();
			Response response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server error.");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	}

}
