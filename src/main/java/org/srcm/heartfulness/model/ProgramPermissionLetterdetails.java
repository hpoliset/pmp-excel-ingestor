package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to hold the coordinator permission letter details of an Program/event.
 * 
 * @author himasreev
 *
 */
public class ProgramPermissionLetterdetails {

	@JsonIgnore
	private int permissionLetterId;
	@JsonIgnore
	private int programId;
	@JsonIgnore
	private String permissionLetterPath;

	private String permissionLetterName;
	private String uploadedBy;
	@JsonProperty("permission_given_by")
	private String prmsGvnBy;
	@JsonProperty("permission_given_by_designation")
	private String prmsGvrDesignation;
	@JsonProperty("permission_given_by_phone")
	private String prmsGvrPhone;
	@JsonProperty("permission_given_by_email")
	private String prmsGvrEmailId;
	private String presignedURL;

	public ProgramPermissionLetterdetails() {
	}

	public ProgramPermissionLetterdetails(int programId, String permissionLetterName, String permissionLetterPath,
			String uploadedBy) {
		super();
		this.programId = programId;
		this.permissionLetterName = permissionLetterName;
		this.permissionLetterPath = permissionLetterPath;
		this.uploadedBy = uploadedBy;
	}
	
	public ProgramPermissionLetterdetails(int programId, String permissionLetterName, String permissionLetterPath,
			String uploadedBy, String prmsGvnBy, String prmsGvrDesignation, String prmsGvrPhone, String prmsGvrEmailId) {
		super();
		this.programId = programId;
		this.permissionLetterName = permissionLetterName;
		this.permissionLetterPath = permissionLetterPath;
		this.uploadedBy = uploadedBy;
		this.prmsGvnBy = prmsGvnBy;
		this.prmsGvrDesignation = prmsGvrDesignation;
		this.prmsGvrPhone = prmsGvrPhone;
		this.prmsGvrEmailId = prmsGvrEmailId;
	}

	public int getPermissionLetterId() {
		return permissionLetterId;
	}

	public void setPermissionLetterId(int permissionLetterId) {
		this.permissionLetterId = permissionLetterId;
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public String getPermissionLetterName() {
		return permissionLetterName;
	}

	public void setPermissionLetterName(String permissionLetterName) {
		this.permissionLetterName = permissionLetterName;
	}

	public String getPermissionLetterPath() {
		return permissionLetterPath;
	}

	public void setPermissionLetterPath(String permissionLetterPath) {
		this.permissionLetterPath = permissionLetterPath;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	
	public String getPrmsGvnBy() {
		return prmsGvnBy;
	}

	public void setPrmsGvnBy(String prmsGvnBy) {
		this.prmsGvnBy = prmsGvnBy;
	}

	public String getPrmsGvrDesignation() {
		return prmsGvrDesignation;
	}

	public void setPrmsGvrDesignation(String prmsGvrDesignation) {
		this.prmsGvrDesignation = prmsGvrDesignation;
	}

	public String getPrmsGvrPhone() {
		return prmsGvrPhone;
	}

	public void setPrmsGvrPhone(String prmsGvrPhone) {
		this.prmsGvrPhone = prmsGvrPhone;
	}

	public String getPrmsGvrEmailId() {
		return prmsGvrEmailId;
	}

	public void setPrmsGvrEmailId(String prmsGvrEmailId) {
		this.prmsGvrEmailId = prmsGvrEmailId;
	}

	public String getPresignedURL() {
		return presignedURL;
	}

	public void setPresignedURL(String presignedURL) {
		this.presignedURL = presignedURL;
	}

	@Override
	public String toString() {
		return "ProgramPermissionLetterdetails [permissionLetterId=" + permissionLetterId + ", programId=" + programId
				+ ", permissionLetterName=" + permissionLetterName + ", permissionLetterPath=" + permissionLetterPath
				+ ", uploadedBy=" + uploadedBy + "]";
	}

}
