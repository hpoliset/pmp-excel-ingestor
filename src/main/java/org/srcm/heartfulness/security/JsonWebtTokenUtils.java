package org.srcm.heartfulness.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.srcm.heartfulness.model.json.request.AuthenticationRequest;
import org.srcm.heartfulness.model.json.response.SrcmAuthenticationResponse;

/**
 * 
 * @author HimaSree
 *
 */
@Component
public class JsonWebtTokenUtils {

	// @Value("${jwt.secret.key}")
	private String secret = "der33343er3e3434rer343dr3434";

	//@Value("${jwt.token.expiration}")
	private Long expiration = 404800l;

	/**
	 * 
	 * @param user
	 * @param authenticationResponse
	 * @return
	 */
	public String generateToken(AuthenticationRequest authenticationRequest,
			SrcmAuthenticationResponse authenticationResponse) {
		Map<String, Object> claims = new HashMap<String, Object>();
		// since email is the username
		claims.put("sub", authenticationRequest.getUsername());
		claims.put("created", this.generateCurrentDate());
		claims.put("accessToken", authenticationResponse.getAccess_token());
		return this.generateToken(claims);
	}

	/**
	 * 
	 * @param claims
	 * @return
	 */
	private String generateToken(Map<String, Object> claims) {
		return Jwts.builder().setClaims(claims).setExpiration(this.generateExpirationDate())
				.signWith(SignatureAlgorithm.HS512, this.secret).compact();
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public boolean isTokenValid(String token) {
		String username;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			username = claims.getSubject();
			if (username.length() > 0)
				return true;
		} catch (Exception e) {
			username = null;
		}
		return false;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public String getEmailFromToken(String token) {
		String email;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			email = (String) claims.get("email");
		} catch (Exception e) {
			email = null;
		}
		return email;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public Date getCreatedDateFromToken(String token) {
		Date created;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			created = new Date((Long) claims.get("created"));
		} catch (Exception e) {
			created = null;
		}
		return created;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	/**
	 * 
	 * @param token
	 * @return
	 */
	public Boolean isTokenExpired(String token) {
		final Date expiration = this.getExpirationDateFromToken(token);
		return expiration.before(this.generateCurrentDate());
	}

	/**
	 * 
	 * @return
	 */
	private Date generateCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 
	 * @return
	 */
	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + this.expiration * 1000);
	}

}
