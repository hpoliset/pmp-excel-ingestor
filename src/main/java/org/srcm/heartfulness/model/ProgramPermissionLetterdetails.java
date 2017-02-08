package org.srcm.heartfulness.model;

/**
 * Class to hold the coordinator permission letter details of an Program/event.
 * 
 * @author himasreev
 *
 */
public class ProgramPermissionLetterdetails {

	private int permissionLetterId;

	private int programId;

	private String permissionLetterName;

	private String permissionLetterPath;

	private String uploadedBy;
	
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

	@Override
	public String toString() {
		return "ProgramPermissionLetterdetails [permissionLetterId=" + permissionLetterId + ", programId=" + programId
				+ ", permissionLetterName=" + permissionLetterName + ", permissionLetterPath=" + permissionLetterPath
				+ ", uploadedBy=" + uploadedBy + "]";
	}

}
