package org.srcm.heartfulness.model;

/**
 * 
 * @author himasreev
 *
 */
public class SessionFiles {

	private int fileId;

	private int sessionId;

	private String fileName;

	private String filePath;

	private String fileType;

	private String uploadedBy;

	public SessionFiles() {
		super();
	}

	public SessionFiles(int fileId, int sessionId, String fileName, String filePath, String fileType, String uploadedBy) {
		super();
		this.fileId = fileId;
		this.sessionId = sessionId;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileType = fileType;
		this.uploadedBy = uploadedBy;
	}

	public SessionFiles(int sessionId, String fileName, String filePath, String fileType, String uploadedBy) {
		super();
		this.sessionId = sessionId;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileType = fileType;
		this.uploadedBy = uploadedBy;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	@Override
	public String toString() {
		return "SessionFiles [fileId=" + fileId + ", sessionId=" + sessionId + ", fileName=" + fileName + ", filePath="
				+ filePath + ", fileType=" + fileType + ", uploadedBy=" + uploadedBy + "]";
	}

}
