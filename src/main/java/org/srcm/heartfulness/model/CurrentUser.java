package org.srcm.heartfulness.model;

import org.springframework.security.core.authority.AuthorityUtils;

/**
 * This class is customized user object user by spring for holding
 * authentication object
 * 
 * @author himasreev
 *
 */
@SuppressWarnings("serial")
public class CurrentUser extends org.springframework.security.core.userdetails.User {

	private User user;

	private String isPmpAllowed;

	private String isSahajmargAllowed;

	public CurrentUser(User user) {
		super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
		this.user = user;
	}

	/**
	 * customized constructor to get the user details other than username,
	 * password and role
	 * 
	 * @param email
	 * @param password
	 * @param role
	 * @param isPmpAllowed
	 * @param isSahajmargAllowed
	 */
	public CurrentUser(String email, String password, String role, String isPmpAllowed, String isSahajmargAllowed) {
		super(email, password, AuthorityUtils.createAuthorityList(role));
		this.isPmpAllowed = isPmpAllowed;
		this.isSahajmargAllowed = isSahajmargAllowed;
	}

	public User getUser() {
		return user;
	}

	public int getId() {
		return user.getId();
	}

	public String getRole() {
		return user.getRole();
	}

	public String getIsPmpAllowed() {
		return isPmpAllowed;
	}

	public void setIsPmpAllowed(String isPmpAllowed) {
		this.isPmpAllowed = isPmpAllowed;
	}

	public String getIsSahajmargAllowed() {
		return isSahajmargAllowed;
	}

	public void setIsSahajmargAllowed(String isSahajmargAllowed) {
		this.isSahajmargAllowed = isSahajmargAllowed;
	}

	public void setUser(User user) {
		this.user = user;
	}

}