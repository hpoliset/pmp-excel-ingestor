package org.srcm.heartfulness.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.service.UserProfileService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller - Login Implementation
 * 
 * @author HimaSree
 *
 */
@Controller
public class LoginController {

	@Autowired
	private UserProfileService userProfileService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String showLoginForm(HttpServletRequest request, Model model) {
		model.addAttribute("user", new User());
		return "index";
	}
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String showForm(HttpServletRequest request, Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("newUser", new User());
		return "Home";
	}

	/**
	 * To validate Login with username and password If user details does not
	 * exist in pmp db, user profile will be fetched from SRCM
	 * 
	 * @param modelMap
	 * @param username
	 * @param password
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String login(ModelMap modelMap, @PathVariable @RequestParam String username, @RequestParam String password,
			HttpServletRequest request) {
		try {
			SrcmAuthenticationResponse AuthenticationResponse = userProfileService.ValidateLogin(new AuthenticationRequest(username, password));
			User user = null;
			user = userProfileService.loadUserByEmail(username);
			if (user == null) {
				Result result = userProfileService.getUserProfile(AuthenticationResponse.getAccess_token());
				user = new User();
				user.setFirstName(result.getUserProfile()[0].getFirst_name());
				user.setLastName(result.getUserProfile()[0].getLast_name());
				user.setEmail(result.getUserProfile()[0].getEmail());
				userProfileService.save(user);
			}
			HttpSession session = request.getSession(true);
			session.setAttribute("username", user.getFirstName() + " " + user.getLastName());
			session.setAttribute("email", username);
			session.setAttribute("AuthenticationResponse", AuthenticationResponse);
			modelMap.addAttribute("user",user);
			return "success";
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			System.out.println(e.getResponseBodyAsString());
			ObjectMapper mapper = new ObjectMapper();
			ErrorResponse error;
			try {
				error = mapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
				modelMap.put("error", "The e-mail address and/or password you specified are not correct.");
				return "error";
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signUp(@Valid @ModelAttribute("newUser") User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "error";
		}
		try {
			user.setUserType("se");
			user = userProfileService.createUser(user);
			//model.addAttribute("passowrdRestUrl",user.getPasswordResetUrl());
		} catch (HttpClientErrorException e) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				if (null != mapper.readTree(e.getResponseBodyAsString()).get("non_field_errors").toString()) {
					bindingResult.rejectValue("email", "error.newUser", "An account already exists for this email.");
					return "accounterror";
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "changepassword";
	}

	/**
	 * To signout the current logged in user
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/signout", method = RequestMethod.GET)
	public String SignOut(HttpServletRequest request) {
		request.getSession().removeAttribute("username");
		request.getSession().removeAttribute("AuthenticationResponse");
		request.getSession().invalidate();
		 return "redirect:home";
	}

}
