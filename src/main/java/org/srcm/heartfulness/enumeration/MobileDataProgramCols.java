/**
 * 
 */
package org.srcm.heartfulness.enumeration;

/**
 * @author Koustav Dutta
 *
 */
public enum MobileDataProgramCols {
	
	VERSION_ID("Version ID",0,0),
	ID("ID",1,0),
	EVENT_ID("Event ID", 2, 0), 
	EVENT_TYPE("Event Type", 3, 0),
	EVENT_NAME("Event Name", 4, 0),
	EVENT_FROM_DATE("Event Date", 5, 0),
	EVENT_TO_DATE("To Date", 6, 0),
	EVENT_CITY("Event City", 7, 0), 
	EVENT_STATE("Event State", 8, 0), 
	EVENT_COORDINATOR_NAME("Event Coordinator Name", 9, 0), 
	EVENT_COORDINATOR_MAIL("Event Coordinator Email ID", 10, 0), 
	ORGANIZATION_NAME("Organisation Name", 11, 0),
	ORGANIZATION_CONTACT_PERSON("Organisation Contact Person", 12, 0),
	ORGANIZATION_CONTACT_EMAIL_ID("Organisation Contact Email ID", 13, 0),
	ORGANIZATION_CONTACT_MOBILE("Organisation Contact Mobile", 14, 0),
	ORGANIZATION_WEBSITE("Organisation Website", 15, 0),
	PRECEPTOR_NAME("Preceptor Name", 16, 0), 
	PRECEPTOR_ID("Preceptor ID",17, 0);	
	
	private String header;
	private int cell;
	private int row;
	
	private MobileDataProgramCols(String header, int cell, int row) {
		this.header = header;
		this.cell = cell;
		this.row = row;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
}
