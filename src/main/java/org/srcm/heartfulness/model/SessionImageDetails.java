package org.srcm.heartfulness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author himasreev
 *
 */
public class SessionImageDetails {

	@JsonIgnore
	private int imageId;
	@JsonIgnore
	private int sessionId;
	private String imageName;
	@JsonIgnore
	private String imagePath;
	private String uploadedBy;
	private String fileType;
	private String presignedURL;

	public SessionImageDetails() {
		super();
	}

	public SessionImageDetails(int imageId, int sessionId, String imageName, String imagePath, String uploadedBy) {
		super();
		this.imageId = imageId;
		this.sessionId = sessionId;
		this.imageName = imageName;
		this.imagePath = imagePath;
		this.uploadedBy = uploadedBy;
	}

	public SessionImageDetails(int sessionId, String imageName, String imagePath, String uploadedBy) {
		super();
		this.sessionId = sessionId;
		this.imageName = imageName;
		this.imagePath = imagePath;
		this.uploadedBy = uploadedBy;
	}

	public SessionImageDetails(int sessionId, String imageName, String imagePath, String fileType, String uploadedBy) {
		super();
		this.sessionId = sessionId;
		this.imageName = imageName;
		this.imagePath = imagePath;
		this.fileType = fileType;
		this.uploadedBy = uploadedBy;
	}
	
	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getPresignedURL() {
		return presignedURL;
	}

	public void setPresignedURL(String presignedURL) {
		this.presignedURL = presignedURL;
	}
	
}
