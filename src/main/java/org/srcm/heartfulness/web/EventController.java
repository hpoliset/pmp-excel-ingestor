package org.srcm.heartfulness.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.srcm.heartfulness.authorizationservice.PmpAuthorizationService;
import org.srcm.heartfulness.constants.SMSConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.Participant;
import org.srcm.heartfulness.model.Program;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.service.ProgramService;

/**
 * Controller - Event
 * 
 * @author himasreev
 *
 */
@Controller
public class EventController {

	@Autowired
	AuthorizationHelper authHelper;

	@Autowired
	private PmpAuthorizationService pmpAuthService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private AESEncryptDecrypt aesEncryptDecrypt;

	@Autowired
	Environment env;

	@RequestMapping(value = "/updateevent", method = RequestMethod.GET)
	public String showEventForm(@RequestParam(required = false, value = "id") String encryptedValue,
			HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + SMSConstants.SMS_HEARTFULNESS_UPDATEEVENT_URL + encryptedValue;
	}

	@RequestMapping(value = "/saveevent", method = RequestMethod.POST)
	public String modifyEvent(@ModelAttribute("program") Program program, Model model, BindingResult result) {
		if (!result.hasErrors()) {
			programService.createProgram(program);
			model.addAttribute("message", "Event successfully updated ");
		}
		model.addAttribute("program", program);
		return "programform_new";
	}

	@RequestMapping(value = "/eventForm", method = RequestMethod.GET)
	public String showEventForm(Model model, HttpServletRequest request) {
		try {
			authHelper.setCurrentUsertoContext(request.getSession());
			return pmpAuthService.showEventsForm();
		} catch (AccessDeniedException e) {
			return "accessdenied";
		} catch (NullPointerException e) {
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/programForm", method = RequestMethod.GET)
	public String showProgramForm(Model model,
			@RequestParam(required = false, name = "programId") String encryptedProgramId, HttpServletRequest request) {
		try {
			authHelper.setCurrentUsertoContext(request.getSession());
			return pmpAuthService.showProgramForm(encryptedProgramId, model);
		} catch (AccessDeniedException e) {
			return "accessdenied";
		} catch (NullPointerException e) {
			return "redirect:/home";
		}
	}

	// @ResponseBody
	@RequestMapping(value = "/createEvent", method = RequestMethod.POST)
	public String createProgram(@Valid @ModelAttribute("program") Program program, BindingResult result, Model model) {

		if (result.hasErrors()) {

			if (!program.getEncryptedId().isEmpty()) {

				String decryptedProgramId = aesEncryptDecrypt.decrypt(program.getEncryptedId(),
						env.getProperty("security.encrypt.token"));
				List<Participant> participantList = programService.getProgramById(Integer.valueOf(decryptedProgramId))
						.getParticipantList();
				model.addAttribute("encryptedProgramId", program.getEncryptedId());
				model.addAttribute("participantList", participantList);
			}
			model.addAttribute("program", program);
			return "programform";
		}

		if (program != null && program.getProgramId() == 0 && program.getEncryptedId().isEmpty()) {
			programService.createProgram(program);
			model.addAttribute("result", "Event has been successfully created");
			return "eventresponse";
		} else {
			String decryptedProgramId = aesEncryptDecrypt.decrypt(program.getEncryptedId(),
					env.getProperty("security.encrypt.token"));
			program.setProgramId(Integer.valueOf(decryptedProgramId));
			programService.createProgram(program);
			model.addAttribute("result", "Event has been successfully updated");
			return "eventresponse";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getEventList", method = RequestMethod.POST)
	public ResponseEntity<?> getEventList(HttpSession session, Model model) {
		try {
			authHelper.setCurrentUsertoContext(session);
			return pmpAuthService.getEventList();
		} catch (AccessDeniedException e) {
			return new ResponseEntity<String>("Accessdenied", HttpStatus.UNAUTHORIZED);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getParticipantList", method = RequestMethod.POST)
	public ResponseEntity<?> loadParticipants(Model model,
			@RequestParam(required = false, name = "programId") String encryptedProgramId) {
		List<Participant> participantList = new ArrayList<Participant>();
		if (!encryptedProgramId.isEmpty()) {
			String decryptedProgramId = aesEncryptDecrypt.decrypt(encryptedProgramId,
					env.getProperty("security.encrypt.token"));
			participantList = programService.getParticipantByProgramId(Integer.valueOf(decryptedProgramId),null,null);
		}
		// model.addAttribute("participantListSize",participantList);
		return new ResponseEntity<List<Participant>>(participantList, HttpStatus.OK);
	}

	/**
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showHome(HttpServletRequest request, Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("newUser", new User());
		return "home_new";
	}

	/**
	 * To signout the current logged in user
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String SignOut(HttpServletRequest request, ModelMap model) {
		request.getSession().removeAttribute("username");
		request.getSession().removeAttribute("AuthenticationResponse");
		request.getSession().invalidate();
		model.addAttribute("signout", "You have signed out successfully.");
		return "redirect:login";
	}
}
