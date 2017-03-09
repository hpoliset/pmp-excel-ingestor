package org.srcm.heartfulness.model;

/**
 * 
 * @author himasreev
 *
 */
public class SessionImageDetails {

	private int imageId;
	private int sessionId;
	private String imageName;
	private String imagePath;
	private String uploadedBy;

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
}
