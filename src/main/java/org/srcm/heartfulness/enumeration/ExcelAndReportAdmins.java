package org.srcm.heartfulness.enumeration;

public enum ExcelAndReportAdmins {

	SYSTEM_ADMIN("SYSTEM_ADMIN"),
	G_CONNECT_ADMIN("G_CONNECT_ADMIN"),
	REGIONAL_ADMIN("REGIONAL_ADMIN");

	private String value;

	private ExcelAndReportAdmins(String value) {
		this.value = value;
	}

	private ExcelAndReportAdmins() {
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
