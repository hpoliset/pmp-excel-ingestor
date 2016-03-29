package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.service.UserProfileService;

/**
 * Controller - Login Implementation
 * 
 * @author himasreev
 *
 */
@Controller
public class LoginController {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	AuthorizationHelper authHelper;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("newUser", new User());
		return "home";
	}

	/**
	 * To signout the current logged in user
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public String SignOut(HttpServletRequest request, ModelMap model) {
		request.getSession().removeAttribute("username");
		request.getSession().removeAttribute("AuthenticationResponse");
		request.getSession().invalidate();
		model.addAttribute("signout", "You have signed out successfully.");
		return "redirect:home";
	}

}
