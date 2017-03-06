package org.srcm.heartfulness.enumeration;

public enum ExcelAndReportAdmins {
	
	SYSTEM_ADMIN("ROLE_SYSTEM_ADMIN"),
	G_CONNECT_ADMIN("ROLE_G_CONNECT_ADMIN"),
	REGIONAL_ADMIN("ROLE_REGIONAL_ADMIN");
	
	String value;

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
