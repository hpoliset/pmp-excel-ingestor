package org.srcm.heartfulness.webservice;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.encryption.decryption.AESEncryptDecrypt;
import org.srcm.heartfulness.model.IntroductionDetails;
import org.srcm.heartfulness.model.User;
import org.srcm.heartfulness.model.json.response.ErrorResponse;
import org.srcm.heartfulness.model.json.response.Introductionresponse;
import org.srcm.heartfulness.model.json.response.Result;
import org.srcm.heartfulness.model.json.response.UserProfile;
import org.srcm.heartfulness.service.PMPMailService;
import org.srcm.heartfulness.service.UserProfileService;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/request")
public class IntroductionController {

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private AESEncryptDecrypt encryptDecryptAES;

	@Autowired
	PMPMailService mailservice;

	@Autowired
	Environment env;

	private static final Logger LOGGER = LoggerFactory.getLogger(IntroductionController.class);
	
	/**
	 * method to store the request details of seeker 
	 * 	and sends welcome mail to the user & mail with user details to HFN team
	 * @param user
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "introduce", method = RequestMethod.POST)
	public ResponseEntity<?> registerRequest(@RequestBody User user,@RequestHeader(value = "Authorization") String token) {
		Introductionresponse response=new Introductionresponse();
		try{
			Result result = userProfileService.getUserProfile(encryptDecryptAES.decrypt(token,
					env.getProperty("security.encrypt.token")));
			UserProfile srcmProfile = result.getUserProfile()[0];
			if(user.getEmail().equalsIgnoreCase(srcmProfile.getEmail())){
				User newUser = userProfileService.loadUserByEmail(srcmProfile.getEmail());
				if (null == newUser) {
					newUser = new User();
					newUser.setName(srcmProfile.getName());
					newUser.setFirst_name(srcmProfile.getFirst_name());
					newUser.setLast_name(srcmProfile.getLast_name());
					newUser.setEmail(srcmProfile.getEmail());
					newUser.setMessage(user.getMessage());
					newUser.setName(user.getName());
					newUser.setCity(user.getCity());
					newUser.setCountry(user.getCountry());
					newUser.setState(user.getState());
					newUser.setMobile(user.getMobile());
					userProfileService.save(newUser);
				}else{
					newUser.setMessage(user.getMessage());
					newUser.setName(user.getName());
					newUser.setCity(user.getCity());
					newUser.setCountry(user.getCountry());
					newUser.setState(user.getState());
					newUser.setMobile(user.getMobile());
					userProfileService.save(newUser);
				}
				newUser.setMessage(user.getMessage());
				IntroductionDetails introdet = userProfileService.updateIntroductionDetails(newUser);
				response.setProfileCreatedForUser(PMPConstants.REQUIRED_NO);
				response.setIntroduced(PMPConstants.REQUIRED_YES);
				mailservice.sendMail(newUser, introdet);
				LOGGER.debug("Mail sent successfully..");
				return new ResponseEntity<Introductionresponse>(response, HttpStatus.OK);
			}else{
				ErrorResponse error = new ErrorResponse("Email ID doesnot match with logged in e-mail", "username mismatch.");
				return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
			}
		} catch (HttpClientErrorException e) {
			//ErrorResponse error = new ErrorResponse(e.getResponseBodyAsString(), e.getMessage());
			ObjectMapper mapper = new ObjectMapper();
			ErrorResponse error;
			try {
				error = mapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
				return new ResponseEntity<ErrorResponse>(error, e.getStatusCode());
			} catch (IOException e1) {
				ErrorResponse err = new ErrorResponse("Invalid Request.", e1.getMessage());
				return new ResponseEntity<ErrorResponse>(err, HttpStatus.NOT_FOUND);
			}
			//return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (SQLException e) {
			ErrorResponse error = new ErrorResponse("Email ID doesnot exist", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		}  catch (AddressException e) {
			ErrorResponse error = new ErrorResponse("Address Exception", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (AuthenticationFailedException e) {
			ErrorResponse error = new ErrorResponse("Host - Authentication failed", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (MessagingException e) {
			ErrorResponse error = new ErrorResponse("Email Not Sent", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Error", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * method to create the new profile the seeker
	 * 				 and stores the request details of seeker
	 * 				 and sends welcome mail to the user & mail with user details to HFN team
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "createprofileandintroduce", method = RequestMethod.POST)
	public ResponseEntity<?> createProfileandRegisterRequest(@RequestBody User user) {
		Introductionresponse response=new Introductionresponse();
		try{
			user.setUser_type("se");
			User newUser = userProfileService.createUser(user);
			newUser.setMessage(user.getMessage());
			if (null != userProfileService.loadUserByEmail(newUser.getEmail())) {
				IntroductionDetails introdet = userProfileService.updateIntroductionDetails(newUser);
				response.setProfileCreatedForUser(PMPConstants.REQUIRED_YES);
				response.setIntroduced(PMPConstants.REQUIRED_YES);
				mailservice.sendMail(newUser, introdet);
			}
			return new ResponseEntity<Introductionresponse>(response, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<String>(e.getResponseBodyAsString(), e.getStatusCode());
		} catch (SQLException e) {
			ErrorResponse error = new ErrorResponse("Failed Request", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			ErrorResponse error = new ErrorResponse("Please try after some time.", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.REQUEST_TIMEOUT);
		} catch (AddressException e) {
			ErrorResponse error = new ErrorResponse("Address Exception", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (AuthenticationFailedException e) {
			ErrorResponse error = new ErrorResponse("Authentication failed while sending mail", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		} catch (MessagingException e) {
			ErrorResponse error = new ErrorResponse("Email Not Sent", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			ErrorResponse error = new ErrorResponse("Error", e.getMessage());
			return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
		}
	}

}
