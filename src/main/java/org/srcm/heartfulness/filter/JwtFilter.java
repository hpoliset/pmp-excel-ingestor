package org.srcm.heartfulness.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * @author HimaSree
 *
 */
public class JwtFilter extends GenericFilterBean {

	//TODO fetching secret key from properties file 
	//@Value("${jwt.secret.key}")
	private String secret = "der33343er3e3434rer343dr3434";

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {
		/*final HttpServletRequest request = (HttpServletRequest) req;
		final String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new ServletException("Missing or invalid Authorization header.");
		}
		// The part after // "Bearer "
		final String token = authHeader.substring(7);
		try {
			final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			request.setAttribute("claims", claims);
		} catch (final SignatureException e) {
			throw new ServletException("Invalid token.");
		}*/
		chain.doFilter(req, res);
	}

}