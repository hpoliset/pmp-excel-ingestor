package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	
	/**
	 * method to get the user profile
	 * @param request
	 * @param response
	 * @param user
	 * @param session
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String getUserProfile(HttpServletRequest request,HttpServletResponse response, @ModelAttribute("user")User user,HttpSession session,ModelMap map) {
		
		user=(User) session.getAttribute("user");
		System.out.println(user);
		map.addAttribute("user",user);
		map.addAttribute("username",user.getFirst_name());
		map.addAttribute("id",user.getId());
		return "profile";
	}
	
	/**
	 * method to update profile
	 * @param request
	 * @param response
	 * @param user
	 * @param session
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public String updateUserProfileInSession(HttpServletRequest request,HttpServletResponse response, @ModelAttribute("user")User user,HttpSession session,ModelMap map) {
		map.addAttribute("username",user.getFirst_name());
		session.setAttribute("user", user);
		System.out.println(user);
		map.addAttribute("user",user);
		map.addAttribute("id",user.getId());
		map.addAttribute("updateDiv","Profile Updated successfully");
		return "profile";
	}
	
}
