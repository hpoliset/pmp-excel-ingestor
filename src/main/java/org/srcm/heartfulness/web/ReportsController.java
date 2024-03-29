package org.srcm.heartfulness.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.srcm.heartfulness.authorizationservice.PmpAuthorizationService;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.Channel;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.service.ChannelService;
import org.srcm.heartfulness.service.ReportService;
import org.srcm.heartfulness.util.ZipUtils;
import org.srcm.heartfulness.vo.ReportVO;

/**
 * Controller - Basic Report Implementation Created by MaheshK on 11/12/15.
 */
@Controller
public class ReportsController {

	@Autowired
	private ReportService reportService;

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	private PmpAuthorizationService pmpAuthService;

	@Autowired
	ChannelService channelService;

	/**
	 * To populate the reportsForm view with event country, state , types
	 * 
	 * @param request
	 * @see {@link HttpServletRequest}
	 * @param modelMap
	 * @see {@link ModelMap}
	 * @return The reports form
	 */
	@RequestMapping(value = "/reports/reportsForm", method = RequestMethod.GET)
	public String showReportsForm(HttpServletRequest request, ModelMap modelMap, RedirectAttributes redirectAttributes) {

		try {
			authHelper.setCurrentUsertoContext(request.getSession());
			return pmpAuthService.showReportsForm(modelMap);
		} catch (AccessDeniedException e) {
			return "accessdenied";
		} catch (NullPointerException e) {
			redirectAttributes.addFlashAttribute("redirecturl", "/reports/reportsForm");
			return "redirect:/login";
		}
	}

	@RequestMapping("/reports")
	@ResponseBody
	public String index() {
		return "Greetings from Reports Controller";
	}

	/**
	 * To generate the full participant details as report based on the filter
	 * conditions
	 * 
	 * @param request
	 * @see {@link HttpServletRequest}
	 * @param response
	 * @see {@link HttpServletResponse}
	 * @param channel
	 *            - Program Channel
	 * @param fromDate
	 *            - Start Date
	 * @param tillDate
	 *            - End Date
	 * @param city
	 *            - City
	 * @param state
	 *            - State
	 * @param country
	 *            - Country
	 * @throws IOException
	 * @see {@link IOException}
	 */
	@RequestMapping(value = "/reports/generate", method = RequestMethod.POST)
	public void generateReport(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) String channel, @RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String tillDate, @RequestParam(required = false) String city,
			@RequestParam(required = false) String state, @RequestParam(required = false) String country)
					throws IOException {

		ReportVO reportVO = new ReportVO();
		ZipUtils zipUtils = new ZipUtils();
		reportVO.setChannel(channel);
		reportVO.setFromDate(fromDate);
		reportVO.setTillDate(tillDate);
		reportVO.setCountry(country);
		reportVO.setState(state);
		reportVO.setCity(city);

		authHelper.setCurrentUsertoContext(request.getSession());
		pmpAuthService.setRoleAndUsernameFromContext(reportVO);

		Collection<ParticipantFullDetails> participants = reportService.getParticipants(reportVO);

		StringBuilder sb = new StringBuilder();

		/*sb.append("Id\tprintName\tfirstName\tmiddleName\tlastName\temail\tmobilePhone\t")
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
				.append("\n");*/

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

		response.reset();
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment;filename=Report_"
				+ new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".zip");
		response.getOutputStream().write(zipUtils.getByteArray(participants, sb));
	}

	/**
	 * Fetches the list of states for the given country, to be used in the
	 * Report parameter screen.
	 * 
	 * @param country
	 *            - Event country
	 * @return the list of state
	 */
	@RequestMapping(value = "/reports/getStates", method = RequestMethod.POST)
	@ResponseBody
	public List<String> getStatesForCountry(@RequestParam(required = false, name = "country") String country) {
		List<String> eventStates = reportService.getStatesForCountry(country);
		return eventStates;
	}

	/**
	 * Fetches the list of active channel, to be used in the Report parameter
	 * screen.
	 * 
	 * @return the list of state
	 */
	@RequestMapping(value = "/reports/getProgramChannels", method = RequestMethod.POST)
	@ResponseBody
	public List<Channel> getProgramChannels() {
		List<Channel> programChannels = channelService.findAllActiveChannels();
		return programChannels;
	}
}
