package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.service.UserProfileService;

/**
 * 
 * @author HimaSree
 *
 */
@Controller
public class UserProfileController {
	
	@Autowired
	UserProfileService userProfileService;
	
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public String showUserProfile(@Valid @ModelAttribute("user")User user, BindingResult result, ModelMap model) {
		userProfileService.save(user);
		model.addAttribute("updateMsg","Profile Updated Successfully");
		return "profile";
	}
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView updateUserProfile(HttpServletRequest request, @ModelAttribute("user")User user) {
		String email = (String)request.getSession().getAttribute("email");
		user = userProfileService.loadUserByEmail(email);
		return new ModelAndView("profile", "user", user);
	}
	
}
