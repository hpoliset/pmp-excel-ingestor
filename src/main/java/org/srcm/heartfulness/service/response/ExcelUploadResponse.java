package org.srcm.heartfulness.service.response;

import java.util.List;

import org.srcm.heartfulness.enumeration.ExcelType;

/**
 * This class is the response for the uploaded files
 * in case of restful service it will act as a json object
 * which sends validation error
 * 
 * @author Goutham
 *
 */
public class ExcelUploadResponse {

	private String fileName;
	private ExcelType excelVersion;
	private String status;
	private List<String> errorMsg;
	
	public ExcelUploadResponse() {
		super();
	}

	public ExcelUploadResponse(String fileName, ExcelType excelVersion, String status, List<String> errorMsg) {
		super();
		this.fileName = fileName;
		this.excelVersion = excelVersion;
		this.status = status;
		this.errorMsg = errorMsg;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ExcelType getExcelVersion() {
		return excelVersion;
	}

	public void setExcelVersion(ExcelType excelVersion) {
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
		return "ExcelUploadResponse [fileName=" + fileName + ", excelVersion=" + excelVersion + ", status=" + status
				+ ", errorMsg=" + errorMsg + "]";
	}
}