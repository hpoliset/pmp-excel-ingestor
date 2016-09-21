package org.srcm.heartfulness.authorizationservice;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.ParticipantFullDetails;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.service.ChannelService;
import org.srcm.heartfulness.service.ProgramService;
import org.srcm.heartfulness.service.ReportService;
import org.srcm.heartfulness.vo.ReportVO;

/**
 * 
 * @author himasreev
 *
 */
@Service
public class PmpAuthorizationServiceImpl implements PmpAuthorizationService {

	private String role;

	private String username;

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	private ProgramService programService;

	@Autowired
	private AESEncryptDecrypt aesEncryptDecrypt;

	@Autowired
	Environment env;

	@Autowired
	private ReportService reportService;

	@Autowired
	ChannelService channelService;

	/**
	 * Method to show reports form.
	 */
	@Override
	public String showReportsForm(ModelMap modelMap) {
		getPrincipal();
		/*
		 * if (PMPConstants.LOGIN_ROLE_PRECEPTOR.equals(role)) { List<String>
		 * eventCountries = reportService.getCountriesByPreceptor(username);
		 * List<String> eventTypes =
		 * reportService.getEventTypesByPreceptor(username);
		 * modelMap.addAttribute("eventCountries", eventCountries);
		 * modelMap.addAttribute("eventTypes", eventTypes); }else if
		 * (PMPConstants.LOGIN_ROLE_ADMIN.equals(role)) {
		 */
		List<String> eventCountries = reportService.getCountries();
		List<String> eventTypes = reportService.getEventTypes();
		modelMap.addAttribute("eventCountries", eventCountries);
		modelMap.addAttribute("eventTypes", eventTypes);
		modelMap.addAttribute("programChannels", channelService.findAllActiveChannels());
		// }
		return "reportsForm";
	}

	@Override
	public String showUserProfile() {
		return "profile";
	}

	@Override
	public String showInputForm() {
		return "ingestionForm";
	}

	@Override
	public String showBulkUploadForm() {
		return "bulkUploadIngestionForm";
	}

	@Override
	public Collection<ParticipantFullDetails> getParticipants(ReportVO reportVO) {
		getPrincipal();
		/*
		 * if (PMPConstants.LOGIN_ROLE_PRECEPTOR.equals(role)) {
		 * reportVO.setCoordinator(username); }
		 */
		return reportService.getParticipants(reportVO);
	}

	/**
	 * Method to get the principal from the context holder.
	 */
	private void getPrincipal() {
		UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		role = principal.getAuthorities().toString().replaceAll("[\\[\\]]", "").trim();
		username = principal.getUsername().toString();
	}

	@Override
	public String showIndexForm() {
		return "indexTemplate";
	}

	@Override
	public String showEventsForm() {
		return "eventform";
	}

	/**
	 * Method to show the program/event form to create and update the event
	 * details with reference of ID.
	 */
	@Override
	public String showProgramForm(String encryptedProgramId, Model model) {
		// Program program = null;
		if (null == encryptedProgramId) {
			model.addAttribute("program", new Program());
		} else {
			// write service to get the program
			String decryptedProgramId = aesEncryptDecrypt.decrypt(encryptedProgramId,
					env.getProperty("security.encrypt.token"));
			Program program = programService.getProgramById(Integer.valueOf(decryptedProgramId));
			model.addAttribute("program", program);
			model.addAttribute("encryptedProgramId", encryptedProgramId);
			model.addAttribute("participantList", program.getParticipantList());
		}
		// model.addAttribute("program",new Program());
		return "programform";
	}

	/**
	 * Method to show the get the available event list for the user.
	 */
	@Override
	public ResponseEntity<?> getEventList() {
		getPrincipal();
		List<Program> programList = null;
		if (PMPConstants.LOGIN_ROLE_PRECEPTOR.equals(role)) {
			programList = programService.getProgramByEmail(username, false);
		} else if (PMPConstants.LOGIN_ROLE_ADMIN.equalsIgnoreCase(role)) {
			programList = programService.getProgramByEmail(username, true);
		}
		for (Program program : programList) {
			String encryptedProgramId = aesEncryptDecrypt.encrypt(String.valueOf(program.getProgramId()),
					env.getProperty("security.encrypt.token"));
			program.setEncryptedId(encryptedProgramId);
		}
		return new ResponseEntity<List<Program>>(programList, HttpStatus.OK);
	}

	@Override
	public String showPmpApiLogForm() {
		return "logform";

	}

	@Override
	public String showPmpApiErrorLogForm() {
		return "errorlogform";
	}

	@Override
	public String showPmpApiPopupForm() {
		return "logdetailsform";
	}

	@Override
	public String showPmpApiErrorPopupForm() {
		return "errorlogdetailsform";
	}


}
