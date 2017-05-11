package org.srcm.heartfulness.webservice;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.srcm.heartfulness.constants.ErrorConstants;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.model.PMPAPIAccessLog;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.model.json.response.Response;
import org.srcm.heartfulness.service.APIAccessLogService;
import org.srcm.heartfulness.service.ChannelService;
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
	
	@Autowired
	ChannelService channelService;
	
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

		sb.append("ParticipantName\tFirstName\tMiddleName\tLastName\tEmail\tMobilePhone\t")
				.append("Gender\tDateOfBirth\tDateOfRegistration\tLanguage\tProfession\t")
				.append("AbhyasiId\tIdCardNumber\tStatus\tAddressLine1\tAddressLine2\tCity\tState\tCountry\tRemarks\t")
				.append("Introduced\tIntroducedBy\tIntroductionDate\tWelcomeCardNumber\tWelcomeCardDate\tAgeGroup\t")
				.append("FirstSittingTaken\tFirstSittingDate\tSecondSittingTaken\tSecondSittingDate\tThirdSittingTaken\tThirdSittingDate\t")
				.append("Batch\tReceiveUpdates\tSyncStatus\tAimsSyncTime\tUploadStatus\t")
				.append("ProgramChannel\tProgramName.\tProgramStartDate\tProgramEndDate\t")
				.append("EventPlace\tEventState\tEventCity\tEventCountry\t")
				.append("OrganizationId\tOrganizationName\tOrganizationDepartment\tOrganizationWebSite\tOrganizationContactName\t")
				.append("OrganizationContactEmail\tOrganizationContactMobile\t")
				.append("PreceptorName\tPreceptorIdCardNumber\tWelcomeCardSignedByName\tWelcomeCardSignerIdCardNumber")
				.append("\n");
		
		/*byte[] byteArray = ZipUtils.getByteArray(participants, sb);
		FileOutputStream fos = new FileOutputStream("D:\\test_Reports\\"+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) +".zip");
		fos.write(byteArray);
		fos.close();*/
		
		Response response = new Response(ErrorConstants.STATUS_SUCCESS, ZipUtils.getByteArray(participants, sb));
		return new ResponseEntity<Response>(response, HttpStatus.OK);
		//return new ResponseEntity<byte[]>(ZipUtils.getByteArray(participants, sb),HttpStatus.OK);
		
		} catch (Exception ex) {
			ex.printStackTrace();
			Response response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server error.");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	}
	/**
	 * Fetches the list of states for the given country, to be used in the
	 * Report parameter screen.
	 * 
	 * @param country
	 *            - Event country
	 * @return the list of state
	 */
	@RequestMapping(value = "/report/getCountries", method = RequestMethod.POST)
	public ResponseEntity<?> getCountrries() {
		try {
			List<String> eventCountries = reportService.getCountries();
			return new ResponseEntity<List<String>>(eventCountries, HttpStatus.OK);
		} catch (Exception e) {
			Response response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server error.");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	}
	/**
	 * Fetches the list of states for the given country, to be used in the
	 * Report parameter screen.
	 * 
	 * @param country
	 *            - Event country
	 * @return the list of state
	 */
	@RequestMapping(value = "/report/getStates", method = RequestMethod.POST)
	public ResponseEntity<?> getStatesForCountry(@RequestParam(required = false, name = "country") String country) {
		try {
			List<String>  eventStates = reportService.getStatesForCountry(country);
			return new ResponseEntity<List<String>>(eventStates, HttpStatus.OK);
		} catch (Exception e) {
			Response response = new Response(ErrorConstants.STATUS_FAILED, "Internal Server error.");
			return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	}

	/**
	 * Fetches the list of active channel, to be used in the Report parameter
	 * screen.
	 * 
	 * @return the list of Channel
	 */
	@RequestMapping(value = "/report/getProgramChannels", method = RequestMethod.POST)
	public ResponseEntity<?> getProgramChannels() {
		List<Channel> programChannels = channelService.findAllActiveChannels();
		return new ResponseEntity<List<Channel>>(programChannels, HttpStatus.OK);
	}

}