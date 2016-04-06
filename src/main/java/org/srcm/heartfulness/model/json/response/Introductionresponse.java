package org.srcm.heartfulness.model.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is the response of Introductory request
 * @author himasreev
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Introductionresponse {
	
	@JsonProperty("profile_created")
	private String profileCreatedForUser;
	
	@JsonProperty("introduced")
	private String introduced;

	public Introductionresponse() {
		super();
	}

	public Introductionresponse(String profileCreatedForUser, String introduced) {
		super();
		this.profileCreatedForUser = profileCreatedForUser;
		this.introduced = introduced;
	}

	public String getProfileCreatedForUser() {
		return profileCreatedForUser;
	}

	public void setProfileCreatedForUser(String profileCreatedForUser) {
		this.profileCreatedForUser = profileCreatedForUser;
	}

	public String getIntroduced() {
		return introduced;
	}

	public void setIntroduced(String introduced) {
		this.introduced = introduced;
	}

	@Override
	public String toString() {
		return "Introductionresponse [profileCreatedForUser=" + profileCreatedForUser + ", introduced=" + introduced
				+ "]";
	}

}
