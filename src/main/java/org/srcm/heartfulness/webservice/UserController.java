package org.srcm.heartfulness.webservice;

import io.jsonwebtoken.Claims;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
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

	/**
	 * This method is used to get userprofile
	 * 
	 * @param accessToken
	 * @param request
	 * @return
	 */
	// T
	@RequestMapping(value = "/me", method = RequestMethod.GET)
	// TODO: @PreAuthorize(("@securityService.isTokenValid())"))
	public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
		try {
			Claims claims = (Claims) request.getAttribute("claims");
			User user = userProfileService.loadUserByEmail(claims.getSubject());
			// user details is not available in pmp database
			if (null == user) {
				Result result = userProfileService.getUserProfile((String) claims.get("accessToken"));
				UserProfile srcmProfile = result.getUserProfile()[0];
				user = new User();
				user.setName(srcmProfile.getName());
				user.setFirstName(srcmProfile.getFirst_name());
				user.setLastName(srcmProfile.getLast_name());
				user.setEmail(srcmProfile.getEmail());
				userProfileService.save(user);
			}
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (IOException e) {
			e.printStackTrace();
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
	@RequestMapping(value = "/me/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user, HttpServletRequest request) {
		Claims claims = (Claims) request.getAttribute("claims");
		User pmpUser = userProfileService.loadUserByEmail(claims.getSubject());
		if(pmpUser.getId() == id){
			//update
			userProfileService.save(user);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}else{
			ErrorResponse error = new ErrorResponse("Authentication credentials were not provided.", "Unauthorized");
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNAUTHORIZED);
		}
		
	}

}
