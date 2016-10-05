package org.srcm.heartfulness.helper;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.srcm.heartfulness.authorization.CustomAuthenticationProvider;

/**
 * This class is the helper class for authorization.
 * 
 * @author HimaSree
 *
 */
@Component
public class AuthorizationHelper {

	@Autowired
	private CustomAuthenticationProvider authenticationProvider;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHelper.class);

	/**
	 * method to auto login with username and password through spring API
	 * 
	 * @param username
	 * @param password
	 */
	public void doAutoLogin(String username, String password) {
		try {
			LOGGER.debug("Trying to autoLogin with {}", username);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			Authentication authentication = this.authenticationProvider.authenticate(token);
			LOGGER.debug("Logging in with [{}]", authentication.getPrincipal());
			SecurityContext ctx = SecurityContextHolder.createEmptyContext();
			SecurityContextHolder.setContext(ctx);
			ctx.setAuthentication(authentication);
		} catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			LOGGER.error("Failure in autoLogin", e);
		}
	}

	/**
	 * method to set the authentication object to the security context holder
	 * 
	 * @param session
	 */
	public void setcurrentUsertoContext(HttpSession session) throws NullPointerException {
		LOGGER.debug("Trying to set Principle");
		UserDetails currentUser = (UserDetails) session.getAttribute("Authentication");
		Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(),
				currentUser.getAuthorities());
		SecurityContext ctx = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(ctx);
		ctx.setAuthentication(auth);
		LOGGER.debug("Principle set for the user: {}", currentUser.getUsername());

	}

}
