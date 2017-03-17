package org.srcm.heartfulness.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/**
 * Created by goutham on 21-12-2015
 */

//ignore "bytes" when return json format
@JsonIgnoreProperties({"bytes"}) 
public class ExcelMetaData {

	private String fileName;
	private int fileSize;
	private String fileType;
	private String excelVersion;
	private String status;
	private List<String> errorMsg;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getExcelVersion() {
		return excelVersion;
	}
	public void setExcelVersion(String excelVersion) {
		this.excelVersion = excelVersion;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(List<String> errorMsg) {
		this.errorMsg = errorMsg;
	}
	@Override
	public String toString() {
		return "ExcelMetaData [fileName=" + fileName + ", fileSize=" + fileSize + ", fileType=" + fileType
				+ ", excelVersion=" + excelVersion + ", status=" + status + ", errorMsg=" + errorMsg + "]";
	}


}