package org.srcm.heartfulness.webservice;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.UserProfileService;

/**
 * 
 * @author HimaSree
 *
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	Environment env;

	/**
	 * This method is used to get userprofile
	 * 
	 * @param accessToken
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	// TODO: @PreAuthorize(("@securityService.isTokenValid())"))
	public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization") String token) {
		try {
			Result result = userProfileService.getUserProfile(encryptDecryptAES.decrypt(token,
					env.getProperty("security.encrypt.token")));
			UserProfile srcmProfile = result.getUserProfile()[0];
			User user = userProfileService.loadUserByEmail(srcmProfile.getEmail());
			if (null == user) {
				user = new User();
				user.setName(srcmProfile.getName());
				user.setFirst_name(srcmProfile.getFirst_name());
				user.setLast_name(srcmProfile.getLast_name());
				user.setEmail(srcmProfile.getEmail());
				userProfileService.save(user);
			}
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", "IOException occured.");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @param userId
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user,
			@RequestHeader(value = "Authorization") String token) {
		try {
			Result result = userProfileService.getUserProfile(encryptDecryptAES.decrypt(token,
					env.getProperty("security.encrypt.token")));
			UserProfile srcmProfile = result.getUserProfile()[0];
			User pmpUser = userProfileService.loadUserByEmail(srcmProfile.getEmail());
			if (pmpUser != null && id == pmpUser.getId()) {
				if (id == pmpUser.getId()) {
					userProfileService.save(user);
				}
			}
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", "IOException occured.");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
