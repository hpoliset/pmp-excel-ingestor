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

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showReportsForm(org.springframework.ui.ModelMap)
	 */
	@Override
	public String showReportsForm(ModelMap modelMap) {
		getPrincipal();
		List<String> eventCountries = reportService.getCountries();
		List<String> eventTypes = reportService.getEventTypes();
		modelMap.addAttribute("eventCountries", eventCountries);
		modelMap.addAttribute("eventTypes", eventTypes);
		modelMap.addAttribute("programChannels", channelService.findAllActiveChannelsBasedOnRole(role));
		return "reportsForm";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showUserProfile()
	 */
	@Override
	public String showUserProfile() {
		return "profile";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showInputForm()
	 */
	@Override
	public String showInputForm() {
		return "ingestionForm";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showBulkUploadForm()
	 */
	@Override
	public String showBulkUploadForm() {
		return "bulkUploadIngestionForm";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#getParticipants(org.srcm.heartfulness.vo.ReportVO)
	 */
	@Override
	public Collection<ParticipantFullDetails> getParticipants(ReportVO reportVO) {
		setRoleAndUsernameFromContext(reportVO);
		return reportService.getParticipants(reportVO);
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#getPrincipal()
	 */
	@Override
	public void getPrincipal() {
		UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		role = principal.getAuthorities().toString().replaceAll("[\\[\\]]", "").trim();
		username = principal.getUsername().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showIndexForm()
	 */
	@Override
	public String showIndexForm() {
		return "indexTemplate";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showEventsForm()
	 */
	@Override
	public String showEventsForm() {
		return "eventform";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showProgramForm(java.lang.String, org.springframework.ui.Model)
	 */
	@Override
	public String showProgramForm(String encryptedProgramId, Model model) {
		if (null == encryptedProgramId) {
			model.addAttribute("program", new Program());
		} else {
			String decryptedProgramId = aesEncryptDecrypt.decrypt(encryptedProgramId,
					env.getProperty("security.encrypt.token"));
			Program program = programService.getProgramById(Integer.valueOf(decryptedProgramId));
			model.addAttribute("program", program);
			model.addAttribute("encryptedProgramId", encryptedProgramId);
			model.addAttribute("participantList", program.getParticipantList());
		}
		return "programform";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#getEventList()
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

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showPmpApiLogForm()
	 */
	@Override
	public String showPmpApiLogForm() {
		return "logform";

	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showPmpApiErrorLogForm()
	 */
	@Override
	public String showPmpApiErrorLogForm() {
		return "errorlogform";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showPmpApiPopupForm()
	 */
	@Override
	public String showPmpApiPopupForm() {
		return "logdetailsform";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#showPmpApiErrorPopupForm()
	 */
	@Override
	public String showPmpApiErrorPopupForm() {
		return "errorlogdetailsform";
	}

	/*
	 * (non-Javadoc)
	 * @see org.srcm.heartfulness.authorizationservice.PmpAuthorizationService#setRoleAndUsernameFromContext(org.srcm.heartfulness.vo.ReportVO)
	 */
	@Override
	public ReportVO setRoleAndUsernameFromContext(ReportVO reportVO) {
		UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		reportVO.setUserRole(principal.getAuthorities().toString().replaceAll("[\\[\\]]", "").trim());
		reportVO.setUsername(principal.getUsername().toString());
		return reportVO;
	}


}
