package org.srcm.heartfulness.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.srcm.heartfulness.model.LoginModel;

/*@CrossOrigin(maxAge=3600)*/
@Controller
public class LoginController {

	@Autowired
	private Environment env;

	/* @CrossOrigin(origins="http://www.google.co.in") */
	@RequestMapping(value = "/pmplogin", method = RequestMethod.GET)
	public String showLoginForm(HttpServletRequest request, HttpServletResponse response) {
		if ("no".equalsIgnoreCase(env.getProperty("srcm.login"))) {
			return "loginForm";
		} else {
			return "srcmLogin";
		}

	}

	@RequestMapping(value = "/pmpadmin", method = RequestMethod.GET)
	public String showUploadForm(HttpServletRequest request) {
		return "home";
	}

	@RequestMapping(value = "eventServices", method = RequestMethod.POST)
	public String submit(ModelMap modelMap, @ModelAttribute("loginModel") @Valid LoginModel loginModel) {
		String password = loginModel.getPassword();
		String user = loginModel.getUserName();
		if (password != null && password.equals(env.getProperty("security.user.password")) && user != null
				&& user.length() > 0 && user.equalsIgnoreCase(env.getProperty("security.user.username"))) {
			modelMap.put("userInfo", loginModel.getUserName());
			return "indexTemplate";
		} else {
			modelMap.put("error", "Invalid UserName / Password");
			return "loginForm";
		}
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String goToindex(HttpServletRequest request) {
		return "indexTemplate";
	}

}
