package org.srcm.heartfulness.webservice;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.helper.AuthorizationHelper;
import org.srcm.heartfulness.model.CurrentUser;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;
import org.srcm.heartfulness.service.UserProfileService;

/**
 * 
 * @author HimaSree
 *
 */
@RestController
@RequestMapping("/api/")
public class AuthenticationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	Environment env;

	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	AuthorizationHelper authHelper;

	/**
	 * method to authenticate the user with user email and password by calling
	 * srcm api
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session,
			ModelMap model) {
		try {
			LOGGER.debug("Trying to Authenticate :  {}", authenticationRequest.getUsername());
			SrcmAuthenticationResponse authenticationResponse = userProfileService.ValidateLogin(authenticationRequest);
			authenticationResponse.setAccess_token(encryptDecryptAES.encrypt(authenticationResponse.getAccess_token(),
					env.getProperty("security.encrypt.token")));
			authenticationResponse.setRefresh_token(encryptDecryptAES.encrypt(
					authenticationResponse.getRefresh_token(), env.getProperty("security.encrypt.token")));
			LOGGER.debug("User:{} is validated and token is generated", authenticationRequest.getUsername());
			authHelper.doAutoLogin(authenticationRequest.getUsername(), authenticationRequest.getPassword());
			session.setAttribute("Authentication", SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal());
			model.addAttribute("Auth", session.getAttribute("Authentication"));
			CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			authenticationResponse.setIspmpAllowed(currentUser.getIsPmpAllowed());
			authenticationResponse.setIsSahajmargAllowed(currentUser.getIsSahajmargAllowed());
			return new ResponseEntity<SrcmAuthenticationResponse>(authenticationResponse, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Invalid Credentials.", "");
			return new ResponseEntity<ErrorResponse>(error, e.getStatusCode());
		} catch (IOException e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOGGER.error("Error occured while authenticating :{}", authenticationRequest.getUsername(), e);
			ErrorResponse error = new ErrorResponse("Please try after some time.", "Server Connection time out");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
