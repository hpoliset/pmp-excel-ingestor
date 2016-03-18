package org.srcm.heartfulness.authorization;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.constants.PMPConstants;
import org.srcm.heartfulness.model.CurrentUser;
import org.srcm.heartfulness.service.UserProfileService;

/**
 * 
 * @author himasreev
 *
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserProfileService userProfileService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
	
	/**
	 * method to get the user details and create authorities for the user and 
	 * stores in spring user object
	 */
	@Override
	public Authentication authenticate(Authentication token) {
		LOGGER.debug("Inside authentication provider {}",token.getName());
		String username = token.getName();
		String password = (String) token.getCredentials();
		org.srcm.heartfulness.model.User user=userProfileService.loadUserByEmail(username);
		if(null==user){
			user=new org.srcm.heartfulness.model.User();
			user.setRole(PMPConstants.LOGIN_ROLE_SEEKER);
			user.setEmail(username);
			user.setPassword(password);
			user.setIspmpAllowed(PMPConstants.REQUIRED_NO);
			user.setIsSahajmargAllowed(PMPConstants.REQUIRED_NO);
		}else{
			if (user.getIspmpAllowed().equalsIgnoreCase(PMPConstants.REQUIRED_YES)) {
				user.setRole(PMPConstants.LOGIN_ROLE_ADMIN);
			}
		}
		CurrentUser currentUser=new CurrentUser(user.getEmail(), password, user.getRole(),user.getIspmpAllowed(),user.getIsSahajmargAllowed());
		Collection<? extends GrantedAuthority> authorities =AuthorityUtils.createAuthorityList(user.getRole().toString());
		LOGGER.debug("Authentication successful for {}",token.getName());
		return new UsernamePasswordAuthenticationToken(currentUser, password, authorities);
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}
}